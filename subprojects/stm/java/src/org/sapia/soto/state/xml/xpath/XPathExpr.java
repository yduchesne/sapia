/*
 * XPathExpr.java
 *
 * Created on June 1, 2005, 8:35 AM
 */

package org.sapia.soto.state.xml.xpath;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.NullObject;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author yduchesne
 */
public class XPathExpr {
  
  private XPath _xpath;
  
  /** Creates a new instance of XPathExpr */
  public XPathExpr() {
  }
  
  public void setExpr(String expr) throws Exception{
    _xpath = new DOMXPath(expr);
  }
  
  public XPath getExpr(){
    return _xpath;
  }
  
  public Namespace createNamespace(){
    return new Namespace();
  }
  
  void addNamespace(Namespace ns) throws JaxenException{
    _xpath.addNamespace(ns._prefix, ns._uri);
  }
  
  public static class Namespace implements ObjectCreationCallback{
    
    private String _prefix, _uri;
    private XPathExpr _expr;
    
    public void setPrefix(String prefix){
      _prefix = prefix;
    }
    
    public void setUri(String uri){
      _uri = uri;
    }
    
    public Object onCreate() throws ConfigurationException{
      if(_prefix == null){
        throw new ConfigurationException("Namespace prefix not set");
      }
      if(_uri == null){
        throw new ConfigurationException("Namespace uri not set");
      }      
      try{
        _expr.addNamespace(this);
      }catch(JaxenException e){
        throw new ConfigurationException("Could not configure namespace", e);
      }
      return new NullObject(){};
    }
  }
    
   
  
  
  
}
