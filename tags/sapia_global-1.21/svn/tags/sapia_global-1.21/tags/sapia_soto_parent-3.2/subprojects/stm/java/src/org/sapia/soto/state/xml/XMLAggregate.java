/*
 * XMLAggregate.java
 *
 * Created on April 6, 2005, 5:40 PM
 */

package org.sapia.soto.state.xml;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.StepState;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 *
 * @author yduchesne
 */
public class XMLAggregate extends StepState implements Step, ObjectCreationCallback{
  
  private static final String EMPTY_STR = "";
  private static final AttributesImpl ATTRIBUTES = new AttributesImpl();  
  
  
  private String _uri  = EMPTY_STR;
  private String _prefix = EMPTY_STR;
  private String _name;
  private String _qName;
  
  /** Creates a new instance of XMLAggregate */
  public XMLAggregate() {
  }
  
  public void setUri(String uri){
    _uri = uri;
  }
  
  public void setPrefix(String prefix){
    _prefix = prefix;
  }
  
  public void setlocalName(String name){
    _name = name;
  }
  
  public String getName(){
    return ClassUtils.getShortClassName(getClass());
  }
  
  public void execute(Result result){
    XMLContext ctx = (XMLContext)result.getContext();
    ContentHandler handler = ctx.getContentHandler();
    try{
      handler.startElement(_uri, _name, _qName, ATTRIBUTES);
    }catch(SAXException e){
      result.error("Could not start aggregation", e);
      return;
    }
    super.execute(result);
    if(!result.isError()){
      try{
        handler.endElement(_uri, _name, _qName);
      }catch(SAXException e){
        result.error("Could not end aggregation", e);
        return;
      }
    }
  }
  
  public Object onCreate() throws org.sapia.util.xml.confix.ConfigurationException{
    if(_prefix.length() > 0){
      _qName = _prefix + ":" + _name;
    }
    else{
      _qName = _name;
    }
    return this;
  }
  
}
