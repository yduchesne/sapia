package org.sapia.util.xml.confix.test;

import org.jdom.input.SAXBuilder;
import org.sapia.util.xml.confix.XMLConsumer;
import org.xml.sax.InputSource;


/**
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class XMLConsumerConfig extends NestedConfig implements XMLConsumer {

  public XMLConsumerConfig() {
  }
  
	/**
   * @see org.sapia.util.xml.confix.XMLConsumer#consume(org.xml.sax.InputSource)
   */
  public void consume(InputSource is) throws Exception {
		SAXBuilder builder = new SAXBuilder();
    _theConfigElement = builder.build(is).getRootElement();
  }

}
