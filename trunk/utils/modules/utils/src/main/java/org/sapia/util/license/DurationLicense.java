package org.sapia.util.license;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * This class implements a <code>License</code> that starts at a given date and lasts for
 * a given number of days.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DurationLicense implements License{
  
  static final long serialVersionUID = 1L;
  
  private Date _startDate;
  private int  _duration;
  private Date _endDate;
  private boolean _activated;
  
  public DurationLicense(int durationDays){
    if(durationDays < 1){
      throw new IllegalArgumentException("Duration should be equal to or greater than 1");
    }
    _duration = durationDays;

  }
  
  /**
   * @return the <code>Date</code> at which this license "starts".
   */
  public Date getStartDate(){
    return _startDate;
  }
  
  /**
   * @return the number of days for which this license should last.
   */
  public int getDurationDays(){
    return _duration;
  }
  
  /**
   * Returns the date at which this license terminates.
   * @return a <code>Date</code>.
   */
  public Date getEndDate(){
    return _endDate;
  }
  
  /**
   * @see org.sapia.util.license.License#isValid(java.lang.Object)
   */
  public boolean isValid(Object context) {
    Date currentDate =  new Date();
    if(_startDate == null){
      activate(null);
    }
    return currentDate.before(_endDate);
  }
  
  /**
   * @see org.sapia.util.license.License#activate(java.lang.Object)
   */
  public void activate(Object context) {
    if(!_activated){
      Date currentDate =  new Date();      
      init(currentDate);
      _activated = true;
    }
  }
  
  /**
   * Returns if this license is valid or not, taking the given date as
   * a base of calculation (validity is computed in the following way:
   * currentDate < creation date + duration).
   * 
   * @param fromDate
   * @return <code>true</code> if this license is valid.
   */
  public boolean isValid(Date currentDate){
    if(_startDate == null){
      activate(null);
    }    
    return currentDate.before(_endDate);
  }
  
  private Date computeEndDate(){
    return computeEndDate(_startDate, _duration); 
  }
  
  private synchronized void init(Date currentDate){
    _startDate = new Date();
    _endDate = computeEndDate(_startDate, _duration);
  }
  
  static Date computeEndDate(Date from, int durationDays){
    Calendar cal = Calendar.getInstance();
    cal.setTime(from);
    int i = 1;
    int day = cal.get(Calendar.DAY_OF_YEAR);
    for(; i <= durationDays; i++, day++){
      if(day > cal.getActualMaximum(Calendar.DAY_OF_YEAR)){
        cal.roll(Calendar.YEAR, true);
        day = 1;
      }
      
      cal.roll(Calendar.DAY_OF_YEAR, true);
    }
    return cal.getTime();
  }
  
  /**
   * @see org.sapia.util.license.License#getBytes()
   */
  public byte[] getBytes() throws IOException {
    return Integer.toString(_duration).getBytes();
  }
  
  
}
