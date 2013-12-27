package org.sapia.cocoon.ruby;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
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
import org.jruby.Ruby;
import org.jruby.exceptions.RaiseException;
import org.sapia.cocoon.util.NullSession;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class JRubyGenerator extends AbstractGenerator implements ApplicationContextAware{
  
  private Source sourceObj;
  private ApplicationContext appContext;
  
  private JRubyBean rubyBean;
  
  public void setApplicationContext(ApplicationContext ctx) throws BeansException {
    this.appContext = ctx;
  }
  
  public void setJruby(JRubyBean bean) {
    this.rubyBean = bean;
  }  
  
  public void setup(SourceResolver resolver, Map model, String src, Parameters params) throws ProcessingException, SAXException, IOException {
    if(this.rubyBean == null){
      throw new IllegalStateException("JRuby bean instance not set");
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
    
    Ruby runtime = rubyBean.newInstance();
    CocoonJRubyScriptEngineFactory factory = new CocoonJRubyScriptEngineFactory(runtime);
    ScriptEngine engine = factory.getScriptEngine();

    engine.put("session", session);
    engine.put("request", req);
    engine.put("response", res);
    engine.put("cocoon",  appContext);
    
    ScriptContext context = engine.getContext();    
    
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
    } catch(ScriptException e){
      Throwable cause = e.getCause();
      if(cause instanceof RaiseException){
        RaiseException re = (RaiseException)cause; 
        throw new ProcessingException("Could not process " + sourceObj.getURI() + " - " + re.getException().asJavaString(), re);
      }
      else{
        throw new ProcessingException("Could not process " + sourceObj.getURI() + " at: " + e.getFileName() + "("+e.getLineNumber()+":"+e.getColumnNumber()+"). Message: " + e.getMessage(), cause);
      }
      
    } catch (Exception ex) {
      throw new ProcessingException("Could not process " + sourceObj.getURI(), ex);
    }
  }
  
  public void recycle() {
    super.recycle();
    this.sourceObj = null;
  }
  
}
