package org.sapia.ubik.rmi.server.transport.mina;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;

import org.sapia.ubik.net.Connection;
import org.sapia.ubik.net.SocketConnectionFactory;

/**
 * Implements a factory of {@link MinaRmiClientConnection} instances.
 */
public class MinaRmiConnectionFactory extends SocketConnectionFactory {

  private int bufsize;

  public MinaRmiConnectionFactory(int bufsize) {
    super(MinaTransportProvider.TRANSPORT_TYPE);
    this.bufsize = bufsize;
  }

  /**
   * @see org.sapia.ubik.net.SocketConnectionFactory#newConnection(Socket)
   */
  public Connection newConnection(Socket sock) throws IOException {
    return new MinaRmiClientConnection(sock, bufsize);
  }

  /**
   * @see org.sapia.ubik.net.SocketConnectionFactory#newConnection(String, int)
   */
  public Connection newConnection(String host, int port) throws IOException {
    try {
      return new MinaRmiClientConnection(new Socket(host, port), bufsize);
    } catch (ConnectException e) {
      throw new RemoteException(String.format("Could not connect to %s:%s", host, port));
    } catch (SocketException e) {
      throw new RemoteException(String.format("Could not connect to %s:%s", host, port));
    }
  }
}
