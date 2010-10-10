package org.sapia.soto.util;

import java.util.List;

import org.sapia.util.xml.confix.CompositeObjectFactory;
import org.sapia.util.xml.confix.ConfigurationException;

/**
 * This class implements an object factory that creates objects based on
 * definitions that are associated to a given namespace.
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
public class CompositeObjectFactoryEx extends CompositeObjectFactory {
  /**
   * Constructor for SotoObjectFactory.
   */
  public CompositeObjectFactoryEx() {
    super.setMapToPrefix(true);
  }

  public void registerDefs(Namespace defs) throws ConfigurationException {
    DefObjectFactory fac;

    if(super.containsObjectFactory(defs.getPrefix())) {
      fac = (DefObjectFactory) super.getFactoryFor(defs.getPrefix());
    } else {
      super.registerFactory(defs.getPrefix(), fac = new DefObjectFactory());
    }

    List lst = defs.getDefs();
    for(int i = 0; i < lst.size(); i++) {
      fac.addDef((Def)lst.get(i));
    }
    
    lst = defs.getPackages();
    for(int i = 0; i < lst.size(); i++) {
      fac.addPackage((String)lst.get(i));
    }    
    
    if(defs.getDelegate() != null){
      fac.setDelegate(defs.getDelegate());
    }
  }
}
