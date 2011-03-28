package org.sapia.soto.state.markup;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.XmlAware;
import org.sapia.soto.state.Output;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.State;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.config.StepContainer;
import org.sapia.soto.util.Utils;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * This instance serializes markup through a given {@link org.sapia.soto.state.markup.MarkupSerializer}
 * implementation. The serializer implementation to use is specified through a predefined setting 
 * (see {@link org.sapia.soto.state.markup.MarkupSerializerFactory} for more details on this setting).
 * <p>
 * That setting can be overrided for this instance only, by specifying the serializer type on this instance 
 * (see {@link #setType(String)}  
 *  
 * @author yduchesne
 *
 */
public class MarkupStep extends StepContainer 
  implements XmlAware, EnvAware, State, Step, ObjectCreationCallback, ObjectHandlerIF{
  
  static OutputStream NULL_OUTPUT = new NullOutputStream();
  
  private MarkupInfo info = new MarkupInfo();
  private String id;
  protected Env env;
  private String type;
  private String text;
  MarkupSerializer serializer;
  
  public String getId() {
    return id;
  }

  /**
   * @param id the identifier of this instance, if used as a state in a state machine.
   */
  public void setId(String id){
    this.id = id;
  }
  
  /**
   * Sets this element's text content (CDATA).
   * @param txt
   */
  public void setText(String txt){
    text = txt;
  }
  
  public String getName() {
    if(info.getName() != null) return info.getName();
    return ClassUtils.getShortClassName(getClass());
  }
  
  public void setEnv(Env env) {
    this.env = env;
  }
  
  public void setNameInfo(String name, String prefix, String uri) {
    info.setName(name);
    info.setPrefix(prefix);
    info.setUri(uri);
  }
  
  /**
   * @param uri the markup element XML namespace URI.
   */  
  public void setUri(String uri){
    info.setUri(uri);
  }
  
  /**
   * @param prefix the markup element XML namespace prefix.
   */
  public void setPrefix(String prefix){
    info.setPrefix(prefix);
  }

  /**
   * @param name the markup element name.
   */
  public void setName(String name){
    info.setName(name);
  }
  
  /**
   * @param type corresponds to the serializer type to use when performing
   * output.
   * 
   * @see MarkupSerializerFactory
   */
  public void setType(String type){
    this.type = type;
  }
  
  
  public Object onCreate() throws ConfigurationException {
    if(type != null){
      this.serializer = MarkupSerializerFactory.newInstance(type, env.getSettings());
    }
    else{
      this.serializer = MarkupSerializerFactory.newInstance(env.getSettings());  
    }
    return this;
  }
  
  public void execute(Result res) {
    PrintWriter pw = null;    
    if(res.getContext() instanceof Output){
      try{
        pw = new PrintWriter(((Output)res.getContext()).getOutputStream());
      }catch(IOException e){
        res.error("Could not acquire output stream", e);
        return;
      }
    }
    else{
      pw = new PrintWriter(NULL_OUTPUT);
    }
    try{
      serializer.start(info, res.getContext(), pw);
      if(text != null){
        serializer.text(text, res.getContext(), pw);
      }
    }catch(Exception e){
      res.error("Error serializing markup", e);
    }
    super.execute(res);
    if(res.isAborted() || res.isError()){
      return;
    }
    try{
      serializer.end(info, res.getContext(), pw);
    }catch(Exception e){
      res.error("Error serializing markup", e);
    }   
    pw.flush();
  }
  
  public void handleObject(String name, Object value) throws ConfigurationException {
    if(value instanceof String){
      info.addAttribute(name, (String)value).encodeAsAttribute = false;
    }
    else if(value instanceof Step){
      super.handleObject(name, value);
    }
    else{
      throw new ConfigurationException(Utils.createInvalidAssignErrorMsg(name, value, String.class));
    }
  }
  
  static class NullOutputStream extends OutputStream{
    public void write(int b) throws IOException {
    }
    
  }

}
