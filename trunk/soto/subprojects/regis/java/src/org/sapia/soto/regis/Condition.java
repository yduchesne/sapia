package org.sapia.soto.regis;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.sapia.util.xml.ProcessingException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.Dom4jProcessor;
import org.sapia.util.xml.confix.XMLConsumer;
import org.xml.sax.InputSource;

public class Condition extends PropertyRefTag implements XMLConsumer{
  
  private Element _elem;
  private String _value;
  
  /** Creates a new instance of Condition */
  public Condition() {
  }
  
  public void setValue(String value){
    _value = value;
  }
  
  public void consume(InputSource input) throws Exception {
    if(_elem != null) {
      throw new ConfigurationException(getClass().getName() + " only takes a SINGLE nested xml element");
    }    
    SAXReader reader = new SAXReader();
    Element elem = reader.read(input).getRootElement();              
    _elem = elem;
  }  
  
  protected boolean isEqual() throws ConfigurationException{
    String property = (String)super.getProperty();
    
    if(_value == null){
      if(property == null){
        return false;
      }
      else{
        return true;
      }
    }
    else{
      if(property == null){
        return false;
      }
      else{
        return _value.equals(property);
      }
    }    
  }
  
  protected Object process() throws ConfigurationException{
    Dom4jProcessor proc = new Dom4jProcessor(env().getObjectFactory());
    try {
      return proc.process(null, _elem);
    } catch(ProcessingException e) {
      throw new ConfigurationException(
          "Could not process nested XML in " + getClass().getName(), e);
    }
  }    

}
