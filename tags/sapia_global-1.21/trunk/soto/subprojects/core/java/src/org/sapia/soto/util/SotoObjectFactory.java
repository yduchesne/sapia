package org.sapia.soto.util;

import java.util.List;

import org.sapia.util.xml.confix.CompositeObjectFactory;
import org.sapia.util.xml.confix.ConfigurationException;

/**
 * An object factory that creates objects based on definitions, categorized by
 * namespace.
 * 
 * @see org.sapia.soto.util.Def
 * @see org.sapia.soto.util.Namespace
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
public class SotoObjectFactory extends CompositeObjectFactory {
  /**
   * Constructor for SotoObjectFactory.
   */
  public SotoObjectFactory() {
    super.setMapToPrefix(true);
  }

  public void registerDefs(Namespace defs) throws ConfigurationException {
    DefObjectFactory fac;

    if(super.containsObjectFactory(defs.getPrefix())) {
      fac = (DefObjectFactory) super.getFactoryFor(defs.getPrefix());
    } else {
      super.registerFactory(defs.getPrefix(), fac = new DefObjectFactory());
    }

    Def def;
    List lst = defs.getDefs();

    for(int i = 0; i < lst.size(); i++) {
      def = (Def) lst.get(i);
      fac.addDef(def);
    }
  }
}
