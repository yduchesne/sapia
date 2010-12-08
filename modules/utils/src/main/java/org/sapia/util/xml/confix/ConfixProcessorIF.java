package org.sapia.util.xml.confix;


// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.xml.ProcessingException;

// Import of Sun's JDK classes
// ---------------------------
import java.io.InputStream;


/**
 * Specifies the common "public" behavior of all XML processor - that
 * transform a XML configuration in to an object graph.
 *
 * @author JC Desrochers.
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface ConfixProcessorIF {
  /**
   * This method takes an XML stream as input and returns an object
   * representation of the passed-in XML.
   *
   * @param is an XML stream
   * @return an object representation of the XML stream.
   * @throws ProcessingException
   */
  public Object process(InputStream is) throws ProcessingException;
}
