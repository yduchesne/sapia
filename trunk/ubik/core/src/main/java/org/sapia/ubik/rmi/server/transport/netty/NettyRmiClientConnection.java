package org.sapia.ubik.rmi.server.transport.netty;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;

import org.javasimon.Split;
import org.javasimon.Stopwatch;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.sapia.ubik.log.Category;
import org.sapia.ubik.log.Log;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.net.netty.NettyAddress;
import org.sapia.ubik.rmi.server.VmId;
import org.sapia.ubik.rmi.server.stats.Stats;
import org.sapia.ubik.rmi.server.transport.MarshalOutputStream;
import org.sapia.ubik.rmi.server.transport.MarshalStreamFactory;
import org.sapia.ubik.rmi.server.transport.RmiConnection;
import org.sapia.ubik.rmi.server.transport.RmiObjectOutput;

/**
 * A connection over a Socket the connection uses the
 * {@link MarshalOutputStream} class to serialize outgoing objects.
 */
public class NettyRmiClientConnection implements RmiConnection {

  private static Stopwatch serializationTime = Stats.createStopwatch(NettyRmiClientConnection.class, "SerializationDuration",
      "Time required to serialize an object");

  private static Stopwatch sendTime = Stats.createStopwatch(NettyRmiClientConnection.class, "SendDuration",
      "Time required to send an object over the network");

  private Socket sock;
  private int bufsize;
  private ServerAddress address;
  private ChannelBuffer byteBuffer;
  private ObjectOutputStream oos;
  private ObjectInputStream ois;
  private Category log = Log.createCategory(getClass());

  public NettyRmiClientConnection(Socket sock, int bufsize) throws IOException {
    this.sock = sock;
    this.address = new NettyAddress(sock.getInetAddress().getHostAddress(), sock.getPort());
    this.bufsize = bufsize;
    this.byteBuffer = ChannelBuffers.dynamicBuffer(bufsize);
  }

  /**
   * @see org.sapia.ubik.net.Connection#getServerAddress()
   */
  public ServerAddress getServerAddress() {
    return address;
  }

  /**
   * @see org.sapia.ubik.rmi.server.transport.RmiConnection#send(Object, VmId,
   *      String)
   */
  public void send(Object o, VmId vmId, String transportType) throws IOException, RemoteException {
    byteBuffer.clear();
    if (oos == null) {
      oos = MarshalStreamFactory.createOutputStream(new ChannelBufferOutputStream(byteBuffer));
    }
    ((RmiObjectOutput) oos).setUp(vmId, transportType);
    Split split = serializationTime.start();
    oos.writeObject(o);
    oos.flush();
    split.stop();

    try {
      doSend();
    } catch (java.net.SocketException e) {
      throw new RemoteException("Communication with server interrupted; server probably disappeared", e);
    }
  }

  /**
   * @see org.sapia.ubik.net.Connection#send(java.lang.Object)
   */
  public void send(Object o) throws IOException, RemoteException {
    byteBuffer.clear();
    if (oos == null) {
      oos = MarshalStreamFactory.createOutputStream(new ChannelBufferOutputStream(byteBuffer));
    }
    Split split = serializationTime.start();
    oos.writeObject(o);
    oos.flush();
    split.stop();

    try {
      doSend();
    } catch (java.net.SocketException e) {
      throw new RemoteException("Communication with server interrupted; server probably disappeared", e);
    }
  }

  /**
   * @see org.sapia.ubik.net.Connection#receive()
   */
  public Object receive() throws IOException, ClassNotFoundException, RemoteException {
    try {
      DataInputStream dis = new DataInputStream(sock.getInputStream());
      log.debug("Receiving response of %s bytes", dis.readInt());
      if (ois == null) {
        ois = MarshalStreamFactory.createInputStream(new BufferedInputStream(sock.getInputStream(), bufsize));
      }
      return ois.readObject();
    } catch (EOFException e) {
      throw new RemoteException("Communication with server interrupted; server probably disappeared", e);
    } catch (SocketException e) {
      throw new RemoteException("Connection could not be opened; server is probably down", e);
    }
  }

  /**
   * @see org.sapia.ubik.net.Connection#close()
   */
  public void close() {
    try {
      sock.close();
    } catch (Throwable t) {
      // noop
    }
  }

  private void doSend() throws IOException {
    Split split = sendTime.start();
    OutputStream sos = new BufferedOutputStream(sock.getOutputStream(), bufsize);
    DataOutputStream dos = new DataOutputStream(sos);
    byte[] toWrite = new byte[byteBuffer.writerIndex()];
    dos.writeInt(toWrite.length);
    byteBuffer.readBytes(toWrite);
    dos.write(toWrite);
    dos.flush();
    split.stop();
  }
}
