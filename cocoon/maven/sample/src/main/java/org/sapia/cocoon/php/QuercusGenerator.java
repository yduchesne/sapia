package org.sapia.cocoon.php;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.script.ScriptContext;
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

import com.caucho.quercus.Quercus;
import com.caucho.quercus.script.QuercusScriptEngineFactory;

public class QuercusGenerator extends AbstractGenerator implements ApplicationContextAware{
  
  private Source sourceObj;
  private ApplicationContext appContext;
  
  private Quercus quercus;
  
  public void setApplicationContext(ApplicationContext ctx) throws BeansException {
    this.appContext = ctx;
  }
  
  public Quercus getQuercus() {
    return quercus;
  }

  public void setQuercus(Quercus quercus) {
    this.quercus = quercus;
  }  
  
  public void setup(SourceResolver resolver, Map model, String src, Parameters params) throws ProcessingException, SAXException, IOException {
    if(this.quercus == null){
      throw new IllegalStateException("Quercus instance not set");
    }
    super.setup(resolver, model, src, params);
     sourceObj = resolver.resolveURI(this.source);  
    if(!sourceObj.exists()){
      throw new SourceNotFoundException(src);
    }
  }
  
  public void generate() throws IOException, SAXException, ProcessingException {
   
    QuercusScriptEngineFactory factory = new QuercusScriptEngineFactory();
    CocoonQuercusScriptEngine engine =  new CocoonQuercusScriptEngine(factory, quercus);
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
  }
  
}
