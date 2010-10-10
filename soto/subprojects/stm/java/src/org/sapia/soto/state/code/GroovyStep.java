package org.sapia.soto.state.code;

import groovy.lang.GroovyClassLoader;

import java.io.ByteArrayInputStream;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.math.JVMRandom;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
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
public class GroovyStep implements Step, ObjectCreationCallback {
  private String        _name;
  private String        _src;
  private Step          _inner;
  private StringBuffer  _imports    = new StringBuffer();

  // patch to avoid LinkageErrors due to duplicate definition
  // exceptions (this is related to a bug in Groovy).
  private static Object _groovyLock = new Object();

  public GroovyStep() {
  }

  public void setName(String name) {
    _name = name;
  }

  public void addImport(String imp) {
    _imports.append("import ").append(imp).append(';');
  }

  public void setImport(String imp) {
    addImport(imp);
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    if(_name == null) {
      return ClassUtils.getShortClassName(getClass());
    }

    return _name;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    try {
      Class clazz = generate();
      _inner = (Step) clazz.newInstance();

      return this;
    } catch(RuntimeException e) {
      throw e;
    } catch(Exception e) {
      throw new ConfigurationException("Could not generate Groovy code for: "
          + _src, e);
    }
  }

  public void setText(String src) throws Exception {
    _src = src;
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(Result)
   */
  public void execute(Result st) {
    if(_inner == null) {
      throw new IllegalStateException("Source code not defined");
    }

    _inner.execute(st);
  }

  private Class generate() throws Exception {
    if(_src == null) {
      throw new IllegalStateException("Groovy source not specified");
    }

    String src = _src;

    synchronized(_groovyLock) {
      src = _imports.toString() + "class Soto_State_Java_" + getName() + "_"
          + new JVMRandom().nextLong()
          + " implements org.sapia.soto.state.Step{"
          + "  public String getName(){ return \"" + getName() + "\" }"
          + "  public void execute(" + Result.class.getName() + " result){"
          + src + "  }" + "}";

      // System.out.println(src);
      ByteArrayInputStream bis = new ByteArrayInputStream(src.getBytes());
      GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread()
          .getContextClassLoader());

      return loader.parseClass(bis, getName());
    }
  }
}
