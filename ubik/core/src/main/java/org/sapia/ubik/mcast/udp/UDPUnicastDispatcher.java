package org.sapia.ubik.mcast.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.sapia.ubik.concurrent.BlockingCompletionQueue;
import org.sapia.ubik.concurrent.NamedThreadFactory;
import org.sapia.ubik.log.Category;
import org.sapia.ubik.log.Log;
import org.sapia.ubik.mcast.Defaults;
import org.sapia.ubik.mcast.EventConsumer;
import org.sapia.ubik.mcast.McastUtil;
import org.sapia.ubik.mcast.RemoteEvent;
import org.sapia.ubik.mcast.RespList;
import org.sapia.ubik.mcast.Response;
import org.sapia.ubik.mcast.TimeoutException;
import org.sapia.ubik.mcast.UnicastDispatcher;
import org.sapia.ubik.mcast.server.UDPServer;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.util.Assertions;
import org.sapia.ubik.util.Localhost;

/**
 * Implements the {@link UnicastDispatcher} interface over UDP.
 *
 * @author Yanick Duchesne
 */
public class UDPUnicastDispatcher extends UDPServer implements UnicastDispatcher {

  private static final long STARTUP_TIMEOUT = 5000;

  private Category log = Log.createCategory(getClass());
  private EventConsumer consumer;
  private long responseTimeout = Defaults.DEFAULT_SYNC_RESPONSE_TIMEOUT.getValueInMillis();
  private int senderCount = Defaults.DEFAULT_SENDER_COUNT;
  private ExecutorService senders;
  private ExecutorService handlers;
  private UDPUnicastAddress addr;

  public UDPUnicastDispatcher(EventConsumer consumer, int maxThreads) throws SocketException {
    super(consumer.getNode() + "Unicast@" + consumer.getDomainName().toString());
    this.consumer = consumer;
    handlers = Executors.newFixedThreadPool(maxThreads, NamedThreadFactory.createWith("udp.unicast.dispatcher.Handler").setDaemon(true));
  }

  public UDPUnicastDispatcher(int soTimeout, int port, EventConsumer consumer, int maxThreads) throws SocketException {
    super(consumer.getNode() + "@" + consumer.getDomainName().toString(), port);
    this.consumer = consumer;
    handlers = Executors.newFixedThreadPool(maxThreads, NamedThreadFactory.createWith("udp.unicast.dispatcher.Handler").setDaemon(true));
  }

  public void setSenderCount(int senderCount) {
    this.senderCount = senderCount;
  }

  @Override
  public void start() {
    super.start();

    try {
      super.startupBarrier.await(STARTUP_TIMEOUT);
    } catch (InterruptedException e) {
      throw new IllegalStateException("Thread interrupted while waiting for startup", e);
    } catch (Exception e) {
      throw new IllegalStateException("Problem while starting up", e);
    }

    senders = Executors.newFixedThreadPool(senderCount, NamedThreadFactory.createWith("udp.unicast.dispatcher.Sender").setDaemon(true));

    InetAddress inetAddr = sock.getLocalAddress();
    if (inetAddr == null) {
      try {
        inetAddr = Localhost.getPreferredLocalAddress();
      } catch (UnknownHostException e) {
        throw new IllegalStateException(e);
      }
    }
    log.debug("Local address: %s", inetAddr.getHostAddress());
    addr = new UDPUnicastAddress(inetAddr, getPort());
  }

  @Override
  public void close() {
    if (sock != null) {
      sock.close();
    }
  }

  @Override
  public void dispatch(ServerAddress addr, String type, Object data) throws IOException {

    DatagramSocket sock = new DatagramSocket();

    sock.setSoTimeout((int) responseTimeout);

    try {
      RemoteEvent evt = new RemoteEvent(null, type, data).setNode(consumer.getNode());
      evt.setUnicastAddress(addr);
      log.debug("dispatch() : %s, type: %s, data: %s", addr, type, data);
      UDPUnicastAddress inet = (UDPUnicastAddress) addr;
      doSend(inet.getInetAddress(), inet.getPort(), sock, McastUtil.toBytes(evt, bufSize()), false, type);
    } catch (TimeoutException e) {
      // will not occur - see doSend();
    } finally {
      try {
        sock.close();
      } catch (RuntimeException e) {
      }
    }
  }

  @Override
  public Response send(ServerAddress addr, String type, Object data) throws IOException {

    DatagramSocket sock = new DatagramSocket();
    sock.setSoTimeout((int) responseTimeout);
    RemoteEvent evt = new RemoteEvent(null, type, data).setNode(consumer.getNode()).setSync();
    evt.setUnicastAddress(addr);
    UDPUnicastAddress inet = (UDPUnicastAddress) addr;

    try {
      return (Response) doSend(inet.getInetAddress(), inet.getPort(), sock, McastUtil.toBytes(evt, bufSize()), true, type);
    } catch (TimeoutException e) {
      return new Response(evt.getId(), e).setStatusSuspect();
    } finally {
      try {
        sock.close();
      } catch (RuntimeException e) {
      }
    }
  }

  @Override
  public RespList send(List<ServerAddress> addresses, final String type, Object data) throws IOException, InterruptedException {

    final BlockingCompletionQueue<Response> queue = new BlockingCompletionQueue<Response>(addresses.size());

    final RemoteEvent evt = new RemoteEvent(null, type, data).setNode(consumer.getNode()).setSync();
    evt.setUnicastAddress(addr);
    final byte[] bytes = McastUtil.toBytes(evt, bufSize());

    for (int i = 0; i < addresses.size(); i++) {

      final UDPUnicastAddress addr = (UDPUnicastAddress) addresses.get(i);

      senders.execute(new Runnable() {

        @Override
        public void run() {
          DatagramSocket sock = null;
          try {
            sock = new DatagramSocket();
            sock.setSoTimeout((int) responseTimeout);
            Response resp = (Response) doSend(addr.getInetAddress(), addr.getPort(), sock, bytes, true, type);
            queue.add(resp);
          } catch (TimeoutException e) {
            log.warning("Response from %s not received in timely manner", addr);

            try {
              queue.add(new Response(evt.getId(), e).setStatusSuspect());
            } catch (IllegalStateException ise) {
              log.info("Could not add response to queue", ise, new Object[] {});
            }
          } catch (IOException e) {
            log.error("IO problem sending remote event to " + addr, e);
          } finally {
            if (sock != null)
              sock.close();
          }
        }
      });
    }

    return new RespList(queue.await(responseTimeout));

  }

  @Override
  public ServerAddress getAddress() throws IllegalStateException {
    Assertions.illegalState(addr == null, "The address of this instance is not yet available");
    return addr;
  }

  @Override
  protected void handlePacketSizeToShort(DatagramPacket pack) {
    log.error("Buffer size to short; set to: %s. This size is not enough to receive some incoming packets", bufSize());
  }

  @Override
  protected int bufSize() {
    return super.bufSize();
  }

  @Override
  protected void handle(final DatagramPacket pack, final DatagramSocket sock) {
    handlers.execute(new Runnable() {
      @Override
      public void run() {
        doHandle(pack, sock);
      }
    });
  }

  private void doHandle(DatagramPacket pack, DatagramSocket sock) {
    try {
      Object o = McastUtil.fromDatagram(pack);

      if (o instanceof RemoteEvent) {
        RemoteEvent evt = (RemoteEvent) o;

        if (evt.isSync()) {
          InetAddress addr = pack.getAddress();
          int port = pack.getPort();

          // ------------------------------------------------------------------

          if (consumer.hasSyncListener(evt.getType())) {
            Object response = consumer.onSyncEvent(evt);

            try {
              doSend(addr, port, sock, McastUtil.toBytes(new Response(evt.getId(), response), bufSize()), false, evt.getType());
            } catch (TimeoutException e) {
              // will not occur - see doSend()
            }

            // ------------------------------------------------------------------

          } else {
            try {
              doSend(addr, port, sock, McastUtil.toBytes(new Response(evt.getId(), null).setNone(), bufSize()), false, evt.getType());
            } catch (TimeoutException e) {
              // will not occur - see doSend()
            }
          }
        } else {
          consumer.onAsyncEvent(evt);
        }
      } else {
        log.error("Object not a remote event: %s", o);
      }
    } catch (IOException e) {
      log.error("IO Problem handling remote event", e);
    } catch (ClassNotFoundException e) {
      log.error(e);
    }
  }

  private Object doSend(InetAddress addr, int port, DatagramSocket sock, byte[] bytes, boolean synchro, String type) throws IOException,
      TimeoutException {
    if (bytes.length > bufSize()) {
      throw new IOException("Size of data larger than buffer size; increase this instance's buffer size through the setBufsize() method");
    }

    log.debug("doSend() : %s, event type: %s", addr, type);
    DatagramPacket pack = new DatagramPacket(bytes, 0, bytes.length, addr, port);

    sock.send(pack);

    if (synchro) {
      bytes = new byte[bufSize()];
      pack = new DatagramPacket(bytes, bytes.length);

      try {
        sock.receive(pack);
      } catch (SocketTimeoutException e) {
        throw new TimeoutException();
      }

      try {
        return McastUtil.fromDatagram(pack);
      } catch (ClassNotFoundException e) {
        throw new IOException("Could not deserialize object", e);
      }
    } else {
      return null;
    }
  }
}
