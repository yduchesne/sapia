package org.sapia.regis.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.sapia.regis.Node;
import org.sapia.regis.Property;

/**
 * This annotation is used to annotate fields or methods that should be injected with
 * Registry the corresponding {@link Property} of a given {@link Node} at runtime.
 * 
 * @see NodeType
 * @see RegisAnnotationProcessor
 * 
 * @author yduchesne
 */
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Prop {

  /**
   * Returns the name of the corresponding property, or empty string if the field or
   * method name of the mapped bean should be used.
   */
  String name() default "";
  
  /**
   * Indicates if the value corresponding to the property is optional. 
   */
  boolean optional() default true;

  /**
   * Provides a default value in case the corresponding property is null.
   */
  String defaultTo() default "";
}
