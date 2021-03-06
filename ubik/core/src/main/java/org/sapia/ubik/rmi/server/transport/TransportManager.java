package org.sapia.ubik.rmi.server.transport;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sapia.ubik.module.Module;
import org.sapia.ubik.module.ModuleContext;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.rmi.server.transport.http.HttpTransportProvider;
import org.sapia.ubik.rmi.server.transport.memory.InMemoryTransportProvider;
import org.sapia.ubik.rmi.server.transport.mina.MinaTransportProvider;
import org.sapia.ubik.rmi.server.transport.netty.NettyTransportProvider;
import org.sapia.ubik.rmi.server.transport.socket.SocketTransportProvider;
import org.sapia.ubik.util.Assertions;

/**
 * The transport manager is the single-entry point into Ubik RMI's transport
 * layer. It allows registering {@link TransportProvider} instances, which
 * provide transport implementations on top of different network protocols.
 * <p>
 * This class registers the following transport providers automatically:
 *
 * <ul>
 * <li> {@link SocketTransportProvider}.
 * <li> {@link MinaTransportProvider}.
 * <li> {@link NettyTransportProvider}.
 * <li> {@link InMemoryTransportProvider}.
 * <li> {@link HttpTransportProvider}.
 * </ul>
 *
 * @author Yanick Duchesne
 */
public class TransportManager implements Module {

  private Map<String, TransportProvider> providers = new ConcurrentHashMap<String, TransportProvider>();

  @Override
  public void init(ModuleContext context) {
    registerProvider(new SocketTransportProvider());
    registerProvider(new NettyTransportProvider());
    registerProvider(new MinaTransportProvider());
    registerProvider(new InMemoryTransportProvider());
    registerProvider(new HttpTransportProvider());

    String[] propNames = System.getProperties().keySet().toArray(new String[System.getProperties().size()]);
    String propName;
    String className;

    for (int i = 0; i < propNames.length; i++) {
      propName = propNames[i];

      if (propName.startsWith(Consts.TRANSPORT_PROVIDER)) {
        className = System.getProperty(propName);

        Assertions.illegalState(className == null, "No class name defined for transport provider %s", propName);

        try {
          TransportProvider provider = (TransportProvider) Class.forName(className).newInstance();
          registerProvider(provider);
        } catch (Throwable e) {
          throw new IllegalStateException("Could not instantiate transport provider: " + className, e);
        }
      }
    }
  }

  @Override
  public void start(ModuleContext context) {
  }

  @Override
  public void stop() {
    for (TransportProvider provider : providers.values()) {
      provider.shutdown();
    }

    providers.clear();
  }

  /**
   * Registers the transport provider of the given type with the transport
   * manager. The provider is internally mapped to its "transport type".
   *
   * @see TransportProvider#getTransportType()
   * @param provider
   *          a {@link TransportProvider} instance.
   * @throws IllegalArgumentException
   *           if a provider is already registered for the given type.
   */
  public void registerProvider(TransportProvider provider) {
    if (providers.containsKey(provider.getTransportType())) {
      throw new IllegalArgumentException("Transport provider already registered for: " + provider.getTransportType());
    }

    providers.put(provider.getTransportType(), provider);
  }

  /**
   * Returns the transport provider corresponding to the given type.
   *
   * @param type
   *          the logical type of the desired transport provider.
   * @return a {@link TransportProvider}.
   * @throws IllegalArgumentException
   *           if no provider is registered for the passed in type.
   */
  public TransportProvider getProviderFor(String type) {
    TransportProvider provider = providers.get(type);

    if (provider == null) {
      throw new IllegalArgumentException("No transport provider for: " + type);
    }

    return provider;
  }

  /***
   * Gets a connection pool that holds connections to a server, given the
   * server's address.
   *
   * @return a {@link Connections} instance.
   * @param address
   *          a {@link ServerAddress}.
   * @throws RemoteException
   *           if an problem occurs acquiring the connection.
   */
  public Connections getConnectionsFor(ServerAddress address) throws RemoteException {
    return getProviderFor(address.getTransportType()).getPoolFor(address);
  }

  /**
   * Returns the default transport provider.
   *
   * @return the {@link SocketTransportProvider}.
   */
  public MinaTransportProvider getDefaultProvider() {
    return (MinaTransportProvider) providers.get(MinaTransportProvider.TRANSPORT_TYPE);
  }

  /**
   * Invokes {@link TransportProvider#shutdown()} on all the registered
   * providers.
   */
  public void shutdown() {

  }
}
