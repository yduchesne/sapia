package org.sapia.soto.ubik.monitor.impl;

import java.io.InputStream;
import java.rmi.RemoteException;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.ubik.monitor.MonitorAgent;
import org.sapia.soto.ubik.monitor.Status;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * An instance of this class monitors a given URI; it uses Soto's resource resolving
 * mechanism to do so.
 * 
 * @author Yanick Duchesne.
 */
public class UriMonitorAgent 
  implements MonitorAgent, 
             ObjectCreationCallback,
             EnvAware{
  
  private String _uri, _id, _class;
  private Env _env;
 
  public void setEnv(Env env) {
    _env = env;
  }
  
  public void setServiceId(String id){
    _id = id;
  }
  
  public void setServiceClass(String className){
    _class = className;
  }
  
  public void setUri(String uri){
    _uri = uri;
  }
  
  public Status checkStatus() throws RemoteException {
    Status stat = new Status(_class, _id);
    try{
      InputStream is = _env.resolveStream(_uri);
      is.close();
      return stat;
    }catch(Exception e){
      stat.setError(e);
      return stat;
    }
  }
  
  public Object onCreate() throws ConfigurationException {
    if(_id == null){
      throw new ConfigurationException("service id not specified");
    }
    if(_class == null){
      throw new ConfigurationException("service class not specified");
    }    
    return this;
  }
}
