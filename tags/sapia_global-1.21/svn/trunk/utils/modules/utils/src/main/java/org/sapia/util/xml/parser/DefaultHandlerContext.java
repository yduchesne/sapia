package org.sapia.util.xml.parser;


// Imports of David Meggison's SAX classes
// ---------------------------------------
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

// Import of Sun's JDK classes
// ---------------------------
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


// Import of Apache's Log4j classes
// --------------------------------
//import org.apache.log4j.Logger;

/**
 * The <CODE>DefaultHandlerContext</CODE> class is the default implementation of the
 * <CODE>HandlerContextIF</CODE> interface.
 *
 * @see HandlerStateIF
 * @see StatefullSAXHandler
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DefaultHandlerContext implements HandlerContextIF {
  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  CLASS ATTRIBUTES  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** Defines the logger of this class. */

  //  private static final Logger _theLogger = Logger.getLogger(DefaultHandlerContext.class);
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The current handler state. */
  private HandlerStateIF _theCurrentState;

  /** The list (LIFO) of handler states. */
  private LinkedList _theHandlerStates;

  /** The map of prefix to namespace. */
  private Map _theNamespaceMapping;

  /** The map of handler state to their object result. */
  private Map _theResults;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new DefaultHandlerContext instance with the argument passed in.
   *
   * @param anInitialState The initial handler state of this context.
   * @exception IllegalArgumentException If the initial handler state is null.
   */
  public DefaultHandlerContext(HandlerStateIF anInitialState) {
    if (anInitialState == null) {
      throw new IllegalArgumentException(
        "The initial handler state passes in is null.");
    }

    _theCurrentState       = anInitialState;
    _theHandlerStates      = new LinkedList();
    _theNamespaceMapping   = new HashMap();
    _theResults            = new HashMap();
    _theHandlerStates.addFirst(anInitialState);
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////  INTERACE IMPLEMENTATION  ///////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the current handler state of this context.
   *
   * @return The current handler state of this context.
   */
  public synchronized HandlerStateIF getCurrentState() {
    return _theCurrentState;
  }

  /**
   * Changes the current handler state to the handler state passed in. The collection
   * of state objects needs to behave like a stack (LIFO - Last in, first out) in
   * order to go back to the previous state of a handler.<P>
   *
   * The handler context will notify the child handler state of the start of the element
   * by calling it's <CODE>startElement()</CODE> method passing the arguments received.
   *
   * @param aHandlerState The new current handler state.
   * @param anUri The namespace URI of the element that starts.
   * @param aLocalName The local name of the element that starts.
   * @param aQualifiedName The qualified name of the element that starts.
   * @param someAttributes The attributes of the element that starts.
   * @exception SAXException If an error occurs while setting the new handler state.
   */
  public synchronized void setCurrentState(HandlerStateIF aHandlerState,
    String anUri, String aLocalName, String aQualifiedName,
    Attributes someAttributes) throws SAXException {
    _theCurrentState = aHandlerState;
    _theHandlerStates.addFirst(aHandlerState);

    try {
      // Notifying the child handler state of the start of the element
      _theCurrentState.startElement(this, anUri, aLocalName, aQualifiedName,
        someAttributes);
    } catch (SAXException se) {
      String aMessage =
        "Error when notifying the child handler state of the start of the element " +
        aLocalName;

      /* _theLogger.error(aMessage, se);
       if (se.getException() != null) {
         _theLogger.error("--> SAX NESTED EXCEPTION IS: ", se.getException());
       }*/
      throw new SAXException(aMessage, se);
    }
  }

  /**
   * Removes the current handler state from this handler context. The previous handler
   * state becomes the current state.<P>
   *
   * The handler context will notify the parent handler state of the end of the
   * element by calling it's <CODE>endElement()</CODE> method passing the arguments
   * received.
   *
   * @param anURI The namespace URI of the element that closes.
   * @param aLocalName The local name of the element that closes.
   * @param aQualifiedName The qualified name of the element that closes.
   * @exception SAXException If an error occurs while removing the new handler state.
   */
  public synchronized void removeCurrentState(String anUri, String aLocalName,
    String aQualifiedName) throws SAXException {
    // Validating the state of this context
    if (_theHandlerStates.size() == 1) {
      throw new IllegalStateException(
        "Could not remove the root handler state.");
    }

    // Removing the current handler state
    _theHandlerStates.removeFirst();

    // Setting the previous handler state to be the current
    _theCurrentState = (HandlerStateIF) _theHandlerStates.getFirst();

    try {
      // Notifying the parent handler state of the end of the element
      _theCurrentState.endElement(this, anUri, aLocalName, aQualifiedName);
    } catch (SAXException se) {
      String aMessage =
        "Error when notifying the parent handler state of the end of the element " +
        aLocalName;

      /*_theLogger.error(aMessage, se);
      if (se.getException() != null) {
        _theLogger.error("--> SAX NESTED EXCEPTION IS: ", se.getException());
      }*/
      throw new SAXException(aMessage, se);
    }
  }

  /**
   * Sets the namespace URI passed in as the current mapping for the given prefix.
   *
   * @param aPrefix The Namespace prefix being declared.
   * @param anUri The Namespace URI mapped to the prefix.
   */
  public void startPrefixMapping(String aPrefix, String anUri) {
    // Get the list of URI for the prefix
    LinkedList aNamespaceList = (LinkedList) _theNamespaceMapping.get(aPrefix);

    // If not in the map, create a new list
    if (aNamespaceList == null) {
      aNamespaceList = new LinkedList();

      // Adding the list of namespace to the map of prefix
      _theNamespaceMapping.put(aPrefix, aNamespaceList);
    }

    // Add the uri in the list of mapping at the top of the list.
    aNamespaceList.addFirst(anUri);
  }

  /**
   * Removes the current mapping of the prefix passed in.
   *
   * @param aPrefix The Namespace prefix being declared.
   */
  public void endPrefixMapping(String aPrefix) {
    // Get the list of URI for the prefix
    LinkedList aNamespaceList = (LinkedList) _theNamespaceMapping.get(aPrefix);

    if (aNamespaceList != null) {
      aNamespaceList.removeFirst();
    } else {
      throw new IllegalStateException(
        "The prefix mapping is not found in the map.");
    }
  }

  /**
   * Returns the current namespace URI for the passed in prefix.
   *
   * @return The current namespace URI or <CODE>null</CODE> if not mapping exists.
   */
  public String getNamespaceURIFor(String aPrefix) {
    String aNamespace = null;

    // Get the namespace list for the prefix
    LinkedList aNamespaceList = (LinkedList) _theNamespaceMapping.get(aPrefix);

    // Get the current namespace URI, if it exists
    if ((aNamespaceList != null) && !aNamespaceList.isEmpty()) {
      aNamespace = (String) aNamespaceList.getFirst();
    }

    return aNamespace;
  }

  /**
   * associates the result object to the handler state passed in.
   *
   * @param aHandlerState The handler state for which a result is provided.
   * @param aResult The result object of the handler state.
   */
  public void setResultFor(HandlerStateIF aHandlerState, Object aResult) {
    _theResults.put(aHandlerState, aResult);
  }

  /**
   * Returns the result for the handler state passed in.
   *
   * @param aHandlerState The handler state for which to search for a result.
   * @return The result object found, or <CODE>null</CODE> if no handler state was foud
   *         or if no result was associated for the handler state passed in.
   */
  public Object getResultFor(HandlerStateIF aHandlerState) {
    return _theResults.get(aHandlerState);
  }
}
