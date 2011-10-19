package org.sapia.soto.jmx.config;

/**
 * @author Yanick Duchesne
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
public class Param {
  private String _name;
  private String _desc;

  public void setName(String name) {
    _name = name;
  }

  public String getName() {
    return _name;
  }

  public String getDescription() {
    return _desc;
  }

  public void setDescription(String desc) {
    _desc = desc;
  }
}
