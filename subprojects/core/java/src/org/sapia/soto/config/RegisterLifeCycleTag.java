package org.sapia.soto.config;

import org.sapia.soto.SotoContainer;
import org.sapia.soto.lifecycle.LifeCycleManager;
import org.sapia.soto.util.Utils;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.NullObject;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectHandlerIF;

public class RegisterLifeCycleTag implements ObjectCreationCallback, ObjectHandlerIF{

  private SotoContainer _container;
  private String _type;
  private LifeCycleManager _lcm;
  
  public RegisterLifeCycleTag(SotoContainer container){
    _container = container;
  }
  
  public void setType(String type){
    _type = type;
  }
  
  public void handleObject(String anElementName, Object anObject) throws ConfigurationException {
    if(anObject instanceof LifeCycleManager){
      _lcm = (LifeCycleManager)anObject;
    }
    else{
      throw new ConfigurationException(Utils.createInvalidAssignErrorMsg(anElementName, anObject, LifeCycleManager.class));
    }
    
  }
  
  public Object onCreate() throws ConfigurationException {
    if(_lcm == null){
      throw new ConfigurationException("LifeCycleManager instance not set");
    }
    if(_type == null){
      throw new ConfigurationException("LifeCycleManager type identifier not set");
    }
    _container.registerLifeCycleManager(_type, _lcm);
    return new NullObject(){};
  }
}
