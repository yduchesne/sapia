/*
 * Choose.java
 *
 * Created on May 31, 2005, 9:09 AM
 */

package org.sapia.soto.state.xml.xpath;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.jaxen.JaxenException;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.config.StepContainer;

/**
 *
 * @author yduchesne
 */
public class Choose implements Step{
  
  private List _whens = new ArrayList();
  private Otherwise _otherwise;
  
  
  /** Creates a new instance of Choose */
  public Choose() {
  }
  
  public String getName(){
    return ClassUtils.getShortClassName(getClass());
  }
  
  public void execute(Result res){
    for(int i = 0; i < _whens.size(); i++){
      When w = (When)_whens.get(i);
      if(w.doExecute(res)){
        return;
      }
    }
    if(_otherwise != null){
      _otherwise.execute(res);
    }
  }
  
  public When createWhen(){
    When w = new When();
    _whens.add(w);
    return w;
  }
  
  public Otherwise createOtherwise(){
    if(_otherwise == null){
      _otherwise = new Otherwise();
    }
    return _otherwise;
  }
  
  //////////////////// INNER CLASSES /////////////////////
  
  public static class When extends StepContainer{
    
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
      _expr = new XPathExpr();
      return _expr;
    }
    
    public XPathExpr createTest(){
      _expr = new XPathExpr();
      return _expr;
    }    

    public void setPush(boolean push){
      _push = push;
    }

    boolean doExecute(Result res){
      if(_expr == null){
        throw new IllegalStateException("XPath expression not set");
      }
      if(res.getContext().hasCurrentObject()){
        Object result;
        try{
          result = _expr.getExpr().selectSingleNode(res.getContext().currentObject());
          if(result != null){
            if(result instanceof Boolean && !((Boolean)result).booleanValue()){            
              return false;
            }
            if(_push){
              res.getContext().push(result);
            }
            super.execute(res);
            if(_push){
              res.getContext().pop();
            }
            return true;
          }
          else{
            return false;
          }
        }catch(JaxenException e){
          res.error("Could not process XPath expression '" + 
            _expr.toString() + "' on " + res.getContext().currentObject(), e);
        }
      }
      return false;
    }    
  }
  
  public static class Otherwise extends StepContainer{
    
  }
  
}
