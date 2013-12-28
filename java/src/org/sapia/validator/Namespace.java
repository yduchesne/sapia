package org.sapia.validator;

import java.util.ArrayList;
import java.util.List;

import org.sapia.validator.config.Def;

/**
 * An instance of this class holds rule definitions.
 * 
 * @see org.sapia.validator.config.Def
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Namespace {
  private String _prefix;
  private List   _defs = new ArrayList();

  /**
   * Constructor for Namespace.
   */
  public Namespace() {
  }

  /**
   * Returns this namespace's prefix.
   * 
   * @return a prefix, as a string.
   */
  public String getPrefix() {
    return _prefix;
  }

  /**
   * Sets this instance's prefix.
   * 
   * @param prefix a prefix.
   */
  public void setPrefix(String prefix) {
    _prefix = prefix;
  }

  /**
   * Internally creates a rule definition instance, adds it
   * to this instance, and returns it to the caller.
   * 
   * @return a <code>Def</code> instance.
   */
  public Def createRuleDef() {
    Def d = new Def();

    _defs.add(d);

    return d;
  }
  
  /**
   * Return this instance's list of rule definitions.
   * 
   * @return a <code>List</code> of <code>Def</code> instances.
   */
  public List getRuleDefs() {
    return _defs;
  }
}
