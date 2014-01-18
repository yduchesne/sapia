package org.sapia.mediafire.core

/**
 * Abstracts the logging mechanism.
 */
trait Tracer {

  def trace(msg: String): Unit
}