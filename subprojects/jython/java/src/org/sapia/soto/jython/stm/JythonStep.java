package org.sapia.soto.jython.stm;

import org.apache.commons.lang.ClassUtils;

import org.python.core.Py;
import org.python.core.PyObject;

import org.python.util.PythonInterpreter;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;
import org.sapia.resource.Resource;
import org.sapia.resource.StringResource;

import java.io.InputStream;
import org.sapia.soto.jython.*;

/**
 * Implements the <code>Step</code> interface over a Jython script. The script
 * source can be specified as the path to a source file, or as CDATA content
 * within the XML tag corresponding to this class. <p/>This class depends on a
 * preconfigured <code>JythonService</code> instance to internally acquire
 * <code>PythonInterpreter</code> instances.
 * 
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class JythonStep implements Step, EnvAware {
  private static final String RESULT = "result";
  private JythonService       _jython;
  private PyObject            _code;
  private Env                 _env;
  private long                _lastModified;
  private Resource            _res;

  public JythonStep() {
  }

  /**
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {
    try {
      _env = env;
      _jython = (JythonService) env.lookup(JythonService.class);
    } catch(NotFoundException e) {
      throw new IllegalStateException(
          "Could not find instance of " + JythonService.class);
    }
  }

  public void setSrc(String src) throws Exception {
    _res = _env.resolveResource(src);
    _code = compile(_res);
  }

  public void setText(String src) throws Exception {
    _res = new StringResource(getName(), src);
    _code = compile(_res);
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result res) {
    if(_lastModified != _res.lastModified()) {
      try {
        reload();
      } catch(Exception e) {
        res.error("Could not reload jython script: " + _res.getURI());
      }
    }

    PythonInterpreter py = _jython.getInterpreter();
    py.set(RESULT, res);
    py.exec(_code);
  }

  private synchronized void reload() throws Exception {
    if(_lastModified != _res.lastModified()) {
      _code = compile(_res);
      _lastModified = _res.lastModified();
    }
  }

  private PyObject compile(Resource res) throws Exception {
    InputStream is = res.getInputStream();

    try {
      return Py.compile(is, res.getURI(), "exec");
    } finally {
      _lastModified = res.lastModified();
      is.close();
    }
  }
}
