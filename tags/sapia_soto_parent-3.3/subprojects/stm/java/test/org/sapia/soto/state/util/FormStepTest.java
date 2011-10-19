package org.sapia.soto.state.util;

import junit.framework.TestCase;

import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.MapScope;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Scope;
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
public class FormStepTest extends TestCase {
  public FormStepTest(String name) {
    super(name);
  }

  public void testExecuteNoScope() {
    ContextImpl ctx;
    Result res = new Result(new StateMachine(), ctx = new ContextImpl());
    Scope sc = new MapScope();
    sc.putVal("firstName", "foo");
    sc.putVal("lastName", "bar");
    sc.putVal("age", "25");
    ctx.addScope("test", sc);

    FormStep step = new FormStep();
    step.createParam().setFrom("firstName").setTo("firstName");
    step.createParam().setFrom("lastName").setTo("lastName");
    step.createParam().setFrom("age").setTo("age");
    step.setClass(TestFormBean.class.getName());
    step.execute(res);

    TestFormBean bean = (TestFormBean) ctx.pop();
    super.assertEquals("foo", bean.getFirstName());
    super.assertEquals("bar", bean.getLastName());
    super.assertEquals(25, bean.getAge());

  }
  
  public void testExecuteWithScope(){
    ContextImpl ctx;
    Result res = new Result(new StateMachine(), ctx = new ContextImpl());    
    Scope sc = new MapScope();
    sc.putVal("firstName", "foo");
    sc.putVal("lastName", "bar");
    sc.putVal("age", "25");
    ctx.addScope("test", sc);
    
    FormStep step = new FormStep();
    step.createParam().setFrom("firstName").setTo("firstName")
        .setScopes("test");
    step.createParam().setFrom("lastName").setTo("lastName").setScopes("test");
    step.createParam().setFrom("age").setTo("age").setScopes("test");
    step.setClass(TestFormBean.class.getName());
    step.execute(res);
    TestFormBean bean = (TestFormBean) ctx.pop();
    super.assertEquals("foo", bean.getFirstName());
    super.assertEquals("bar", bean.getLastName());
    super.assertEquals(25, bean.getAge());
    
  }
}
