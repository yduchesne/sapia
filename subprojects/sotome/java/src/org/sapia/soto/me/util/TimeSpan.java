/**
 * 
 */
package org.sapia.soto.me.util;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class TimeSpan {

  private long _startTime;
  private long _endTime;
  
  public TimeSpan() {
    reset();
  }
  
  public void reset() {
    _startTime = 0;
    _endTime = 0;
  }
  
  public TimeSpan start() {
    _startTime = System.currentTimeMillis();
    return this;
  }
  
  public TimeSpan stop() {
    _endTime = System.currentTimeMillis();
    return this;
  }
  
  public long getDeltaMillis() {
    return _endTime - _startTime;
  }
}
