package org.sapia.soto.corus.jmx;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.Service;
import org.sapia.soto.ubik.EventChannelProvider;

public class CorusJmxService implements Service, EnvAware {

  private EventChannelProvider _channel;

  private Env _env;

  private String _domain, _host, _port, _pid;


  public void setEventChannel(EventChannelProvider channel) {
    _channel = channel;
  }

  public void setEnv(Env env) {
    _env = env;
  }
  
  public void setDomain(String domain){
    _domain = domain;
  }

  public void init() throws Exception {
  }

  public void start() throws Exception {
  }
  
  @Override
  public void dispose() {}

}
