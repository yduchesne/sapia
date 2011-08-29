package org.sapia.regis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.sapia.resource.ClasspathResourceHandler;
import org.sapia.resource.FileResourceHandler;
import org.sapia.resource.UrlResourceHandler;
import org.sapia.util.text.MapContext;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.text.TemplateElementIF;
import org.sapia.util.text.TemplateException;
import org.sapia.util.text.TemplateFactory;

public class Utils {
  
  private static FileResourceHandler _FILE_RESOURCE_HANDLER = new FileResourceHandler();
  private static ClasspathResourceHandler _CLASSPATH_RESOURCE_HANDLER = new ClasspathResourceHandler();
  private static UrlResourceHandler _URL_RESOURCE_HANDLER = new UrlResourceHandler();
  static {
    _FILE_RESOURCE_HANDLER.setFallBackToClasspath(false);
  }
  
  public static void copyMapToProps(Properties target, Map src){
    Iterator itr = src.entrySet().iterator();
    while(itr.hasNext()){
      String key = (String)itr.next();
      target.setProperty(key, (String)src.get(key));
    }
  }
  
  public static void copyPropsToMap(Map target, Properties src){
    Enumeration en = src.propertyNames();
    while(en.hasMoreElements()){
      String key = (String)en.nextElement();
      target.put(key, src.getProperty(key));
    }
  }  

  public static void loadProps(Class clazz, Properties props, String resource) throws FileNotFoundException, IOException{
    InputStream is = load(clazz, resource);
    try{
      props.load(is);
      Properties newProps = replaceVars(new SystemContext(), props);
      Enumeration names = newProps.propertyNames();
      while(names.hasMoreElements()){
        String name = (String)names.nextElement();
        props.setProperty(name, newProps.getProperty(name));
      }
    }finally{
      is.close();
    }
  }
  
  public static InputStream load(Class clazz, String resourceUri) throws FileNotFoundException, IOException{
    InputStream is = null;

    try {
      is = _FILE_RESOURCE_HANDLER.getResource(resourceUri);
    } catch (IOException ioe) {
      // no file found... looking further
    }
    
    if (is == null) {
      try {
        is = _URL_RESOURCE_HANDLER.getResource(resourceUri);
      } catch (IOException ioe) {
        // no url found... looking further
      }        
    }
    
    if (is == null) {
      try {
        is = _CLASSPATH_RESOURCE_HANDLER.getResource(resourceUri);
      } catch (IOException ioe) {
        // no resource found in classpath... search using passed in class
        is = clazz.getClassLoader().getResourceAsStream(resourceUri);
      }        
    }
    
    if (is == null) {
      throw new FileNotFoundException(resourceUri);
    }
    
    return is;
  }

  public static String loadAsString(InputStream is) throws IOException, Exception{
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line;
    StringBuffer buf = new StringBuffer();
    while((line = reader.readLine()) != null){
      buf.append(line);
      buf.append(System.getProperty("line.separator"));
    }
    is.close();
    return buf.toString();
  }
  
  /**
   * Replaces the property values in the given <code>Properties</code>
   * instance with the variable values provided by the given context.
   * <p>
   * Property values may hold variables, as illustrated by the following example:
   * <pre>
   * some-property=${some-variable} and some-value
   * </pre>
   * 
   * @param ctx a <code>TemplateContextIF</code> holding variables.
   * @param props a <code>Properties</code> instance whose values should be interpolated.
   * @return the resulting <code>Properties</code> (with interpolated values).
   * @throws TemplateException an error occurs while performing the replacement.
   */
  public static Properties replaceVars(TemplateContextIF ctx, 
      Properties props){
    return doReplaceVars(ctx, props, true);
  }  
  
  /**
   * Replaces the property values in the given <code>Properties</code>
   * instance with the variable values provided by the given context.
   * <p>
   * Property values may hold variables, as illustrated by the following example:
   * <pre>
   * some-property=${some-variable} and some-value
   * </pre>
   * 
   * @param ctx a <code>TemplateContextIF</code> holding variables.
   * @param props a <code>Map</code> whose values should be interpolated.
   * @return the resulting <code>Map</code> (with interpolated values).
   * @throws TemplateException an error occurs while performing the replacement.
   */
  public static Map replaceVars(TemplateContextIF ctx, 
      Map props){
    return doReplaceVars(ctx, props, true);
  }
  
  public static Map doReplaceVars(TemplateContextIF ctx, 
      Map props, boolean secondPass){
    Iterator names = props.keySet().iterator();
    Map result = new HashMap(props);
    TemplateFactory fac = new TemplateFactory();
    MapContext child = new MapContext(props, ctx, false);
    while(names.hasNext()){
      String name = (String)names.next();
      String value = (String)props.get(name);
      if(value != null){
        TemplateElementIF elem = fac.parse(value);
        try{
          value = elem.render(child);
        }catch(TemplateException e){
          //noop
        }
        result.put(name, value);
      }
    }
    if(secondPass){
      return doReplaceVars(ctx, result, false);
    }
    return result;
  }      
  
  public static Properties doReplaceVars(TemplateContextIF ctx, 
      Properties props, boolean secondPass){
    Enumeration names = props.propertyNames();
    Properties result = new Properties(props);
    TemplateFactory fac = new TemplateFactory();
    PropertiesContext child = new PropertiesContext(props, ctx);
    while(names.hasMoreElements()){
      String name = (String)names.nextElement();
      String value = props.getProperty(name);
      if(value != null){
        TemplateElementIF elem = fac.parse(value);
        try{
          value = elem.render(child);
        }catch(TemplateException e){
          //noop
        }
        result.setProperty(name, value);
      }
    }
    if(secondPass){
      return doReplaceVars(ctx, result, false);
    }
    return result;
  }    
  
  public static void deleteRecurse(File f){
    if(f.isDirectory()){
      File[] files = f.listFiles();
      if(files != null){
        for(int i = 0; i < files.length; i++){
          if(files[i].isDirectory()){
            deleteRecurse(files[i]);
          }
          else{
            files[i].delete();
          }
        }
      }
      f.delete();
    }
    else{
      f.delete();
    }
  }    
}
