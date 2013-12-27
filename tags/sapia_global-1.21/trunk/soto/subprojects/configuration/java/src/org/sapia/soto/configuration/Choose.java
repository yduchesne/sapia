/*
 * Choose.java
 *
 * Created on November 2, 2005, 4:45 PM
 */

package org.sapia.soto.configuration;

import java.util.ArrayList;
import java.util.List;
import org.sapia.soto.config.NullObjectImpl;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * @author yduchesne
 */
public class Choose implements ObjectCreationCallback{
  
  private List _whens = new ArrayList();
  private Otherwise _otherwise;
  
  /** Creates a new instance of Choose */
  public Choose() {
  }
  
  public When createWhen(){
    When w = new When();
    _whens.add(w);
    return w;
  }
  
  public Otherwise createOtherwise(){
    if(_otherwise == null){
      _otherwise = new Otherwise();
    }
    return _otherwise;
  }
  
  public Object onCreate() throws ConfigurationException{
    for(int i = 0; i < _whens.size(); i++){
      When w = (When)_whens.get(i);
      if(w.isEqual()){
        return w.doCreate();
      }
      else{
        continue;
      }
    }
    if(_otherwise == null)
      return new NullObjectImpl();
    return _otherwise.doCreate();
  }

  ///////////////// inner classes ////////////////
  
  // Otherwise //
  
  public static class Otherwise extends Condition{
    
    public Object onCreate() throws ConfigurationException{    
      return new NullObjectImpl();
    }
    protected Object doCreate() throws ConfigurationException{
      return super.process();      
    }
  }
  
  // When //
  
  public static class When extends Condition{

    public When() {
    }

    public Object onCreate() throws ConfigurationException{    
      return new NullObjectImpl();
    }
    protected Object doCreate() throws ConfigurationException{
      return super.process();      
    }
  }  
}
