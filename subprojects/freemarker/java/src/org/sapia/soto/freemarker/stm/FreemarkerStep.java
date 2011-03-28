package org.sapia.soto.freemarker.stm;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.python.core.PyObject;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.freemarker.FreemarkerService;
import org.sapia.soto.state.ExecContainer;
import org.sapia.soto.state.Executable;
import org.sapia.soto.state.MVC;
import org.sapia.soto.state.Output;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.State;
import org.sapia.soto.state.Step;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;
import org.w3c.dom.Node;

import freemarker.ext.dom.NodeModel;
import freemarker.ext.jython.JythonWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
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
public class FreemarkerStep extends ExecContainer implements State, Step,
    EnvAware, ObjectHandlerIF {

  private FreemarkerService _freemarker;
  private String            _src, _id;
  private static boolean    _jythonEnabled;
  static {
    try {
      Class.forName("org.python.core.PyObject");
      _jythonEnabled = true;
    } catch(Exception e) {
      _jythonEnabled = false;
    }
  }

  /**
   * @see org.sapia.soto.state.State#getId()
   */
  public String getId() {
    return _id;
  }

  public void setId(String id) {
    _id = id;
  }

  /**
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {
    try {
      _freemarker = (FreemarkerService) env.lookup(FreemarkerService.class);
    } catch(NotFoundException e) {
      throw new IllegalStateException(
          "Could not find Freemarker service; probably not initialized: "
              + e.getMessage());
    }
  }

  /**
   * @param uri
   *          the URI of the Freemarker template to which this instance
   *          corresponds.
   */
  public void setSrc(String uri) throws Exception {
    _src = uri;
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
    if(_src == null) {
      throw new IllegalStateException("Template source not specified");
    }
    super.execute(res);
    if(res.isAborted() || res.isError()) {
      return;
    }
    
    Map params = ((MVC) res.getContext()).getViewParams();
    SimpleHash root = new SimpleHash();
    Iterator itr = params.entrySet().iterator();
    Map.Entry entry;
    while(itr.hasNext()) {
      entry = (Map.Entry) itr.next();
      root.put(entry.getKey().toString(), entry.getValue());
    }
    if(res.getContext().hasCurrentObject()) {
      Object currentObj = res.getContext().currentObject();
      if(_jythonEnabled && currentObj instanceof PyObject) {
        currentObj = JythonWrapper.INSTANCE.wrap(currentObj);
      }
      else if(currentObj instanceof Node){
        currentObj = NodeModel.wrap((Node)currentObj);
      }
      root.put(MVC.MODEL_KEY, currentObj);
      params.put(MVC.MODEL_KEY, currentObj);
    }
    Template t;
    try {
      t = _freemarker.resolveTemplate(_src, res.getContext().getLocale());
    } catch(IOException e) {
      res.error("Could not resolve template", e);
      return;
    }
    Output out;
    try {
      out = (Output) res.getContext();
    } catch(ClassCastException e) {
      throw new IllegalStateException(res.getContext().getClass().getName()
          + " is not an instance of " + Output.class.getName());
    }
    try {
      Writer w = new OutputStreamWriter(out.getOutputStream());
      onPreRender(res);
      t.process(root, w);
      w.flush();
      w.close();
    } catch(Exception e) {
      res.error("Could not process template", e);
      return;
    }
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *      java.lang.Object)
   */
  public void handleObject(String name, Object obj)
      throws ConfigurationException {
    if(obj instanceof Executable) {
      super.addExecutable((Executable) obj);
    } else {
      throw new ConfigurationException("Expecting instances of: "
          + Executable.class.getName() + "; got: " + obj.getClass().getName());
    }
  }
  
  protected void onPreRender(Result res){}

}
