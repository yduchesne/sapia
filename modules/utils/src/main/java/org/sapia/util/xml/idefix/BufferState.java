package org.sapia.util.xml.idefix;


// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.xml.Attribute;
import org.sapia.util.xml.Namespace;

// Import of Sun's JDK classes
// ---------------------------
import java.util.ArrayList;
import java.util.List;


/**
 *
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class BufferState {
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The parent buffer state. */
  private BufferState _theParent;

  /** The element namespace of this buffer state. */
  private Namespace _theElementNamespace;

  /** The element name of this buffer state. */
  private String _theElementName;

  /** The list of attributes of this buffer state. */
  private List _theAttributes;

  /** Indicates if this buffer state will get more attributes or not. */
  private boolean _isGettingMoreAttribute;

  /** The current content. */
  private StringBuffer _theContent;

  /** The list of declared namespace URIs of this buffer state. */
  private List _theDeclaredNamespaces;

  /** The XML string of the nested elements of this string buffer. */
  private StringBuffer _theNestedXmlString;

  /** Indicates if the start element of this buffer state was generated or not. */
  private boolean _isStartElementGenerated;
  
  /** The nested level of this buffer state. */
  private int _nestedLevel;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new BufferState instance with the passed in attributes.
   *
   * @param aNamespace The element namespace.
   * @param anElementName The element name.
   */
  public BufferState(BufferState aParent, Namespace aNamespace,
    String anElementName) {
    _theParent                 = aParent;
    _theElementNamespace       = aNamespace;
    _theElementName            = anElementName;
    _theAttributes             = new ArrayList();
    _isGettingMoreAttribute    = true;
    _theDeclaredNamespaces     = new ArrayList();
    _isStartElementGenerated   = false;
    if (aParent != null) {
      _nestedLevel = aParent.getNestedLevel() + 1;
    }
  }

  /**
   * Returns the parent state of this buffer state.
   *
   * @return The parent state of this buffer state.
   */
  public BufferState getParent() {
    return _theParent;
  }
  
  /**
   * Returns the nested level of this buffer state.
   * 
   * @return The nested level of this buffer state.
   */
  public int getNestedLevel() {
    return _nestedLevel;
  }

  /**
   * Returns the element namespace of this buffer state.
   *
   * @return The element namespace of this buffer state.
   */
  public Namespace getElementNamespace() {
    return _theElementNamespace;
  }

  /**
   * Returns the element name of this buffer state.
   *
   * @return The element name of this buffer state.
   */
  public String getElementName() {
    return _theElementName;
  }

  /**
   * Returns the list of attributes of this buffer state.
   *
   * @return A list of <CODE>Attribute</CODE> objects.
   * @see Attribute
   */
  public List getAttributes() {
    return _theAttributes;
  }

  /**
   * Returns <CODE>true</CODE> if this buffer state expect more attributes, <CODE>false</CODE> otherwise.
   *
   * @return <CODE>true</CODE> if this buffer state expect more attributes, <CODE>false</CODE> otherwise.
   */
  public boolean isGettingMoreAttribute() {
    return _isGettingMoreAttribute;
  }

  /**
   * Return <CODE>true</CODE> if the element of this buffer state is empty.
   *
   * @return True if the element of this buffer state is empty.
   */
  public boolean isElementEmpty() {
    return ((_theContent == null) || (_theContent.length() == 0)) &&
    ((_theNestedXmlString == null) || (_theNestedXmlString.length() == 0));
  }

  /**
   * Returns <CODE>true</CODE> if the buffer state does not contains nested XML.
   *
   * @return <CODE>true</CODE> if the buffer state does not contains nested XML.
   */
  public boolean isNestedXMLEmpty() {
    return (_theNestedXmlString == null) ||
    (_theNestedXmlString.length() == 0);
  }

  /**
   * Returns <CODE>true</CODE> if the buffer state does not have some XML content.
   *
   * @return <CODE>true</CODE> if the buffer state does not have some XML content.
   */
  public boolean isContentEmpty() {
    return (_theContent == null) || (_theContent.length() == 0);
  }

  /**
   * Returns the string buffer of the content of this buffer state.
   *
   * @return The string buffer of the content of this buffer state.
   */
  public StringBuffer getContent() {
    if (_theContent == null) {
      _theContent = new StringBuffer();
    }

    return _theContent;
  }

  /**
   * Returns the list of declared namespace of this buffer state.
   *
   * @return The list of <CODE>Namespace</CODE> objects of this buffer state.
   * @see Namespace
   */
  public List getDeclaredNamespaces() {
    return _theDeclaredNamespaces;
  }

  /**
   * Returns the XML string of the nested elements of this buffer state.
   *
   * @return The XML string of the nested elements of this buffer state.
   */
  public StringBuffer getNestedXmlString() {
    if (_theNestedXmlString == null) {
      _theNestedXmlString = new StringBuffer();
    }

    return _theNestedXmlString;
  }

  /**
   * Returns <CODE>true</CODE> if the start element was generated for this buffer state, <CODE>false</CODE> otherwise.
   *
   * @return <CODE>true</CODE> if the start element was generated for this buffer state, <CODE>false</CODE> otherwise.
   */
  public boolean isStartElementGenerated() {
    return _isStartElementGenerated;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  MUTATOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Adds the attribute passed in to this buffer state.
   *
   * @param anAttribute The attribute to add.
   */
  public void addAttribute(Attribute anAttribute) {
    _theAttributes.add(anAttribute);
  }

  /**
   * Adds the attribute passed in to this buffer state.
   *
   * @param anIndex The index at which to add the attribute.
   * @param anAttribute The attribute to add.
   */
  public void addAttribute(int anIndex, Attribute anAttribute) {
    _theAttributes.add(anIndex, anAttribute);
  }

  /**
   * Removes the attribute passed in from this buffer state.
   *
   * @param anAttribute The attribute to remove.
   */
  public void removeAttribute(Attribute anAttribute) {
    _theAttributes.remove(anAttribute);
  }

  /**
   * Removes all the attribute from this buffer state.
   */
  public void clearAttributes() {
    _theAttributes.clear();
  }

  /**
   * Tells the buffer state that it will get or it wont get any more attributes.
   *
   * @param isGettingMoreAttribute The new value for the indicator.
   */
  public void setIsGettingMoreAttribute(boolean isGettingMoreAttribute) {
    _isGettingMoreAttribute = isGettingMoreAttribute;
  }

  /**
   * Adds the declared namespace passed in to this buffer state.
   *
   * @param aNamespace The declared namespace to add.
   */
  public void addDeclaredNamespace(Namespace aNamespace) {
    if (!isNamespaceDeclared(aNamespace)) {
      _theDeclaredNamespaces.add(aNamespace);
    }
  }

  /**
   * Returns true if this buffer state has declared the namespace passed in.
   * Otherwise it delegates the call to the parent buffer state. If it has no parent,
   * it return false.
   *
   * @return True if the namespace passed in is delcared or not.
   */
  protected boolean isNamespaceDeclared(Namespace aNamespace) {
    if (_theDeclaredNamespaces.contains(aNamespace)) {
      return true;
    } else if (_theParent != null) {
      return _theParent.isNamespaceDeclared(aNamespace);
    } else {
      return false;
    }
  }

  /**
   * Removes the declared namespace passed in from this buffer state.
   *
   * @param aNamespace The declared namespace to remove.
   */
  public void removeDeclaredNamespace(Namespace aNamespace) {
    _theDeclaredNamespaces.remove(aNamespace);
  }

  /**
   * Removes all the declared namespace from this buffer state.
   */
  public void clearDelcaredNamespaces() {
    _theDeclaredNamespaces.clear();
  }

  /**
   * Changes the buffer that contains the XML string of the nested elements.
   *
   * @param aBuffer The new buffer for the nested XML string.
   * @exception IllegalStateException If the buffer for the nested XML was already set.
   */
  public void setNestedXmlString(StringBuffer aBuffer) {
    if ((_theNestedXmlString != null) && (aBuffer != _theNestedXmlString)) {
      throw new IllegalStateException(
        "Could not overwrite the buffer of the nested XML");
    }

    _theNestedXmlString = aBuffer;
  }

  /**
   * Indicates if the start element of this buffer state was generated or not.
   *
   * @param isStartElementGenerated The new value for the indicator.
   */
  public void setIsStartElementGenerated(boolean isStartElementGenerated) {
    _isStartElementGenerated = isStartElementGenerated;
  }
}
