package org.sapia.soto.scheduler;

import java.util.Calendar;

/**
 * A field that holds a year.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class YearField extends CronField{
  
  static final long serialVersionUID = 1L;  
  
  public YearField(){
    super("year");
  }
  
  /**
   * @see org.sapia.soto.scheduler.CronCalendarField#isValid(int)
   */
  public boolean isValid(int value) {
    return value > 0;
  }
  
  /**
   * @see org.sapia.soto.scheduler.CronCalendarField#setNextValue(CronField, java.util.Calendar)
   */
  protected void setNextValue(CronField previous, Calendar toSet) {
    if(isDefined()){
      toSet.set(Calendar.YEAR, getValue());      
    }
  }
  
  public String toString(){
    if(isDefined()){
      return ""+getValue();
    }
    return "*";
  }

}
