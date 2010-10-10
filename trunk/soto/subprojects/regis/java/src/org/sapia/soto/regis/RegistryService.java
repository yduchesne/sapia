package org.sapia.soto.regis;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.Service;
import org.sapia.soto.config.NullObjectImpl;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.NullObject;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class RegistryService implements Registry, Service, EnvAware{
  
  private Properties _props = new Properties();
  private Env _env;
  private Registry _delegate;

  public RegistryService(){
  }
  
  public RegisSession open() {
    return _delegate.open();
  }

  public org.sapia.regis.Node getRoot() {
    return _delegate.getRoot();
  }
  
  public void init() throws Exception {
    RegistryContext ctx = new RegistryContext(_props);
    _delegate = ctx.connect();
  }
  
  public void start() throws Exception {
  }
  
  public void dispose() {
  }
  
  public void close() {
  }
  
  public void setEnv(Env env) {
    _env = env;
    RegistryUtils.registerHandler(_env);
  }
  
  public Property createProperty(){
    return new Property(_props);
  }
  
  public void setUri(String configUri) throws Exception{
    InputStream is = _env.resolveStream(configUri);
    try{
      _props.load(is);
    }finally{
      is.close();
    }
  }
  
  public void setProperties(Properties props){
    Enumeration names = props.propertyNames();
    while(names.hasMoreElements()){
      String name = (String)names.nextElement();
      _props.setProperty(name, props.getProperty(name));
    }
  }
  
  public static class Property implements ObjectCreationCallback{
    String name, value;
    Properties props;
    static final NullObject NULL = new NullObjectImpl();
    
    Property(Properties props){
      this.props = props;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
    
    public Object onCreate() throws ConfigurationException {
      if(name != null && value != null){
        props.setProperty(name, value);
      }
      return NULL;
    }
    
  }

}
