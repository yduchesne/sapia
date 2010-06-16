package org.sapia.corus.admin.cli;

import org.sapia.console.Context;
import org.sapia.corus.admin.facade.CorusConnector;


/**
 * @author Yanick Duchesne
 */
public class CliContextImpl extends Context implements CliContext {
  private CorusConnector _corus;

  public CliContextImpl(CorusConnector corus) {
    _corus = corus;
  }

  public CorusConnector getCorus() {
    return _corus;
  }
  
}
