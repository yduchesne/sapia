package org.sapia.soto.state.helpers;

import org.sapia.soto.state.ExecContainer;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

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
public abstract class CompositeStep extends ExecContainer implements
    ObjectHandlerIF, Step {
  public CompositeStep() {
  }

  /**
   * @see org.sapia.soto.state.ExecContainer#execute(Result)
   */
  public void execute(Result res) {
    if(doExecute(res)) {
      if(res.isError() || res.isAborted()) {
        return;
      } else if(res.getNextStateId() != null) {
        return;
      } else {
        super.execute(res);
      }
    }
  }

  protected abstract boolean doExecute(Result res);

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *      java.lang.Object)
   */
  public void handleObject(String name, Object obj)
      throws ConfigurationException {
    if(obj instanceof Step) {
      super.addExecutable((Step) obj);
    }
  }
}
