package org.sapia.regis.codegen.output;

import java.io.IOException;

import org.sapia.regis.Registry;

/**
 * The entry point into code generation. Create an instance of this class
 * with the required arguments and invoke {@link #generate()}
 * 
 * @author yduchesne
 *
 */
public class CodeGenerator {
  
  private Registry registry;
  private CodeGenConfig config;
  
  /**
   * Creates an instance of this class to be used for generating code. 
   * @param reg the {@link Registry} from which to generate code.
   * @param config a {@link CodeGenConfig} instance, which encapsulates the 
   * configuration used for code generation.
   * 
   * @see CodeGenConfig
   */
  public CodeGenerator(Registry reg, CodeGenConfig config){
    this.registry = reg;
    this.config = config;
  }
  
  /**
   * Proceeds to generating the code corresponding to the {@link Registry}
   * encapsulated by this instance.
   * 
   * @throws IOException
   */
  public void generate() throws IOException{
    if(config.getDestinationDir() == null){
      throw new IllegalStateException("Destination directory not specified");
    }
    NodeIntrospector root = new NodeIntrospector(registry.getRoot(), this.config);
    root.generate();
  }

}
