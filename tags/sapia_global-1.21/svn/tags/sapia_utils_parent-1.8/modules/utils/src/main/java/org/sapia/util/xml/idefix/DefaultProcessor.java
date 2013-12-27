package org.sapia.util.xml.idefix;


//import org.apache.log4j.Logger;
import org.sapia.util.xml.ProcessingException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


/**
 *
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DefaultProcessor implements IdefixProcessorIF {
  /** Define the logger instance of this class. */

  /*  private static final Logger _theLogger =
            Logger.getLogger(DefaultProcessor.class);*/

  /** The serializer factory used by this processor. */
  private SerializerFactoryIF _theSerializerFactory;

  /** The namespace factory used by this processor. */
  private NamespaceFactoryIF _theNamespaceFactory;
  
  /** The encoding style of this processor. */
  private String _theEncodingStyle;
  
  /** The indentation indicator of this processor. */
  private boolean _isIndentingOutput;

  /**
   * Creates a new DefaultProcessor instance with the argument passed in.
   *
   * @param aSerializerFactory The serializer factory of this new processor.
   */
  public DefaultProcessor(SerializerFactoryIF aSerializerFactory, NamespaceFactoryIF aNamespaceFactory) {
    _theSerializerFactory = aSerializerFactory;
    _theNamespaceFactory = aNamespaceFactory;
    _theEncodingStyle = ENCODING_INLINE;
    _isIndentingOutput = false;
  }

  /**
   * Sets the encodnig style of the idefix processor.
   * 
   * @param anEncoding The encoding style for the processor.
   * @see #ENCODING_INLINE
   * @see #ENCODING_SOAP
   */
  public void setEncodingStyle(String anEncoding) {
    if (!ENCODING_INLINE.equals(anEncoding) && !ENCODING_SOAP.equals(anEncoding)) {
      throw new IllegalArgumentException("The encoding style passed in is invalid: " + anEncoding);
    }
    
    _theEncodingStyle = anEncoding;
  }

  /**
   * Set the default indenting output mode of this processor.
   * 
   * @param aValue The new value of the indenting indicator.
   */
  public void setIndentingOutput(boolean aValue) {
    _isIndentingOutput = aValue;
  }

  /**
   * This method takes an object and walk though its hierarchy to generate an XML
   * representation of it. The result XML will be returned as a <CODE>String</CODE>
   * object.
   *
   * @param anObject The object to process.
   * @return The string of the XML representation.
   * @exception ProcessingException If an error occurs while processing the object.
   */
  public String process(Object anObject) throws ProcessingException {
    if (anObject == null) {
      throw new IllegalArgumentException("The object passed in is null");
    }

    try {
      XmlBuffer anXmlBuffer = new XmlBuffer("UTF-8");
      anXmlBuffer.setIndentingOuput(_isIndentingOutput);
      SerializationContext aContext = new SerializationContext(
          _theSerializerFactory, _theNamespaceFactory, anXmlBuffer, _theEncodingStyle);

      SerializerIF aSerializer = _theSerializerFactory.getSerializer(anObject.getClass());
      aSerializer.serialize(anObject, aContext);

      return aContext.getXmlBuffer().toString();
    } catch (SerializerNotFoundException sne) {
      String aMessage = "Could not process the object: " + anObject;

      //      _theLogger.error(aMessage, sne);
      throw new ProcessingException(aMessage, sne);
    } catch (SerializationException se) {
      String aMessage = "Could not serialize the object: " + anObject;

      //    _theLogger.error(aMessage, se);
      throw new ProcessingException(aMessage, se);
    } catch (RuntimeException re) {
      String aMessage = "System error processing the object: " + anObject;

      //   _theLogger.error(aMessage, re);
      throw new ProcessingException(aMessage, re);
    }
  }

  /**
   * This method takes an object and walk though its hierarchy to generate an XML
   * representation of it. The result XML will be streamed into the output stream
   * passed in.
   *
   * @param anObject The object to process.
   * @param anOutput The output stream in which to add the XML.
   * @exception IOException If an error occurs writing to the output stream.
   * @exception ProcessingException If an error occurs while processing the object.
   */
  public void process(Object anObject, OutputStream anOutput)
    throws IOException, ProcessingException {
    if (anObject == null) {
      throw new IllegalArgumentException("The object passed in is null");
    }

    try {
      XmlBuffer anXmlBuffer = new XmlBuffer("UTF-8");
      anXmlBuffer.setIndentingOuput(_isIndentingOutput);
      SerializationContext aContext = new SerializationContext(
          _theSerializerFactory, _theNamespaceFactory, anXmlBuffer, _theEncodingStyle);

      SerializerIF aSerializer = _theSerializerFactory.getSerializer(anObject.getClass());
      aSerializer.serialize(anObject, aContext);

      OutputStreamWriter aWriter = new OutputStreamWriter(new BufferedOutputStream(
            anOutput), "UTF-8");
      aWriter.write(aContext.getXmlBuffer().toString());
      aWriter.flush();
    } catch (SerializerNotFoundException sne) {
      String aMessage = "Could not process the object: " + anObject;

      // _theLogger.error(aMessage, sne);
      throw new ProcessingException(aMessage, sne);
    } catch (SerializationException se) {
      String aMessage = "Could not serialize the object: " + anObject;

      // _theLogger.error(aMessage, se);
      throw new ProcessingException(aMessage, se);
    } catch (IOException ioe) {
      String aMessage = "Could not process the object: " + anObject;

      //_theLogger.error(aMessage, ioe);
      throw new ProcessingException(aMessage, ioe);
    } catch (RuntimeException re) {
      String aMessage = "System error processing the object: " + anObject;

      //_theLogger.error(aMessage, re);
      throw new ProcessingException(aMessage, re);
    }
  }
}
