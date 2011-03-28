package org.sapia.soto.scheduler;

import java.io.Serializable;

/**
 * An instance of this class can be used to conveniently hold cron-related
 * information that can be programmatically modified. It is meant to simplify
 * the creation of <code>CronTrigger</code> instances in an object-oriented
 * manner. An instance of this class can be used as a model (as in MVC). 
 * <p>
 * An instance of this class encapsulates so-called "cron fields" (instances
 * of <code>CronField</code>). These fields correspond to:
 * <p>
 * <ul>
 *   <li>The seconds in the minute.
 *   <li>The minutes in the hour.
 *   <li>The hours in the day.
 *   <li>The day of the month.
 *   <li>The month in the year.   
 *   <li>The day of the week.
 *   <li>The year.
 * </ul>
 * <p>
 * The above fields can have the following values, respectively:
 * <ul>
 *   <li>0 to 59.
 *   <li>0 to 59.
 *   <li>1 to 24.
 *   <li>1 to 31.
 *   <li>1 to 12.
 *   <li>1 to 7 (With Sunday corresponding to 1).
 *   <li>Any given year.
 * </ul>
 * <p>
 * Note that all the fields can have an "undefined" value, which corresponds
 * to "any"/"every". For example, to represent "at 13:00:00 every day", we would
 * specify the hours, the minutes and the seconds, and leave the other fields 
 * undefined.
 * <p>
 * Usage:
 * <pre>
 * CronDescriptor desc = new CronDescriptor();
 * desc.setField(Fields.FIELD_HOUR,    13);
 * desc.setField(Fields.FIELD_MINUTES, 0);
 * desc.setField(Fields.FIELD_SECONDS, 0);
 * 
 * // displays detailed info
 * System.out.println(desc.toString());
 * 
 * // creates a CronTrigger (see Quartz API).
 * 
 * CronTrigger trig = new CronTrigger()
 * trig.setCronExpression(desc.getCronExpression());
 *  
 * </pre> 
 * <p>
 * Note that normally, an instance of this class should have at least one of its fields
 * defined to make any sense.
 * 
 * @see Fields
 * @see org.sapia.soto.scheduler.CronField
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CronDescriptor implements Serializable, Fields{
  
  static final long serialVersionUID = 1L;
  
  private CronField[] _fields = new CronField[FIELD_YEAR+1];
  
  public CronDescriptor(){
    _fields[FIELD_SECONDS]     = new SecondField();
    _fields[FIELD_MINUTES]     = new MinuteField();    
    _fields[FIELD_HOUR]        = new HourField();
    _fields[FIELD_DAY]         = new DayField();    
    _fields[FIELD_MONTH]       = new MonthField();
    _fields[FIELD_DAY_OF_WEEK] = new DayOfWeekField();    
    _fields[FIELD_YEAR]        = new YearField();
    for(int i = 0; i < _fields.length; i++){
      _fields[i].setDescriptor(this);
    }
  }
  
  /**
   * Sets the value of the given field.
   * @param field a field index.
   * @param value a value.
   * 
   * @see #UNDEFINED
   */
  public void setValue(int field, int value){
    checkFieldRange(field);
     _fields[field].setValue(value);
  }
  
  /**
   * @param field sets the value of the given field to
   * <code>UNDEFINED</code>.
   * 
   * @see #UNDEFINED
   */
  public void unsetValue(int field){
    checkFieldRange(field);
    _fields[field].unsetValue();
  }
  
  /**
   * @param field a field index.
   * @return the value for the given field.
   * @see #UNDEFINED
   */
  public int getValue(int field){
    checkFieldRange(field);
    return _fields[field].getValue();
  }
  
  /**
   * @param field a field index.
   * @return <code>true</code> if the given field has been defined.
   */
  public boolean isDefined(int field){
    checkFieldRange(field);
    return _fields[field].isDefined();    
  }
  
  /**
   * @return <code>true</code> if at least one of the fields that
   * this instance holds is defined.
   */
  public boolean isDefined(){
    for(int i = 0; i < _fields.length; i++){
      if(_fields[i].isDefined()){
        return true;
      }
    }
    return false;
  }
  
  /**
   * @return the cron-compliant command string corresponding to this instance's
   * state.
   */
  public String getCronExpression(){
    StringBuffer buf = new StringBuffer();
    for(int i = 0; i < _fields.length; i++){
      buf.append(_fields[i].toString());
      if(i < _fields.length - 1){
        buf.append(" ");
      }
    }
    return buf.toString();    
  }
  
  /**
   * @return the seconds that this instance holds,
   * or the <code>Field.UNDEFINED</code> constant if 
   * no seconds have been specified.
   */
  public int getSeconds(){
    return _fields[FIELD_SECONDS].getValue();
  }
  
  /**
   * @param secs some seconds.
   */
  public void setSeconds(int secs){
    _fields[FIELD_SECONDS].setValue(secs);    
  }
  
  /**
   * @return the minutes that this instance holds,
   * or the <code>Field.UNDEFINED</code> constant if 
   * no minutes have been specified.
   */
  public int getMinutes(){
    return _fields[FIELD_MINUTES].getValue();
  }
  
  /**
   * @param minutes some minutes.
   */
  public void setMinutes(int minutes){
    _fields[FIELD_MINUTES].setValue(minutes);    
  }  
  
  /**
   * @return the hour that this instance holds,
   * or the <code>Field.UNDEFINED</code> constant if 
   * no hour has been specified.
   */
  public int getHour(){
    return _fields[FIELD_HOUR].getValue();
  }
  
  /**
   * @param hour some hour.
   */
  public void setHour(int hour){
    _fields[FIELD_HOUR].setValue(hour);    
  }  
  
  /**
   * @return the day of the month that this instance holds,
   * or the <code>Field.UNDEFINED</code> constant if 
   * no day of month has been specified.
   */
  public int getDay(){
    return _fields[FIELD_DAY].getValue();
  }
  
  /**
   * @param day some day of the month.
   */
  public void setDay(int day){
    _fields[FIELD_DAY].setValue(day);    
  }
  
  /**
   * @return the day of the month that this instance holds,
   * or the <code>Field.UNDEFINED</code> constant if 
   * no day of week has been specified.
   */
  public int getDayOfWeek(){
    return _fields[FIELD_DAY_OF_WEEK].getValue();
  }
  
  /**
   * @param dayOfWeek some day of the week.
   */
  public void setDayOfWeek(int dayOfWeek){
    _fields[FIELD_DAY_OF_WEEK].setValue(dayOfWeek);    
  }
  
  /**
   * @return the month of the year that this instance holds,
   * or the <code>Field.UNDEFINED</code> constant if 
   * no mpnth has been specified.
   */
  public int getMonth(){
    return _fields[FIELD_MONTH].getValue();
  }
  
  /**
   * @param month some day of the year.
   */
  public void setMonth(int month){
    _fields[FIELD_MONTH].setValue(month);    
  }
  
  /**
   * @return the year that this instance holds,
   * or the <code>Field.UNDEFINED</code> constant if 
   * no year has been specified.
   */
  public int getYear(){
    return _fields[FIELD_MONTH].getValue();
  }
  
  /**
   * @param year some year.
   */
  public void setYear(int year){
    _fields[FIELD_YEAR].setValue(year);    
  }    
  
  private void checkFieldRange(int field){
    if(field < 0 && field > FIELD_YEAR){
      throw new IllegalArgumentException("Invalid field index: " + field);
    }
  }
  
  public String toString(){
    StringBuffer buf = new StringBuffer("[ ");
    for(int i = _fields.length - 1; i >= 0; i--){
      if(i < _fields.length - 1){
        buf.append(", ");
      }
      buf.append(_fields[i].getName()).append(": ").append(_fields[i].toString());
    }
    buf.append(" ]");
    return buf.toString();
  }
  
}
