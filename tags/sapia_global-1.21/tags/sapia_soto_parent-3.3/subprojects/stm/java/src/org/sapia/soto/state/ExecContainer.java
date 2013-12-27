package org.sapia.soto.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This class is a utility that holds a list of <code>Executable</code>s.
 * 
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
public class ExecContainer {
  protected List _execs = new ArrayList();

  public ExecContainer() {
  }

  /**
   * Adds an executable to this instance.
   * 
   * @param exec
   *          an <code>Executable</code>.
   */
  public void addExecutable(Executable exec) {
    _execs.add(exec);
  }
  
  /**
   * @param execs  a <code>Collection</code> of executables.
   */
  public void addExecutables(Collection execs){
    Iterator itr = execs.iterator();
    while(itr.hasNext()){
      addExecutable((Executable)itr.next());
    }
  }

  /**
   * @param st
   *          a <code>State</code>.
   */
  public void execute(Result st) {
    Executable step;

    for(int i = 0; i < _execs.size(); i++) {
      step = (Executable) _execs.get(i);
      step.execute(st);

      if(st.isError()) {
        handleError(st);

        return;
      } else if(st.isAborted()) {
        return;
      }/* else if(st.getNextStateId() != null) {
        return;
      }*/
    }

    handleSuccess(st);
  }
  
  /**
   * @return the <code>Collection</code> of <code>Executable</code>s that this instance holds.
   */
  public Collection getExecutables(){
    return _execs;
  }

  /**
   * Template method (to be overridden) that is internally called when an error
   * has been assigned to the given <code>State</code>.
   * 
   * @param st
   *          a <code>State</code>.
   */
  protected void handleError(Result st) {
  }

  /**
   * Template method (to be overridden) that is internally called when all
   * <code>Executables</code> held with this instance have been successfully
   * executed (i.e.: no error has been assigned to the given <code>State</code>).
   * 
   * @param st
   *          a <code>State</code>.
   */
  protected void handleSuccess(Result st) {
  }
}
