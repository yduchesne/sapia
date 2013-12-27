package org.sapia.soto.state.code;

import java.io.StringReader;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;

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
public class BeanshellStep implements Step {
  private String _src;

  public BeanshellStep() {
    super();
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
  public void execute(Result result) {
    if(_src == null) {
      throw new IllegalStateException("Beanshell source not specified");
    }
    
    Interpreter interp = new Interpreter();

    try {
      interp.set("result", result);
    } catch(EvalError e) {
      result.error("Could not initialize interpreter", e);
      return;
    }

    try {
      interp.eval(new StringReader(_src));
    } catch(TargetError e) {
      result.error(e.getTarget());
    } catch(EvalError e) {
      result.error("Could not interpret Beanshell script", e);
    }
  }

  public void setText(String src) {
    _src = src;
  }
}
