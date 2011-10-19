package org.sapia.soto.state.config;

import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.state.State;
import org.sapia.soto.state.Step;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

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
public class StateSet implements ObjectHandlerIF {
  private List _includes;
  private List _excludes;
  private List _steps = new ArrayList();

  public StateSet() {
  }

  public StatePattern createInclude() {
    if(_includes == null) {
      _includes = new ArrayList();
    }

    StatePattern sp = new StatePattern();
    _includes.add(sp);

    return sp;
  }

  public StatePattern createExclude() {
    if(_excludes == null) {
      _excludes = new ArrayList();
    }

    StatePattern sp = new StatePattern();
    _excludes.add(sp);

    return sp;
  }

  public List getSteps() {
    return _steps;
  }

  public boolean matches(State st) {
    if(_includes != null) {
      if(matches(st, _includes)) {
        if(_excludes != null) {
          return !matches(st, _excludes);
        }

        return true;
      }

      return false;
    } else {
      if(_excludes != null) {
        if(matches(st, _excludes)) {
          return false;
        }
      }

      return true;
    }
  }

  public void addStep(Step step) {
    _steps.add(step);
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *      java.lang.Object)
   */
  public void handleObject(String name, Object obj)
      throws ConfigurationException {
    if(obj instanceof Step) {
      _steps.add(obj);
    }
  }

  private static boolean matches(State s, List patterns) {
    for(int i = 0; i < patterns.size(); i++) {
      if(((StatePattern) patterns.get(i)).matches(s.getId())) {
        return true;
      }
    }

    return false;
  }
}
