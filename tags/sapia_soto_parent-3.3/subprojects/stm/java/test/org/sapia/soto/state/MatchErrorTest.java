package org.sapia.soto.state;

import junit.framework.TestCase;

import org.sapia.soto.SotoContainer;
import org.sapia.soto.state.config.Globals;

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
public class MatchErrorTest extends TestCase {

  public MatchErrorTest(String name) {
    super(name);
  }

  public void testErrorHandler() throws Exception {
    StateMachine stm = new StateMachine();
    Globals g = new Globals();
    MatchError handler1 = new MatchError();
    handler1.setPattern("java.lang.**");
    MatchError handler2 = new MatchError();
    handler2.setPattern("*");
    TestStep step1 = new TestStep(true);
    TestStep step2 = new TestStep(true);
    TestStep step3 = new TestStep(true);
    handler1.addExecutable(step1);
    handler2.addExecutable(step2);
    handler2.addExecutable(step3);
    g.createErrorHandlers().addHandleError(handler1);
    g.createErrorHandlers().addHandleError(handler2);
    stm.addGlobals(g);
    ErrorState errState1 = new ErrorState(new Exception("someException"));
    errState1.setId("err1");
    ErrorState errState2 = new ErrorState("Some message");
    errState2.setId("err2");
    stm.addState(errState1);
    stm.addState(errState2);
    stm.init();

    stm.execute("err1", new ContextImpl());
    super.assertTrue(step1.exec);
    super.assertTrue(!step2.exec);

    step1.exec = false;
    stm.execute("err2", new ContextImpl());
    super.assertTrue(!step1.exec);
    super.assertTrue(step2.exec);
    super.assertTrue(step3.exec);

  }

  public void testLoad() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load("org/sapia/soto/state/errorHandlerTest.xml");
    cont.start();
    StateMachineService stm = (StateMachineService) cont.lookup("test");
    try {
      stm.execute("someState", null, new ContextImpl());
    } catch(Exception e) {
      // ok;
    }
  }

  static class ErrorState implements State {
    Throwable err;
    String    errMsg;
    String    id;

    ErrorState(Throwable err) {
      this.err = err;
    }

    ErrorState(String errMsg) {
      this.errMsg = errMsg;
    }

    /**
     * @see org.sapia.soto.state.State#getId()
     */
    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    /**
     * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
     */
    public void execute(Result st) {
      if(err != null) {
        st.error(err);
      } else {
        st.error(errMsg);
      }
    }
  }

}
