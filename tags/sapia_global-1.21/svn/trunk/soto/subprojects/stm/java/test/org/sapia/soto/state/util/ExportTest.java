/*
 * ExportTest.java
 * JUnit based test
 *
 * Created on September 12, 2005, 11:52 PM
 */

package org.sapia.soto.state.util;

import junit.framework.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.state.*;

/**
 *
 * @author yduchesne
 */
public class ExportTest extends TestCase {
  
  public ExportTest(String testName) {
    super(testName);
  }

  public void testExecute() throws Exception{
    ContextImpl ctx = new ContextImpl();
    Result res = new Result(new StateMachine(), ctx);
    Scope scope1 = new MapScope();
    Scope scope2 = new MapScope();
    scope1.putVal("key", "value");
    ctx.addScope("scope1", scope1);
    ctx.addScope("scope2", scope2);
    Export exp = new Export();
    exp.setFrom("scope1");
    exp.setTo("scope2");
    exp.setKey("key");
    exp.execute(res);
    super.assertEquals("value", ctx.get("key", "scope2"));
  }
  
  public void testExecuteWithBean() throws Throwable{
    ContextImpl ctx = new ContextImpl();
    Result res = new Result(new StateMachine(), ctx);
    TestFormBean bean = new TestFormBean();
    bean.setFirstName("foo");
    Scope scope1 = new MapScope();
    Scope scope2 = new MapScope();
    scope1.putVal("bean", bean);
    ctx.addScope("scope1", scope1);
    ctx.addScope("scope2", scope2);
    Export exp = new Export();
    exp.setFrom("scope1");
    exp.setTo("scope2");
    exp.setKey("bean.firstName");
    exp.setExportKey("firstName");
    exp.execute(res);
    super.assertEquals("foo", ctx.get("firstName", "scope2"));
    if(res.isError()){
      Err err = res.handleError();
      if(err.getThrowable() != null)
        throw err.getThrowable();
      else
        fail(err.getMsg());
    }
    
  }  
  
}
