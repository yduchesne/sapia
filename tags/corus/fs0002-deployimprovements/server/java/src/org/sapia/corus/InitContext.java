package org.sapia.corus;

import org.sapia.corus.util.PropertyContainer;

/**
 * Encapsulates state pertaining to the initialization of the Corus server and its
 * components.
 * 
 * @author yduchesne
 *
 */
public class InitContext {

  private static ThreadLocal<InitContext> currentContext = new ThreadLocal<InitContext>();
  private PropertyContainer properties;
  
  /**
   * @return the {@link PropertyContainer} held by this instance.
   */
  public PropertyContainer getProperties() {
    return properties;
  }
  
  /**
   * Sets this instance's property container.
   * 
   * @param properties a {@link PropertyContainer}
   */
  public void setProperties(PropertyContainer properties) {
    this.properties = properties;
  }
  
  static void attach(PropertyContainer properties){
    if(currentContext.get() == null){
      InitContext ctx = new InitContext();
      ctx.setProperties(properties);
      currentContext.set(ctx);
    }
  }
  
  static void unattach(){
    currentContext.set(null);
  }
  
  public static InitContext get(){
    if(currentContext.get() == null){
      throw new IllegalStateException("Context not attached to thread");
    }
    return currentContext.get();
  }
  
}
