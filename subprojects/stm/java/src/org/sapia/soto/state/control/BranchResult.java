package org.sapia.soto.state.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.helpers.CompositeStep;

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
public class BranchResult implements Step {
  private List      _cases = new ArrayList();
  private Otherwise _other;

  /**
   *  
   */
  public BranchResult() {
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  public When createWhen() {
    When caseObj = new When();
    _cases.add(caseObj);

    return caseObj;
  }

  public Otherwise createOtherwise() {
    return _other = new Otherwise();
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(Result)
   */
  public void execute(Result st) {
    When caseObj;

    for(int i = 0; i < _cases.size(); i++) {
      caseObj = (When) _cases.get(i);

      if(caseObj.isTrue(st)) {
        caseObj.execute(st);

        return;
      }
    }

    if(_other != null) {
      _other.execute(st);
    }
  }

  // INNER CLASSES //////////////////////////////////////////////////////////
  public static class Otherwise extends CompositeStep {
    /**
     * @see org.sapia.soto.state.Step#getName()
     */
    public String getName() {
      return ClassUtils.getShortClassName(getClass());
    }

    /**
     * @see org.sapia.soto.state.helpers.CompositeStep#doExecute(Result)
     */
    protected boolean doExecute(Result st) {
      return true;
    }
  }

  public static class When extends CompositeStep {
    private String _token;

    public When() {
    }

    /**
     * @see org.sapia.soto.state.Step#getName()
     */
    public String getName() {
      return ClassUtils.getShortClassName(getClass());
    }
    
    public void setToken(String token){
      _token = token;
    }

    public boolean isTrue(Result res) {
      if(_token == null){
        return res.getToken().getValue() == null;
      }
      else{
        return res.getToken().getValue() != null && 
        res.getToken().getValue().equals(_token);
      }
    }

    /**
     * @see org.sapia.soto.state.helpers.CompositeStep#doExecute(Result)
     */
    protected boolean doExecute(Result st) {
      return true;
    }
  }
}
