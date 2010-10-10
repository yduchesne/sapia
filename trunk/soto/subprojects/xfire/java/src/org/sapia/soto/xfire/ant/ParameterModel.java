package org.sapia.soto.xfire.ant;

import java.util.Date;

import javax.jws.WebParam;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaParameter;

/**
 * Holds data about a method parameter.
 * 
 * @author yduchesne
 *
 */
public class ParameterModel {

  private MethodModel method;
  private WebParam wp;
  private JavaParameter parameter;
  private Class type;
  private DocletTag tag;
  private int index;
  
  ParameterModel(MethodModel method, WebParam param, 
      JavaParameter parameter, 
      Class type, 
      DocletTag tag, int index){
    this.method = method;
    this.wp = param;
    this.parameter = parameter;
    this.tag = tag;
    this.type = type;
    this.index = index;
  }
  
  /**
   * @return the <code>Class</code> of this instance's corresponding
   * parameter.
   */
  public Class getType(){
    return type;
  }
  
  /**
   * @return the QDox <code>JavaClass</code> of this instance's parameter.
   */
  public JavaClass getJavaClass(){
    return parameter.getType().getJavaClass();
  }
  
  /**
   * @return the order in which this instance's parameter appears in its 
   * corresponding method's signature.
   */
  public int getIndex(){
    return index;
  }

  /**
   * @return the name of this parameter, as specified in the JavaDoc.
   */
  public String getName(){
    if(wp == null || wp.name() == null || wp.name().length() == 0){
      return parameter.getName();
    }
    else{
      return wp.name();
    }
  }
  
  /**
   * @return the <code>DocletTag</code> of this instance's
   * parameter.
   */
  public DocletTag getDocTag(){
    return tag;
  }    

  /**
   * @return the well-formatted documentation of this instance's parameter.
   */  
  public String getDoc(){
    int i = tag.getValue().indexOf(" ");
    if(i <= 0 || i == tag.getValue().length() - 1){
      return tag.getValue();
    }
    else{
      return tag.getValue().substring(i+1);
    }
  }
  
  /**
   * @return <code>true</code> if this instance's parameter is a primitive.
   */
  public boolean isPrimitive(){
    return parameter.getType().isPrimitive();
  }
  
  /**
   * @return <code>true</code> if this instance's parameter is a <code>java.lang.String</code>.
   */
  public boolean isString(){
    return getJavaClass().getFullyQualifiedName().equals(String.class.getName());
  }
  
  /**
   * @return <code>true</code> if this instance's parameter is a <code>java.util.Date</code>.
   */  
  public boolean isDate(){
    return getJavaClass().getFullyQualifiedName().equals(Date.class.getName());
  }
  
  /**
   * @return the target namespace of this instance's parameter.
   */
  public String getTargetNamespace(){
    if(wp == null || wp.targetNamespace() == null){
      return method.getWebService().getAnnotation().targetNamespace();
    }
    else{
      return wp.targetNamespace(); 
    }
  }

}
