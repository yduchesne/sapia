package org.sapia.regis.loader;

import java.util.Collections;
import java.util.HashMap;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.ProcessingException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.Dom4jProcessor;
import org.sapia.util.xml.confix.ObjectFactoryIF;
import org.sapia.util.xml.confix.XMLConsumer;
import org.xml.sax.InputSource;

public class Condition implements XMLConsumer {
  private String            _name;
  private String            _elemName;
  private String[]          _equals;
  private int[][]          _matches;  
  private TemplateContextIF _ctx;
  private ObjectFactoryIF   _fac;
  private Element           _elem;

  public Condition(String elemName) {
    _elemName = elemName;
  }
  public Condition(String elemName, TemplateContextIF ctx, ObjectFactoryIF fac) {
    _elemName = elemName;
    init(ctx, fac);
  }  

  public void setParam(String name) {
    _name = name;
  }

  public void setEquals(String eq) {
    if(eq != null){
      _equals = eq.split(",");
      for(int i = 0; i < _equals.length; i++){
        _equals[i] = _equals[i].trim();
      }
    }
  }
  
  public void setMatches(String pattern) {
    if(pattern != null){
      String[] patterns = pattern.split(",");
      _matches = new int[patterns.length][];
      for(int i = 0; i < _matches.length; i++){
        _matches[i] = UriPatternHelper.compilePattern(patterns[i].trim());
      }
    }
  }  
  
  public void setValue(String eq) {
    setEquals(eq);
  }  

  public boolean isEqual() {
    
    Object val = _ctx.getValue(_name);

    if(val == null) {
      return false;
    }

    if(_equals == null && _matches == null) {
      return true;
    }
    
    if(_equals != null){
      val = val.toString();
      for(int i = 0; i < _equals.length; i++){
        if(val.equals(_equals[i])){
          return true;
        }
      }
      return false;      
    }
    else{
      val = val.toString();
      for(int i = 0; i < _matches.length; i++){
        if(UriPatternHelper.match(new HashMap(), (String)val, _matches[i])){
          return true;
        }
      }
      return false;      
    }

  }

  public Object create() throws ConfigurationException {
    if((_elem == null) || (_name == null)) {
      return new NullObjectImpl();
    }

    if(isEqual()) {
      Dom4jProcessor proc = new Dom4jProcessor(_fac);

      try {
        return proc.process(null, _elem);
      } catch(ProcessingException e) {
        throw new ConfigurationException(
            "Could not process xml nested in 'if' element", e);
      }
    }

    return new NullObjectImpl();
  }

  /**
   * @see org.sapia.util.xml.confix.XMLConsumer#consume(org.xml.sax.InputSource)
   */
  public void consume(InputSource is) throws Exception {
    if(_elem != null) {
      throw new ConfigurationException("'" + _elemName
          + "' only takes a SINGLE nested xml element");
    }
    SAXReader reader = new SAXReader();
    _elem = reader.read(is).getRootElement();
  }
  
  protected void init(TemplateContextIF ctx, ObjectFactoryIF fac){
    _ctx = ctx;
    _fac = fac;
  }
  
  
}
