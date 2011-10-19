package org.sapia.soto.scheduler;

/**
 * This interface holds the constants corresponding to the fields that
 * a <code>CronDescriptor</code> holds.
 * 
 * @see CronDescriptor
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface Fields {
  
  /**
   * This constant corresponds to index of the <code>SecondField</code>.
   * 
   * @see SecondField
   */  
  public int FIELD_SECONDS  = 0;

  /**
   * This constant corresponds to index of the <code>MinuteField</code>.
   * 
   * @see MinuteField
   */  
  public int FIELD_MINUTES  = 1;
  
  /**
   * This constant corresponds to index of the <code>HourField</code>.
   * 
   * @see HourField
   */  
  public int FIELD_HOUR     = 2;
  
  /**
   * This constant corresponds to index of the <code>DayField</code>.
   * 
   * @see DayField
   */  
  public int FIELD_DAY      = 3;
  
  /**
   * This constant corresponds to index of the <code>MonthField</code>.
   * 
   * @see MonthField
   */  
  public int FIELD_MONTH    = 4;
  
  /**
   * This constant corresponds to index of the <code>DayOfWeekField</code>.
   * 
   * @see DayField
   */  
  public int FIELD_DAY_OF_WEEK = 5;  
  
  
  /**
   * This constant corresponds to index of the <code>YearField</code>.
   * 
   * @see YearField
   */
  public int FIELD_YEAR     = 6;
  
  /**
   * This constant should be used when a field is to have its value
   * set to "undefined".
   */
  public static final int UNDEFINED = -1;  

}
