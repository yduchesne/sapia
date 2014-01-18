package org.sapia.mediafire.core

/**
 * A factory of Tracer instances.
 */
object TracerFactory {

  var tracer: Tracer = new DefaultTracer()
    
  def getTracer(): Tracer = {
    return tracer
  }
  
  def setTracer(someTracer: Tracer): Unit = {
    tracer = someTracer
  }
}