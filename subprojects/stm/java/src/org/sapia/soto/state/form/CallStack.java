/*
 * CallStack.java
 *
 * Created on September 2, 2005, 5:19 PM
 *
 */

package org.sapia.soto.state.form;

import java.util.Stack;

/**
 *
 * @author yduchesne
 */
public class CallStack {
  
  private Stack _calls = new Stack();
  private IdFactory _fac;
    
  /** Creates a new instance of CallStack */
  public CallStack(IdFactory fac) {
    _fac = fac;
  }
  
  public synchronized Call peek(){
    if(_calls.size() == 0)
      return null;
    return (Call)_calls.peek();
  }
  
  public synchronized Call pop(){
    if(_calls.size() == 0)
      return null;
    return (Call)_calls.pop();
  }  
  
  public synchronized Call create(){
    Call call = new Call(_fac);
    _calls.push(call);
    return call;
  }
  
  public synchronized void clear(){
    if(_calls.size() == 0) return;
    _calls.clear();
  }
  
  public String toString(){
    return new StringBuffer("[")
      .append("calls=").append(_calls)
      .append("]").toString();
  }
  
}
