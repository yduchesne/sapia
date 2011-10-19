package org.sapia.soto.scheduler;

/**
 * Implements an hour field.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class HourField extends CronField{
  
  static final long serialVersionUID = 1L;
  
  public static final int MIN_HOUR = 0;
  public static final int MAX_HOUR = 24;
  
  public HourField(){
    super("hour");
  }
  
  /**
   * @see org.sapia.soto.scheduler.CronCalendarField#isValid(int)
   */
  public boolean isValid(int value) {
    return value >= MIN_HOUR && value <= MAX_HOUR;
  }
  
  public String toString(){
    if(isDefined()){
      return ""+getValue();
    }
    else{
      return "*";
    }
  }

}
