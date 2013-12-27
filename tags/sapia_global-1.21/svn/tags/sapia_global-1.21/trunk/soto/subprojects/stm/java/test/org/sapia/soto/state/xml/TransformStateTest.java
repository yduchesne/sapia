package org.sapia.soto.state.xml;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.state.StateMachine;
import org.xml.sax.InputSource;

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
public class TransformStateTest extends TestCase {

  public TransformStateTest(String name) {
    super(name);
  }

  public void testNoStylesheet() throws Exception {
    SotoContainer cont = new SotoContainer();
    StateMachine stm = new StateMachine();
    TransformState st = new TransformState();
    st.setEnv(cont.toEnv());
    st.setId("txform");
    stm.addState(st);
    stm.init();
    XMLContext ctx = new XMLContextImpl();

    OutputFormat format = new OutputFormat("xml", "UTF-8", true);
    Serializer ser = SerializerFactory.getSerializerFactory("xml")
        .makeSerializer(System.out, format);
    ctx.setContentHandler(ser.asContentHandler());
    ctx.push(new InputSource(new FileInputStream(new File("etc/stm/dog.xml"))));
    stm.execute("txform", ctx);
  }

  public void testSingleStylesheet() throws Exception {
    SotoContainer cont = new SotoContainer();
    StateMachine stm = new StateMachine();
    TransformState st = new TransformState();
    st.setEnv(cont.toEnv());
    st.setId("txform");
    stm.addState(st);
    StyleStep step = new StyleStep();
    step.setEnv(cont.toEnv());
    step.setSrc("etc/stm/dogToCat.xsl");
    st.addExecutable(step);
    stm.init();
    XMLContext ctx = new XMLContextImpl();
    OutputFormat format = new OutputFormat("xml", "UTF-8", true);
    Serializer ser = SerializerFactory.getSerializerFactory("xml")
        .makeSerializer(System.out, format);
    ctx.setContentHandler(ser.asContentHandler());
    ctx.push(new InputSource(new FileInputStream(new File("etc/stm/dog.xml"))));
    stm.execute("txform", ctx);
  }

  public void testMultiStylesheets() throws Exception {
    SotoContainer cont = new SotoContainer();
    StateMachine stm = new StateMachine();
    TransformState st = new TransformState();
    st.setEnv(cont.toEnv());
    st.setId("txform");
    stm.addState(st);
    StyleStep step1 = new StyleStep();
    step1.setEnv(cont.toEnv());
    step1.setSrc("etc/stm/dogToCat.xsl");
    st.addExecutable(step1);
    StyleStep step2 = new StyleStep();
    step2.setEnv(cont.toEnv());
    step2.setSrc("etc/stm/catToDog.xsl");
    st.addExecutable(step2);
    stm.init();
    XMLContext ctx = new XMLContextImpl();
    OutputFormat format = new OutputFormat("xml", "UTF-8", true);
    Serializer ser = SerializerFactory.getSerializerFactory("xml")
        .makeSerializer(System.out, format);
    ctx.setContentHandler(ser.asContentHandler());
    ctx.push(new InputSource(new FileInputStream(new File("etc/stm/dog.xml"))));
    stm.execute("txform", ctx);
  }

}
