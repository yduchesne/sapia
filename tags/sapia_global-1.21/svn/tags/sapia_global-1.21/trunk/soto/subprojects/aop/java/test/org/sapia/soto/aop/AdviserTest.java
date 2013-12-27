package org.sapia.soto.aop;

import junit.framework.TestCase;

import org.sapia.soto.SotoContainer;

/**
 * @author Yanick Duchesne 29-Sep-2003
 */
public class AdviserTest extends TestCase {
  /**
   * Constructor for AdviserTest.
   */
  public AdviserTest(String name) {
    super(name);
  }

  public void testMethodWithoutSig() throws Exception {
    Adviser adviser = new Adviser();
    TestAroundAdvice advice;
    AdviceDef def = adviser.createAdviceDef();
    def.setAdvice(advice = new TestAroundAdvice());
    def.setId("around");

    MethodPointCut pc = adviser.createMethod();
    pc.setName("first*");

    AdviceRef ref = pc.createAdviceRef();
    ref.setId("around");

    TestService svc = new TestService();
    svc = (TestService) adviser.advise(svc);
    svc.firstMethod();
    svc.firstMethod("someArg");

    super.assertEquals(2, advice.preCount);
    super.assertEquals(2, advice.postCount);
  }

  public void testMethodWithSig() throws Exception {
    Adviser adviser = new Adviser();
    TestAroundAdvice advice;
    AdviceDef def = adviser.createAdviceDef();
    def.setAdvice(advice = new TestAroundAdvice());
    def.setId("around");

    MethodPointCut pc = adviser.createMethod();
    pc.setName("first*");
    pc.setSig("java.lang.*");

    AdviceRef ref = pc.createAdviceRef();
    ref.setId("around");

    TestService svc = new TestService();
    svc = (TestService) adviser.advise(svc);
    svc.firstMethod();
    svc.firstMethod("someArg");

    super.assertEquals(1, advice.preCount);
    super.assertEquals(1, advice.postCount);
  }

  public void testBeforeAdvice() throws Exception {
    Adviser adviser = new Adviser();
    TestBeforeAdvice advice;
    AdviceDef def = adviser.createAdviceDef();
    def.setAdvice(advice = new TestBeforeAdvice());
    def.setId("before");

    MethodPointCut pc = adviser.createMethod();
    pc.setName("*");

    AdviceRef ref = pc.createAdviceRef();
    ref.setId("before");

    TestService svc = new TestService();
    svc = (TestService) adviser.advise(svc);
    svc.firstMethod();
    svc.firstMethod("someArg");
    svc.secondMethod();

    super.assertEquals(3, advice.count);
  }

  public void testAfterAdvice() throws Exception {
    Adviser adviser = new Adviser();
    TestAfterAdvice advice;
    AdviceDef def = adviser.createAdviceDef();
    def.setAdvice(advice = new TestAfterAdvice());
    def.setId("after");

    MethodPointCut pc = adviser.createMethod();
    pc.setName("*");

    AdviceRef ref = pc.createAdviceRef();
    ref.setId("after");

    TestService svc = new TestService();
    svc = (TestService) adviser.advise(svc);
    svc.firstMethod();
    svc.firstMethod("someArg");
    svc.secondMethod();

    super.assertEquals(3, advice.count);
  }

  public void testThrowsAdvice() throws Exception {
    Adviser adviser = new Adviser();
    TestThrowsAdvice advice;
    AdviceDef def = adviser.createAdviceDef();
    def.setAdvice(advice = new TestThrowsAdvice());
    def.setId("throws");

    MethodPointCut pc = adviser.createMethod();
    pc.setName("*");

    AdviceRef ref = pc.createAdviceRef();
    ref.setId("throws");

    TestService svc = new TestService();
    svc = (TestService) adviser.advise(svc);

    try {
      svc.throwsMethod();
    } catch(Exception e) {
      //ok
    }

    super.assertTrue("Throws advice was not invoked", advice.invoked);
  }

  public void testReturnAdvice() throws Exception {
    Adviser adviser = new Adviser();
    TestReturnAdvice advice;
    AdviceDef def = adviser.createAdviceDef();
    def.setAdvice(advice = new TestReturnAdvice());
    def.setId("return");

    MethodPointCut pc = adviser.createMethod();
    pc.setName("getName");

    AdviceRef ref = pc.createAdviceRef();
    ref.setId("return");

    TestService svc = new TestService();
    svc = (TestService) adviser.advise(svc);
    svc.getName();
    super.assertEquals(1, advice.count);
  }

  public void testGroup() throws Exception {
    Adviser adviser = new Adviser();
    TestAfterAdvice after = new TestAfterAdvice();
    AdviceDef outer = adviser.createAdviceDef();
    outer.setAdvice(after);
    outer.setId("outer");

    Group group = adviser.createGroup();
    group.setId("test");

    AdviceDef inner = group.createAdviceDef();
    AdviceRef outerRef = group.createAdviceRef();
    outerRef.setId("outer");

    TestAroundAdvice adv = new TestAroundAdvice();
    inner.setAdvice(adv);

    MethodPointCut pc = adviser.createMethod();
    pc.setName("*");

    GroupRef ref = pc.createGroupRef();
    ref.setId("test");

    TestService svc = new TestService();
    svc = (TestService) adviser.advise(svc);
    svc.firstMethod();
    super.assertEquals(1, adv.preCount);
    super.assertEquals(1, adv.postCount);
    super.assertEquals(1, after.count);
  }

  public void testConfig() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load("org/sapia/soto/aop/aopConfig.xml");
    cont.start();
    cont.dispose();
  }

  public void testImplements() throws Exception {
    Adviser adviser = new Adviser();
    adviser
        .setImplements("java.io.Serializable, org.sapia.soto.aop.TestMarker*, java.**.Comparable");

    TestImplementation impl = new TestImplementation();
    super.assertTrue(adviser.matchesImplements(impl));
    adviser.setImplements("**");
    impl = new TestImplementation();
    super.assertTrue(adviser.matchesImplements(impl));
    adviser.setImplements("org.sapia.soto.aop.TestMarker*");
    impl = new TestImplementation();
    super.assertTrue(adviser.matchesImplements(impl));
  }

  public void testPreInvocation() throws Exception {
    Adviser adviser = new Adviser();
    TestBeforeAdviceInvoker first;
    TestBeforeAdvice second;
    TestAfterAdvice third;
    AdviceDef firstDef = adviser.createAdviceDef();
    AdviceDef secondDef = adviser.createAdviceDef();
    AdviceDef thirdDef = adviser.createAdviceDef();
    firstDef.setAdvice(first = new TestBeforeAdviceInvoker());
    firstDef.setId("first");
    secondDef.setAdvice(second = new TestBeforeAdvice());
    secondDef.setId("second");
    thirdDef.setAdvice(third = new TestAfterAdvice());
    thirdDef.setId("third");

    MethodPointCut pc = adviser.createMethod();
    pc.setName("*");

    pc.createAdviceRef().setId("first");
    pc.createAdviceRef().setId("second");
    pc.createAdviceRef().setId("third");

    TestService svc = new TestService();
    svc = (TestService) adviser.advise(svc);
    svc.firstMethod();

    Invocation inv = first.invocation;
    super.assertTrue("Invocation not passed to advice", inv != null);
    super.assertTrue("Invocation was not called", inv.wasInvoked());
    super.assertEquals(0, second.count);
    super.assertEquals(1, third.count);
  }
}
