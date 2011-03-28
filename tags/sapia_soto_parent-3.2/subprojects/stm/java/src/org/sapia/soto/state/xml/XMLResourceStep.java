/*
 * XMLResourceStep.java
 *
 * Created on April 11, 2005, 8:52 AM
 */

package org.sapia.soto.state.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;
import org.sapia.resource.Resource;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.xml.sax.SAXException;

/**
 * This class can be used as a <code>State</code>, or as a <code>Step</code>
 * within a <code>State</code>.
 *
 * An instance of this class parses an XML file, piping the resulting SAX events
 * to the <code>XMLContext</code> instance passed to this instance upon execution.
 *
 * @author yduchesne
 */
public class XMLResourceStep implements Step, EnvAware, ObjectCreationCallback{
  
  private String _uri;
  private Env _env;
  private Resource _res;

  
  /** Creates a new instance of XMLResourceStep */
  public XMLResourceStep() {
  }
  
  public String getName(){
    return ClassUtils.getShortClassName(getClass());
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_uri == null){
      throw new ConfigurationException("Source not specified for XML resource");
    }
    try{
      _res = _env.resolveResource(_uri);
    }catch(IOException e){
      throw new ConfigurationException("Could not resolve resource: " + _uri, e);
    }
    return this;
  }
  
  public void setSrc(String uri){
    _uri = uri;
  }
  
  public void execute(Result result){
    InputStream is = null;
    SAXParserFactory factory = SAXParserFactory.newInstance();    
    factory.setValidating(false);    
    factory.setNamespaceAware(true);
    try{
      SAXParser parser = factory.newSAXParser();
      parser.parse(_res.getInputStream(), new DefaultHandlerAdapter(((XMLContext)result.getContext()).getContentHandler()));
    }catch(IOException e){
      result.error("Could not read resource: " + _res.getURI()); 
    }catch(SAXException e){
      result.error("Could not instantiate SAX parser", e);      
    }catch(ParserConfigurationException e){
      result.error("Could not instantiate SAX parser", e);
    }finally{
      if(is != null){
        try{
          is.close();
        }catch(IOException e){
        }
      }
    }

  }
  
  
}
