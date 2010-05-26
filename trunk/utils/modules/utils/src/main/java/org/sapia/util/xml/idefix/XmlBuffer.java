package org.sapia.util.xml.idefix;


// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.xml.Attribute;
import org.sapia.util.xml.CData;
import org.sapia.util.xml.Namespace;

// Import of Sun's JDK classes
// ---------------------------
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


/**
 *
 *
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class XmlBuffer {
  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  CLASS ATTRIBUTES  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** Defines the default character encoding. */
  public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";

  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The XML scribe instance to generate the XML strings. */
  private XmlScribe _theScribe;

  /** The internal string buffer of this xml buffer. */
  private StringBuffer _theBuffer;

  /** Indicates if the buffer contains an xml declaration or not. */
  private boolean _useXmlDeclaration;

  /** The encoding of this xml buffer. */
  private String _theCharacterEncoding;

  /** The map of namespace definition by URI .*/
  private Map _theNamespaceByURI;

  /** The list of buffer states. */
  private LinkedList _theStates;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new XmlBuffer
   */
  public XmlBuffer() {
    this(false);
  }

  /**
   * Creates a new XmlBuffer
   */
  public XmlBuffer(boolean insertXmlDeclaration) {
    this(DEFAULT_CHARACTER_ENCODING);
    _useXmlDeclaration = insertXmlDeclaration;
  }

  /**
   * Creates a new XmlBuffer.
   */
  public XmlBuffer(String aCharacterEncoding) {
    _theScribe              = new XmlScribe(aCharacterEncoding);
    _theBuffer              = new StringBuffer();
    _useXmlDeclaration      = true;
    _theCharacterEncoding   = aCharacterEncoding;
    _theNamespaceByURI      = new HashMap();
    _theStates              = new LinkedList();
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  ACCESSOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the namespace prefix of the URI passed in.
   *
   * @param aNamespaceURI The URI of the namespace to look for.
   * @return The associated prefix or null if the namespace URI is not found.
   */
  public String getNamespacePrefix(String aNamespaceURI) {
    LinkedList someNamespaces = (LinkedList) _theNamespaceByURI.get(aNamespaceURI);

    if (someNamespaces == null) {
      return null;
    } else {
      NamespaceReference aNamespaceRef = (NamespaceReference) someNamespaces.getFirst();

      return aNamespaceRef.getNamespace().getPrefix();
    }
  }

  /**
   * Returns true if this xml buffer is empty (no element has been started
   * on this xml buffer).
   *
   * @return True if this xml buffer is empty.
   */
  public boolean isEmpty() {
    return ((_theBuffer.length() == 0) && _theStates.isEmpty());
  }
  
  /**
   * Returns the value of the indenting output indicator of this xml buffer.
   * 
   * @return True if this buffer indents xml output, false otherwise.
   */
  public boolean isIndentingOuput() {
    return _theScribe.isIndentingOuput();
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  MUTATOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Changes the value of the indenting output indicator of this xml buffer.
   * 
   * @param aValue The new value of the indicator.
   */
  public void setIndentingOuput(boolean aValue) {
    _theScribe.setIndentingOutput(aValue);
  }
  
  /**
   * Adds the namespace definition passed in to this xml buffer. The namespace
   * will be use for all the future elements created by this xml buffer. To define the
   * default namespace this method accepts either <CODE>null</CODE> or an empty
   * string as the namespace prefix.
   *
   * @param aNamespaceURI The URI of the namespace to add.
   * @param aNamespacePrefix The prefix associated to the namespace.
   * @return This xml buffer instance.
   * @exception IllegalArgumentException If the namespace URI passed in is null.
   */
  public XmlBuffer addNamespace(String aNamespaceURI, String aNamespacePrefix) {
    // Validate arguments
    if (aNamespaceURI == null) {
      throw new IllegalArgumentException("The namespace URI passed in null");
    }

    // Get the list of namespaces for the URI
    LinkedList someNamespaces = (LinkedList) _theNamespaceByURI.get(aNamespaceURI);

    if (someNamespaces == null) {
      someNamespaces = new LinkedList();
      _theNamespaceByURI.put(aNamespaceURI, someNamespaces);
    }

    // Convert for default prefix if necessary
    if (aNamespacePrefix == null) {
      aNamespacePrefix = "";
    }

    Namespace aNamespace = new Namespace(aNamespaceURI, aNamespacePrefix);

    // If the list of namespaces is empty add the current namespace    
    if (someNamespaces.isEmpty()) {
      someNamespaces.addFirst(new NamespaceReference(aNamespace));
    } else {
      NamespaceReference aNamespaceRef = (NamespaceReference) someNamespaces.getFirst();

      // If the current state is the same namespace, increment the reference counter
      if (aNamespace.equals(aNamespaceRef.getNamespace())) {
        aNamespaceRef.addReference();
      } else {
        someNamespaces.addFirst(new NamespaceReference(aNamespace));
      }
    }

    return this;
  }

  /**
   * Removes the namespace definition from this xml buffer.
   *
   * @param aNamespaceURI The URI of the namespace to remove.
   * @return This xml buffer instance.
   * @exception IllegalArgumentException If the namespace URI passed in is null or
   *            if the namespace URI is not found.
   */
  public XmlBuffer removeNamespace(String aNamespaceURI) {
    // Validate the argument
    if (aNamespaceURI == null) {
      throw new IllegalArgumentException("The namespace URI passed in null");
    }

    // Retrieve the list of namespaces for the URI
    LinkedList someNamespaces = (LinkedList) _theNamespaceByURI.get(aNamespaceURI);

    if (someNamespaces == null) {
      throw new IllegalStateException("No namespace found for the URI " +
        aNamespaceURI);
    }

    // If there is one reference left remove the namespace, otherwise decrement the ref count
    NamespaceReference aNamespaceRef = (NamespaceReference) someNamespaces.getFirst();

    if (aNamespaceRef.getReferenceCount() == 1) {
      someNamespaces.removeFirst();
    } else {
      aNamespaceRef.removeReference();
    }

    return this;
  }

  /**
   * Start an element in the XML buffer. The element will be create for the default namespace.
   *
   * @anElementName The name of the element to start.
   * @return This xml buffer instance.
   * @exception IllegalArgumentException If the element name passed in is null.
   */
  public XmlBuffer startElement(String anElementName) {
    return startElement(null, anElementName);
  }

  /**
   * Start an element in the XML buffer using the namespace URI passed in.
   *
   * @param aNamespaceURI The namespace URI in which belong the element.
   * @param anElementName The name of the element to create.
   * @return This xml buffer instance.
   * @exception IllegalArgumentException If the element name passed in is null.
   */
  public XmlBuffer startElement(String aNamespaceURI, String anElementName) {
    if (anElementName == null) {
      throw new IllegalArgumentException("The element name passed in is null");
    }

    BufferState aParentState = null;

    if (!_theStates.isEmpty()) {
      aParentState = (BufferState) _theStates.getFirst();
    }

    validateStartingXmlGeneration(aParentState);

    String      aPrefix    = getNamespacePrefix(aNamespaceURI);
    Namespace   aNamespace = new Namespace(aNamespaceURI, aPrefix);
    BufferState aState     = new BufferState(aParentState, aNamespace,
        anElementName);
    aState.addDeclaredNamespace(aNamespace);
    _theStates.addFirst(aState);

    return this;
  }

  /**
   * Adds the string passed in as content of the current XML element.
   *
   * @param aContent The content to add.
   * @return This xml buffer instance.
   * @exception IllegalStateException If there is no element started.
   */
  public XmlBuffer addContent(String aContent) {
    if (_theStates.isEmpty()) {
      throw new IllegalStateException("Could not add content [" + aContent +
        "] there is no element started");
    }

    BufferState aState = (BufferState) _theStates.getFirst();
    validateStartingXmlGeneration(aState);
    _theScribe.xmlEncode(aContent, aState.getContent());

    return this;
  }

  /**
   * Adds the string passed in as CData of the current XML element.
   *
   * @param aContent The content to add as CData.
   * @return This xml buffer instance.
   * @exception IllegalStateException If there is no element started.
   */
  public XmlBuffer addContent(CData aCData) {
    if (_theStates.isEmpty()) {
      throw new IllegalStateException("Could not add CData [" + aCData +
        "] there is no element started");
    }

    BufferState aState = (BufferState) _theStates.getFirst();
    validateStartingXmlGeneration(aState);
    _theScribe.composeCData(aCData.toString(), aState.getContent());

    return this;
  }

  /**
   * Ends the element name passed in. The element to close will be in the default namespace.
   *
   * @param anElementName The name of the element to end.
   * @return This xml buffer instance.
   * @exception IllegalArgumentException If the element name passed in is null or if it does not
   *            match the current element name and namespace.
   * @exception IllegalStateException If there is no element started.
   */
  public XmlBuffer endElement(String anElementName) {
    return endElement(null, anElementName);
  }

  /**
   * Ends the element name passed in the provided namespace.
   *
   * @param aNamespaceURI The namespace of the element to close.
   * @param anElementName The name of the element to end.
   * @return This xml buffer instance.
   * @exception IllegalArgumentException If the element name passed in is null or if it does not
   *            match the current element name and namespace.
   * @exception IllegalStateException If there is no element started.
   */
  public XmlBuffer endElement(String aNamespaceURI, String anElementName) {
    if (anElementName == null) {
      throw new IllegalArgumentException("The element name passed in is null");
    } else if (_theStates.isEmpty()) {
      throw new IllegalStateException("Could not end the element [" +
        anElementName + "] on an empty xml buffer");
    }

    BufferState aState = (BufferState) _theStates.removeFirst();

    if (!anElementName.equals(aState.getElementName())) {
      throw new IllegalArgumentException("The element name to end [" +
        anElementName + "] does not match the starting tag [" +
        aState.getElementName() + "]");
    } else if (((aNamespaceURI != null) &&
          !aNamespaceURI.equals(aState.getElementNamespace().getURI())) ||
          ((aNamespaceURI == null) &&
          (aState.getElementNamespace().getURI() != null))) {
      throw new IllegalArgumentException("The namespace URI to end [" +
        aNamespaceURI + "] does not match the starting namespace URI [" +
        aState.getElementNamespace().getURI() + "]");
    }

    if (_theStates.isEmpty()) {
      generateCompleteXmlFor(aState, _theBuffer);
    } else {
      BufferState aParentState = (BufferState) _theStates.getFirst();
      generateCompleteXmlFor(aState, aParentState.getNestedXmlString());
    }

    return this;
  }

  /**
   * Adds the attribute passed in to the current XML element.
   *
   * @param aName The name of the attribute to add.
   * @param aValue The value of the attribute to add.
   * @return This xml buffer instance.
   * @exception IllegalStateException If there is no current element or if the method
   *            <CODE>endAttribute()</CODE> was previously called for the current element.
   */
  public XmlBuffer addAttribute(String aName, String aValue) {
    return addAttribute(null, aName, aValue);
  }

  /**
   * Adds the attribute passed in to the current XML element for the passed in namespace.
   *
   * @param aNamespaceURI The namespace of the attribute to add.
   * @param aName The name of the attribute to add.
   * @param aValue The value of the attribute to add.
   * @return This xml buffer instance.
   * @exception IllegalStateException If there is no current element or if the method
   *            <CODE>endAttribute()</CODE> was previously called for the current element.
   */
  public XmlBuffer addAttribute(String aNamespaceURI, String aName,
    String aValue) {
    if (_theStates.isEmpty()) {
      throw new IllegalStateException("Could not add the attribute [" + aName +
        "] on an empty xml buffer");
    }

    BufferState aState = (BufferState) _theStates.getFirst();

    if (!aState.isGettingMoreAttribute()) {
      throw new IllegalStateException("Could not add the attribute [" + aName +
        "] on element for which the endAttribute() methos was previously called");
    }

    String    aPrefix     = getNamespacePrefix(aNamespaceURI);
    Attribute anAttribute = new Attribute(aPrefix, aName, aValue);
    aState.addAttribute(anAttribute);

    Namespace aNamespace = new Namespace(aNamespaceURI, aPrefix);
    aState.addDeclaredNamespace(aNamespace);

    return this;
  }

  /**
   * This method tells the xml buffer that the current element will not get any further
   * attribute. It is a hint to let the xml buffer reduce it's footprint.
   *
   * @return This xml buffer instance.
   * @exception IllegalStateException If there is no current element.
   */
  public XmlBuffer endAttribute() {
    // hint that would tell the xml buffer that no more attributes will be added.
    // upon this call it should generate the starting element of the current buffer state
    if (_theStates.isEmpty()) {
      throw new IllegalStateException("There is no current element");
    }

    BufferState aState = (BufferState) _theStates.getFirst();
    aState.setIsGettingMoreAttribute(false);

    return this;
  }

  /**
   * Generates an XML string using the buffer state passed in.
   *
   * @param aState The buffer state that contains the info about the XML to generate.
   * @param aBuffer The buffer into which to add the generated XML string.
   */
  private void generateCompleteXmlFor(BufferState aState, StringBuffer aBuffer) {
    if (!aState.isStartElementGenerated()) {
      generateStartingXmlFor(aState, aBuffer, true);

      if (!aState.isElementEmpty()) {
        if (!aState.isNestedXMLEmpty()) {
          aBuffer.append(aState.getNestedXmlString().toString());
        }

        if (!aState.isContentEmpty()) {
          aBuffer.append(aState.getContent().toString());
        }

        if (aState.isNestedXMLEmpty() && !aState.isContentEmpty() && _theScribe.isIndentingOuput()) {
          _theScribe.setIndentingOutput(false);
          _theScribe.composeEndingElement(aState.getElementNamespace().getPrefix(), aState.getElementName(), aBuffer, aState.getNestedLevel());
          _theScribe.setIndentingOutput(true);
        } else {
          _theScribe.composeEndingElement(aState.getElementNamespace().getPrefix(), aState.getElementName(), aBuffer, aState.getNestedLevel());
        }
      }
    } else if (!aState.isElementEmpty()) {
      if (!aState.isContentEmpty()) {
        aBuffer.append(aState.getContent().toString());
      }

      _theScribe.composeEndingElement(aState.getElementNamespace().getPrefix(),
        aState.getElementName(), aBuffer, aState.getNestedLevel());
    }
  }

  /**
   * Validates if it is necessary to generate a start element for the state passed in. If the
   * conditions are meet, this method will call the <CODE>generateStartingXmlFor()</CODE> method
   * to generate the starting element.
   *
   * @param aState The buffer state to validate.
   */
  private void validateStartingXmlGeneration(BufferState aState) {
    if ((aState != null) && !aState.isGettingMoreAttribute() &&
          !aState.isStartElementGenerated()) {
      if (aState.getParent() == null) {
        generateStartingXmlFor(aState, _theBuffer, false);
        aState.setNestedXmlString(_theBuffer);
      } else {
        generateStartingXmlFor(aState, aState.getParent().getNestedXmlString(),
          false);
        aState.setNestedXmlString(aState.getParent().getNestedXmlString());
      }
    }
  }

  /**
   * Generates an XML string using the buffer state passed in.
   *
   * @param aState The buffer state that contains the info about the XML to generate.
   * @param aBuffer The buffer into which to add the generated XML string.
   */
  private void generateStartingXmlFor(BufferState aState, StringBuffer aBuffer,
    boolean isClosingElement) {
    int anIndex = 0;

    for (Iterator it = aState.getDeclaredNamespaces().iterator(); it.hasNext();
          anIndex++) {
      Namespace aNamespace = (Namespace) it.next();

      if ((aNamespace.getPrefix() == null) ||
            (aNamespace.getPrefix().length() == 0)) {
        Attribute anAttribute = new Attribute("xmlns", aNamespace.getURI());
        aState.addAttribute(anIndex, anAttribute);
      } else {
        Attribute anAttribute = new Attribute("xmlns", aNamespace.getPrefix(),
            aNamespace.getURI());
        aState.addAttribute(anIndex, anAttribute);
      }
    }

    if (isClosingElement && aState.isElementEmpty()) {
      _theScribe.composeStartingElement(aState.getElementNamespace().getPrefix(),
        aState.getElementName(), aState.getAttributes(), true, aBuffer, aState.getNestedLevel());
    } else {
      _theScribe.composeStartingElement(aState.getElementNamespace().getPrefix(),
        aState.getElementName(), aState.getAttributes(), false, aBuffer, aState.getNestedLevel());
    }

    aState.setIsStartElementGenerated(true);
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  OVERRIDEN METHODS  //////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the string of this xml buffer.
   *
   * @return The string of this xml buffer.
   */
  public String toString() {
    if (_useXmlDeclaration) {
      StringBuffer aBuffer = new StringBuffer();
      _theScribe.composeXmlDeclaration(_theCharacterEncoding, aBuffer);
      aBuffer.append(_theBuffer.toString());

      return aBuffer.toString();
    } else {
      return _theBuffer.toString();
    }
  }

  public static class NamespaceReference {
    private Namespace _theNamespace;
    private int       _theReferenceCount;

    public NamespaceReference(Namespace aNamespace) {
      _theNamespace        = aNamespace;
      _theReferenceCount   = 1;
    }

    public int getReferenceCount() {
      return _theReferenceCount;
    }

    public Namespace getNamespace() {
      return _theNamespace;
    }

    public void addReference() {
      _theReferenceCount++;
    }

    public void removeReference() {
      _theReferenceCount--;
    }
  }
}
