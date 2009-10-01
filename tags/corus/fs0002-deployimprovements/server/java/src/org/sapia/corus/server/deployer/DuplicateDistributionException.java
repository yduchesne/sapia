package org.sapia.corus.server.deployer;

import org.sapia.corus.exceptions.LogicException;


/**
 * Signals that a distribution with a given name and version has already been deployed.
 * 
 * @author Yanick Duchesne
 */
public class DuplicateDistributionException extends LogicException {
  /**
   * Constructor for DuplicateDistributionException.
   * @param msg
   */
  public DuplicateDistributionException(String msg) {
    super(msg);
  }

  /**
   * Constructor for DuplicateDistributionException.
   * @param msg
   * @param err
   */
  public DuplicateDistributionException(String msg, Throwable err) {
    super(msg, err);
  }

  /**
   * Constructor for DuplicateDistributionException.
   * @param err
   */
  public DuplicateDistributionException(Throwable err) {
    super(err);
  }
}
