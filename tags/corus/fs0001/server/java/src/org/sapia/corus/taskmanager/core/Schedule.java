package org.sapia.corus.taskmanager.core;

/**
 * Encapsulates scheduling info.
 * 
 * @author yduchesne
 *
 */
public class Schedule {
  
  public enum IntervalType{
    FIXED_RATE,
    FIXED_DELAY;
  }
  
  private IntervalType _type = IntervalType.FIXED_DELAY;
  private long _delay; 
  private long _interval = 10;
  private int _maxExec = 1;

  public Schedule(long interval){
    _interval = interval;
  }
  
  public Schedule setDelay(long delay){
    _delay = delay;
    return this;
  }
  
  public long getDelay(){
    return _delay;
  }
  
  public Schedule setType(IntervalType type){
    _type = type;
    return this;
  }
  
  public IntervalType getType(){
    return _type;
  }
  
  public long getInterval(){
    return _interval;
  }
  
  public Schedule setMaxExec(int max){
    _maxExec = max;
    return this;
  }
  
  public int getMaxExec(){
    return _maxExec;
  }
  
  /**
   * @return a {@link Schedule} corresponding to a one-time,
   * immediate execution.
   */
  public static Schedule oneShot(){
    return new Schedule(1).setMaxExec(1); 
  }
  
  /**
   * Returns a schedule corresponding to a recurring execution.
   * 
   * @param interval the interval at which any corresponding task
   * shall be executed.
   * 
   * @return a new {@link Schedule}
   */
  public static Schedule recurring(long interval){
    return new Schedule(interval);
  }
  
}
