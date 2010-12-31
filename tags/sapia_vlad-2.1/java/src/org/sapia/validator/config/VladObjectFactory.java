package org.sapia.validator.config;

import org.sapia.util.xml.confix.CompositeObjectFactory;
import org.sapia.util.xml.confix.CreationStatus;
import org.sapia.util.xml.confix.ObjectCreationException;
import org.sapia.util.xml.confix.ObjectFactoryIF;
import org.sapia.util.xml.confix.ReflectionFactory;
import org.sapia.validator.Defs;
import org.sapia.validator.Namespace;
import org.sapia.validator.RuleRef;
import org.sapia.validator.RuleSet;
import org.sapia.validator.RuleSetRef;
import org.sapia.validator.Vlad;

import java.util.*;

/**
 * Instantiates Java objects corresponding to the various Vlad configuration
 * files (rule definitions and rulesets).
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class VladObjectFactory implements ObjectFactoryIF {
  private CompositeObjectFactory _comp      = new CompositeObjectFactory();
  private List                   _factories = new ArrayList();
  private List                   _listeners = new ArrayList();
  private Vlad                   _vlad;

  /**
   * Constructor for VladObjectFactory.
   */
  public VladObjectFactory( /*String[] packages*/
  ) {
    _factories.add(new ReflectionFactory(new String[0]));
  }

  public VladObjectFactory(Vlad vlad /*, String[] packages*/) {
    this();
    _vlad = vlad;
  }

  public void registerDef(String prefix, String localName, Def def) {
    if (_vlad == null) {
      throw new IllegalStateException("Vlad not set");
    }

    XmlObjectFactory fac;

    if (_comp.containsObjectFactory(prefix)) {
      fac = (XmlObjectFactory) _comp.getFactoryFor(prefix);
    } else {
      fac = new XmlObjectFactory(_vlad);
      _comp.registerFactory(prefix, fac);
    }

    fac.registerDef(localName, def);
  }

  public void addFactoryFor(String[] packages) {
    _factories.add(new ReflectionFactory(packages));
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectFactoryIF#newObjectFor(String, String, String, Object)
   */
  public CreationStatus newObjectFor(String prefix, String uri,
    String localName, Object parent)
    throws ObjectCreationException {
    if ((prefix != null) && (prefix.length() != 0)) {
      return getFactoryFor(prefix).newObjectFor(prefix, uri, localName, parent);
    }

    ObjectFactoryIF fac;

    for (int i = 0; i < _factories.size(); i++) {
      fac = (ObjectFactoryIF) _factories.get(i);

      try {
        return fac.newObjectFor(prefix, uri, localName, parent);
      } catch (Throwable e) {
        // noop
      }
    }

    if (localName.equalsIgnoreCase("vlad")) {
      return CreationStatus.create(_vlad);
    } else if (localName.equalsIgnoreCase("namespace")) {
      return CreationStatus.create(new Namespace()).assigned(false);
    } else if (localName.equalsIgnoreCase("ruleset")) {
      return CreationStatus.create(new RuleSet()).assigned(false);
    } else if (localName.equalsIgnoreCase("ruleSetRef")) {
      return CreationStatus.create(new RuleSetRef()).assigned(false);
    } else if (localName.equalsIgnoreCase("ruleRef")) {
      return CreationStatus.create(new RuleRef()).assigned(false);
    } else if (localName.equalsIgnoreCase("defs")) {
      return CreationStatus.create(new Defs(this)).assigned(false);
    }

    String name;

    if (prefix != null) {
      name = prefix + ':' + localName;
    } else {
      name = localName;
    }

    throw new ObjectCreationException("Could not creation object for: " + name);
  }

  protected ObjectFactoryIF getFactoryFor(String prefix)
    throws IllegalArgumentException {
    ObjectFactoryIF fac = (ObjectFactoryIF) _comp.getFactoryFor(prefix);

    if (fac == null) {
      throw new IllegalStateException("No factory for prefix: " + prefix);
    }

    return fac;
  }
}
