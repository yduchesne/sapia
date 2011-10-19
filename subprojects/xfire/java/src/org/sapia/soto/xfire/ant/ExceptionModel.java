package org.sapia.soto.xfire.ant;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Type;

/**
 * Holds data about a given exception.
 * 
 * @author yduchesne
 *
 */
public class ExceptionModel {
  
  private Type type;
  private DocletTag tag;
  
  public ExceptionModel(Type type, DocletTag tag){
    this.type = type;
    this.tag = tag;
  }
  
  /**
   * @return the <code>DocletTag</code> corresponding to this instance's exception.
   */
  public DocletTag getDocTag(){
    return tag;
  }

  /**
   * @return the well-formatted documentation of this instance's exception.
   */
  public String getDoc(){
    String value = tag.getValue().trim();
    if(value.startsWith(type.getJavaClass().getName())){
      return value.substring(type.getJavaClass().getName().length());
    }
    else{
      return tag.getValue();
    }
  }
  
  /**
   * 
   * @return the <code>JavaClass</code> corresponding to the class 
   * of this instance's exception.
   */
  public JavaClass getJavaClass(){
    return type.getJavaClass();
  }

}
