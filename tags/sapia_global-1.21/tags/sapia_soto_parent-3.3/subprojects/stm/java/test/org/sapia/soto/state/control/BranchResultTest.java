package org.sapia.soto.state.control;

import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.ResultToken;
import org.sapia.soto.state.StateMachine;
import org.sapia.soto.state.StepState;
import org.sapia.soto.state.TestStep;

import junit.framework.TestCase;

public class BranchResultTest extends TestCase {

  public BranchResultTest(String arg0) {
    super(arg0);
  }
  
  public void testWhenWithTokenValue() throws Exception{
    BranchResult branch = new BranchResult();
    BranchResult.When when = branch.createWhen();
    Result res = new Result(null, null, new ContextImpl());
    res.getToken().setValue(ResultToken.OK);
    super.assertTrue(!when.isTrue(res));
    when.setToken(ResultToken.OK);
    super.assertTrue(when.isTrue(res));
  }
  
  public void testWhenWithoutTokenValue() throws Exception{
    BranchResult branch = new BranchResult();
    BranchResult.When when = branch.createWhen();
    Result res = new Result(null, null, new ContextImpl());
    res.getToken().setValue(ResultToken.OK);
    super.assertTrue(!when.isTrue(res));
    res.getToken().consume();    
    super.assertTrue(when.isTrue(res));
  }
  
  public void testOtherWise() throws Exception{
    TestStep step1 = new TestStep(true);
    TestStep step2 = new TestStep(true);
    BranchResult branch = new BranchResult();
    BranchResult.When when = branch.createWhen();
    when.addExecutable(step1);
    BranchResult.Otherwise otherwise = branch.createOtherwise();
    otherwise.addExecutable(step2);
    when.setToken(ResultToken.OK);
    Result res = new Result(null, null, new ContextImpl());
    branch.execute(res);
    super.assertTrue(!step1.exec);
    super.assertTrue(step2.exec);
  }    

}
