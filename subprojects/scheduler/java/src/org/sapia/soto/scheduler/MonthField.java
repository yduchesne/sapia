package org.sapia.soto.scheduler;

/**
 * A field that holds a month.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class MonthField extends CronField{
  
  static final long serialVersionUID = 1L;  
  
  public static final String[] MONTH_NAMES = 
    new String[]{
      "january",
      "february",
      "march",
      "april",
      "may",
      "june",
      "july",
      "august",
      "september",
      "october",
      "november",
      "december"
    };
  
  public int MIN_MONTH = 1;
  public int MAX_MONTH = 12;  
  
  public MonthField(){
    super("month");
  }
  
  /**
   * @see org.sapia.soto.scheduler.CronCalendarField#isValid(int)
   */
  public boolean isValid(int value) {
    return value >= MIN_MONTH && value <= MAX_MONTH;
  }
  
  public String toString(){
    if(isDefined()){
      return MONTH_NAMES[getValue()]; 
    }
    else{
      return "*";
    }
  }
}
