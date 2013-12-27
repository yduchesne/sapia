package org.sapia.util.xml.idefix.test;

/**
 *
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class BasicTypes {

  public BasicTypes() {
  }

  public byte getPositiveByte() {
    return Byte.MAX_VALUE;
  }
  public byte getNegativeByte() {
    return Byte.MIN_VALUE;
  }
  public Byte getByteObject() {
    return new Byte((byte) 95);
  }

  public short getPositiveShort() {
    return Short.MAX_VALUE;
  }
  public short getNegativeShort() {
    return Short.MIN_VALUE;
  }
  public Short getShortObject() {
    return new Short((short) 1095);
  }

  public int getPositiveInt() {
    return Integer.MAX_VALUE;
  }
  public int getNegativeInt() {
    return Integer.MIN_VALUE;
  }
  public Integer getIntegerObject() {
    return new Integer(188295);
  }

  public long getPostitiveLong() {
    return Long.MAX_VALUE;
  }
  public long getNegativeLong() {
    return Long.MIN_VALUE;
  }
  public Long getLongObject() {
    return new Long(100000000095L);
  }

  public float getPositiveFloat() {
    return Float.MAX_VALUE;
  }
  public float getNegativeFloat() {
    return Float.MIN_VALUE;
  }
  public Float getFloatObject() {
    return new Float(1243.95f);
  }

  public double getPositiveDouble() {
    return Double.MAX_VALUE;
  }
  public double getNegativeDouble() {
    return Double.MIN_VALUE;
  }
  public Double getDoubleObject() {
    return new Double(10000000000243.95);
  }

  public boolean getBoolean() {
    return true;
  }
  public Boolean getBooleanObject() {
    return new Boolean(false);
  }

  public String getString() {
    return "foo-bar";
  }
  public char getChar() {
    return 'Z';
  }
}
