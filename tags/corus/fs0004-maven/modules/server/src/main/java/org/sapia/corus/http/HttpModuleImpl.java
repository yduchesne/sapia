package org.sapia.corus.http;

import org.sapia.corus.annotations.Bind;
import org.sapia.corus.client.services.http.HttpExtension;
import org.sapia.corus.client.services.http.HttpModule;
import org.sapia.corus.core.CorusRuntime;
import org.sapia.corus.core.ModuleHelper;
import org.sapia.corus.http.filesystem.FileSystemExtension;
import org.sapia.corus.http.interop.SoapExtension;
import org.sapia.corus.http.jmx.JmxExtension;
import org.sapia.ubik.rmi.server.transport.http.HttpTransportProvider;
import org.sapia.ubik.rmi.server.transport.socket.MultiplexSocketTransportProvider;


/**
 * Implements the {@link HttpModule} interface.
 * @author Yanick Duchesne
 */
@Bind(moduleInterface=HttpModule.class)
public class HttpModuleImpl extends ModuleHelper implements HttpModule {

  private HttpExtensionManager _httpExt;
  
  /**
   * Constructor for HttpModuleImpl.
   */
  public HttpModuleImpl() {
    super();
  }
  
  /**
   * @see org.sapia.corus.client.Module#getRoleName()
   */
  public String getRoleName() {
    return ROLE;
  }

  /**
   * @see org.sapia.corus.core.soto.Service#init()
   */
  public void init() throws Exception {
    // Create the interop and http extension transports
    String transportType = CorusRuntime.getTransport().getTransportProvider().getTransportType();
    if (transportType.equals(MultiplexSocketTransportProvider.TRANSPORT_TYPE)) {
      _httpExt = new HttpExtensionManager(logger());      
    } else if (transportType.equals(HttpTransportProvider.DEFAULT_HTTP_TRANSPORT_TYPE)) {
      _httpExt = new HttpExtensionManager(logger());      
    } else {
      throw new IllegalStateException("Could not initialize the http module using the transport type: " + transportType);
    }
    _httpExt.init();      
  }
  
  public void start() throws Exception {
    
    //////////// adding default extensions ///////////
    
    addHttpExtension(new FileSystemExtension());
    addHttpExtension(new JmxExtension());    
    SoapExtension ext = new SoapExtension(serverContext());
    addHttpExtension(ext);    
    
    _httpExt.start();
  }  
  
  public void dispose() {
    _httpExt.dispose();    
  }
 
  public void addHttpExtension(HttpExtension ext) {
    _httpExt.addHttpExtension(ext);
  }
}
