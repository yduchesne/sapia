package org.sapia.soto.state;

/**
 * This behavior specifies the behavior of handlers of state machine execution
 * errors. Upon executing a state or series of states, an error can be signaled
 * to the state machine, through the <code>Result</code> object that passes
 * from one state to another. If such is the case, the state machine recuperates
 * the signaled error and creates a new <code>Result</code> object, passing on
 * that new result's context stack the error that was generated.
 * <p>
 * Then, the state machine goes through each of its error handlers, calling the
 * <code>handle()</code> method on them, and passing to the method the new
 * <code>Result</code> object that was created. The first
 * <code>handle()</code> method call that returns <code>true</code>
 * indicates that the error has been handled.
 * <p>
 * Error handler implementations have access to the error that was generated by
 * accessing the stack of the current execution context:
 * 
 * <pre>
 * 
 *  public boolean handle(Result result){
 *    Err err (Err)= result.getContext().currentObject();
 *    ... do something with it...
 *    return true;
 *  }
 *  
 * </pre>
 * 
 * <p>
 * If an error handler does not handle a given type of error, it only needs
 * returning false upon its <code>handle()</code> method being called:
 * 
 * <pre>
 * 
 *  public boolean handle(Result result){
 *    Err err = (Err)result.getContext().currentObject();
 *    if(err.getThrowable() != null){
 *      .. do something with it ...
 *    	 return true;
 *    }
 *    return false;
 *  }
 *  
 * </pre>
 * 
 * 
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public interface ErrorHandler {

  /**
   * @param result
   *          a <code>Result</code>.
   * @return <code>true</code> if the handler could handle the error passed in
   *         through the given <code>Result</code>.
   */
  public boolean handle(Result result);

}