package org.sapia.soto.xfire.ant;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;

import org.apache.tools.ant.Project;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.Type;

/**
 * Holds data about a given method.
 * 
 * @author yduchesne
 *
 */
public class MethodModel {
  
  private WebServiceModel ws;
  private WebMethod wm;
  private Method method;
  private JavaMethod javaMethod;
  private int index;
  private List<ParameterModel> paramModels = new ArrayList<ParameterModel>();  
  private List<ExceptionModel> exceptionModels = new ArrayList<ExceptionModel>();  
  
  MethodModel(
      WebServiceModel ws,
      WebMethod wm,
      Method method, 
      int index){
    this.ws = ws;
    this.wm = wm;
    this.method = method;
    this.index = index;
  }
  
  /**
   * @return the <code>WebServiceModel</code> to which this instance
   * corresponds.
   */
  public WebServiceModel getWebService(){
    return ws;
  }
  
  /**
   * @return the Java <code>Method</code> object to which this instance
   * corresponds.
   */
  public Method getMethod(){
    return method;
  }
  
  /**
   * @return the QDox <code>JavaMethod</code> object to which this
   * instance corresponds.
   */
  public JavaMethod getJavaMethod(){
    return javaMethod;
  }
  
  /**
   * @return the annotation corresponding to this instance's method, or <code>null</code> if no such annotation was set.
   */
  public WebMethod getAnnotation(){
    return wm;
  }
  
  /**
   * Returns the "operation name" of this instance, as specified in the @WebMethod
   * annotation of this instance's corresponding method. If no operation name has
   * been specified, then this instance's corresponding method name is chosen.
   * 
   * @return this instance's operation name.
   */
  public String getOperationName(){
    if(wm == null || wm.operationName() == null || wm.operationName().length() == 0){
      return method.getName();
    }
    else{
      return wm.operationName();
    }
  }

  /**
   * @return the "index" of this instance's method - the order in which this instance's
   * method appears the web service.
   */
  public int getIndex(){
    return index;
  }
  
  /**
   * @return the number of parameters held by this instance.
   */
  public int getParamCount(){
    return paramModels.size(); 
  }
  
  /**
   * @return <code>ReturnModel</code> corresponding to the return value of this 
   * instance's method.
   */
  public ReturnModel getReturn(){
    return new ReturnModel(this, 
        method.getAnnotation(WebResult.class), 
        javaMethod.getReturns(), 
        javaMethod.getTagByName("return"));
  }
  
  /**
   * @return a <code>List</code> of <code>ParameterModel</code>s corresponding
   * to the parameters of this instance's method.
   */
  public List<ParameterModel> getParameters(){
    return paramModels;
  }  
  
  /**
   * @return a <code>List</code> of <code>ExceptionModel</code>s corresponding
   * to the exceptions thrown by this instance's method.
   */
  public List<ExceptionModel> getExceptions(){
    return exceptionModels;
  }  
  
  void build() throws Exception{
    JavaMethod[] methods = ws.getJavaClass().getMethods();
    for(int i = 0; i < methods.length; i++){
      JavaParameter[] params = methods[i].getParameters();
      if(isMethod(params, method.getParameterTypes())){
        javaMethod = methods[i];
      }
    }
    if(javaMethod == null){
      throw new IllegalStateException("No method found for: "
          + method.getName() + "; " + method);
    }
    
    ///// parameters....
    
    DocletTag[] paramTags = javaMethod.getTagsByName("param");
    if(paramTags.length < method.getParameterTypes().length){
      ws.getGenerator().log("Parameters for method " + method + " of " + ws.getServiceClass().getName() + " ignored: there are less @param javadoc " + 
          "tags then there are parameters declared in the method's signature", Project.MSG_WARN);
    }
    else if(paramTags.length > method.getParameterTypes().length){
      ws.getGenerator().log("Parameters for method " + method + " of " + ws.getServiceClass().getName() + " ignored: there are more @param javadoc " + 
          "tags then there are parameters declared in the method's signature", Project.MSG_WARN);
    }    
    else{
      int paramCount = 0;
      for(int i = 0; i < paramTags.length; i++){
        if(paramTags[i].getParameters().length > 0){
          JavaParameter param = javaMethod.getParameterByName(paramTags[i].getParameters()[0]);
          if(param != null){
            Annotation[] annos = method.getParameterAnnotations()[i];
            paramModels.add(
                new ParameterModel(this, 
                    findWebParam(annos), 
                    param, 
                    method.getParameterTypes()[i], 
                    paramTags[i], 
                    paramCount++));
          }
          else{
            ws.getGenerator().log("Invalid @param javadoc tag for method " + method + " of " + ws.getServiceClass().getName() + "; could not find parameter " + 
                "with matching name in method signature. This parameter will be ignored", Project.MSG_WARN);
            paramCount++;
          }
        }
      }
    }
    
    ///// exceptions ....
    
    DocletTag[] throwsTags = javaMethod.getTagsByName("throws");
    Type[] exceptions = javaMethod.getExceptions();
    throwsTags = sortExceptions(throwsTags, exceptions);
    if(throwsTags == null){
      // noop
    }
    if(throwsTags.length < exceptions.length){
      ws.getGenerator().log("Exceptions for " + method + " of " + ws.getServiceClass().getName() + " ignored: there are less @throws javadoc " + 
          "tags then there are exceptions declared in the method's signature", Project.MSG_WARN);
    }
    else if(throwsTags.length > exceptions.length){
      ws.getGenerator().log("Exceptions for " + method + " of " + ws.getServiceClass().getName() + " ignored: there are more @throws javadoc " + 
          "tags then there are exceptions declared in the method's signature", Project.MSG_WARN);
    }
    else{
      for(int i = 0; i < throwsTags.length; i++){
        exceptionModels.add(
            new ExceptionModel(exceptions[i], 
                throwsTags[i]));
      }
    }
  }
  
  private boolean isMethod(JavaParameter[] javaParams, Class[] params){
    if(javaParams.length != params.length){
      return false;
    }
    for(int i = 0; i < javaParams.length; i++){
      if(!javaParams[i].getType().getJavaClass().getFullyQualifiedName().equals(params[i].getName())){
        return false;
      }
    }
    return true;
  }
  
  private WebParam findWebParam(Annotation[] annos){
    for(int i = 0; i < annos.length; i++){
      if(annos[i] instanceof WebParam){
        return (WebParam)annos[i];
      }
    }
    return null;
  }
  
  private DocletTag[] sortExceptions(DocletTag[] tags, Type[] exceptions){
    Set<TagWrapper> tagList = new TreeSet<TagWrapper>();
    
    for (int i = 0; i < tags.length; i++) {
      boolean found = false;
      for(int j = 0; j < exceptions.length; j++){
        if(tags[i].getValue().trim().startsWith(exceptions[j].getJavaClass().getName())){
          tagList.add(new TagWrapper(tags[i], j));
          found = true;
          break;
        }
      }
      if(!found){
        ws.getGenerator().log("Exceptions will be ignored; no matching exception found for @throws " + 
            tags[i].getValue() + " in " + this.method, Project.MSG_WARN);
        return null;
      }
      else{
        found = false;
      }
    }
    DocletTag[] newTags = new DocletTag[tags.length];
    Iterator<TagWrapper> wrappers = tagList.iterator();
    int count = 0;
    while(wrappers.hasNext()){
      TagWrapper tw = wrappers.next();
      newTags[count++]  = tw.tag;
    }
    return newTags;
  }
  
  ////////// Wrapper..
  
  static class TagWrapper implements Comparable{
    
    private DocletTag tag;
    private int index;
    
    TagWrapper(DocletTag tag, int index){
      this.tag = tag;
      this.index = index;
    }
    
    public int compareTo(Object o) {
      TagWrapper other = (TagWrapper)o;
      return index - other.index;
    }
    
  }
  
}
