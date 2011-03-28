package org.sapia.soto.scheduler;

/**
 * Implement a "minutes" field.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class MinuteField extends CronField{
  
  static final long serialVersionUID = 1L;  
  
  public static int MIN_MINUTE = 0;
  public static int MAX_MINUTE = 59;
  
  public MinuteField(){
    super("minutes");
  }
   
  /**
   * @see org.sapia.soto.scheduler.CronCalendarField#isValid(int)
   */
  public boolean isValid(int value) {
    return value >= MIN_MINUTE && value <= MAX_MINUTE;
  }
  
  public String toString(){
    if(isDefined()){
      return ""+getValue();
    }
    return "*";
  }

}
