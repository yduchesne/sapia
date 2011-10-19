package org.sapia.soto.state.dispatcher;

import org.sapia.soto.state.Context;

/**
 * This interface specifies matching behavior. Implementations thereof are
 * intented to be used in the context of STM; more precisely, an implementation
 * of this interface dispatches "requests" to appropriate state machines (the
 * implementation determines which state of which state machine should be
 * executed, according to the passed in information - a given URI and context
 * object).
 * <p>
 * This interface has been designed for the purpose of allowing the use of
 * multiple state machines in the context of a given application. Such a
 * fragmentation allows for a modularity that might prove necessary on given
 * projects (each state machine being in charge of a specific type of logic).
 * <p>
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
public interface Dispatcher {

  /**
   * This method dispatches the request corresponding to a given URI (by
   * convention, a given as a list of tokens delimited by '/') and context
   * object. This method is meant to allow easy integration of dispatchers
   * within web containers (the URI can in this case correspond to the return
   * value of the <code>getPathInfo()</code> method of the
   * <code>HttpServletRequest</code>), but does not force such usage.
   * 
   * @param uri
   *          a URI, corresponding to a request.
   * @param ctx
   *          a <code>Context</code>, holding request data.
   * @throws Exception
   *           if a problem occurs while dispatching.
   * @return <code>true</code> if this instance proceeded to the dispatch.
   */
  public boolean dispatch(String uri, Context ctx) throws Exception;

}
