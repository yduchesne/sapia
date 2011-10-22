package org.sapia.regis.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Stack;

import org.sapia.regis.loader.ConfigObjectFactory;
import org.sapia.util.text.MapContext;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.text.TemplateElementIF;
import org.sapia.util.text.TemplateFactory;
import org.sapia.util.xml.confix.Dom4jProcessor;

public class VarContext {
  
  private static ThreadLocal local = new ThreadLocal();
  
  public static TemplateContextIF currentVars(){
    Stack st = stack();
    if(st.isEmpty()){
      return new SystemContext();
    }
    else return (TemplateContextIF)st.peek();
  }
  
  public static TemplateContextIF includeVars(Map newVars){
    TemplateContextIF parent = currentVars();
    MapContext child = new MapContext(newVars, parent, false);
    stack().push(child);
    return child;
  }
  
  public static void pop(TemplateContextIF ctx){
    Stack st = stack();
    if(currentVars() == ctx && st.size() > 0){
      st.pop();
    }
  }
  
  public static Object include(InputStream is, Map vars) throws Exception{
    String content = null;
    try{
      content = Utils.loadAsString(is);
    }catch(Exception e){
      is.close();
      throw e;
    }
    TemplateContextIF ctx = VarContext.includeVars(vars);
    try{
      TemplateFactory tmplFac = new TemplateFactory("$[", "]");
      TemplateElementIF template = tmplFac.parse(content);      
      content = template.render(ctx);
      ConfigObjectFactory fac = new ConfigObjectFactory();
      Dom4jProcessor proc = new Dom4jProcessor(fac);
      is = new ByteArrayInputStream(content.getBytes());
      return proc.process(is);
    }finally{
      VarContext.pop(ctx);
      is.close();
    }    
  }
  
  static Stack stack(){
    Stack st = (Stack)local.get();
    if(st == null){
      st = new Stack();
      local.set(st);
    }
    return st;
  }

}
