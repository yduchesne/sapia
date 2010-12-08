package org.sapia.util.text;

import java.util.Map;


/**
 * A template context that tries to resolve properties from provided properties (in the
 * form of a <code>Map</code>, and which can hold a reference to an "ancestor" context. This
 * pattern allows to hierarchically organize template contexts in the from of a
 * chain of responsability.
 * <p>
 * The following code creates a <code>MapContext</code> that resolves values from
 * a <code>Map</code> or from the system properties.
 *
 * <pre>
 *  Map someValues = new HashMap();
 *  // here init the map with some data...
 *  MapContext ctx = new MapContext(map, new SystemContext(), false);
 *  ...
 * </pre>
 *
 * @author Yanick Duchesne
 * 2003-04-08
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class MapContext implements TemplateContextIF {
  private Map               _props;
  private TemplateContextIF _ancestor;
  private boolean           _ancestorFirst;

  /**
   * Creates an instance of this class.
   *
   * @param a <code>Map</code> instance that will be used to resolve values.
   * @param ancestor a <code>TemplateContextIF</code> instance to which the
   * lookup can eventually be delegated.
   * @param ancestorFirst if <code>true</code>, indicates that this instance
   * should first try to delegate the value lookup to its ancestor prior to trying
   * itself - the opposite applies if false.
   */
  public MapContext(Map props, TemplateContextIF ancestor, boolean ancestorFirst) {
    _props           = props;
    _ancestor        = ancestor;
    _ancestorFirst   = ancestorFirst;
  }

  /**
   * @see org.sapia.util.text.TemplateContextIF#getValue(String)
   */
  public Object getValue(String aName) {
    Object value;

    if (_ancestorFirst) {
      value = fromAncestor(aName);

      if (value == null) {
        value = _props.get(aName);
      }
    } else {
      value = _props.get(aName);

      if (value == null) {
        value = fromAncestor(aName);
      }
    }

    return value;
  }

  /**
   * @see org.sapia.util.text.TemplateContextIF#put(String, Object)
   */
  public void put(String name, Object value) {
    _props.put(name, value);
  }

  protected Object fromAncestor(String aName) {
    if (_ancestor != null) {
      return _ancestor.getValue(aName);
    }

    return null;
  }
}
