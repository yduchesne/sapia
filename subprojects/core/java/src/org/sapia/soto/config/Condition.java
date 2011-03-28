package org.sapia.soto.config;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.sapia.util.xml.ProcessingException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.Dom4jProcessor;
import org.sapia.util.xml.confix.ObjectFactoryIF;
import org.sapia.util.xml.confix.XMLConsumer;
import org.xml.sax.InputSource;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class Condition implements XMLConsumer {
  private String            _name;
  private String            _elemName;
  private String            _equals;
  private ObjectFactoryIF   _fac;
  private Element           _elem;

  public Condition(String elemName, ObjectFactoryIF fac) {
    _fac = fac;
    _elemName = elemName;
  }

  public void setParam(String name) {
    _name = name;
  }

  public void setEquals(String eq) {
    _equals = eq;
  }

  public boolean isEqual() {
    SotoIncludeContext ctx = SotoIncludeContext.currentContext();
    
    Object val = null;
    if(ctx != null){
      val = ctx.getTemplateContext().getValue(_name);
    }

    if(val == null) {
      return false;
    }

    if(_equals == null) {
      return true;
    }

    return val.toString().equals(_equals);
  }

  public Object create() throws ConfigurationException {
    if((_elem == null) || (_name == null)) {
      return new NullObjectImpl();
    }

    if(isEqual()) {
      Dom4jProcessor proc = new Dom4jProcessor(_fac);

      try {
        return proc.process(null, _elem);
      } catch(ProcessingException e) {
        throw new ConfigurationException(
            "Could not process xml nested in 'if' element", e);
      }
    }

    return new NullObjectImpl();
  }

  /**
   * @see org.sapia.util.xml.confix.XMLConsumer#consume(org.xml.sax.InputSource)
   */
  public void consume(InputSource is) throws Exception {
    if(_elem != null) {
      throw new ConfigurationException("'" + _elemName
          + "' only takes a SINGLE nested xml element");
    }
    SAXReader reader = new SAXReader();
    _elem = reader.read(is).getRootElement();
  }
}
