package org.sapia.util.xml.idefix;


// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.xml.ProcessingException;

import java.io.IOException;

// Import of Sun's JDK classes
// ---------------------------
import java.io.OutputStream;


/**
 * Specifies the common "public" behavior of an Idefix processor - that
 * transform an object graph to a XML string.
 *
 * @see IdefixProcessorFactory
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface IdefixProcessorIF {
  
  /** Defines the INLINE encoding style. */
  public static final String ENCODING_INLINE = "inline";

  /** Defines the SOAP encoding style. */
  public static final String ENCODING_SOAP = "soap";

  /**
   * Sets the encodnig style of the idefix processor.
   * 
   * @param anEncoding The encoding style for the processor.
   * @see #ENCODING_INLINE
   * @see #ENCODING_SOAP
   */
  public void setEncodingStyle(String anEncoding);

  /**
   * Set the default indenting output mode of this processor.
   * 
   * @param aValue The new value of the indenting indicator.
   */
  public void setIndentingOutput(boolean aValue);
  
  /**
   * This method takes an object and walk though its hierarchy to generate an XML
   * representation of it. The result XML will be returned as a <CODE>String</CODE>
   * object.
   *
   * @param anObject The object to process.
   * @return The string of the XML representation.
   * @exception ProcessingException If an error occurs while processing the object.
   */
  public String process(Object anObject) throws ProcessingException;

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
    throws IOException, ProcessingException;
}
