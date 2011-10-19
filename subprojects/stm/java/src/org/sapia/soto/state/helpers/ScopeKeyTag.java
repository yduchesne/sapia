package org.sapia.soto.state.helpers;

import org.sapia.soto.config.NullObjectImpl;
import org.sapia.soto.state.StmKey;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class ScopeKeyTag implements ObjectCreationCallback{
  
  private StmKey _key;
  
  public void setLookup(String key){
    _key = StmKey.parse(key);
  }

  public void setKey(String key){
    _key = StmKey.parse(key);
  }
  
  public Object onCreate() throws ConfigurationException {
    if(_key == null){
      return new NullObjectImpl();
    } 
    return _key;
  }
}
