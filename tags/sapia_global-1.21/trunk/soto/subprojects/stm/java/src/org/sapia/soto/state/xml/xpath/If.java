/*
 * If.java
 *
 * Created on May 31, 2005, 8:18 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.sapia.soto.state.xml.xpath;

import org.apache.commons.lang.ClassUtils;
import org.jaxen.JaxenException;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.State;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.config.StepContainer;


/**
 * This class implements a <code>Step</code> that evaluates an XPath
 * expression on the current object of the execution context (the current
 * object is expected to be an instance of <code>org.w3c.dom.Node</code>).
 * <p>
 * An instance of this class executes its nested steps if the XPath expression
 * evaluates to a <code>Node</code>.
 *
 * @author yduchesne
 */
public class If extends StepContainer implements Step, State{
  
  private XPathExpr _expr;
  private boolean _push;
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
    return _expr = new XPathExpr();
  }  
  
  public XPathExpr createTest(){
    return _expr = new XPathExpr();
  }    
  
  public void setPush(boolean push){
    _push = push;
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
          if(result instanceof Boolean && !((Boolean)result).booleanValue()){
            return;
          }
          if(_push){
            res.getContext().push(result);
          }
          super.execute(res);
          if(_push){
            res.getContext().pop();
          }          
        }
      }catch(JaxenException e){
        res.error("Could not process XPath expression '" + 
          _expr.toString() + "' on " + res.getContext().currentObject(), e);
      }
    }
  }
}
