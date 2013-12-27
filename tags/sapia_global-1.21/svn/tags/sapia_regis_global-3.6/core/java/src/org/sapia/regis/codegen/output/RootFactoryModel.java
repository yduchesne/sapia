package org.sapia.regis.codegen.output;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import org.sapia.regis.Registry;
import org.sapia.regis.codegen.RootHelper;

class RootFactoryModel {
  
  private ClassModel rootModel;
    
  RootFactoryModel(ClassModel rootModel){
      this.rootModel = rootModel;
  }
  
  public void output(File destDir) throws IOException{
    String intfName = rootModel.getContext().getHints().getParentInterface().getContext().getClassName();
    String implName = rootModel.getContext().getClassName();
    String factoryClassName = intfName + "Factory";
    PrintWriter writer = rootModel.getContext().createWriter(destDir, factoryClassName);
    
    try{
      writer.println("package " + rootModel.getContext().getPackagePath().toString('.') + ";");
      writer.println();
      writer.println("/**");
      writer.println(" *");
      writer.println(" * Creates a {@link " + intfName + "} instance that wraps a {@link " + Registry.class.getName() + "} ");
      writer.println(" *");
      writer.println(" * Generated: " + new Date());
      writer.println(" * @author: " + System.getProperty("user.name"));      
      writer.println(" * @version: " + rootModel.getContext().getConfig().getVersion());
      writer.println(" */");
      writer.println("public class " + factoryClassName + " extends " + RootHelper.class.getName() + " {");
      writer.println();
      writer.println("  public static final String VERSION = \"" + rootModel.getContext().getConfig().getVersion() +"\";");
      writer.println();      
      writer.println("  public static " + intfName + " createFor(" + Registry.class.getName() + " reg) {" );
      writer.println("    return getRootFrom(" + implName + ".class, reg);" );
      writer.println("  }");
      writer.println();
      writer.println("  public static String getVersion(){" );
      writer.println("    return VERSION;" );
      writer.println("  }");      
      writer.println("}");
    }finally{
      writer.flush();
      writer.close();
    }
    
  }

}
