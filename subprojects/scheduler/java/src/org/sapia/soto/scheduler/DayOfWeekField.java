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
public class DayOfWeekField extends CronField{
  
  static final long serialVersionUID = 1L;  
  
  private static final String[] DAY_NAMES = 
    new String[]{
      "SUN", 
      "MON", 
      "TUE", 
      "WED", 
      "THU", 
      "FRI", 
      "SAT"
    };
  
  private static int MIN_DAY = 1;
  private static int MAX_DAY = 7;  
  
  public DayOfWeekField(){
    super("day-of-week");
  }
  
  /**
   * @see org.sapia.soto.scheduler.CronField#setValue(int)
   */
  public void setValue(int value) {
    super.setValue(value);
    descriptor().unsetValue(FIELD_DAY);    
  }
  
  /**
   * @see org.sapia.soto.scheduler.CronCalendarField#isValid(int)
   */
  public boolean isValid(int value) {
    return value >= MIN_DAY && value <= MAX_DAY;
  }
  
  public String toString(){
    if(isDefined()){
      return DAY_NAMES[getValue()-1];      
    }
    else{
      if(descriptor().isDefined(FIELD_DAY))
        return "?";
      else return "*";
    }
  }

}
