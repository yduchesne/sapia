package org.sapia.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This class olds a hierarchy of error message strings, organized by
 * locale.
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
class Hierarchy {
  private ErrorMessage _msg;
  private Hierarchy    _parent;
  private Map          _children = new HashMap();

  /**
   * Constructor for Hierarchy.
   */
  Hierarchy(Hierarchy parent) {
    _parent = parent;
  }

  void bind(ErrorMessage msg) {
    if (msg.getLocale() == null) {
      _msg = msg;
    } else {
      String[] path = split(msg.getLocale());

      bind(msg, path, 0);
    }
  }

  ErrorMessage lookup(String locale) {
    if ((locale == null) || (locale.length() == 0)) {
      return _msg;
    }

    String[] path = split(locale);

    return lookup(path, 0);
  }

  ErrorMessage reverseLookup(String locale) {
    if ((locale == null) || (locale.length() == 0)) {
      return _msg;
    }

    String[] path = split(locale);

    return reverselookup(path, 0);
  }

  private ErrorMessage reverselookup(String[] path, int index) {
    if (index >= path.length) {
      if ((_msg == null) && (_parent != null)) {
        return _parent.reverse();
      }

      return _msg;
    }

    Hierarchy h = (Hierarchy) _children.get(path[index]);
    if (h == null) {
      if(_msg != null){
        return _msg;
      }
      else if (_parent != null) {
        return _parent.reverse();
      }
      return null;
    } else {
      return h.reverselookup(path, ++index);
    }
  }

  private ErrorMessage reverse() {
    if (_msg != null) {
      return _msg;
    } else if (_parent != null) {
      return _parent.reverse();
    } else {
      return null;
    }
  }

  private ErrorMessage lookup(String[] path, int index) {
    if (index >= path.length) {
      return _msg;
    }

    Hierarchy h = (Hierarchy) _children.get(path[index]);

    if (h == null) {
      return null;
    } else {
      return h.lookup(path, ++index);
    }
  }

  private void bind(ErrorMessage msg, String[] path, int index) {
    if (index >= path.length) {
      _msg = msg;
    } else {
      Hierarchy h = (Hierarchy) _children.get(path[index]);
      if (h == null) {
        h = new Hierarchy(this);
        _children.put(path[index], h);
      }

      h.bind(msg, path, ++index);
    }
  }

  public ErrorMessage getMessage() {
    return _msg;
  }

  private static String[] split(String path) {
    List            parts = new ArrayList();

    StringTokenizer st = new StringTokenizer(path, "/");

    String          token;

    while (st.hasMoreTokens()) {
      token = st.nextToken();
      parts.add(token);
    }

    return (String[]) parts.toArray(new String[parts.size()]);
  }
}
