package org.sapia.soto.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.sapia.soto.Env;
import org.sapia.soto.InstanceOfSelector;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.ServiceConfiguration;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.lifecycle.LifeCycleManager;
import org.sapia.soto.spring.resource.SpringResourceLoader;
import org.sapia.soto.spring.selectors.BeanCountSelector;
import org.sapia.soto.spring.selectors.BeanNameSelector;
import org.sapia.soto.spring.selectors.BeanPostProcessorSelector;
import org.sapia.util.CompositeRuntimeException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.OrderComparator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * This class implements Soto's {@link LifeCycleManager} interface and Spring's 
 * {@link ApplicationContext} interface. It is used to integrate Spring singleton services
 * into a SotoContainer, in a transparent fashion.
 * <p>
 * The following <b>limitations</b> must be noted:
 * <ul>
 *   <li>Only singleton beans are supported (according to Soto's instantiation model). 
 *   <li>Scopes are not supported (this class does not implement the {@link ConfigurableBeanFactory} interface,
 *   and therefore does not provide the <code>registerScope()</code> method.
 *   <li>The {@link BeanFactoryPostProcessor} is not supported - the underlying configuration is Soto-based, therefore
 *   implementing that interface in the context of Soto does not make sense.
 *   <li>
 * </ul>
 * 
 * 
 * @author yduchesne
 */
public class SpringLifeCycleManager implements LifeCycleManager, ApplicationContext, ObjectHandlerIF{
  
  private static final String MSG_SOURCE_NAME = "messageSource";
  
  private Env _env;
  private SpringResourceLoader _loader;
  private long _startupDate = System.currentTimeMillis();
  private MessageSource _source;
  private PathMatchingResourcePatternResolver _matchingResolver;
  private ApplicationEventMulticaster _multicaster;
  private List _postProcessors = new ArrayList(3);
  
  ///// LifeCycleManager's own life-cycle methods //////////////////////////////////
  
  public void init(Env env){
    _env = env;
    _loader = new SpringResourceLoader(_env, getClass().getClassLoader());
    _matchingResolver = new PathMatchingResourcePatternResolver(_loader);
  }

  public void postInit(Env env) {
    multicaster().multicastEvent(new ContextRefreshedEvent(this));
    if(_source == null){
      messageSource();
    }
  }
  
  public void dispose(Env env){
    multicaster().multicastEvent(new ContextClosedEvent(this));
  }
  
  ///// Bean life-cycle
  
  public void initService(ServiceConfiguration conf) throws Exception {
    if(conf.getService() instanceof BeanNameAware){
      ((BeanNameAware)conf.getService()).setBeanName(conf.getServiceID());
    }
    if(conf.getService() instanceof BeanClassLoaderAware){
      ((BeanClassLoaderAware)conf.getService()).setBeanClassLoader(this.getClass().getClassLoader());
    }
    if(conf.getService() instanceof BeanFactoryAware){
      ((BeanFactoryAware)conf.getService()).setBeanFactory(this);
    }    
    if(conf.getService() instanceof ResourceLoaderAware){
      ((ResourceLoaderAware)conf.getService()).setResourceLoader(_loader);
    }
    if(conf.getService() instanceof ApplicationContextAware){
      ((ApplicationContextAware)conf.getService()).setApplicationContext(this);
    }    
    if(conf.getService() instanceof ApplicationEventPublisherAware){
      ((ApplicationEventPublisherAware)conf.getService()).setApplicationEventPublisher(this);
    }
    if(conf.getService() instanceof MessageSourceAware){
      ((MessageSourceAware)conf.getService()).setMessageSource(messageSource());
    }    
    
    //// initializing
    
    if(!(conf.getService() instanceof BeanPostProcessor)){
      List processors = new ArrayList(_postProcessors);
      processors.addAll(_env.lookup(new BeanPostProcessorSelector(), false));
      for(int i = 0; i < processors.size(); i++){
        BeanPostProcessor proc = (BeanPostProcessor)processors.get(i);
        Object service = proc.postProcessBeforeInitialization(
            conf.getService(), 
            conf.getServiceID() != null ? conf.getServiceID() : conf.getService().getClass().getName());
        if(service != null){
          conf.setService(service);
        }
      }
    }
    
    if(conf.getService() instanceof InitializingBean){
      ((InitializingBean)conf.getService()).afterPropertiesSet();
    }
    else{
      conf.invokeInitMethod();
    }
    
    if(!(conf.getService() instanceof BeanPostProcessor)){
      List processors = new ArrayList(_postProcessors);
      processors.addAll(_env.lookup(new BeanPostProcessorSelector(), false));      
      for(int i = 0; i < processors.size(); i++){
        BeanPostProcessor proc = (BeanPostProcessor)processors.get(i);
        Object service = proc.postProcessAfterInitialization(
            conf.getService(), 
            conf.getServiceID() != null ? conf.getServiceID() : conf.getService().getClass().getName());
        if(service != null){
          conf.setService(service);
        }
      }    
    }
    
    if(conf.getService() instanceof ApplicationListener){
      multicaster().addApplicationListener((ApplicationListener)conf.getService());
    }
    
    if(conf.getService() instanceof BeanPostProcessor){
      _postProcessors.add((BeanPostProcessor)conf.getService());
    }    
  }
  
  public void startService(ServiceConfiguration conf) throws Exception {
  }
  
  public Object lookupService(String name, Object service){
    if(service instanceof FactoryBean){
      try{
        return ((FactoryBean)service).getObject();
      }catch(Exception e){
        throw new CompositeRuntimeException("Could not get object from factory bean", e);
      }
    }
    else{
      return service;
    }
  }
  
  public Class getServiceClass(Object service) {
    if(service instanceof FactoryBean){
      return ((FactoryBean)service).getObjectType();
    }
    else{
      return service.getClass();
    }
  }

  public void disposeService(ServiceConfiguration conf) throws Exception{
    if(conf.getService() instanceof DisposableBean){
      try{
        ((DisposableBean)conf.getService()).destroy();
      }catch(Exception e){
        e.printStackTrace();
      }
    }
  }
  
  ///// BeanFactory //////////////////////////////////////////////////////////////////////////////////
  
  public boolean containsBean(String name) {
    return _env.lookup(new BeanNameSelector(name), false).size() > 0;    
  }
  
  public Object getBean(String name) throws BeansException {
    try{
      return _env.lookup(name);
    }catch(NotFoundException e){
      throw new NoSuchBeanDefinitionException(e.getMessage());
    }
  }
  
  public Object getBean(String name, Class type) throws BeansException {
    try{
      Object service = _env.lookup(name);
      if(type.isAssignableFrom(service.getClass())){
        return service;
      }
      else{
        throw new BeanNotOfRequiredTypeException("Wrong service type", type, service.getClass());
      }
    }catch(NotFoundException e){
      throw new NoSuchBeanDefinitionException(e.getMessage());
    }
    
  }
  
  public String[] getAliases(String name) {
    return new String[0];
  }
  
  public Class getType(String name) throws NoSuchBeanDefinitionException {
    return getBean(name).getClass();
  }
  
  public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
    return false;
  }
  
  public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
    return true;
  }
  
  public boolean isTypeMatch(String name, Class type) throws NoSuchBeanDefinitionException {
    return type.isAssignableFrom(getBean(name).getClass());
  }
  
  ///// ApplicationContext //////////////////////////////////////////////////////////////////////////////////
  
  public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
    throw new UnsupportedOperationException("getAutowireCapableBeanFactory()");
  }
  
  public int getBeanDefinitionCount() {
    return _env.lookup(new BeanCountSelector(), false).size();
  }
  
  public boolean containsBeanDefinition(String name) {
    return _env.lookup(new BeanNameSelector(name), false).size() > 0;
  }
  
  public boolean containsLocalBean(String name) {
    return containsBean(name);
  }
  
  public String[] getBeanDefinitionNames() {
    List metas = _env.lookup(new BeanNameSelector(null), true);
    String[] toReturn = new String[metas.size()];
    for(int i = 0; i < metas.size(); i++){
      ServiceMetaData meta = (ServiceMetaData)metas.get(i);
      toReturn[i] = meta.getServiceID();
    }
    return toReturn;
  }
  
  public String[] getBeanNamesForType(Class type) {
    List metas = _env.lookup(new InstanceOfSelector(type), true);
    String[] toReturn = new String[metas.size()];
    for(int i = 0; i < metas.size(); i++){
      ServiceMetaData meta = (ServiceMetaData)metas.get(i);
      toReturn[i] = meta.getServiceID();
    }
    return toReturn;
  }
  
  public String[] getBeanNamesForType(Class type, boolean arg1, boolean arg2) {
    return getBeanNamesForType(type);
  }
  
  public Map getBeansOfType(Class type) throws BeansException {
    List metas = _env.lookup(new InstanceOfSelector(type), true);
    Map toReturn = new TreeMap();
    for(int i = 0; i < metas.size(); i++){
      ServiceMetaData meta = (ServiceMetaData)metas.get(i);
      toReturn.put(
        meta.getServiceID() != null ? meta.getServiceID() : meta.getService().getClass().getName(), 
        meta.getService());
    }
    return toReturn;
  }
  
  public Map getBeansOfType(Class type, boolean arg1, boolean arg2) throws BeansException {
    return getBeansOfType(type);
  }
  
  public ClassLoader getClassLoader() {
    return getClass().getClassLoader();
  }
  
  public String getDisplayName() {
    return "SotoBeanFactory";
  }
  
  public ApplicationContext getParent() {
    return null;
  }
  
  public BeanFactory getParentBeanFactory() {
    return null;
  }  
  
  public long getStartupDate() {
    return _startupDate;
  }
  
  public Resource getResource(String uri) {
    return _loader.getResource(uri);
  }
  
  ////// MessageSource //////////////////////////////////////////////////////////////////////////////////
  
  public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
    return messageSource().getMessage(resolvable, locale);
  }
  
  public String getMessage(String arg0, Object[] arg1, Locale arg2) throws NoSuchMessageException {
    return messageSource().getMessage(arg0, arg1, arg2);
  }
  
  public String getMessage(String arg0, Object[] arg1, String arg2, Locale arg3) {
    return messageSource().getMessage(arg0, arg1, arg2,  arg3);
  }
  
  public Resource[] getResources(String arg0) throws IOException {
    return _matchingResolver.getResources(arg0);
  }
  
  ///// ApplicationEventPublisher ////////////////////////////////////////////////////////////////////////
  
  public void publishEvent(ApplicationEvent event) {
    multicaster().multicastEvent(event);
  }
  
  ///// ObjectHandlerIF ////////////////////////////////////////////////////////////////////////
  
  public void handleObject(String name, Object obj) throws ConfigurationException {
    if(obj instanceof BeanPostProcessor){
      _postProcessors.add(obj);
      Collections.sort(_postProcessors, new OrderComparator());
    }
    if(obj instanceof ApplicationListener){
      _multicaster.addApplicationListener((ApplicationListener)obj);
    }
  }  
  
  ///// Private methods //////////////////////////////////////////////////////////////////////////////////

  private MessageSource messageSource(){
    if(_source == null){
      try{
        _source = (MessageSource)_env.lookup(MSG_SOURCE_NAME);
      }catch(NotFoundException e){
      }
      if(_source == null){
        try{
          _source = (MessageSource)_env.lookup(MessageSource.class);
        }catch(NotFoundException e){
        }      
      }
      if(_source == null){
        _source = new StaticMessageSource();
      }
    }
    return _source;
  }
  
  private ApplicationEventMulticaster multicaster(){
    if(_multicaster == null){
      try{
        _multicaster = (ApplicationEventMulticaster)_env.lookup(ApplicationEventMulticaster.class);
      }catch(NotFoundException e){
      }      
    }
    if(_multicaster == null){
      _multicaster = new SimpleApplicationEventMulticaster();
    }
    return _multicaster;
  }

}
