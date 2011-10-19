package org.sapia.soto.state.code;

import junit.framework.TestCase;

import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateMachine;

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
public class GroovyStepTest extends TestCase {
  public GroovyStepTest(String name) {
    super(name);
  }

  public void testExecute() throws Exception {
    ContextImpl ctx;
    Result res = new Result(new StateMachine(), ctx = new ContextImpl());
    GroovyStep gs = new GroovyStep();
    gs.addImport("java.util.Date");
    gs.setText("Date d = new Date(); result.setNextStateId(\"someState\");");
    gs.onCreate();
    gs.execute(res);
    super.assertEquals("someState", res.getNextStateId());
  }
}
