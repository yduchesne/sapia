package org.sapia.soto.ubik.monitor.impl;

import java.io.IOException;
import java.util.Collections;

import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.Service;
import org.sapia.soto.ubik.EventChannelProvider;
import org.sapia.soto.ubik.monitor.MonitorAgent;
import org.sapia.soto.ubik.monitor.StatusResultList;
import org.sapia.soto.util.Utils;
import org.sapia.ubik.mcast.AsyncEventListener;
import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.mcast.RemoteEvent;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * An instance of this class publishes itself through multicast. It is intented
 * to collaborate with remote <code>MonitorAgent</code>s
 * (such as the {@link UbikMonitorAgentLayer}), calling their status check method
 * over the network, using the <a href="http://www.sapia-oss.org/projects/ubik" target="ubik">Ubik</a>
 * distributed computing framework.
 * <p>
 * 
 * @see UbikMonitorAgentLayer
 * 
 * @author Yanick Duchesne
 */
public class DomainMonitorService
    extends    AbstractMonitor
    implements Service, 
               AsyncEventListener,
               EnvAware, 
               ObjectHandlerIF,
               MonitorConsts {

  private Env _env;
  private EventChannel _channel;
  
  public void setEnv(Env env) {
    _env = env;
  }

  /**
   * Sets the event channel to be used by this instance to perform publishing and discovery
   * over multicast.
   * 
   * @param channel an <code>EventChannelProvider</code>.
   */
  public void setEventChannel(EventChannelProvider provider) {
    _channel = provider.getEventChannel();
  }

  //////// Soto Service interface
  
  public void init() throws Exception {
    lookupChannel();
  }

  public void start() throws Exception {
    if (_channel != null) {
      _channel.registerAsyncListener(DISCO_AGENT, this);

      Debug.debug(getClass(), "Dispatching monitor to agents...");
      _channel.dispatch(DISCO_ADMIN, "");
    }
    else{
      Debug.debug(getClass(), "No event channel set");
    }
  }

  public void dispose() {
  }

  //////// Monitor interface
  
  public StatusResultList getStatusForClass(String className) {
    StatusResultList resultList = new StatusResultList();
    super.doGetStatus(className, resultList, false);
    Collections.sort(resultList.getModifiableResults());
    return resultList;
  }

  public StatusResultList getStatusForId(String serviceId) {
    StatusResultList resultList = new StatusResultList();
    super.doGetStatus(serviceId, resultList, true);
    Collections.sort(resultList.getModifiableResults());
    return resultList;
  }

  public void refresh() {
    super.clearAgents();
    try{
      Debug.debug(getClass(), "Dispatching monitor to agents...");
      _channel.dispatch(DISCO_ADMIN, "");
    }catch(Exception e){}
  }

  public void onAsyncEvent(RemoteEvent evt) {
    if (evt.getType().equals(DISCO_AGENT)) {
      Debug.debug(getClass(), "Discovered monitor agent");
      try{
        super.addAgent(new RemoteAgentBinding((MonitorAgent)evt.getData()));
      }catch(IOException e){}
    }
  }
  
  public void handleObject(String anElementName, Object anObject) throws ConfigurationException {
    if(anObject instanceof MonitorAgent){
      super.addAgent((MonitorAgent)anObject);
    }
    else{
      throw new ConfigurationException(Utils.createInvalidAssignErrorMsg(
          anElementName, 
          anObject, 
          MonitorAgent.class));
    }
  }

  protected void lookupChannel() throws Exception {
    if (_channel == null) {
      _channel = ((EventChannelProvider) _env
          .lookup(EventChannelProvider.class)).getEventChannel();
    }
  }
  
  protected static class RemoteAgentBinding extends AbstractMonitor.AgentBinding{
    private Object _this = new Object();
    protected RemoteAgentBinding(MonitorAgent agent){
      super(agent);
    }
    public boolean equals(Object obj) {
      return false;
    }
    public int hashCode() {
      return _this.hashCode();
    }
  }
  
  /*
  public static void main(String[] args) {
    try{
      System.setProperty("soto.debug", "true");
      SotoContainer cont = new SotoContainer();
      EventChannelProviderImpl chan = new EventChannelProviderImpl();
      chan.init();
      chan.start();
      DomainMonitorService mon = new DomainMonitorService();
      mon.setEventChannel(chan);
      mon.init();
      mon.start();
      Thread.sleep(100000);

    }catch(Exception e){
      e.printStackTrace();      
    }
  } */ 
  

}
