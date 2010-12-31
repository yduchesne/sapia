package org.sapia.validator;

import org.sapia.validator.config.Def;
import org.sapia.validator.config.VladObjectFactory;

/**
 * Provides an object representation of a rule definition file.
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Defs {
  private VladObjectFactory _factory;

  /**
   * Constructor for Defs.
   */
  public Defs(VladObjectFactory factory) {
    _factory = factory;
  }

  /**
   * Adds the given namespace to this definition.
   * 
   * @param a <code>Namespace</code>.
   */
  public void addNamespace(Namespace ns) {
    if (ns.getPrefix() == null) {
      throw new IllegalArgumentException(
        "Attribute 'prefix' not defined on 'namepsace' element");
    }

    Def d;

    for (int i = 0; i < ns.getRuleDefs().size(); i++) {
      d = (Def) ns.getRuleDefs().get(i);
      _factory.registerDef(ns.getPrefix(), d.getName(), d);
    }
  }
}
