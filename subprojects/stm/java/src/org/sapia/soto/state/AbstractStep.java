/*
 * AbstractStep.java
 *
 * Created on June 10, 2005, 9:04 AM
 */

package org.sapia.soto.state;

import org.apache.commons.lang.ClassUtils;

/**
 * Abstract implementation of the <code>Step</code> interface. Implements
 * the <code>getName()</code> method by returning the class name of the 
 * implementation (excluding the package name).
 *
 * @see Step
 *
 * @author yduchesne
 */
public abstract class AbstractStep implements Step{
  
  /** Creates a new instance of AbstractStep */
  public AbstractStep() {
  }
  
  public String getName(){
    return ClassUtils.getShortClassName(getClass());
  }
  
}
