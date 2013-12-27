package org.sapia.soto.aop;

import org.sapia.soto.ConfigurationException;

import java.util.List;
import java.util.Map;

/**
 * An instance of this class refers to an advice group.
 * 
 * @see org.sapia.soto.aop.Group
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
public class GroupRef {
  private String _id;

  /**
   * Constructor for AdviceRef.
   */
  public GroupRef() {
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
   * Returns a list of advices contained in the group corresponding to this
   * instance's identifier.
   * 
   * @param defs
   *          a <code>Map</code> containing id-to <code>Group</code>
   *          mappings.
   */
  public List resolve(Map defs) throws ConfigurationException {
    if(_id == null) {
      throw new ConfigurationException(
          "'id' attribute not set on advice group reference");
    }

    Group group = (Group) defs.get(_id);

    if(group == null) {
      throw new ConfigurationException("No advice group matches ID: " + _id);
    }

    return group.getAdvices();
  }
}
