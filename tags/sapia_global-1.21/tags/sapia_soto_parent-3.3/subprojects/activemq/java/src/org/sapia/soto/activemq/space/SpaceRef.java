package org.sapia.soto.activemq.space;

import java.lang.reflect.Field;

import org.codehaus.activespace.SpaceFactory;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class SpaceRef implements ObjectCreationCallback, EnvAware{
  
  private int _deliveryMode;
  private String _id, _destination;
  private Env _env;
  private Object _template;
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void setFactory(String id){
    _id = id;
  }
  
  public void setDestination(String destination){
    _destination = destination;
  }  
  
  public void setTemplate(Object template){
    _template = template;
  }
  
  public void setDeliveryMode(String mode) throws Exception{
    Field f = SpaceFactory.class.getField(mode.toUpperCase());
    _deliveryMode = ((Integer)f.get(mode)).intValue();
  }  
  
  public Object onCreate() throws ConfigurationException {
    ActiveSpaceFactory spaceFac;
    try{
      if(_id == null){
        spaceFac = (ActiveSpaceFactory)_env.lookup(ActiveSpaceFactory.class);
      }
      else{
        spaceFac = (ActiveSpaceFactory)_env.lookup(_id);
      }
    }catch(Exception e){
      throw new ConfigurationException("Could not acquire ActiveSpaceFactory instance", e);
    }      
    try{
      return spaceFac.createSpace(_destination, _deliveryMode, _template);
    }catch(Exception e){
      throw new ConfigurationException("Could not create a Space", e);
    }
  }

}
