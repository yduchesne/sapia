package org.sapia.soto.ubik.monitor.impl;

import java.rmi.Remote;
import java.util.Properties;

import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.ubik.EventChannelProvider;
import org.sapia.ubik.mcast.AsyncEventListener;
import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.mcast.RemoteEvent;
import org.sapia.ubik.rmi.server.Hub;

/**
 * This class extends the  {@link AbstractMonitorAgentLayer} class and adds support for making
 * its status check method call available remotely, using the <a href="http://www.sapia-oss.org/projects/ubik" target="ubik">Ubik</a>
 * distributed computing framework.
 * <p>
 * An instance of this class publishes itself over multicast to remote monitors by using an 
 * <a href="http://www.sapia-oss.org/projects/ubik/api/org/sapia/ubik/mcast/EventChannel.html" target="eventChannel">EventChannel</a>instance.
 * In addition, it discovers appearing remote monitors through similar logic (in this case, it is remote monitors that publish
 * their presence over multicast and are discovered by instances of this class).
 * <p>
 * In addition, an instance of this class detects if it has been started by a <a href="http://www.sapia-oss.org/projects/corus" target="corus">Corus</a>
 * server, and if so registers itself with Corus' process status notification mechanism.
 * <p>
 * 
 * @author yduchesne
 *
 */
public class UbikMonitorAgentLayer extends AbstractMonitorAgentLayer 
  implements  EnvAware, 
              AsyncEventListener, 
              MonitorConsts, Remote {

  private Env _env;

  private EventChannel _channel;

  private Object _stub;
  
  private MonitorStatusRequestListener _corusHook;
  
  private static boolean _corusCapable;  
  
  static{
    try{
      Class.forName("org.sapia.corus.interop.api.InteropLink");
      _corusCapable = true;
    }catch(Exception e){
      _corusCapable = false;
    }
  }  

  public void setEnv(Env env) {
    _env = env;
  }

  /**
   * Sets the event channel to be used by this instance to perform publishing and discovery
   * over multicast.
   * 
   * @param channel an <code>EventChannelProvider</code>.
   */
  public void setEventChannel(EventChannelProvider channel) {
    _channel = channel.getEventChannel();
  }
  
  public void init(ServiceMetaData meta) throws Exception {
    super.init(meta);
    lookupChannel();
    
    if(_corusCapable){
      _corusHook = new MonitorStatusRequestListener(this);
    }    
  }

  public void start(ServiceMetaData meta) throws Exception {
    super.start(meta);
    if (_channel != null) {
      _channel.registerAsyncListener(DISCO_ADMIN, this);
      
      Debug.debug(getClass(), "Dispatching monitor agent to admin clients...");
      _channel.dispatch(DISCO_AGENT, _stub);
    }
  }

  public void dispose() {
    super.dispose();
    if (_channel != null) {
      _channel.unregisterListener(this);
    }
  }

  public void onAsyncEvent(RemoteEvent evt) {
    if (evt.getType().equals(DISCO_ADMIN)) {
      Debug.debug(getClass(), "Discovered Monitor");
      try {
        if(_stub != null){
          _channel.dispatch(DISCO_AGENT, _stub);
        }
        else{
          Debug.debug(getClass(), "Stub is null");
        }
      } catch (Exception e) {
        Debug.debug(e);
      }
    }
  }
  
  protected void lookupChannel() throws Exception {
    if (_channel == null) {
      try{
        _channel = ((EventChannelProvider) _env
            .lookup(EventChannelProvider.class)).getEventChannel();
      }catch(org.sapia.soto.NotFoundException e){}
    }
    if(_channel != null){
      _stub = Hub.exportObject(this);      
    }
  }
  
  protected Properties postProcess(Properties props) {
    if(props == null) return null;
    if(!_corusCapable){
      String pid = System.getProperty("corus.process.id"); 
      String corusHost = System.getProperty("corus.server.host");
      String corusPort = System.getProperty("corus.server.port");    
      String corusDomain = System.getProperty("corus.server.domain");
      if(pid != null){
        props.setProperty("corus.process.id", pid);
      }
      if(corusHost != null){
        props.setProperty("corus.server.host", corusHost);
      }
      if(corusPort != null){
        props.setProperty("corus.server.port", corusPort);
      }        
      if(corusDomain != null){
        props.setProperty("corus.server.domain", corusDomain);
      }    

    }
    return props;
  }
  
  /*
  public static void main(String[] args) {
    try{
      System.setProperty("soto.debug", "true");
      SotoContainer cont = new SotoContainer();
      Bean bean = new Bean();
      ServiceMetaData meta = new ServiceMetaData("test", bean);
      EventChannelProviderImpl chan = new EventChannelProviderImpl();
      chan.setDomain("default");
      chan.init();
      chan.start();
      MonitorAgentLayer layer = new MonitorAgentLayer();
      layer.setEventChannel(chan);
      layer.init(meta);
      layer.start(meta);
      Thread.sleep(100000);

    }catch(Exception e){
      e.printStackTrace();      
    }
  }
  
  public static final class Bean implements FeedbackMonitorable{
    
    public Properties monitor() throws Exception {
      Properties props = new Properties();
      props.setProperty("foo", "bar");
      props.setProperty("sna", "fu");
      return props;
    }
  } */ 
}
