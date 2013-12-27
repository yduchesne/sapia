package org.sapia.regis.traversal;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;

/**
 * Use this class to dump to content of a {@link Registry} to stdout.
 * 
 * @author yduchesne
 *
 */
public class DumpRegistry {
  
  public void dump(Registry regis){
    
    RegisSession session = regis.open();
    try{
      new Traversal(regis.getRoot()).traverse(new StdoutVisitor());
    }finally{
      session.close();
    }
  }
  
  /**
   * This method connects to a registry and dumps that registry's content to stdout.
   * This method expects either:
   * 
   * <ul>
   *    <li>The <code>org.sapia.regis.factory</code> property to be passed at the
   *        command line (<code>-Dorg.sapia.regis.factory=......</code>);
   *    <li>the <code>org.sapia.regis.bootstrap</code> property to be passed at the
   *        command line;
   *    <li>if none of the above is specified, an argument indication the location of the bootstrap file to use.
   * </ul>
   * 
   * @see RegistryContext
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception{
    if(System.getProperty(RegistryContext.FACTORY_CLASS) != null){
      RegistryContext context = new RegistryContext(System.getProperties());
      DumpRegistry dumper = new DumpRegistry();
      dumper.dump(context.connect());
    }
    else if(System.getProperty(RegistryContext.BOOTSTRAP) != null){
      RegistryContext context = new RegistryContext(System.getProperties());
      DumpRegistry dumper = new DumpRegistry();
      dumper.dump(context.connect());
    }
    else{
      if(args.length == 0){
        System.out.println("Expected bootstrap file location as argument, or one of the following JVM properties");
        System.out.println("- " + RegistryContext.BOOTSTRAP);
        System.out.println("- " + RegistryContext.FACTORY_CLASS);
      }
      else{
        File bootstrap = new File(args[0]);
        if(!bootstrap.exists()){
          System.out.println(
              String.format(
                  "Bootstrap file not found: %s",
                  bootstrap.getAbsolutePath()
              )
          );
        }
        else if(bootstrap.isDirectory()){
          System.out.println(
              String.format(
                  "Bootstrap file is in fact a directory: %s",
                  bootstrap.getAbsolutePath()
              )
          );
        }
        else{
          Properties props = new  Properties();
          FileInputStream fis = new FileInputStream(bootstrap);
          try{
            props.load(fis);
          }finally{
            fis.close();
          }
          RegistryContext context = new RegistryContext(System.getProperties());
          DumpRegistry dumper = new DumpRegistry();
          dumper.dump(context.connect());
        }
      }
    }
  }

}
