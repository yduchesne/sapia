/*
 * Variable.java
 *
 * Created on November 8, 2005, 3:10 PM
 */

package org.sapia.soto.configuration.jconfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.config.NullObjectImpl;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author yduchesne
 */
public class Variables implements EnvAware, ObjectCreationCallback{
  
  private String _propertyUri;
  private ConfigurationDef _owner;
  private Env _env;
  
  /** Creates a new instance of Variable */
  public Variables(ConfigurationDef owner) {
    _owner = owner;
  }

  public void setProperties(String uri){
    _propertyUri = uri;
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_propertyUri != null){
      try{
        Properties props = new Properties();
        InputStream is = _env.resolveStream(_propertyUri);
        try{
          props.load(is);
        }finally{
          is.close();
        }
        Enumeration names = props.propertyNames();
        while(names.hasMoreElements()){
          String name = (String)names.nextElement();
          String value = props.getProperty(name);
          if(value != null){
            _owner.addVariable(name, value);
          }
        }
      }catch(IOException e){
        throw new ConfigurationException("Could not process properties from resource: " + _propertyUri, e);
      }
    }
    return new NullObjectImpl();
  }
}
