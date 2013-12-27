package org.sapia.soto.examples;

import java.util.Date;

import org.sapia.soto.Service;

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
public class TypeService implements Service {

  private short   someShort;
  private int     someInt;
  private long    someLong;
  private float   someFloat;
  private double  someDouble;
  private Date    someDate;
  private boolean someBoolean;
  private String[] someArray;

  /**
   * @return
   */
  public boolean isSomeBoolean() {
    return someBoolean;
  }

  /**
   * @return
   */
  public Date getSomeDate() {
    return someDate;
  }

  /**
   * @return
   */
  public double getSomeDouble() {
    return someDouble;
  }

  /**
   * @return
   */
  public float getSomeFloat() {
    return someFloat;
  }

  /**
   * @return
   */
  public int getSomeInt() {
    return someInt;
  }

  /**
   * @return
   */
  public long getSomeLong() {
    return someLong;
  }

  /**
   * @return
   */
  public short getSomeShort() {
    return someShort;
  }

  /**
   * @param b
   */
  public void setSomeBoolean(boolean b) {
    someBoolean = b;
  }

  /**
   * @param date
   */
  public void setSomeDate(Date date) {
    someDate = date;
  }

  /**
   * @param double1
   */
  public void setSomeDouble(double double1) {
    someDouble = double1;
  }

  /**
   * @param f
   */
  public void setSomeFloat(float f) {
    someFloat = f;
  }

  /**
   * @param i
   */
  public void setSomeInt(int i) {
    someInt = i;
  }

  /**
   * @param l
   */
  public void setSomeLong(long l) {
    someLong = l;
  }

  /**
   * @param s
   */
  public void setSomeShort(short s) {
    someShort = s;
  }

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
  }

  public String[] getSomeArray() {
    return someArray;
  }

  public void setSomeArray(String[] someArray) {
    this.someArray = someArray;
  }

}
