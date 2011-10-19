package org.sapia.soto.freemarker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.sapia.soto.Env;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.freemarker.stm.FreemarkerIncludeStep;
import org.sapia.soto.freemarker.stm.FreemarkerStep;
import org.sapia.soto.state.StateMachine;
import org.sapia.soto.state.StepState;

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
public class FreemarkerTest extends TestCase {

  private SotoContainer _soto;
  
  public FreemarkerTest(String name) {
    super(name);
  }
  
  public void setUp() throws Exception {
    _soto = new SotoContainer();
  }

  public void testTemplate() throws Exception {
    SotoContainer cont = new SotoContainer();
    Env env = cont.toEnv();
    FreemarkerServiceImpl service = new FreemarkerServiceImpl();
    service.setEnv(env);
    ServiceMetaData meta = new ServiceMetaData(_soto, "test", service, new ArrayList());
    service.init();
    cont.bind(meta);
    cont.start();
    StateMachine stm = new StateMachine();
    StepState state = new StepState();
    state.setId("test");
    FreemarkerStep step = new FreemarkerStep();
    step.setEnv(env);
    step.setSrc("etc/freemarker/plain.ftl");
    state.addExecutable(step);
    stm.addState(state);
    TestContext ctx = new TestContext();
    ctx.getViewParams().put("message", "Hello World");
    stm.init();
    stm.execute("test", ctx);
    super.assertEquals("Hello World", new String(((ByteArrayOutputStream) ctx
        .getOutputStream()).toByteArray()));
  }

  public void testi18Template() throws Exception {
    SotoContainer cont = new SotoContainer();
    Env env = cont.toEnv();
    FreemarkerServiceImpl service = new FreemarkerServiceImpl();
    service.setEnv(env);
    ServiceMetaData meta = new ServiceMetaData(_soto, "test", service, new ArrayList());
    service.init();
    cont.bind(meta);
    cont.start();
    StateMachine stm = new StateMachine();
    StepState state = new StepState();
    state.setId("test");
    FreemarkerStep step = new FreemarkerStep();
    step.setEnv(env);
    step.setSrc("etc/freemarker/plain.ftl");
    state.addExecutable(step);
    stm.addState(state);
    TestContext ctx = new TestContext();
    String content = "ça c'est hétéroclyte (ne regardez pas les phôtes)";
    ctx.getViewParams().put("message", content);
    stm.init();
    stm.execute("test", ctx);
    String result = new String(((ByteArrayOutputStream) ctx.getOutputStream())
        .toByteArray());
    System.out.println(result);
    super.assertEquals(content, result);
  }

  public void testIncludeTemplate() throws Exception {
    SotoContainer cont = new SotoContainer();
    Env env = cont.toEnv();
    FreemarkerServiceImpl service = new FreemarkerServiceImpl();
    service.setEnv(env);
    ServiceMetaData meta = new ServiceMetaData(_soto, "test", service, new ArrayList());
    service.init();
    cont.bind(meta);
    cont.start();
    StateMachine stm = new StateMachine();
    StepState state = new StepState();
    state.setId("test");
    FreemarkerStep step = new FreemarkerStep();
    FreemarkerIncludeStep include = new FreemarkerIncludeStep();
    include.setEnv(env);
    include.setSrc("etc/freemarker/plain.ftl");
    include.setName("include");
    step.setEnv(env);
    step.setSrc("etc/freemarker/include.ftl");
    step.addExecutable(include);
    state.addExecutable(step);
    stm.addState(state);
    TestContext ctx = new TestContext();
    ctx.getViewParams().put("message", "Hello World");
    stm.init();
    stm.execute("test", ctx);
    super.assertEquals("Hello World Hello World", new String(
        ((ByteArrayOutputStream) ctx.getOutputStream()).toByteArray()));

  }
  
  public void testLoadConfig() throws Exception{
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/freemarker/freemarker.soto.xml"));
    cont.start();
    FreemarkerService fmk = (FreemarkerService)cont.lookup(FreemarkerService.class);
    fmk.resolveTemplate("test.ftl");
    System.out.println("loaded !!!!!!");
    cont.dispose();
  }

}
