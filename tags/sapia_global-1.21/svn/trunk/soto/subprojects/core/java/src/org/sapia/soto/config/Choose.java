package org.sapia.soto.config;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.io.SAXReader;
import org.sapia.util.xml.ProcessingException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.Dom4jProcessor;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectFactoryIF;
import org.sapia.util.xml.confix.XMLConsumer;
import org.xml.sax.InputSource;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class Choose implements ObjectCreationCallback {
  private List              _whens = new ArrayList();
  private Otherwise         _otherwise;
  private ObjectFactoryIF   _fac;

  public Choose(ObjectFactoryIF fac) {
    _fac = fac;
  }

  public When createWhen() {
    When whenObj = new When(_fac);
    _whens.add(whenObj);

    return whenObj;
  }

  public Otherwise createOtherwise() {
    if(_otherwise != null) {
      throw new IllegalArgumentException(
          "'otherwise' element already specified");
    }

    return _otherwise = new Otherwise(_fac);
  }

  public Object onCreate() throws ConfigurationException {
    When current;

    for(int i = 0; i < _whens.size(); i++) {
      current = (When) _whens.get(i);

      if(current.isEqual()) {
        return current.create();
      }
    }

    if(_otherwise != null) {
      return _otherwise.create();
    }

    return new NullObjectImpl();
  }

  public static class When extends Condition {
    When(ObjectFactoryIF fac) {
      super("when", fac);
    }
  }

  public static final class Otherwise implements XMLConsumer {
    private ObjectFactoryIF   _fac;
    private org.dom4j.Element _elem;

    Otherwise(ObjectFactoryIF fac) {
      _fac = fac;
    }

    public Object create() throws ConfigurationException {
      if(_elem == null) {
        return new NullObjectImpl();
      }

      Dom4jProcessor proc = new Dom4jProcessor(_fac);

      try {
        return proc.process(null, _elem);
      } catch(ProcessingException e) {
        throw new ConfigurationException(
            "Could not process xml nested in 'if' element", e);
      }
    }

    /**
     * @see org.sapia.util.xml.confix.XMLConsumer#consume(org.xml.sax.InputSource)
     */
    public void consume(InputSource is) throws Exception {
      if(_elem != null) {
        throw new ConfigurationException(
            "'choose' only takes a SINGLE nested xml element");
      }
      SAXReader reader = new SAXReader();
      _elem = reader.read(is).getRootElement();
    }
  }
}
