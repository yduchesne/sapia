package org.sapia.soto.config.types;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class DateType implements ObjectCreationCallback {

  private String _value, _pattern;
  
  public void setValue(String value) {
    _value = value;
  }

  /**
   * A date parsing pattern, as specified in the JDK's
   * <code>SimpleDateFormat</code> API doc.
   * 
   * @param pattern
   *          the pattern that should be used to parse the value held within
   *          this instance.
   * 
   * @see SimpleDateFormat
   */
  public void setPattern(String pattern) {
    _pattern = pattern;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if(_value == null) {
      throw new ConfigurationException("Date value not specified");
    }
    try {
      if(_pattern == null) {
        return DateFormat.getDateInstance().parse(_value);
      } else {
        SimpleDateFormat fm = new SimpleDateFormat(_pattern);
        return fm.parseObject(_value);
      }
    } catch(ParseException e) {
      throw new ConfigurationException("Could not parse date: " + _value + "; "
          + "have you specified an appropriate pattern?", e);
    }
  }
}
