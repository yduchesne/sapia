package org.sapia.util.xml;


// Import of Sun's JDK classes
// ---------------------------
import java.io.Serializable;


/**
 * The <CODE>Namespace</CODE> class is an object representation of a XML namespace
 * definition. It has two arguments: a URI and a prefix.
 *
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Namespace implements Serializable {
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The URI of this namespace. */
  private String _theURI;

  /** The prefix associated to this namespace. */
  private String _thePrefix;

  /** The hash code of this namespace. */
  private int _theHashCode;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new Namespace instance.
   */
  public Namespace() {
    generateHashCode();
  }

  /**
   * Creates a new Namespace instance with the passed in arguments.
   *
   * @param anURI The URI of this namespace.
   * @param aPrefix The prefix of this namespace.
   */
  public Namespace(String anURI, String aPrefix) {
    _theURI      = anURI;
    _thePrefix   = aPrefix;
    generateHashCode();
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  ACCESSOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the URI of this namespace.
   *
   * @return The URI of this namespace.
   */
  public String getURI() {
    return _theURI;
  }

  /**
   * Returns the prefix of this namespace.
   *
   * @return The prefix of this namespace.
   */
  public String getPrefix() {
    return _thePrefix;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  MUTATOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Changes the URI of this namespace.
   *
   * @param anURI The new URI of this namespace.
   */
  public void setURI(String anURI) {
    _theURI = anURI;
    generateHashCode();
  }

  /**
   * Changes the prefix of this namespace.
   *
   * @param aPrefix The new prefix of this namespace
   */
  public void setPrefix(String aPrefix) {
    _thePrefix = aPrefix;
    generateHashCode();
  }

  /**
   * Utility method that generates the hashcode value of this namespace.
   */
  private void generateHashCode() {
    StringBuffer aBuffer = new StringBuffer();
    aBuffer.append(_theURI).append(_thePrefix);
    _theHashCode = aBuffer.toString().hashCode();
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  OVERRIDEN METHODS  //////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the hash code of this namespace.
   *
   * @return The hash code of this namespace.
   */
  public int hashCode() {
    return _theHashCode;
  }

  /**
   * Returns true if the object passed in is of type <CODE>Namespace</CODE> and
   * has the same URI and prefix as this namespace instance.
   *
   * @param anObject The object to compare for equality.
   * @return True if the object is equals, false otherwise.
   */
  public boolean equals(Object anObject) {
    if (anObject == this) {
      return true;
    } else if (!(anObject instanceof Namespace)) {
      return false;
    } else {
      Namespace aNamespace = (Namespace) anObject;

      return ((((_theURI == null) && (aNamespace._theURI == null)) ||
      ((_theURI != null) && (aNamespace._theURI != null) &&
      _theURI.equals(aNamespace._theURI))) &&
      (((_thePrefix == null) && (aNamespace._thePrefix == null)) ||
      ((_thePrefix != null) && (aNamespace._thePrefix != null) &&
      _thePrefix.equals(aNamespace._thePrefix))));
    }
  }

  /**
   * Returns a string representation of this object.
   *
   * @return A string representation of this object.
   */
  public String toString() {
    StringBuffer aBuffer = new StringBuffer(super.toString());
    aBuffer.append("[uri=").append(_theURI).append(" prefix=").append(_thePrefix)
           .append("]");

    return aBuffer.toString();
  }
}
