package org.sapia.validator.config;

import org.sapia.util.xml.confix.CreationStatus;
import org.sapia.util.xml.confix.ObjectCreationException;
import org.sapia.util.xml.confix.ObjectFactoryIF;
import org.sapia.validator.Rule;
import org.sapia.validator.Vlad;

import java.util.*;

/**
 * Creates rule instances based on the prefix:ruleName that is parsed
 * from ruleset configurations.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class XmlObjectFactory implements ObjectFactoryIF {
  private Map  _defs = new HashMap();
  private Vlad _vlad;

  /**
   * Constructor for XmlObjectFactory.
   */
  public XmlObjectFactory(Vlad vlad) {
    _vlad = vlad;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectFactoryIF#newObjectFor(String, String, String, Object)
   */
  public CreationStatus newObjectFor(String prefix, String uri,
    String localName, Object parent)
    throws ObjectCreationException {
    Def def = (Def) _defs.get(localName);

    if (def == null) {
      throw new ObjectCreationException("No definition for: " + prefix + ":"
        + localName);
    }

    try {
      Object toReturn = def.toInstance();
      if(toReturn instanceof Rule){
        ((Rule)toReturn).initName(prefix, localName);
      }
      return CreationStatus.create(toReturn);

      //      if(obj instanceof Rule){
      //      	_vlad.addRule((Rule)obj);
      //      }
      //      return CreationStatus.create(obj);
    } catch (ConfigException e) {
      throw new ObjectCreationException(
        "Could not create object from definition for: " + prefix + ":"
        + localName, e);
    }
  }

  /**
   *
   */
  public void registerDef(String localName, Def def)
    throws IllegalArgumentException {
    if (_defs.get(localName) != null) {
      throw new IllegalArgumentException(
        "A definition already exists for the given name: " + localName);
    }

    _defs.put(localName, def);
  }
}
