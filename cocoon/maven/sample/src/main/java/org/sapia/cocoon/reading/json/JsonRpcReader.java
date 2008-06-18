package org.sapia.cocoon.reading.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.components.modules.input.InputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.reading.Reader;
import org.apache.cocoon.sitemap.SitemapModelComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.xml.sax.SAXException;

public class JsonRpcReader implements SitemapModelComponent, Reader, BeanFactoryAware{
  
  public static final String PARAM_MODULE       = "module";
  public static final String PARAM_METHOD       = "method";
  public static final String PARAM_KEY          = "key";
  public static final String PARAM_SERVICE      = "service";
  public static final String ARG_NAME_PATTERN    = "json-arg-";
  public static final String ARG_VALUE          = "json-arg-value";
  public static final String MIME_TYPE = "application/json;charset=utf-8";
  
  static Set<Class> PRIMITIVES = new HashSet<Class>();
  static{
    PRIMITIVES.add(Boolean.class);
    PRIMITIVES.add(Byte.class);
    PRIMITIVES.add(Short.class);
    PRIMITIVES.add(Integer.class);
    PRIMITIVES.add(Long.class);
    PRIMITIVES.add(Float.class);
    PRIMITIVES.add(Double.class);
  }
  
  private Map objectModel;
  private OutputStream out;
  private String moduleName, method, key, service;
  private BeanFactory factory;
  private Log log = LogFactory.getLog(getClass());
  
  public void setOutputStream(OutputStream out) throws IOException {
    this.out = out;
  }
  
  public void setBeanFactory(BeanFactory factory) throws BeansException {
    this.factory = factory;
  }
  
  public void setup(SourceResolver resolver, Map objectModel, String src, Parameters params)
      throws ProcessingException, SAXException, IOException {
    // TODO Auto-generated method stub
    this.objectModel = objectModel;
    try{
      this.moduleName  = params.getParameter(PARAM_MODULE);
      this.method      = params.getParameter(PARAM_METHOD);
      this.key         = params.getParameter(PARAM_KEY);
      this.service     = params.getParameter(PARAM_SERVICE, null);
    }catch(ParameterException e){
      throw new ProcessingException("Could not acquire required parameter", e);
    }
  }
  
  public void generate() throws IOException, SAXException, ProcessingException {
    Request  request   = ObjectModelHelper.getRequest(objectModel);
    Response response = ObjectModelHelper.getResponse(objectModel);
    response.setStatus(HttpServletResponse.SC_OK);
    List<Arg> args = findArgs(request);
    InputModule module = (InputModule)factory.getBean(InputModule.ROLE+"/"+moduleName); 
    if(module == null){
      throw new ProcessingException("No input module found for: " + moduleName);
    }
    Object bean = null;
    try{
      bean = module.getAttribute(key, null, objectModel);
    }catch(ConfigurationException e){
      throw new ProcessingException("Could not get " + key + " from input module: " + moduleName, e);
    }
    if(service != null){
      if(!(bean instanceof Map)){
        throw new ProcessingException("Object " + bean + " under " + key + " of module " + moduleName +
            " is expected to be an instance of java.util.Map and contain an object bound under the name '" +
            service + "'");
      }
      else{
        Map registry = (Map)bean;        
        bean = registry.get(service);
        if(bean == null){
          throw new ProcessingException("Map under " + key + " of module " + moduleName +
              " is expected to contain an object bound under the name '" +
              service + "' on which the invocation will be performed; no such object was found");          
        }
      }
    }
    try{
      Object result = doInvoke(bean, method, args);
      serializeResponse(result);
    }catch(Throwable e){
      log.error("Error caught while performing invocation", e);
      serializeError(e);
    }
  }
  
  private void serializeResponse(Object toReturn){
    JsonResult result = new JsonResult();
    result.setValue(toReturn);
    serialize(result, out);    
  }
  
  private void serializeError(Throwable e){
    JsonResult result = new JsonResult();
    JsonException je = new JsonException(e);
    result.setException(je);    
    serialize(result, out);
  }  
  
  protected void serialize(JsonResult result, OutputStream out){
    PrintWriter writer = new PrintWriter(out);
    writer.print(JSONSerializer.toJSON(result).toString());
    writer.flush();
  }
  
  public long getLastModified() {
    return System.currentTimeMillis();
  }
  
  public String getMimeType() {
    return MIME_TYPE;
  }
  
  public boolean shouldSetContentLength() {
    return true;
  }
  
  protected Object doInvoke(Object bean, String method, List<Arg> args) throws Throwable{
    Method[] methods = bean.getClass().getMethods();
    for(Method mt:methods){
      if(mt.getName().equals(method) && mt.getParameterTypes().length == args.size()){
        Class[] types = mt.getParameterTypes();
        Object[] params = new Object[types.length];
        for(int i = 0; i < types.length; i++){
          params[i] = args.get(i).convertTo(types[i]);
        }
        try{
          if(log.isDebugEnabled()){
            log.debug("Performing invocation of " + mt + " on " + bean);
          }
          return mt.invoke(bean, params);
        }catch(InvocationTargetException e){
          throw e.getTargetException();
        }catch(Exception e){
          throw e;
        }
      }
    }
    throw new NoSuchMethodException("No method found for: " + method);
  }

  private List<Arg> findArgs(Request req){
    Enumeration<String> names = req.getParameterNames();
    List<Arg> args = new ArrayList<Arg>();
    while(names.hasMoreElements()){
      String name = names.nextElement();
      if(name.startsWith(ARG_NAME_PATTERN)){
        String value = req.getParameter(name);
        if(value == null || value.equals("null")){
          value = null;
        }
        args.add(new Arg(Integer.parseInt(name.substring(ARG_NAME_PATTERN.length())), value));
      }
    }
    Collections.sort(args);
    return args;
  }
  
  static class Arg implements Comparable<Arg>{
    
    int index;
    String value;
    
    Arg(int index, String value){
      this.index = index;
      this.value = value;
    }
    
    public Object convertTo(Class type){
      if(value == null){
        if(type.isPrimitive()){
          return 0;
        }
        else{
          return null;
        }
      }
      else if(type.isPrimitive() || PRIMITIVES.contains(type)){
        JSONObject json = JSONObject.fromObject(value);
        boolean isNull = json.getString(ARG_VALUE) == null || json.getString(ARG_VALUE).equals("null");
        
        if(type.equals(boolean.class) || type.equals(Boolean.class)){
          if(isNull){
            return false;
          }
          else{
            return json.getBoolean(ARG_VALUE);
          }
        }
        else if(isNull){
          return 0;
        }
        else if(type.equals(byte.class) || type.equals(Byte.class)){
          return Byte.parseByte(json.getString(ARG_VALUE));
        }        
        else if(type.equals(short.class) || type.equals(Short.class)){
          return Short.parseShort(json.getString(ARG_VALUE));
        }        
        else if(type.equals(int.class) || type.equals(Integer.class)){
          return json.getInt(ARG_VALUE);
        }
        else if(type.equals(long.class) || type.equals(Long.class)){
          return json.getLong(ARG_VALUE);
        }        
        else if(type.equals(float.class) || type.equals(Float.class)){
          return Float.parseFloat(json.getString(ARG_VALUE));
        }        
        else if(type.equals(double.class) || type.equals(Double.class)){
          return json.getDouble(ARG_VALUE);
        }
        else{
          throw new IllegalArgumentException("Unknown primitive type: " + type.getName());
        }
      }
      else if(type.equals(String.class)){
        JSONObject json = JSONObject.fromObject(value);        
        return json.getString(ARG_VALUE);
      }
      else if(Date.class.isAssignableFrom(type)){
        JSONObject json = JSONObject.fromObject(value);
        json = json.getJSONObject(ARG_VALUE);
        JsonConfig jsonConfig = new JsonConfig();  
        jsonConfig.setRootClass(Date.class);        
        Object toReturn = JSONSerializer.toJava(json, jsonConfig);
        return toReturn;
      }
      else if(type.isArray()){
        JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON(value);  
        JsonConfig jsonConfig = new JsonConfig();  
        jsonConfig.setRootClass(type.getComponentType());
        jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);  
        return JSONSerializer.toJava(jsonArray, jsonConfig);
      }
      // bean...
      else{
        JSONObject json = JSONObject.fromObject(value);
        json = json.getJSONObject(ARG_VALUE);
        JsonConfig config = new JsonConfig();
        config.setRootClass(type);
        return JSONSerializer.toJava(json, config);        
      }
    }
    
    public int compareTo(Arg o) {
      return index - o.index;
    }
    
  }
  

}
