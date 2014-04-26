package org.sapia.archie;


/**
 * Models a part in a {@link Name}.
 * 
 * @see org.sapia.archie.Name
 * @see org.sapia.archie.NameParser
 * 
 * @author Yanick Duchesne
 */
public interface NamePart extends java.io.Serializable {
  
  /**
   * @return this instance as a string that can be parsed by the appropriate
   * {@link NameParser}.
   * 
   * @see NameParser
   */
  public String asString();
}
