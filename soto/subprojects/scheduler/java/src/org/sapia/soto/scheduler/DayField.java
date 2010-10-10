package org.sapia.soto.scheduler;

/**
 * Implements a day field.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DayField extends CronField{
  
  static final long serialVersionUID = 1L;
  
  private static int MIN_DAY = 1;
  private static int MAX_DAY = 31;  
  
  public DayField(){
    super("day-of-month");
  }
  
  /**
   * @see org.sapia.soto.scheduler.CronCalendarField#isValid(int)
   */
  public boolean isValid(int value) {
    return value >= MIN_DAY && value <= MAX_DAY;
  }
  
  /**
   * @see org.sapia.soto.scheduler.CronField#setValue(int)
   */
  public void setValue(int value) {
    super.setValue(value);
    descriptor().unsetValue(FIELD_DAY_OF_WEEK);
  }
  
  public String toString(){
    if(isDefined()){
      return ""+getValue();      
    }
    else{
      return "?";        
    }
  }

}
