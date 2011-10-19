package org.sapia.soto.xfire.ant;

import java.util.Date;

import javax.jws.WebResult;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Type;

/**
 * Holds data about a method's return value.
 * 
 * @author yduchesne
 *
 */
public class ReturnModel {
  
  private MethodModel method;
  private WebResult result;
  private Type type;
  private DocletTag tag;
  
  ReturnModel(MethodModel method, WebResult wr, Type type, DocletTag tag){
    this.method = method;
    this.result = wr;
    this.type = type;
    this.tag = tag;
  }
  
  /**
   * @return the <code>MethodModel</code> to which this instance corresponds.
   */
  public MethodModel getMethod(){
    return method;
  }
  
  /**
   * @return the annotation corresponding to this instance's method return value, or <code>null</code> if no such annotation was set.
   */
  public WebResult getAnnotation(){
    return result;
  }

  /**
   * @return the <code>DocletTag</code> of this instance's return value.
   */
  public DocletTag getDocTag(){
    return tag;
  }
  
  /**
   * @return the QDox <code>JavaClass</code> of this instance's return value.
   */
  public JavaClass getJavaClass(){
    return type.getJavaClass();
  }
  
  /**
   * @return <code>true</code> if this instance's return value is a <code>java.lang.String</code>.
   */
  public boolean isString(){
    return getJavaClass().getFullyQualifiedName().equals(String.class.getName());
  }
  
  /**
   * @return <code>true</code> if this instance's return value is a <code>java.util.Date</code>.
   */  
  public boolean isDate(){
    return getJavaClass().getFullyQualifiedName().equals(Date.class.getName());
  }

  /**
   * @return <code>true</code> if this instance's return value is a primitive.
   */  
  public boolean isPrimitive(){
    return type.isPrimitive();
  }
  
  /**
   * @return <code>true</code> if this instance's return value is a <code>void</code>.
   */  
  public boolean isVoid(){
    return type.isVoid();
  }

}
