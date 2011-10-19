/*
 * DomifyViewTest.java
 * JUnit based test
 *
 * Created on October 6, 2005, 2:25 PM
 */

package org.sapia.soto.state.cocoon.view;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.apache.commons.lang.ClassUtils;
import org.infohazard.domify.DOMAdapter;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateMachine;
import org.sapia.soto.state.StateRuntimeException;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.cocoon.CocoonContext;
import org.sapia.soto.state.xml.StyleStep;
import org.sapia.soto.state.xml.TransformState;
import org.sapia.soto.state.xml.XMLContext;
import org.sapia.soto.state.xml.XMLContextImpl;
import org.sapia.util.xml.confix.ConfigurationException;
import org.xml.sax.ContentHandler;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;


/**
 *
 * @author yduchesne
 */
public class DomifyViewTest extends TestCase {
  
  public DomifyViewTest(String testName) {
    super(testName);
  }

  /**
   * Test of execute method, of class org.sapia.soto.state.cocoon.view.DomifyView.
   */
  public void testExecuteDOM() throws Exception{
    SotoContainer cont = new SotoContainer();
    StateMachine stm = new StateMachine();
    DomifyView view = new DomifyView();
    view.setEnv(cont.toEnv());
    view.setId("domify");
    view.setSrc("etc/stm/dogToCat.xsl");
    stm.addState(view);
    stm.init();
    
    DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
    fac.setNamespaceAware(true);
    fac.setValidating(false);

    XMLContext ctx = new XMLContextImpl();
    OutputFormat format = new OutputFormat("xml", "UTF-8", true);
    Serializer ser = SerializerFactory.getSerializerFactory("xml")
        .makeSerializer(System.out, format);
    ctx.setContentHandler(ser.asContentHandler());
    ctx.push(fac.newDocumentBuilder().parse(new File("etc/stm/dog.xml")));
    stm.execute("domify", ctx);
  }

  
}
