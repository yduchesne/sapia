package org.sapia.ubik.mcast.control;

import java.util.List;
import java.util.Set;

/**
 * Specifies the behavior of control messages that can be split.
 * @author yduchesne
 *
 */
public interface SplittableMessage {
  
  /**
   * @return the {@link Set} of identifiers corresponding to the nodes that are targeted by
   * the message.
   */
  public Set<String> getTargetedNodes();
  
  /**
   * 
   * @param batchSize the size of the batches into which this message should be split.
   * @return the {@link List} of {@link SplittableMessage}s resulting from the split.
   */
  public List<SplittableMessage> split(int batchSize);

}
