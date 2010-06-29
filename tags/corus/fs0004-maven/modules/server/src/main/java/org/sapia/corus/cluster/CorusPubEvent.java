package org.sapia.corus.cluster;

import java.io.Serializable;

import org.sapia.ubik.net.ServerAddress;


/**
 * @author Yanick Duchesne
 */
public class CorusPubEvent implements Serializable {
  
  static final long serialVersionUID = 1L;
  
  private boolean       _new;
  private ServerAddress _origin;

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
