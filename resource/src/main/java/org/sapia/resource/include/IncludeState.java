package org.sapia.resource.include;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.sapia.resource.ResourceCapable;

/**
 * This class is used to perform file-includes programmatically. It internally
 * keeps a stack of {@link org.sapia.resource.include.IncludeContext} instances that
 * correspond to recursive resource-inclusion operations.
 * <p>
 * Internally, state is maintained on a per-thread and per-application basis. This allows multiple
 * threads to perform resource inclusion operations in parallel, and isolates different applications/frameworks so
 * that resource inclusion does not conflict (this framework could be used by a given framework X, that could itself
 * be used by a given framework Y, also using this framework...).
 * <p>
 * In order to use this class, applications must:
 * <ol>
 *   <li>Extend the {@link org.sapia.resource.include.IncludeContext} class and overridde the {@link org.sapia.resource.include.IncludeContext#doInclude(InputStream, Object)}
 *   method.
 *   <li>Implement an {@link org.sapia.resource.include.IncludeContextFactory}, that will create instances of the above {@link org.sapia.resource.include.IncludeContext}.
 *   <li>Implement a {@link org.sapia.resource.ResourceCapable} class. An instance of that class is in charge of resolving resources (see {@link org.sapia.resource.ResourceCapable}
 *   for details).
 * </ol>
 * <p>
 * 
 * Then, the first step in using this class in order to perform resource inclusion is to create an {@link org.sapia.resource.include.IncludeConfig}
 * instance that is kept by the application and can be used for multiple resource-inclusion operations:
 * 
 * <pre>
 * IncludeConfig config = IncludeState.createConfig("org.acme.myapp", myIncludeContextFactory, myResourceCapableObject);
 * </pre>
 * 
 * <p>
 * It should be noted that the {@link #createConfig(String, IncludeContextFactory, ResourceCapable)} method takes a so-called
 * "application key" has an argument. This is because this class could be used by multiple applications/frameworks residing within
 * the same VM and classloader... Provided these frameworks use this class concurrently, this could pose problem. Therefore,
 * the registering inclusion state under a given application key, and to the current thread, ensures that no conflict occur.
 * <p>
 * Note that the best way to guarantee uniqueness of application keys is to use root package names.
 * 
 * <p>
 * Once an IncludeConfig has been created, use it with this class to perform resource-inclusion:
 * <p>
 * 
 * <pre>
 * Object someObject = IncludeState.createContext("file:conf/main.xml", config).include();
 * </pre>
 * <p>
 * The framework handles relative file resolution. Therefore, if the following code comes after the previous one,
 * the resource will be resolved relatively to "conf/main.xml" (meaning that it will correspond
 * to "conf/includes/database.xml"):
 * 
 * <pre>
 * Object someObject = IncludeState.createContext("includes/database.xml", config).include();
 * </pre>
 * <p>
 * This framework provides a {@link Resource} primitive. Multiple implementations exist, that correspond to
 * the following protocol schemes:
 * 
 * <ul>
 *   <li>file (e.g.: file:conf/main.xml, file:/home/foo/conf/main.xml).
 *   <li>resource (e.g.: resource:/org/acme/myapp/main.xml).
 *   <li>http
 * </ul>
 * <p>
 * A {@link org.sapia.resource.ResourceCapable} implementation is provided: the {@link org.sapia.resource.ResourceHandlerChain}
 * class, which implements a chain of responsibility that encapsulates {@link org.sapia.resource.ResourceHandler}s. You could use
 * these implementations as follows:
 * 
 * <pre>
 * ResourceHandlerChain resources = new ResourceHandlerChain();
 * resource.append(new FileResourceHanler());
 * resource.append(new ClassPathResourceHanler());
 * resource.append(new UrlPathResourceHanler());
 * 
 * IncludeConfig config = IncludeState.createConfig("org.acme.myapp", myIncludeContextFactory, resources);
 * </pre>
 * 
 * @see org.sapia.resource.include.IncludeContext
 * @see org.sapia.resource.include.IncludeContextFactory
 * @see org.sapia.resource.include.IncludeConfig
 * 
 * @author yduchesne
 *
 */
public class IncludeState {
  
  private static ThreadLocal _state = new ThreadLocal();
  private Stack _stack = new Stack();
  
  static{
    _state.set(Collections.synchronizedMap(new HashMap()));
  }
  
  IncludeState(){
  }
  
  /**
   * @param appKey the application key    
   * @param fac an <code>IncludeContextFactory</code>.
   * @param resources a <code>ResourceCapable</code> instance.
   * 
   * @return an <code>IncludeConfig</code>.
   */
  public static synchronized IncludeConfig createConfig(
      String appKey, 
      IncludeContextFactory fac, 
      ResourceCapable resources){
    IncludeConfig conf = new IncludeConfig(appKey, fac, resources);
    return conf;
  }
  
  /**
   * @param appKey the application key
   * @return this instance's current <code>IncludeContext</code>, or <code>null</code> if no
   * such instance exists (i.e.: none has been stacked).
   */
  public static synchronized IncludeContext currentContext(String appKey){
    IncludeState instance = instance(appKey);
    if(instance._stack.size() == 0){
      return null;
    }
    else{
      return (IncludeContext)instance._stack.peek(); 
    }
  }
  
  /**
   * This method internally creates an {@link IncludeContext} that it pushes on its internall include stack
   * before returning it.
   * 
   * @param uri the URI for which to create a new <code>IncludeContext</code>.
   * @param config the <code>IncludeConfig</code> to use to create the context.
   * 
   * @return a new <code>IncludeContext</code>.
   */  
  public static synchronized IncludeContext createContext(
      String uri, 
      IncludeConfig config){
    IncludeState instance = instance(config.getAppKey());
    IncludeContext child = config.getFactory().createInstance();
    if(instance != null && instance._stack.size() > 0){
      child.setParent((IncludeContext)instance._stack.peek()); 
    }
    instance._stack.push(child);
    child.setConfig(config);    
    child.setUri(uri);
    return child;
  }  
  
  static void popContext(String appKey){
    IncludeState instance = instance(appKey);
    if(instance._stack.size() > 0){
      instance._stack.pop();
    }
  }
  
  static synchronized IncludeState instance(String appKey){
    Map states = (Map)_state.get();
    if(states == null){
      states = new HashMap();
      _state.set(Collections.synchronizedMap(states));
    }
    IncludeState state = (IncludeState)states.get(appKey);
    if(state == null){
      state = new IncludeState();
      ((Map)_state.get()).put(appKey, state);
    }
    return state;
  }
  
}
