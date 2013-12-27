package org.sapia.soto.reflect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class Filter {
  private List _includes = new ArrayList();
  private List _excludes = new ArrayList();

  public Matcher createInclude() {
    Matcher bm = new Matcher();
    _includes.add(bm);

    return bm;
  }

  public Matcher createExclude() {
    Matcher bm = new Matcher();
    _excludes.add(bm);

    return bm;
  }

  public Set scanMethods(Class clazz) {
    Set methods = new HashSet();

    Matcher m;

    for(int i = 0; i < _includes.size(); i++) {
      m = (Matcher) _includes.get(i);
      methods.addAll(m.scanMethods(clazz));
    }

    for(int i = 0; i < _excludes.size(); i++) {
      m = (Matcher) _excludes.get(i);
      methods.removeAll(m.scanMethods(clazz));
    }

    return methods;
  }
}
