/*
 * Select.java
 *
 * Created on May 31, 2005, 9:22 AM
 */

package org.sapia.soto.state.xml.xpath;

import org.apache.commons.lang.ClassUtils;
import org.jaxen.JaxenException;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.config.StepContainer;

/**
 *
 * @author yduchesne
 */
public class Select extends StepContainer implements Step {
  
  private XPathExpr _expr;
  private String _id;

  public void setId(String id){
    _id = id;
  }

  public String getId(){
    return _id;
  } 
    
  public String getName(){
    return ClassUtils.getShortClassName(getClass());
  }
  
  public void setXpath(String expr) throws Exception{
    _expr = new XPathExpr();
    _expr.setExpr(expr);
  }
  
  public XPathExpr createXpath(){
    _expr = new XPathExpr();
    return _expr;
  }  
  
  public void execute(Result res){
    if(_expr == null){
      throw new IllegalStateException("XPath expression not set");
    }
    if(res.getContext().hasCurrentObject()){
      Object result;
      try{
        result = _expr.getExpr().selectSingleNode(res.getContext().currentObject());
        if(result != null){
          res.getContext().push(result);
          super.execute(res);
          res.getContext().pop();
        }
      }catch(JaxenException e){
        res.error("Could not process XPath expression '" + 
          _expr.toString() + "' on " + res.getContext().currentObject(), e);
      }
    }    
  }
}
