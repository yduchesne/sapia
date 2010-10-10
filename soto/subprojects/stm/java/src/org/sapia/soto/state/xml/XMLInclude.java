/*
 * XMLInclude.java
 *
 * Created on April 6, 2005, 7:25 AM
 */

package org.sapia.soto.state.xml;

import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.Debug;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StatePath;
import org.sapia.soto.state.Step;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

/**
 * This class implements a <code>State</code>/<code>Step</code> that includes an XML input: the
 * path to a state that results in XML SAX events is given. These SAX events are piped
 * to the <code>XMLContext</code> that this instance receives.
 * <p>
 * An instance of this class can in addition transform the given SAX events through an
 * transformation pipelines (specified as a series of <code>StyleStep</code>s.
 * 
 * @see StyleStep
 * 
 * @author yduchesne
 */
public class XMLInclude extends TransformState implements Step{
  
  private StatePath _path;
  private TransformerFactory _fac;  
  
  /** Creates a new instance of XMLInclude */
  public XMLInclude() {
    _fac = TransformerFactory.newInstance();
  }
  
  public String getName(){
    return ClassUtils.getShortClassName(getClass());
  }
  
  public void setTarget(String id){
    _path = StatePath.parse(id);
  }

  /**
   * @see org.sapia.soto.state.xml.TransformState#process(org.sapia.soto.state.Result,
   *      org.xml.sax.ContentHandler)
   */
  protected void process(Result result, ContentHandler handler) {

    XMLContext ctx = (XMLContext)result.getContext();
    
    if(_path == null){
      if(ctx.currentObject() instanceof Node){
        try{
          execute(new DOMSource((Node)result.getContext().currentObject()), 
            ctx.getViewParams(), 
            handler);
        }catch(Throwable t){
          result.error("Could not include XML", t);
          return;
        }
      }
      else{
        throw new IllegalStateException("Target state not specified");
      }
    }

    XMLIncludeReader reader = new XMLIncludeReader(_path.copy(), result);
    try{
      execute(new SAXSource(reader, new XMLIncludeInputSource()), 
        ctx.getViewParams(), 
        handler);
    }catch(Throwable t){
      result.error("Could not include XML", t);
    }
  }

  protected void execute(Object model, Map viewParams, ContentHandler handler)
      throws Throwable {

    SAXSource src = (SAXSource)model;
    SAXResult res = new SAXResult(handler);
    Transformer tx = _fac.newTransformer();
    
    if(Debug.DEBUG){
      Debug.debug(getClass(), "Chaining to " + handler);
    }
    
    Iterator params = viewParams.entrySet().iterator();
    Map.Entry entry;
    while(params.hasNext()) {
      entry = (Map.Entry) params.next();
      tx.setParameter(entry.getKey().toString(), entry.getValue());
    }
    tx.transform(src, res);
  }  
  
  
}
