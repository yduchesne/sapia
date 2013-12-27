/*
 * MapType.java
 *
 * Created on June 28, 2005, 2:30 PM
 */

package org.sapia.soto.config.types;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.config.NullObjectImpl;
import org.sapia.soto.util.Param;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * Evaluates to a <code>java.util.Properties</code> instance.
 * @author yduchesne
 */
public class PropertiesType implements ObjectCreationCallback, EnvAware{
  
  private Properties _props = new Properties();
  private String _uri;
  private Env _env;
  
  /** Creates a new instance of MapType */
  public PropertiesType() {
  }
  
  public PropertyParam createProperty(){
    return new PropertyParam(_props);
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void setUri(String uri){
    _uri = uri;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_uri != null){
      InputStream is = null;
      try{
        is = _env.resolveStream(_uri);
        _props.load(is);
      }catch(IOException e){
        throw new ConfigurationException("Could not load properties", e);
      }finally{
        if(is != null){
          try{
            is.close();
          }catch(IOException e){}
        }
      }
    }
    return _props;
  }
  
  public static class PropertyParam extends Param implements ObjectCreationCallback{
    
    private Properties _owner;
    
    PropertyParam(Properties owner){
      _owner = owner;
    }
    
    public Object onCreate() throws ConfigurationException{
      if(getName() != null && getValue() != null){
        _owner.setProperty(getName(), getValue().toString());
      }
      return new NullObjectImpl();
    }
  }
  
}
