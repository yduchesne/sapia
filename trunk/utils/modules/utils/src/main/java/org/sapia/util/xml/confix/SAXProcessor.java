package org.sapia.util.xml.confix;


// Import of Sapia's Utility classes
// ---------------------------------
import org.sapia.util.xml.ProcessingException;
import org.sapia.util.xml.parser.DefaultHandlerContext;
import org.sapia.util.xml.parser.DelegateHandlerContext;
import org.sapia.util.xml.parser.HandlerContextIF;
import org.sapia.util.xml.parser.StatefullSAXHandler;

// Imports of David Meggison's SAX classes
// ---------------------------------------
import org.xml.sax.SAXException;

import java.io.IOException;

// Import of Sun's JDK classes
// ---------------------------
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;


/**
 * An instance of this class receives SAX events and delegates object creation
 * to an <code>ObjectFactoryIF</code>.
 *
 * @author JC Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SAXProcessor extends AbstractXMLProcessor {
  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  CLASS ATTRIBUTES  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** Initialization of the SAX parser factory instance. */
  private static SAXParserFactory _theSAXParserFactory;

  static {
    _theSAXParserFactory = SAXParserFactory.newInstance();
    _theSAXParserFactory.setNamespaceAware(true);
    _theSAXParserFactory.setValidating(false);
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new SAXProcessor instance with the attribute passed in.
   *
   * @param anObjectFactory The object factory of this processor.
   */
  public SAXProcessor(ObjectFactoryIF anObjectFactory) {
    super(anObjectFactory);
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  OVERRIDEN METHODS  //////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * This method takes an XML stream as input and returns an object
   * representation of the passed-in XML.
   *
   * @param is an XML stream
   * @return an object representation of the XML stream.
   * @exception ProcessingException
   */
  public Object process(InputStream is) throws ProcessingException {
    StatefullSAXHandler aSAXHandler = null;
    try {
      // Initialize the SAX handler
      ConfixHandlerState  aHandlerState   = new ConfixHandlerState(this);
      HandlerContextIF    aHandlerContext = new DefaultHandlerContext(new DelegateHandlerContext(
            aHandlerState));
      aSAXHandler = new StatefullSAXHandler(aHandlerContext);

      // Parse the input stream and get the result
      _theSAXParserFactory.newSAXParser().parse(is, aSAXHandler);

      Object aResult = aHandlerState.getResult();

      return aResult;
    } catch (ParserConfigurationException pce) {
      String aMessage = "Error getting the SAX parser to process the input stream";
      if (aSAXHandler != null && aSAXHandler.getSaxException() != null) {
        throw new ProcessingException(aMessage, aSAXHandler.getSaxException().getException());
      } else {
        throw new ProcessingException(aMessage, pce);
      }
      
    } catch (IOException ioe) {
      String aMessage = "Error reading input stream to process";
      if (aSAXHandler != null && aSAXHandler.getSaxException() != null) {
        throw new ProcessingException(aMessage, aSAXHandler.getSaxException().getException());
      } else {
        throw new ProcessingException(aMessage, ioe);
      }

    } catch (SAXException se) {
      String aMessage = "Error parsing the XML of the input stream.";
      if (aSAXHandler != null && aSAXHandler.getSaxException() != null) {
        throw new ProcessingException(aMessage, aSAXHandler.getSaxException().getException());
      } else {
        throw new ProcessingException(aMessage, se.getException());
      }

    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException ioe) {
        String aMessage = "Error closing the input stream to process.";
        throw new ProcessingException(aMessage, ioe);
      }
    }
  }
}
