package org.sapia.soto.state.cocoon.util;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

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
public class Form implements Serializable {
  private Object _bean;
  private String _name;
  private List   _errors = new ArrayList();

  public void setFormBean(Object bean) {
    _bean = bean;
  }

  public Object getFormBean() {
    return _bean;
  }

  public void setName(String name) {
    _name = name;
  }

  public String getName() {
    return _name;
  }

  public void addErrMessage(String msg) {
    _errors.add(msg);
  }

  public List getErrMessages() {
    List toReturn = new ArrayList(_errors);
    _errors.clear();

    return toReturn;
  }

  public void clear() {
    _errors.clear();
  }
}
