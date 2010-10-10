package org.sapia.soto.aop;

import org.sapia.soto.reflect.Matcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a method pointcut: encapsulates advices that are to be called when
 * given methods are invoked.
 * 
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
public class MethodPointCut extends Matcher {
  private List _adviceRefs = new ArrayList();
  private List _groupRefs  = new ArrayList();

  /**
   * Constructor for MethodPointCut.
   */
  public MethodPointCut() {
    super();
  }

  //  /**
  //   * Sets the name pattern of the method(s) that is (are) to be
  //   * intercepted.
  //   *
  //   * @param name a method name pattern.
  //   */
  //  public void setName(String name) {
  //    _namePattern = PathPattern.parse(name, true);
  //  }
  //  /**
  //   * Sets the signature (in the form of parameter type name patterns)
  //   * of the method(s) that is (are) to be intercepted.
  //   *
  //   * @param name a method name pattern.
  //   */
  //  public void setSig(String sig) {
  //    String[] sigArr = Utils.split(sig, ',', true);
  //    _sig = new Pattern[sigArr.length];
  //
  //    for (int i = 0; i < sigArr.length; i++) {
  //      _sig[i] = PathPattern.parse(sigArr[i], true);
  //    }
  //  }
  //  /**
  //   * Sets the access modifiers of the methods that are matched, in
  //   * a comma-delimited list: "protected, public".
  //   *
  //   * @param viz an access modifier name ("protected" and/or "public").s
  //   */
  //  public void setVisibility(String viz){
  //  	_visibility = viz;
  //  }

  /**
   * Creates an <code>AdviceRef</code> instance and returns it.
   * 
   * @return an <code>AdviceRef</code>.
   */
  public AdviceRef createAdviceRef() {
    AdviceRef ref = new AdviceRef();
    _adviceRefs.add(ref);

    return ref;
  }

  /**
   * Creates a <code>GroupRef</code> instance and returns it.
   * 
   * @return an <code>GroupRef</code>.
   */
  public GroupRef createGroupRef() {
    GroupRef ref = new GroupRef();
    _groupRefs.add(ref);

    return ref;
  }

  /**
   * Scans the given class and returns the list of methods that correspond to
   * this instance's properties.
   * 
   * @return a <code>List</code> of <code>Method</code>s.
   */

  //  List scan(Class toScan) throws ConfigurationException {
  //    List toReturn = new ArrayList();
  //    Method[] methods = toScan.getMethods();
  //
  //    for (int i = 0; i < methods.length; i++) {
  //      if (_namePattern == null) {
  //        throw new ConfigurationException(
  //          "'name' attribute was not specified on method pointcut");
  //      } else {
  //        if (_namePattern.matches(methods[i].getName())) {
  //          if (matchesSig(methods[i])) {
  //            toReturn.add(methods[i]);
  //          }
  //        }
  //      }
  //    }
  //
  //    return toReturn;
  //  }
  /**
   * Returns this instance's advice references.
   * 
   * @return a <code>List</code> of <code>AdviceRef</code> instances.
   */
  List getAdviceRefs() {
    return _adviceRefs;
  }

  /**
   * Returns this instance's advice group references.
   * 
   * @return a <code>List</code> of <code>GroupRef</code> instances.
   */
  List getGroupRefs() {
    return _groupRefs;
  }

  //  private boolean matchesSig(Method m) {
  //    if (_sig == null) {
  //      return true;
  //    } else if (_sig.length == m.getParameterTypes().length) {
  //      int count = 0;
  //
  //      for (int i = 0; i < _sig.length; i++) {
  //        if (_sig[i].matches(m.getParameterTypes()[i].getName())) {
  //          count++;
  //        }
  //      }
  //
  //      return count == _sig.length;
  //    }
  //
  //    return false;
  //  }
}
