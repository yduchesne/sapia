package org.sapia.util.xml.confix;

import org.xml.sax.InputSource;

/**
 * Specifies the behavior of consumers of XML data.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface XMLConsumer {
	
	/**
	 * @param an <code>InputSource</code> of XML data.
	 */
	public void consume(InputSource is) throws Exception;

}
