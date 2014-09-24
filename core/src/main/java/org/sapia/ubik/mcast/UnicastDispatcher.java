package org.sapia.ubik.mcast;

import java.io.IOException;
import java.util.List;

import org.sapia.ubik.net.ServerAddress;

/**
 * Implementations of this interface dispatch objects over the wire in a
 * point-to-point fashion. It is important to note that implementations are
 * expected to behave in a peer-to-peer fashion - they are at once client and
 * server (for their siblings).
 * 
 * @author Yanick Duchesne
 */
public interface UnicastDispatcher {
  /**
   * Dispatches the given data to the node whose address is given.
   * 
   * @param addr
   *          a {@link ServerAddress} that corresponds to the destination node
   *          for the data passed in.
   * @param type
   *          the logical type of the data that is sent - allows the receiver to
   *          perform logic according to the "type".
   * @param data
   *          the {@link Object} to send.
   * @throws IOException
   *           if there was an IO issue performing this operation.
   */
  public void dispatch(ServerAddress addr, String type, Object data) throws IOException;

  /**
   * Sends the given data to the node whose address is given, returning the
   * corresponding response - received from the destination.
   * 
   * @param addr
   *          a {@link ServerAddress} that corresponds to the destination node
   *          for the data passed in.
   * @param type
   *          the logical type of the data that is sent - allows the receiver to
   *          perform logic according to the "type".
   * @param data
   *          the {@link Object} to send.
   * @return a {@link Response}.
   * @throws IOException
   *           if there was an IO issue performing this operation.
   * @throws InterruptedException
   *           if the calling thread is interrupted while internally waiting for
   *           the responses.
   */
  public Response send(ServerAddress addr, String type, Object data) throws IOException;

  /**
   * Sends the given data to the list of destinations specified, and returning
   * the responses received from each destination.
   * 
   * @param addresses
   *          a {@link List} of {@link ServerAddress} instances.
   * @param type
   *          the logical type of the data that is sent - allows the receiver to
   *          perform logic according to the "type".
   * @param data
   *          the {@link Object} to send.
   * 
   * @return a {@link RespList}.
   * @throws IOException
   *           if there was an IO issue performing this operation.
   * @throws InterruptedException
   *           if the calling thread is interrupted while internally waiting for
   *           the responses.
   */
  public RespList send(java.util.List<ServerAddress> addresses, String type, Object data) throws IOException, InterruptedException;

  /**
   * Starts this instance - should be called prior to using this instance.
   */
  public void start();

  /**
   * Closes this instance - which should not be used thereafter.
   */
  public void close();

  /**
   * Returns the address of this instance.
   * 
   * @return a {@link ServerAddress}.
   * 
   * @throws IllegalStateException
   *           if the address of this instance is not yet available. This can be
   *           the case if the {@link #start()} method has not yet been called;
   *           therefore, always call {@link #start()} before calling this
   *           method.
   * 
   * @see #start()
   * @throws IllegalStateException
   *           if this instance's address cannot be obtained.
   */
  public ServerAddress getAddress() throws IllegalStateException;
}
