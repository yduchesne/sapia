package org.sapia.soto.util;

import org.sapia.soto.config.NullObjectImpl;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.NullObject;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectHandlerIF;

public class Defs implements ObjectCreationCallback, ObjectHandlerIF{
  
  static final NullObject NULL = new NullObjectImpl();
  
  private CompositeObjectFactoryEx _fac;
  
  public Defs(CompositeObjectFactoryEx fac){
    _fac = fac;
  }
  
   /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(String, Object)
   */
  public void handleObject(String elementName, Object obj)
      throws ConfigurationException {
    if(obj instanceof Namespace) {
      Namespace ns = (Namespace) obj;
      _fac.registerDefs(ns);
    }
  }
  
  public Object onCreate() throws ConfigurationException {
    return NULL;
  }
}
