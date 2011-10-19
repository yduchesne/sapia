package org.sapia.soto.me.model;

import org.sapia.soto.me.util.Log;
import org.sapia.soto.me.xml.J2meProcessingException;

/**
 * This class implements the <code>soto:unless</code> tag for the SotoMe container.
 *
 * @author Jean-Cédric Desrochers
 */
public class UnlessCondition extends IfCondition {

  public static final String TAG_PARAM  = "param";
  public static final String TAG_EQUALS = "equals";
  
  /**
   * Creates a new UnlessCondition instance.
   */
  public UnlessCondition() {
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.XmlConsumer#consumeXml(org.sapia.soto.me.xml.XmlConsumer.XmlCursor)
   */
  public void consumeXml(XmlCursor aCursor) throws J2meProcessingException {
    if (!evaluationExpression()) {
      if (Log.isDebug()) {
        Log.debug("UNLESS condition (" + getParameter() + " == " + getExpectedValue() + ") meet... processing XML");
      }
      setResultObject(aCursor.processXmlElement(null));
    } else {
      if (Log.isDebug()) {
        Log.debug("UNLESS condition (" + getParameter() + " != " + getExpectedValue() + ") not meet... skipping XML");
      }
      aCursor.skipXmlElement();
    }
  }
}
