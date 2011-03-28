package org.sapia.soto.jmx.config;

import org.sapia.soto.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yanick Duchesne 19-Aug-2003
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
public class Operation {
  private String   _name;
  private String   _description;
  private String[] _sig;
  private List     _params = new ArrayList();

  /**
   * Constructor for Operation.
   */
  public Operation() {
    super();
  }

  public void setName(String name) {
    _name = name;
  }

  String getName() {
    return _name;
  }

  public void setDescription(String desc) {
    _description = desc;
  }

  String getDescription() {
    return _description;
  }

  public void setSig(String sig) {
    _sig = Utils.split(sig, ',', true);
  }

  String[] getSig() {
    return _sig;
  }

  public Param createParam() {
    Param p = new Param();
    _params.add(p);

    return p;
  }

  List getParams() {
    return _params;
  }

  public int hashCode() {
    return _name.hashCode();
  }

  public boolean equals(Object other) {
    try {
      Operation op = (Operation) other;

      return _name.equals(op._name);
    } catch(ClassCastException e) {
      return false;
    }
  }
}
