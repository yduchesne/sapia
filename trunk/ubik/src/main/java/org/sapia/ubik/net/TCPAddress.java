package org.sapia.ubik.net;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


/**
 * This class models a TCP "server address". This class encapsulates host and port information.
 *
 * @author Yanick Duchesne
 */
public class TCPAddress implements java.io.Externalizable, ServerAddress {
  static final long          serialVersionUID = 1L;
  public static final String TRANSPORT_TYPE = "tcp/socket";
  private String             host;
  private int                port;
  protected String           transportType = TRANSPORT_TYPE;

  /** Do not call; used for externalization only. */
  public TCPAddress() {
  }

  /**
   * Creates an instance of this class with the given host and port
   * information.
   *
   * @param host the address of a host.
   * @param port the port of the server running on the given host.
   *
   */
  public TCPAddress(String host, int port) {
    this.host       = host;
    this.port       = port;
  }

  /**
   * Overrides equals; two host identifiers are equal if they have the
   * same host and port.
   *
   * @param obj the instance with which to perform the comparison.
   */
  public boolean equals(Object obj) {
    TCPAddress other;

    try {
      other = (TCPAddress) obj;

      return (other.port == port) && other.host.equals(host);
    } catch (ClassCastException e) {
      return false;
    }
  }

  /**
   * Returns the address of the host to which this instance corresponds.
   *
   * @return this instance's host address.
   */
  public String getHost() {
    return host;
  }

  /**
   * Returns the port of the server to which this instance corresponds.
   *
   * @return a server port..
   */
  public int getPort() {
    return port;
  }

  /**
   * This method returns a hash code based on this instances host and port.
   *
   * @return a hash code, as an <code>int</code>.
   */
  public int hashCode() {
    return host.hashCode() * 31 ^ port * 31;
  }

  /**
   * @see java.io.Externalizable#readExternal(ObjectInput)
   */
  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException {
    host            = in.readUTF();
    port            = in.readInt();
    transportType   = in.readUTF();
  }

  /**
   * @see java.io.Externalizable#writeExternal(ObjectOutput)
   */
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeUTF(host);
    out.writeInt(port);
    out.writeUTF(transportType);
  }

  /**
   * @see org.sapia.ubik.net.ServerAddress#getTransportType()
   */
  public String getTransportType() {
    return transportType;
  }

  /**
   * Returns a string representation of this instance.
   *
   * @return a <code>String</code>.
   */
  public String toString() {
    return "[ host=" + host + ", port=" + port + ", type=" + transportType + " ]";
  }
}
