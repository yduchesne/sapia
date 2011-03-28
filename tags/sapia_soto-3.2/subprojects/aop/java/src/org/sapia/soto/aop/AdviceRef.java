package org.sapia.soto.aop;

import org.sapia.soto.ConfigurationException;

import java.util.Map;

/**
 * An instance of this class refers to an advice - or, rather, to an advice
 * definition.
 * 
 * @see org.sapia.soto.aop.AdviceDef
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
public class AdviceRef {
  private String _id;

  /**
   * Constructor for AdviceRef.
   */
  public AdviceRef() {
    super();
  }

  /**
   * Sets the identifier of the advice to which this instance refers.
   * 
   * @param id the identifier of an advice.
   * @see AdviceDef
   */
  public void setId(String id) {
    _id = id;
  }

  /**
   * Returns an <code>Advice</code> corresponding to this instance's
   * identifier.
   * 
   * @param defs
   *          a <code>Map</code> containing id-to <code>AdviceDef</code>
   *          mappings.
   */
  public Advice resolve(Map defs) throws ConfigurationException {
    if(_id == null) {
      throw new ConfigurationException(
          "'id' attribute not set on advice reference");
    }

    AdviceDef def = (AdviceDef) defs.get(_id);

    if(def == null) {
      throw new ConfigurationException("No advice definitions matches ID: "
          + _id);
    }

    return def.getInstance();
  }
}
