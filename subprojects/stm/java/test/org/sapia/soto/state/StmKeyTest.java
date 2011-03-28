package org.sapia.soto.state;

import junit.framework.TestCase;

public class StmKeyTest extends TestCase {

  public StmKeyTest(String arg0) {
    super(arg0);
  }
  
  public void testLookup(){
    StateMachine stm = new StateMachine();
    ContextImpl ctx = new ContextImpl();
    ctx.addScope("params", new MapScope());
    Result res = new Result(stm, ctx);
    ctx.put("bean", new TestBean(), "params");
    StmKey key = StmKey.parse("bean:params");
    super.assertTrue(key.lookup(res) instanceof TestBean);
    key = StmKey.parse("bean.time:params");
    super.assertTrue(key.lookup(res) instanceof Long);    
  }
  
  public static class TestBean{
    
    public long getTime(){
      return 1;
    }
    
    public void setTime(long time){}
    
  }

}
