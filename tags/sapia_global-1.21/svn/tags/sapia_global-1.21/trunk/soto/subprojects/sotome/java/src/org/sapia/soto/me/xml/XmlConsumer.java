package org.sapia.soto.me.xml;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public interface XmlConsumer {

  public void consumeXml(XmlCursor aCursor) throws J2meProcessingException;
  
  public static interface XmlCursor {
    public Object processXmlElement(Object aParent) throws J2meProcessingException;
    public void skipXmlElement() throws J2meProcessingException;
  }
}
