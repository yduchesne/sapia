package org.sapia.corus.os;

import java.io.File;
import java.io.IOException;

import org.sapia.console.CmdLine;
import org.sapia.corus.client.services.os.OsModule;
import org.sapia.corus.core.ModuleHelper;

public class OsModuleImpl extends ModuleHelper implements OsModule{
  
  /////////////// Lifecycle 
  
  @Override
  public void init() throws Exception {
  }
  
  @Override
  public void start() throws Exception {
  }
  
  @Override
  public void dispose() throws Exception {
  }
  
  /////////////// OsModule interface
  
  public String getRoleName() {
    return OsModule.ROLE;
  }
  
  @Override
  public String executeProcess(
      LogCallback log, 
      File rootDirectory,
      CmdLine commandLine) throws IOException {

    return null;
  }
  
  @Override
  public void killProcess(LogCallback log, String pid) throws IOException {
    
  }
  

}
