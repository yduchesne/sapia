package org.sapia.corus.http;

import org.sapia.corus.client.annotations.Bind;
import org.sapia.corus.client.services.Service;
import org.sapia.corus.client.services.http.HttpExtension;
import org.sapia.corus.client.services.http.HttpModule;
import org.sapia.corus.core.ModuleHelper;
import org.sapia.corus.http.filesystem.FileSystemExtension;
import org.sapia.corus.http.interop.SoapExtension;
import org.sapia.corus.http.jmx.JmxExtension;
import org.sapia.corus.http.rest.RestExtension;
import org.sapia.ubik.rmi.server.transport.http.HttpTransportProvider;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implements the {@link HttpModule} interface.
 * 
 * @author Yanick Duchesne
 */
@Bind(moduleInterface = HttpModule.class)
public class HttpModuleImpl extends ModuleHelper implements HttpModule {

  private HttpExtensionManager httpExt;
  
  @Autowired
  private SslExporter          sslExporter;

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
   * @see Service#init()
   */
  public void init() throws Exception {
    httpExt = new HttpExtensionManager(logger(), serverContext());
    HttpTransportProvider transportProvider = (HttpTransportProvider) serverContext().getTransport().getTransportProvider();
    transportProvider.getRouter().setCatchAllHandler(httpExt);
  }

  /**
   * @see Service#start()
   */
  public void start() throws Exception {
    addHttpExtension(new FileSystemExtension(serverContext));
    addHttpExtension(new JmxExtension(serverContext));
    addHttpExtension(new RestExtension(serverContext));
    addHttpExtension(new SoapExtension(serverContext));
    sslExporter.export(httpExt);
  }

  /**
   * @see Service#dispose()
   */
  public void dispose() {
  }

  /**
   * Adds a {@link HttpExtension} to this instance.
   * 
   * @param ext
   *          a {@link HttpExtension}
   */
  public void addHttpExtension(HttpExtension ext) {
    httpExt.addHttpExtension(ext);
  }
}
