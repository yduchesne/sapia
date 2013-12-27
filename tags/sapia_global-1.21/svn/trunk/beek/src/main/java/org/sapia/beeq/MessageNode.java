package org.sapia.beeq;

/**
 * This interface specifies the behavior of message processing nodes: messages are 
 * created an "put" into a node for further processing.
 * <p>
 * More concretely speaking, this interface was defined with distributed, asynchronous message
 * processing in mind, where each node instance is distributed across the network and is expected
 * to manage its messages in an independent manner.  
 * 
 * @author yduchesne
 *
 */
public interface MessageNode {
  
  /**
   * This factory method creates message instances that are not yet
   * associated to a queue.
   * 
   * @param clientGeneratedId an optional, client-generated identifier
   * to attach to the message that will be created.
   * @return a newly created {@link Message}.
   */
  public Message create(String clientGeneratedId);
  
  /**
   * 
   * @param msg a {@link Message} a put into this node's internal queue.
   * @return the {@link MessageID} that uniquely identifies the message that was queued.
   */
  public MessageID put(Message msg);
  
  /**
   * @param id a {@link MessageID}
   * @return the {@link Message} corresponding to the given ID.
   */
  public Message get(MessageID id);

}
