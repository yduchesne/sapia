package org.sapia.beeq.sender;

import org.sapia.beeq.Message;

public interface Sender {

  /**
   * @param message a {@link Message} to send.
   * @return expected ack or response object, or <code>null</code> if none
   * is returned or expected.
   * @throws Exception if the message could not be send.
   */
  public void send(Message message) throws Exception;
  
  /**
   * @param msg a {@link Message}
   * @return <code>true</code> if this instance is capable/in charge of
   * sending the given message.
   */
  public boolean accepts(Message msg);
  
}
