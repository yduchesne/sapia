package org.sapia.soto.jython;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.sapia.soto.Service;
import org.sapia.soto.util.Param;

/**
 * This class implements the <code>JythonService</code> interface. <p/>An
 * instance of this class intializes the Jython runtime (python.path and
 * python.home, as well as other properties, can be configured). It also creates
 * <code>JythonInterpreter</code> instances for the benefit of client
 * applications.
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
public class JythonServiceImpl implements JythonService, Service {
  private List       _properties = new ArrayList();
  private Properties _props      = new Properties();

  public Param createProperty() {
    Param p = new Param();
    _properties.add(p);

    return p;
  }

  /**
   * @param path
   *          sets the value of the <code>python.path</code> property.
   */
  public void setPythonPath(String path) {
    _props.setProperty("python.path", path);
  }

  /**
   * @param home
   *          sets the value of the <code>python.home</code> property.
   */
  public void setPythonHome(String home) {
    _props.setProperty("python.home", home);
  }

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    Param p;

    for(int i = 0; i < _properties.size(); i++) {
      p = (Param) _properties.get(i);

      if((p.getName() != null) && (p.getValue() != null)) {
        _props.setProperty(p.getName(), p.getValue().toString());
      }
    }

    PySystemState.initialize(System.getProperties(), _props, null);
    _properties = null;
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
  }

  /**
   * @see org.sapia.soto.jython.JythonService#getInterpreter()
   */
  public PythonInterpreter getInterpreter() {
    PySystemState state = new PySystemState();
    PythonInterpreter py = new PythonInterpreter(null, state);

    return py;
  }
/*
  public static void main(String[] args) {
    try {
      System.setProperty("python.path", "./etc/jython");
      PythonInterpreter.initialize(System.getProperties(), new Properties(),
          new String[0]);

      PyObject obj = Py.compile(new FileInputStream(new File(
          "etc/jython/sample.py")), "someFile", "exec");
      PythonInterpreter py = new PythonInterpreter();

      py.exec(obj);

    } catch(Exception e) {
      e.printStackTrace();
    }
  }*/
}
