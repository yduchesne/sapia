package org.sapia.soto.jmx.config;


/**
 * @author Yanick Duchesne 18-Aug-2003
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class Attribute {
  private String  _name;
  private String  _desc;
  private boolean _write  = true;
  private String  _type;

  /**
   * Constructor for Attribute.
   */
  public Attribute() {
    super();
  }

  public void setName(String name) {
    _name = name;
  }

  String getName() {
    return _name;
  }

  public void setDescription(String desc) {
    _desc = desc;
  }

  String getDescription() {
    return _desc;
  }

  public void setType(String type) {
    _type = type;
  }

  String getType() {
    return _type;
  }

  public void setWritable(boolean write) {
    _write = write;
  }

  public boolean isWritable() {
    return _write;
  }
}
