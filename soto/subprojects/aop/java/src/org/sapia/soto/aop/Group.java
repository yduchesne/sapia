package org.sapia.soto.aop;

import org.sapia.soto.ConfigurationException;

import org.sapia.util.xml.confix.ObjectHandlerIF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An instance of this class encapsulates advices and advice references. The
 * latter must correspond to existing advice definitions.
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
public class Group implements ObjectHandlerIF {
  private String _id;
  private List   _defs    = new ArrayList();
  private List   _refs    = new ArrayList();
  private List   _advices = new ArrayList();

  /**
   * Constructor for Group.
   */
  public Group() {
    super();
  }

  /**
   * Sets this group's identifier.
   * 
   * @param id an identifier.
   */
  public void setId(String id) {
    _id = id;
  }

  /**
   * @return this instance's identifier.
   */
  public String getId() {
    return _id;
  }

  /**
   * Internally creates an advice definition and returns it.
   * 
   * @return an <code>AdviceDef</code>.
   */
  public AdviceDef createAdviceDef() {
    AdviceDef def = new AdviceDef();
    def.setId("null");
    _defs.add(def);

    return def;
  }

  /**
   * Internally creates an advice definition and returns it.
   * 
   * @return an <code>AdviceRef</code>.
   */
  public AdviceRef createAdviceRef() {
    AdviceRef ref = new AdviceRef();
    _refs.add(ref);

    return ref;
  }

  /**
   * Returns the advices that this instance encapsulates.
   * 
   * @return a <code>List</code> of <code>Advice</code> instances.
   */
  public List getAdvices() throws ConfigurationException {
    return _advices;
  }

  /**
   * Adds the given advice to this instance.
   * 
   * @param adv
   *          an <code>Advice</code>.
   */
  public void addAdvice(Advice adv) {
    _advices.add(adv);
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(String, Object)
   */
  public void handleObject(String name, Object obj)
      throws org.sapia.util.xml.confix.ConfigurationException {
    if(obj instanceof Advice) {
      _advices.add(obj);
    } else {
      throw new org.sapia.util.xml.confix.ConfigurationException(obj.getClass()
          .getName()
          + " is not an instance of " + Advice.class.getName());
    }
  }

  void resolve(Map defs) throws ConfigurationException {
    AdviceRef ref;
    AdviceDef def;

    for(int i = 0; i < _defs.size(); i++) {
      def = (AdviceDef) _defs.get(i);
      _advices.add(def.getInstance());
    }

    for(int i = 0; i < _refs.size(); i++) {
      ref = (AdviceRef) _refs.get(i);
      _advices.add(ref.resolve(defs));
    }
  }
}
