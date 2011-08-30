package org.sapia.regis.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.sapia.regis.Node;

/**
 * This annotation is used to annotate fields or methods that should be injected with a
 * Registry {@link Node}s at runtime.
 * 
 * @see RegisAnnotationProcessor
 * 
 * @author yduchesne
 */
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Lookup {

  /**
   * If specified, indicates the path to a {@link Lookup}.
   * 
   * @return the path of a {@link Lookup}.
   */
  String path() default "";
  
}
