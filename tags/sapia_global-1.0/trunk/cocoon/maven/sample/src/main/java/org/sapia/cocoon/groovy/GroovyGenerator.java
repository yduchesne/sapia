package org.sapia.cocoon.groovy;

import groovy.text.GStringTemplateEngine;
import groovy.text.Template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

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

public class GroovyGenerator extends AbstractGenerator implements ApplicationContextAware{
  
  private Source sourceObj;
  private ApplicationContext appContext;
  private GroovyBean groovyBean;
  
  public void setApplicationContext(ApplicationContext ctx) throws BeansException {
    this.appContext = ctx;
  }
  
  public void setGroovy(GroovyBean bean) {
    this.groovyBean = bean;
  }  
  
  public void setup(SourceResolver resolver, Map model, String src, Parameters params) throws ProcessingException, SAXException, IOException {
    if(this.groovyBean == null){
      throw new IllegalStateException("Groovy bean instance not set");
    }
    super.setup(resolver, model, src, params);
     sourceObj = resolver.resolveURI(this.source);  
    if(!sourceObj.exists()){
      throw new SourceNotFoundException(src);
    }
  }
  
  public void generate() throws IOException, SAXException, ProcessingException {

    Request  req =  (Request)ObjectModelHelper.getRequest(this.objectModel);
    Response res = (Response)ObjectModelHelper.getResponse(this.objectModel);
    HttpSession session = req.getSession();
    if(session == null){
      session = new NullSession();
    }
    
    Map<String, Object> binding = new HashMap<String, Object>();
   
    binding.put("session", session);
    binding.put("request", req);
    binding.put("response", res);
    binding.put("cocoon",  appContext);
    
    try {
      
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      Writer writer = new OutputStreamWriter(bos);
      GStringTemplateEngine engine = new GStringTemplateEngine(groovyBean.getClassLoader());
      Template tpl = engine.createTemplate(new InputStreamReader(sourceObj.getInputStream()));
      tpl.make(binding).writeTo(writer);
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
  }
  
}
