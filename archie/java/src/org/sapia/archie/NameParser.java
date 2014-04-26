package org.sapia.archie;


/**
 * An instance of this interface creates <code>Name</code> instances from
 * string representations.
 * <p>
 * Understood name representations are implementation specific.
 * 
 * @author Yanick Duchesne
 */
public interface NameParser extends java.io.Serializable{
  
  /**
   * @param name a name literal.
   * @return the {@link Name} corresponding to the given literal.
   * @throws ProcessingException if the given literal could not be processed.
   */
  public Name parse(String name) throws ProcessingException;
  
  /**
   * @param name a {@link Name}.
   * @return the literal for the given {@link Name}.
   */
  public String asString(Name name);
  
  /**
   * @param namePart a {@link NamePart} literal.
   * @return the {@link NamePart} corresponding to the given literal.
   * @throws ProcessingException if the given literal could not be processed.
   */
  public NamePart parseNamePart(String namePart) throws ProcessingException;
}
