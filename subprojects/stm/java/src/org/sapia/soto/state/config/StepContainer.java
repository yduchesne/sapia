package org.sapia.soto.state.config;

import org.sapia.soto.state.ExecContainer;
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
public class StepContainer extends ExecContainer implements ObjectHandlerIF{
  
  public StepContainer() {
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *      java.lang.Object)
   */
  public void handleObject(String name, Object obj)
      throws ConfigurationException {
    if(obj instanceof Step) {
      super.addExecutable((Step)obj);
    }
    else{
      throw new ConfigurationException("Expected instance of: " + Step.class.getName() + 
       ", got instance of " + obj.getClass().getName() + " for element: " + name);
    }
  }
}
