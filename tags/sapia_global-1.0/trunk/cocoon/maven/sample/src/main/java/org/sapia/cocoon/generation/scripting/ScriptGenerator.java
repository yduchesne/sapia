package org.sapia.cocoon.generation.scripting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpSession;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.AbstractGenerator;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.sapia.cocoon.util.NullSession;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class ScriptGenerator extends AbstractGenerator implements ApplicationContextAware{
  
  public static final String DEFAULT_LANG = "php";
  
  private Source sourceObj;
  private String lang;
  private ApplicationContext appContext;
  
  public void setApplicationContext(ApplicationContext ctx) throws BeansException {
    this.appContext = ctx;
  }
  
  public void setup(SourceResolver resolver, Map model, String src, Parameters params) throws ProcessingException, SAXException, IOException {
    super.setup(resolver, model, src, params);
     sourceObj = resolver.resolveURI(this.source);  
    if(!sourceObj.exists()){
      throw new SourceNotFoundException(src);
    }
    lang = params.getParameter("lang", DEFAULT_LANG);
  }
  
  public void generate() throws IOException, SAXException, ProcessingException {
    ScriptEngineManager scriptManager = new ScriptEngineManager();

    ScriptEngine engine = scriptManager.getEngineByExtension(lang);
    
    ScriptContext context = engine.getContext();
    
    
    Request  req =  (Request)ObjectModelHelper.getRequest(this.objectModel);
    Response res = (Response)ObjectModelHelper.getResponse(this.objectModel);
    HttpSession session = req.getSession();
    if(session == null){
      session = new NullSession();
    }
    engine.put("session", session);
    engine.put("request", req);
    engine.put("response", res);
    engine.put("cocoon",  appContext);
    
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      Writer writer = new OutputStreamWriter(bos);
      context.setWriter(new OutputStreamWriter(bos));
      engine.eval(new InputStreamReader(sourceObj.getInputStream()), context);
      writer.flush();
      writer.close();
      XMLReader reader = XMLReaderFactory.createXMLReader();
      reader.setContentHandler(contentHandler);
      reader.parse(new InputSource(new ByteArrayInputStream(bos.toByteArray())));
    } catch(SAXException e){
      throw e;
    } catch (Exception ex) {
      throw new ProcessingException("Could not process " + sourceObj.getURI(), ex);
    }
  }
  
  public void recycle() {
    super.recycle();
    this.sourceObj = null;
    this.lang = null;
  }

}
