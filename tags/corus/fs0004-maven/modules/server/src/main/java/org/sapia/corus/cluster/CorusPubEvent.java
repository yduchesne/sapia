package org.sapia.corus.cluster;

import java.io.Serializable;

import org.sapia.ubik.net.ServerAddress;


/**
 * @author Yanick Duchesne
 */
public class CorusPubEvent implements Serializable {
  private boolean       _new;
  private ServerAddress _origin;

  /**
   * Constructor for CorusPubEvent.
   * @param arg0
   * @param arg1
   */
  public CorusPubEvent(boolean isNew, ServerAddress origin) {
    _new    = isNew;
    _origin = origin;
  }

  public boolean isNew() {
    return _new;
  }

  public ServerAddress getOrigin() {
    return _origin;
  }
}
