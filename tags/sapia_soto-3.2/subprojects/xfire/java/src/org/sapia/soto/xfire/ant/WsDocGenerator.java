package org.sapia.soto.xfire.ant;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebService;

import org.apache.tools.ant.Project;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class WsDocGenerator extends JavaDocBuilder{
  
  private File outputDir;
  private Configuration config;
  private ClassLoader loader;
  private TaskLog log;
  
  public WsDocGenerator(File outputDir, 
    ClassLoader loader,
    TaskLog log){
    this.outputDir = outputDir;
    this.loader = loader;
    this.log = log;
    
    config = new Configuration();
    config.setClassForTemplateLoading(getClass(), "");
  }
  
  public TaskLog getTaskLog(){
    return log;
  }
  
  public void log(String msg, int level){
    log.debug(msg, level);
  }
  
  public void generate() throws Exception{
    JavaSource[] sources = super.getSources();
    for(int i = 0; i < sources.length; i++){
      JavaClass[] classes = sources[i].getClasses();
      for(int j = 0; j < classes.length; j++){
        if(!classes[j].getFullyQualifiedName().startsWith("org.sapia.soto.xfire.ant")){
                    generateFor(loader.loadClass(classes[j].getFullyQualifiedName()));
        }
      }
    }
  }
  
  public void generateFor(Class clazz) throws Exception{
    WebService ws = (WebService)clazz.getAnnotation(WebService.class);
    if(ws != null){

      Class endpointClass = clazz;
      
      if(ws.endpointInterface() != null && ws.endpointInterface().length() > 0){
        endpointClass = Class.forName(ws.endpointInterface());
      }
      
      Method[] methods = clazz.getMethods();
      WebServiceModel model = new WebServiceModel(this, ws, endpointClass);
      model.build();
      Template tmpl = config.getTemplate(getClass().getSimpleName()+".ftl");
      Map root = new HashMap();
      root.put("WebService", model);
      
      String fileName = clazz.getSimpleName() + ".doc.xml";
      String dir = clazz.getName().substring(0, clazz.getName().lastIndexOf("."));
      dir = dir.replace(".", File.separator);
      File outputDir = new File(this.outputDir+File.separator+dir);
      outputDir.mkdirs();
      File outputFile = new File(outputDir, fileName);
      log("Generating " + outputFile.getAbsolutePath(), Project.MSG_INFO);
      PrintWriter pw = null;
      try{
        pw = new PrintWriter(new FileWriter(outputFile));
        tmpl.process(root, pw);
      }finally{
        pw.flush();
        pw.close();
      }
    }
  }
  
  public static void main(String[] args) throws Exception{
    WsDocGenerator generator = new WsDocGenerator(
        new File("java/src"), 
        Thread.currentThread().getContextClassLoader(),
        new StdoutTaskLog());
    generator.addSourceTree(new File("java/src"));
    generator.generate();
  }
  
  

}
