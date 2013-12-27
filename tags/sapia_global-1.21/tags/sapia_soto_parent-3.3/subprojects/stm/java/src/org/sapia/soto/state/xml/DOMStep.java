/*
 * DOMStep.java
 *
 * Created on July 19, 2005, 3:39 PM
 *
 */

package org.sapia.soto.state.xml;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.utils.DOMBuilder;
import org.sapia.soto.state.AbstractStep;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateExecException;
import org.sapia.soto.state.StatePath;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;

/**
 *
 * @author yduchesne
 */
public class DOMStep extends AbstractStep{
  
  private StatePath _state;
  
  /** Creates a new instance of DOMStep */
  public DOMStep() {
  }
  
  public void setState(String state){
    _state = StatePath.parse(state);
  }
  
  public void setTarget(String state){
    setState(state);
  }  
  
  public void execute(Result res){
    if(_state == null){
      throw new IllegalStateException("Target state not set");
    }
    XMLContext ctx = (XMLContext)res.getContext();
    Document doc = null;
    DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
    fac.setNamespaceAware(true);
    fac.setValidating(false);
    try{
      doc = fac.newDocumentBuilder().newDocument();
    }catch(ParserConfigurationException e){
      res.error("Could not create DOM; Document instance could not be instantiated", e);
      return;
    }
    DOMBuilder builder = new DOMBuilder(doc);
    ContentHandler original = ctx.getContentHandler();
    ctx.setContentHandler(builder);
    try{
      res.exec(_state.copy());
    }catch(StateExecException e){
      res.error("Could not create DOM; error triggering target state", e);
      return;
    }finally{
      ctx.setContentHandler(original);
    }
    
    if(doc != null){
      ctx.push(doc);
    }
  }
  
}
