package org.sapia.resource.include;

/**
 * Models a factory of {@link org.sapia.resource.include.IncludeContext}s. This class is meant
 * to be implemented by applications that need resource inclusion features.
 * 
 * @see org.sapia.resource.include.IncludeContext
 * 
 * @author yduchesne
 *
 */
public interface IncludeContextFactory {
  
  /**
   * @return a new <code>IncludeContext</code>.
   */
  public IncludeContext createInstance();

}
