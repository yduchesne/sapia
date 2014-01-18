package org.sapia.mediafire.core

/**
 * Default tracer implementation.
 */
class DefaultTracer extends Tracer {
  
  def trace(msg: String): Unit = {
    System.out.println(msg);
  }

}