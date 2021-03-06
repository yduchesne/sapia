package org.sapia.ubik.mcast;

import java.io.IOException;
import java.util.Properties;

import org.sapia.ubik.concurrent.ConfigurableExecutor;
import org.sapia.ubik.log.Category;
import org.sapia.ubik.log.Log;
import org.sapia.ubik.mcast.avis.AvisBroadcastDispatcher;
import org.sapia.ubik.mcast.hazelcast.HazelcastBroadcastDispatcher;
import org.sapia.ubik.mcast.hazelcast.HazelcastMulticastAddress;
import org.sapia.ubik.mcast.memory.InMemoryBroadcastDispatcher;
import org.sapia.ubik.mcast.memory.InMemoryUnicastDispatcher;
import org.sapia.ubik.mcast.tcp.TcpUnicastDispatcher;
import org.sapia.ubik.mcast.tcp.mina.MinaTcpUnicastDispatcher;
import org.sapia.ubik.mcast.udp.UDPBroadcastDispatcher;
import org.sapia.ubik.mcast.udp.UDPUnicastDispatcher;
import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.util.Conf;

/**
 * This class implements a factory of {@link UnicastDispatcher}s and
 * {@link BroadcastDispatcher}s.
 *
 * @author yduchesne
 *
 */
public final class DispatcherFactory {

  private static Category log = Log.createCategory(DispatcherFactory.class);
  private static boolean isNioEnabled;

  static {
    try {
      Class.forName("org.apache.mina.filter.codec.ProtocolCodecFilter");
      isNioEnabled = true;
    } catch (Exception e) {
      log.info("Netty not detected in classpath, will use non-NIO TCP unicast");
      isNioEnabled = false;
    }

  }

  /**
   * Private constructor.
   */
  private DispatcherFactory() {
  }

  /**
   * Creates a {@link UnicastDispatcher}, based on the given {@link Properties},
   * and returns it.
   *
   * @param consumer
   *          an {@link EventConsumer}.
   * @param props
   *          the {@link Conf} containing configuration values.
   * @return the {@link BroadcastDispatcher} corresponding to the given
   *         properties.
   * @throws IOException
   *           if a problem occurs creating the dispatcher.
   */
  public static UnicastDispatcher createUnicastDispatcher(EventConsumer consumer, Conf props) throws IOException {
    String provider = props.getProperty(Consts.UNICAST_PROVIDER, Consts.UNICAST_PROVIDER_TCP);

    if (provider.equals(Consts.UNICAST_PROVIDER_MEMORY)) {
      log.info("Creating in-memory unicast provider");
      return new InMemoryUnicastDispatcher(consumer);
    } else if (provider.equals(Consts.UNICAST_PROVIDER_UDP)) {
      log.info("Creating UDP unicast provider");
      UDPUnicastDispatcher dispatcher = new UDPUnicastDispatcher(consumer, props.getIntProperty(Consts.MCAST_HANDLER_COUNT,
          Defaults.DEFAULT_HANDLER_COUNT));
      dispatcher.setBufsize(props.getIntProperty(Consts.MCAST_BUFSIZE_KEY, Defaults.DEFAULT_UDP_PACKET_SIZE));
      dispatcher.setSenderCount(props.getIntProperty(Consts.MCAST_SENDER_COUNT, Defaults.DEFAULT_SENDER_COUNT));
      return dispatcher;
    } else {
      if (isNioEnabled) {
        log.info("Creating NIO TCP unicast provider");
        MinaTcpUnicastDispatcher dispatcher = new MinaTcpUnicastDispatcher(consumer, props.getIntProperty(Consts.MCAST_HANDLER_COUNT,
            Defaults.DEFAULT_HANDLER_COUNT), props.getIntProperty(Consts.MARSHALLING_BUFSIZE, Consts.DEFAULT_MARSHALLING_BUFSIZE));
        dispatcher.setSenderCount(props.getIntProperty(Consts.MCAST_SENDER_COUNT, Defaults.DEFAULT_SENDER_COUNT));
        dispatcher.setMaxConnectionsPerHost(props.getIntProperty(Consts.MCAST_MAX_CLIENT_CONNECTIONS, Defaults.DEFAULT_MAX_CONNECTIONS_PER_HOST));
        dispatcher.setResponseTimeout(props.getTimeProperty(Consts.MCAST_SYNC_RESPONSE_TIMEOUT, Defaults.DEFAULT_SYNC_RESPONSE_TIMEOUT).getValueInMillis());
        return dispatcher;
      } else {
        log.info("Creating BIO TCP unicast provider");
        ConfigurableExecutor.ThreadingConfiguration threadingConfig = new ConfigurableExecutor.ThreadingConfiguration();
        threadingConfig.setMaxPoolSize(props.getIntProperty(Consts.MCAST_HANDLER_COUNT, Defaults.DEFAULT_HANDLER_COUNT));
        threadingConfig.setCorePoolSize(props.getIntProperty(Consts.MCAST_HANDLER_COUNT, Defaults.DEFAULT_HANDLER_COUNT));
        TcpUnicastDispatcher dispatcher = new TcpUnicastDispatcher(consumer, threadingConfig);
        dispatcher.setSenderCount(props.getIntProperty(Consts.MCAST_SENDER_COUNT, Defaults.DEFAULT_SENDER_COUNT));
        dispatcher.setMaxConnectionsPerHost(props.getIntProperty(Consts.MCAST_MAX_CLIENT_CONNECTIONS, Defaults.DEFAULT_MAX_CONNECTIONS_PER_HOST));
        dispatcher.setResponseTimeout(props.getTimeProperty(Consts.MCAST_SYNC_RESPONSE_TIMEOUT, Defaults.DEFAULT_SYNC_RESPONSE_TIMEOUT).getValueInMillis());
        return dispatcher;
      }
    }
  }

  /**
   * Creates a {@link BroadcastDispatcher}, based on the given
   * {@link Properties}, and returns it.
   *
   * @param consumer
   *          an {@link EventConsumer}.
   * @param props
   *          the {@link Conf} containing configuration values.
   * @return the {@link BroadcastDispatcher} corresponding to the given
   *         properties.
   * @throws IOException
   *           if a problem occurs creating the dispatcher.
   */
  public static BroadcastDispatcher createBroadcastDispatcher(EventConsumer consumer, Conf props) throws IOException {
    String provider = props.getProperty(Consts.BROADCAST_PROVIDER, Consts.BROADCAST_PROVIDER_UDP);
    if (provider.equals(Consts.BROADCAST_PROVIDER_MEMORY)) {
      log.info("Creating in-memory broadcast provider");
      return new InMemoryBroadcastDispatcher(consumer);
    } else if (provider.equals(Consts.BROADCAST_PROVIDER_AVIS)) {
      log.info("Creating AVIS broadcast provider");
      String avisUrl = props.getNotNullProperty(Consts.BROADCAST_AVIS_URL);
      AvisBroadcastDispatcher dispatcher = new AvisBroadcastDispatcher(consumer, avisUrl);
      dispatcher.setBufsize(props.getIntProperty(Consts.MCAST_BUFSIZE_KEY, AvisBroadcastDispatcher.DEFAULT_BUFSZ));
      return dispatcher;
    } else if (provider.equals(Consts.BROADCAST_PROVIDER_HAZELCAST)) {
      log.info("Creating HAZELCAST broadcast provider");
      String topic = props.getNotNullProperty(Consts.BROADCAST_HAZELCAST_TOPIC);
      HazelcastBroadcastDispatcher dispatcher = new HazelcastBroadcastDispatcher(consumer, topic);
      return dispatcher;
    } else {
      log.info("Creating UDP broadcast provider");
      UDPBroadcastDispatcher dispatcher = new UDPBroadcastDispatcher(consumer, props.getProperty(Consts.MCAST_ADDR_KEY, Consts.DEFAULT_MCAST_ADDR),
          props.getIntProperty(Consts.MCAST_PORT_KEY, Consts.DEFAULT_MCAST_PORT), props.getIntProperty(Consts.MCAST_TTL, Defaults.DEFAULT_TTL));
      dispatcher.setBufsize(props.getIntProperty(Consts.MCAST_BUFSIZE_KEY, Defaults.DEFAULT_UDP_PACKET_SIZE));
      return dispatcher;
    }
  }

  /**
   * @param from
   *          the {@link Properties} from which to construct a
   *          {@link MulticastAddress}.
   * @return the {@link MulticastAddress} that was created from the given
   *         {@link Properties}.
   */
  public static MulticastAddress getMulticastAddress(Properties from) {
    Conf props = new Conf().addProperties(from);
    String provider = props.getProperty(Consts.BROADCAST_PROVIDER, Consts.BROADCAST_PROVIDER_UDP);
    if (provider.equals(Consts.BROADCAST_PROVIDER_MEMORY)) {
      return new InMemoryBroadcastDispatcher.InMemoryMulticastAddress(props.getNotNullProperty(Consts.BROADCAST_MEMORY_NODE));
    } else if (provider.equals(Consts.BROADCAST_PROVIDER_AVIS)) {
      String avisUrl = props.getProperty(Consts.BROADCAST_AVIS_URL);
      return new AvisBroadcastDispatcher.AvisAddress(avisUrl);
    } else if (provider.equals(Consts.BROADCAST_PROVIDER_HAZELCAST)) {
      return new HazelcastMulticastAddress(props.getNotNullProperty(Consts.BROADCAST_HAZELCAST_TOPIC));
    } else {
      return new UDPBroadcastDispatcher.UDPMulticastAddress(props.getProperty(Consts.MCAST_ADDR_KEY, Consts.DEFAULT_MCAST_ADDR),
          props.getIntProperty(Consts.MCAST_PORT_KEY, Consts.DEFAULT_MCAST_PORT));
    }
  }

}
