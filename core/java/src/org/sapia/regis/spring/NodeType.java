package org.sapia.regis.spring;

import java.lang.annotation.ElementType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.sapia.regis.Node;

/**
 * This annotation is used to annotate classes that should be injected with
 * properties.
 *
 * @see Prop
 * @see RegisAnnotationProcessor
 * 
 * @author yduchesne
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NodeType {

  /**
   * Indicates the class corresponding to a given {@link Node}.
   */
  Class<?> type();
  
  /**
   * Indicates if all properties should be looked up, regardless if they have 
   * a corresponding {@link Prop} annotation on the field/setter.
   */
  public boolean auto() default false;
  
}
