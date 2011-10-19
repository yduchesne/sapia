/*
 * Pop.java
 *
 * Created on June 9, 2005, 9:37 AM
 */

package org.sapia.soto.state.util;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;

/**
 *
 * @author yduchesne
 */
public class Pop implements Step{
  
  /** Creates a new instance of Pop */
  public Pop() {
  }
  
  public String getName(){
    return ClassUtils.getShortClassName(getClass());
  }
  
  public void execute(Result res){
    res.getContext().pop();
  }
  
}
