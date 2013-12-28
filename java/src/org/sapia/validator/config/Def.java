package org.sapia.validator.config;


/**
 * Models a rule definition.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Def {
  private String _name;
  private String _class;

  public void setName(String name) {
    _name = name;
  }

  public String getName() {
    return _name;
  }

  public void setClass(String clazz) {
    _class = clazz;
  }

  public Object toInstance() throws ConfigException {
    if (_name == null) {
      throw new ConfigException("'name' attribute not specified for definition");
    }

    if (_class == null) {
      throw new ConfigException(
        "'class' attribute not specified for definition " + _name);
    }

    try {
      return Class.forName(_class).newInstance();
    } catch (Throwable t) {
      throw new ConfigException("Could not instantiate definition " + _name, t);
    }
  }
}
