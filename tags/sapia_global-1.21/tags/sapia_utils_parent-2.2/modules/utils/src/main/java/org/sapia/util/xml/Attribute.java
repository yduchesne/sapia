package org.sapia.util.xml;


// Import of Sun's JDK classes
// ---------------------------
import java.io.Serializable;


/**
 * The <CODE>Attribute</CODE> class is a simple object representation of an XML attribute.
 * It contains three attributes: a name, a value and a namespace prefix.
 *
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Attribute implements Serializable {
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The namespace prefix of this attribute. */
  private String _theNamespacePrefix;

  /** The name of this attribute. */
  private String _theName;

  /** The value of this attribute. */
  private String _theValue;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new Attribute instance.
   */
  public Attribute() {
  }

  /**
   * Creates a new Attribute instance.
   */
  public Attribute(String aName, String aValue) {
    _theName    = aName;
    _theValue   = aValue;
  }

  /**
   * Creates a new Attribute instance.
   */
  public Attribute(String aNamespacePrefix, String aName, String aValue) {
    _theNamespacePrefix   = aNamespacePrefix;
    _theName              = aName;
    _theValue             = aValue;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  ACCESSOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the namespace prefix of this attribute.
   *
   * @return The namespace prefix of this attribute.
   */
  public String getNamespacePrefix() {
    return _theNamespacePrefix;
  }

  /**
   * Returns the name of this attribute.
   *
   * @return The name of this attribute.
   */
  public String getName() {
    return _theName;
  }

  /**
   * Returns the value of this attribute.
   *
   * @return The value of this attribute.
   */
  public String getValue() {
    return _theValue;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  MUTATOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Changes the namespace prefix of this attribute.
   *
   * @param aNamespacePrefix The new namespace prefix.
   */
  public void setNamespacePrefix(String aNamespacePrefix) {
    _theNamespacePrefix = aNamespacePrefix;
  }

  /**
   * Changes the name of this attribute.
   *
   * @param aName The new name.
   */
  public void setName(String aName) {
    _theName = aName;
  }

  /**
   * Changes the value of this attribute.
   *
   * @param aValue The new value.
   */
  public void setValue(String aValue) {
    _theValue = aValue;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  OVERRIDEN METHODS  //////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns a string representation of this attribute.
   *
   * @return A string representation of this attribute.
   */
  public String toString() {
    StringBuffer aBuffer = new StringBuffer(super.toString());
    aBuffer.append("[namespacePrefix=").append(_theNamespacePrefix)
           .append(" name=").append(_theName).append(" value=").append(_theValue)
           .append("]");

    return aBuffer.toString();
  }
}
