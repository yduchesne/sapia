package org.sapia.soto.xfire.ant;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.tools.ant.Project;

import com.thoughtworks.qdox.model.JavaClass;

/**
 * An instance of this class holds data about a given web service.
 * 
 * @author yduchesne
 *
 */
public class WebServiceModel {

  private WsDocGenerator generator;
  private WebService annotation;
  private Class serviceClass;
  private JavaClass javaClass;
  private List<MethodModel> methods = new ArrayList<MethodModel>();
  
  
  WebServiceModel(WsDocGenerator generator, WebService annot, Class serviceClass){
    this.generator = generator;
    this.annotation = annot;
    this.serviceClass = serviceClass;
  }
  
  /**
   * @return this instance's <code>WsDocGenerator</code>.
   */
  public WsDocGenerator getGenerator(){
    return generator;
  } 
  
  /**
   * @return the <code>Class</code> of the web service for which documentation
   * is being generated.
   */
  public Class getServiceClass(){
    return serviceClass;
  }
  
  /**
   * @return the QDox <code>JavaClass</code> of the web service for which documentation
   * is being generated.
   */  
  public JavaClass getJavaClass(){
    return javaClass;
  }
  
  /**
   * @return the <code>WebService</code> annotation of this instance's wrapped web service
   * class.
   */
  public WebService getAnnotation(){
    return annotation;
  }
  
  /**
   * @return the <code>List</code> of <code>MethodModel</code>s corresponding the methods
   * of this instance's web service class.
   */
  public List<MethodModel> getMethods(){
    return methods;
  }
  
  void build() throws Exception{
    
    javaClass = generator.getClassByName(serviceClass.getName());
    if(javaClass == null){
      throw new IllegalStateException("Could not find source for: " + serviceClass.getName());
    }
    
    Method[] classMethods = serviceClass.getDeclaredMethods();
    int methodIndex = 0;

    for(int i = 0; i < classMethods.length; i++){
      Method m = classMethods[i];
      WebMethod wm = (WebMethod)m.getAnnotation(WebMethod.class);
      if(wm != null && !wm.exclude()){
        MethodModel methodModel = new MethodModel(this, wm, classMethods[i], methodIndex);
        methodModel.build();
        methods.add(methodModel);
        methodIndex++;
      }
      else{
        generator.log("Method " + m + " ignored; not flagged with a @WebMethod annotation", Project.MSG_WARN);
      }
    }
  }

}
