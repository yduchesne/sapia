package org.sapia.regis.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Property;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;
import org.sapia.regis.codegen.NodeCapable;
import org.sapia.regis.codegen.NodeLookup;
import org.sapia.regis.impl.PropertyImpl;
import org.sapia.regis.util.Work;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * This class loads a  
 * @author yduchesne
 *
 */
public class RegisAnnotationProcessor implements BeanPostProcessor, InitializingBean{
  
  private Registry    registry;
  private String      bootstrap;  
  
  void setRegistry(Registry registry) {
    this.registry = registry;
  }
  
  public void setBootstrap(String bootstrap) {
    this.bootstrap = bootstrap; 
  }
  
  @Override
  public void afterPropertiesSet() throws Exception {
    Properties props = new Properties();
    props.setProperty(RegistryContext.BOOTSTRAP, bootstrap);
    RegistryContext ctx = new RegistryContext(props);
    registry = ctx.connect();
  }

  @Override
  public Object postProcessBeforeInitialization(final Object bean, String name)
      throws BeansException {
    Work.with(registry).run(new Runnable(){
      public void run(){
        NodeLookup lookup = NodeLookup.create(registry);
        for(Field field : getFields(bean.getClass())){
          processMutator(new FieldMutator(field), bean, lookup);
        }
        for(Method method : getMethods(bean.getClass())){
          processMutator(new MethodMutator(method), bean, lookup);
        }
      }
    });
    return bean;
  }
  
  @Override
  public Object postProcessAfterInitialization(Object bean, String name)
      throws BeansException {
    return bean;
  }
  
  private void processMutator(Mutator mutator, Object bean, NodeLookup lookup) throws BeansException{
    
    if(mutator.getType().equals(Registry.class)){
      mutator.setAccessible(true);        
      try{
        mutator.set(bean, registry);
      }catch(Exception e){
        throw new FatalBeanException(
            String.format("Could not set %s instance on field or method %s of %s" , 
                Registry.class, 
                mutator.getName(),
                bean.getClass()
            ), 
            e
        );
      }
    }
    else if(mutator.getType().equals(Node.class) && mutator.isAnnotationPresent(Lookup.class)){
      
      Lookup anno = mutator.getAnnotation(Lookup.class);
      
      if(anno.path().equals("")){
        throw new FatalBeanException(
            String.format(
                "node() not specified on Regis annotation instance for field or method %s of %s", 
                mutator.getName(),
                bean.getClass()
            )
        );
      }
      mutator.setAccessible(true);
      Node node = registry.getRoot().getChild(Path.parse(anno.path()));
      if(node == null){
        throw new FatalBeanException(
            String.format(
                "Node not found at %s for field or method %s of %s", 
                anno.path(), 
                mutator.getName(),
                bean.getClass()
            )
        );
      }
      try{
        mutator.set(bean, node);
      }catch(Exception e){
        throw new FatalBeanException(
            String.format(
                "Could not set node %s on field or method %s of %s" , 
                node.getAbsolutePath(), 
                mutator.getName(),
                bean.getClass()
            ), 
            e
        );
      }
    }
    else if(bean.getClass().isAnnotationPresent(NodeType.class)){
      NodeType nodeType = bean.getClass().getAnnotation(NodeType.class);
      Prop anno = mutator.getAnnotation(Prop.class);
      Node node = lookup.getNodeFor(nodeType.type());
      
      Property prop;
      if(anno == null && nodeType.auto()){
        String propName = propertyName(mutator);
        prop = node.getProperty(propName);
        if(prop.isNull()){
          return;
        }
      }
      else if(anno != null){
        String propName = anno.name().equals("") ? propertyName(mutator) : anno.name();
        prop = node.getProperty(propName);
        if(prop.isNull()){
          if(anno.defaultTo().equals("")){
            throw new FatalBeanException(
                String.format("Could not set property %s on field or method %s of %s: " +
                              "property not found in registry node: %s", 
                              propName, 
                              mutator.getName(), 
                              bean.getClass(), 
                              node.getAbsolutePath())
            );
          }
          else{
            prop = new PropertyImpl(propName, anno.defaultTo());
          }
        }        
      }
      // anno == null && !nodeType.auto()
      else{
        return;
      }

      SimpleTypeConverter converter = new SimpleTypeConverter();
      Object converted = converter.convertIfNecessary(prop.getValue(), mutator.getType());
      mutator.setAccessible(true);
      try{
        mutator.set(bean, converted);
      }catch(Exception e){
        throw new FatalBeanException(
            String.format(
                "Could not set property on field or method %s of %s: " +
                "property value was coerced to %s, field or method type: %s", 
                mutator.getName(), 
                bean.getClass(), 
                converted.getClass(),
                mutator.getType()
            ),
            e
        );
      }
    }
    else if(!mutator.getType().equals(Node.class) && mutator.isAnnotationPresent(Lookup.class)){
      NodeCapable node = (NodeCapable)lookup.getRawInstanceFor(mutator.getType());
      mutator.setAccessible(true);
      try{
        mutator.set(bean, node);
      }catch(Exception e){
        throw new FatalBeanException(
            String.format(
                "Could not set node %s on field or method %s of %s" , 
                node.getNode().getAbsolutePath(), 
                mutator.getName(),
                bean.getClass()
            ), 
            e
        );
      }
    }    
  }
  
  ///////////////////////////// INNER CLASSES /////////////////////////////////
  
  interface Mutator{
    
    Class<?> getType();
    void set(Object instance, Object value) throws Exception;
    void setAccessible(boolean accessible);
    public <T extends Annotation> T getAnnotation(Class<T> annoClass);
    public boolean isAnnotationPresent(Class<? extends Annotation> annoClass);
    public String getName();    
    
  }
  
  private static class FieldMutator implements Mutator{
    
    Field field;
    
    public FieldMutator(Field field) {
      this.field = field;
    }
    
    @Override
    public Class<?> getType() {
      return field.getType();
    }
    
    @Override
    public void set(Object instance, Object value) throws Exception {
      field.set(instance, value);
    }
    
    @Override
    public void setAccessible(boolean accessible) {
      field.setAccessible(accessible);
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annoClass){
      return field.getAnnotation(annoClass);
    }
    
    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annoClass) {
      return field.isAnnotationPresent(annoClass);
    }
    
    @Override
    public String getName(){
      return field.getName();
    }
  }
  
  private static class MethodMutator implements Mutator{
    
    Method method;
    
    public MethodMutator(Method method) {
      this.method = method;
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annoClass) {
      return method.getAnnotation(annoClass);
    }
    
    @Override
    public String getName() {
      return method.getName();
    }
    
    @Override
    public Class<?> getType() {
      return method.getParameterTypes()[0];
    }
    
    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annoClass) {
      return method.isAnnotationPresent(annoClass);
    }
    
    @Override
    public void set(Object instance, Object value) throws Exception {
      method.invoke(instance, new Object[]{value});
    }
    
    @Override
    public void setAccessible(boolean accessible) {
      method.setAccessible(accessible);
    }
  }
  
  private List<Field> getFields(Class<?> clazz){
    
    List<Field> fields = new ArrayList<Field>();

    Class<?> current = clazz;
    do{
      for(Field f : current.getDeclaredFields()){
        fields.add(f);
      }
    }while((current = current.getSuperclass()) != null && !current.equals(Object.class));
    
    return fields;
  }
  
  private List<Method> getMethods(Class<?> clazz){
    
    List<Method> methods = new ArrayList<Method>();

    Class<?> current = clazz;
    do{
      for(Method m : current.getDeclaredMethods()){
        if(m.getName().startsWith("set") && m.getParameterTypes().length == 1){
          methods.add(m);
        }
      }
    }while((current = current.getSuperclass()) != null && !current.equals(Object.class));
    
    return methods;
  }  
  
  
  private String propertyName(Mutator mutator){
    String propName = mutator.getName();
    if(propName.startsWith("is")){
      propName = propName.substring(2);
    }
    if(propName.startsWith("set")){
      propName = propName.substring(3);
    }    
    return decapitalize(propName);
    
  }
  
  private String decapitalize(String s){
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < s.length(); i++){
      if(i == 0){
        sb.append(Character.toLowerCase(s.charAt(i)));
      }
      else{
        sb.append(s.charAt(i));
      }
    }
    return sb.toString();
  }
  
  
}
