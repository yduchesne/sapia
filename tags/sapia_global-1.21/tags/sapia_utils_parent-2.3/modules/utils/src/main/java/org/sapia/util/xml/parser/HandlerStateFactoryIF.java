package org.sapia.util.xml.parser;


/**
 * This factory class creates <CODE>HandlerStateIF</CODE> instances according to
 * different criterias.
 *
 * @see HandlerStateIF
 * @see HandlerStateCreationException
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface HandlerStateFactoryIF {
  /**
   * Creates a new handler state for the namespace URI and the local element name passed in.
   *
   * @param aNamespaceURI The namespace URI associated with the element for which to create a handler.
   * @param aLocalName The element type local name.
   * @exception HandlerStateCreationException If an error occur while creating the handler state.
   */
  public HandlerStateIF createHandlerState(String aNamespaceURI,
    String aLocalName) throws HandlerStateCreationException;
}
