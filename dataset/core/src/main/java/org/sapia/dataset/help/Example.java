package org.sapia.dataset.help;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to provide method usage examples.
 * 
 * @author yduchesne
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Example {
  
  /**
   * The example's caption.
   */
  String caption();

  /**
   * A documentation string.
   */
  String content();
}
