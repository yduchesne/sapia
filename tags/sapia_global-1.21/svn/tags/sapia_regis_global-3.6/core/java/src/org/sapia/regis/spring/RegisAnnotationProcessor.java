package org.sapia.regis.spring;

import java.lang.annotation.Annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * This class loads a a {@link Registry} and processes the following Regis-specific annotations:
 * 
 * <ol>
 *  <li> {@link NodeType}
 *  <li> {@link Prop}
 *  <li> {@link Lookup}
 * </ol>
 * 
 * This class supports built-in {@link Converter}s that handle the following types:
 * 
 * <ol>
 *   <li> {@link Enum} 
 *   <li> {@link Date}
 *   <li> All Java primitives.
 * </ol>
 * 
 * Additional {@link Converter}s can be added to an instance of this class (see {@link #setConverters(List)}).
 * In such a case, newly added converters have precedence over the ones that an instance of this
 * class already holds.
 * 
 * @author yduchesne
 *
 */
public class RegisAnnotationProcessor implements BeanPostProcessor, InitializingBean{
  
  private Registry        registry;
  private String          bootstrap;  
  private List<Converter> converters = new ArrayList<Converter>();
  
  {
    converters.add(new EnumConverter());
    converters.add(new DateConverter());
    converters.add(new ConverterAdapter());
  }
  
  void setRegistry(Registry registry) {
    this.registry = registry;
  }
  
  /**
   * Sets this instance's bootstrap String.
   * 
   * @param bootstrap a boostrap {@link String}.
   */
  public void setBootstrap(String bootstrap) {
    this.bootstrap = bootstrap; 
  }
  
  /**
   * Adds the given {@link List} of {@link Converter}s to the head of this 
   * instance's current list of such converters. This means that the newly 
   * added converters have precedence over the ones that this instance already 
   * encapsulates.
   * 
   * @param toAdd the {@link List} of {@link Converter}s to add
   * to this instance.
   */
  public void setConverters(List<Converter> toAdd){
    toAdd.addAll(converters);
    converters = toAdd;
  }
  
  @Override
  public void afterPropertiesSet() throws Exception {
    if(bootstrap == null){
      throw new IllegalStateException("Bootstrap not set");
    }
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
          if(!anno.defaultTo().equals("")){
            prop = new PropertyImpl(propName, anno.defaultTo());            
          }
          else if(anno.optional()){
            return;
          }
          else {
            throw new FatalBeanException(
                String.format("Could not set property %s on field or method %s of %s: " +
                              "property not found in registry node: %s", 
                              propName, 
                              mutator.getName(), 
                              bean.getClass(), 
                              node.getAbsolutePath())
            );
          }
        }        
      }
      // anno == null && !nodeType.auto()
      else{
        return;
      }

      Converter converter = findConverterFor(mutator.getType());
      Object converted = null;
      try{
        converted = converter.convertFrom(mutator.getType(), prop.getValue());
      }catch(ParseException e){
        throw new FatalBeanException(
            String.format("Could not set property %s on field or method %s of %s: " +
                          "problem parsing value to field or method type", 
                          prop.getKey(), 
                          mutator.getName(), 
                          bean.getClass()
            ),
            e
        );        
      }
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
  
  private Converter findConverterFor(Class<?> type){
    for(Converter c : converters){
      if(c.accepts(type)){
        return c;
      }
    }
    throw new IllegalStateException(String.format("No Converter could be found for %s", type));
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
  
  private static class ConverterAdapter implements Converter{
    
    private SimpleTypeConverter delegate = new SimpleTypeConverter();
    
    @Override
    public boolean accepts(Class<?> type) {
      return true;
    }
    
    @Override
    public Object convertFrom(Class<?> type, String value) {
      return delegate.convertIfNecessary(value, type);
    }
    
  }
  
  private static class EnumConverter implements Converter{
      
    @Override
    public boolean accepts(Class<?> type) {
      return type.isEnum();
    }
    
    @Override
    @SuppressWarnings(value="unchecked")
    public Object convertFrom(Class<?> type, String value) {
      return Enum.valueOf((Class<? extends Enum>)type, value.toUpperCase());
    }
  }
  
  private static class DateConverter implements Converter{
    
    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z");
    
    @Override
    public boolean accepts(Class<?> type) {
      return type.equals(Date.class);
    }
    
    @Override
    public Object convertFrom(Class<?> type, String value) throws ParseException{
      return format.parse(value);
    }
  }
  
  
}
