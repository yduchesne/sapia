package org.sapia.soto.util.concurrent;

import java.sql.Timestamp;
import java.util.Comparator;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class define a data structure used in this routing rule monitor. A monitored rule
 * is simply a routing rule coupled with an invalidity instant.
 *  
 * @author Jean-Cédric Desrochers
 */
public class MonitoredObject implements Comparator {
  
  /** Defines a comparator instance for sorts. */
  public static final Comparator OBJECT_COMPARATOR = new MonitoredObject(null);
  
  /** The perishable object that is monitored. */
  private PerishableObject _object;
  
  /** The instant at which this monitored object was created. */
  private long _creationInstant;
  
  /** The expiration instant of this monitored object. */
  private long _expirationInstant;
  
  /**
   * Creates a new MonitoredObject instance.
   * 
   * @param anObject The perishable object that is monitored.
   */
  public MonitoredObject(PerishableObject anObject) {
    _object = anObject;
    _creationInstant = System.currentTimeMillis();
    calculateExpirationInstant();
  }

  /**
   * Returns the creation instant of this monitored object.
   * 
   * @return The creation instant of this monitored object.
   */
  public long getCreationInstant() {
    return _creationInstant;
  }

  /**
   * Returns the perishable object monitored.
   * 
   * @return The perishable object monitored.
   */
  public PerishableObject getObject() {
    return _object;
  }
  
  /**
   * Returns the expiration instant of this monitored object.
   * 
   * @return The expiration instant of this monitored object.
   */
  public long getExpirationInstant() {
    return _expirationInstant;
  }
  
  /**
   * Resets the creation instant of this monitored object and recalculate
   * the expiration instance. Used for renewal of perishable objects.
   */
  public void reset() {
    _creationInstant = System.currentTimeMillis();
    calculateExpirationInstant();
  }
  
  /**
   * Calculates and return the expiration instant of this monitored object.
   *  
   * @return The calculated expiration instant.
   */
  public long calculateExpirationInstant() {
    if (_object != null) {
      if (_object.getTimeToLiveMillis() == PerishableObject.INFINITE_TIME_TO_LIVE) {
        _expirationInstant = Long.MAX_VALUE;
      } else {
        _expirationInstant = _creationInstant + _object.getTimeToLiveMillis();
      }
    }

    return _expirationInstant;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return new ToStringBuilder(this).
        append("object", _object).
        append("creationInstant", new Timestamp(_creationInstant)).
        append("expirationInstant", new Timestamp(_expirationInstant)).
        toString();
  }
  
  /* (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  public int compare(Object anObject, Object anotherObject) {
    long delta = ((MonitoredObject) anObject).calculateExpirationInstant() -
                 ((MonitoredObject) anotherObject).calculateExpirationInstant();

    if (delta > 0) {
      return 1;
    } else if (delta < 0) {
      return -1;
    } else {
      return 0;
    }
  }
}