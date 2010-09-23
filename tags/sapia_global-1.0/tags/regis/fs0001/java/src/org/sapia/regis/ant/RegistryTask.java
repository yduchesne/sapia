package org.sapia.regis.ant;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.naming.InitialContext;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.sapia.regis.Configurable;
import org.sapia.regis.Path;
import org.sapia.regis.remote.client.RegistryImporter;
import org.sapia.regis.util.Utils;

/**
 * This Ant task is used to update a configuration into a remote registry.
 * It must be given the host/port of that registry, or the JNDI name of that
 * registry as well as the URL of the Ubik JNDI server to which it is bound.
 * 
 * @see org.sapia.regis.remote.RegistryServer
 * 
 * @author yduchesne
 *
 */
public class RegistryTask extends Task{

  private String host, username, password, url, jndiName;
  private Path path;
  private int port = -1;

  private File config;
  
  /**
   * @param host the registry host to which to connect
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * @param port the port of the registry which to connect
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * @param url the URL of the Ubik JNDI server from which to lookup
   * the registry to update (example: <code>ubik://localhost:1099</code>)
   */
  public void setUrl(String url){
    this.url = url;
  }
  
  public void setJndiName(String jndiName) {
    this.jndiName = jndiName;
  }
  
  /**
   * @param file the registry configuration <code>File</code> to upload
   * into the remote registry
   */
  public void setConfig(File file){
    config = file;
  }
  
  /**
   * @param password the password of the registry to which to connect
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @param username the username of the registry to which to connect
   */  
  public void setUsername(String username) {
    this.username = username;
  }
  
  /**
   * @param path the path to the node that is to be updated (if not specified, root
   * the root not will be updated).
   */
  public void setNodePath(String path){
    this.path = Path.parse(path);
  }
  
  public void execute() throws BuildException{
    AntClassLoader taskloader = (AntClassLoader)this.getClass().getClassLoader();
    taskloader.setThreadContextLoader();
    try{
      RegistryImporter importer = new RegistryImporter();
      Configurable conf;
      if(jndiName != null){
        if(url == null){
          throw new BuildException("Naming service url not set");
        }
        Properties props = new Properties();
        props.setProperty(InitialContext.PROVIDER_URL, url);
        try{
          conf = (Configurable)importer.lookup(jndiName, props);
        }catch(Exception e){
          throw new BuildException("Could not lookup registry", e);
        }
      }
      else{
        if(host == null){
          throw new BuildException("Registry host not set");
        }      
        if(port == -1){
          throw new BuildException("Registry host not set");
        }
        try{
          conf = (Configurable)importer.lookup(host, port);
        }catch(Exception e){
          throw new BuildException("Could not connect to registry", e);
        }
      }
      if(username == null){
        throw new BuildException("Registry username not set");
      }
      if(password == null){
        throw new BuildException("Registry password not set");
      }    
      if(config == null){
        throw new BuildException("Registry config file not set");
      }
      try{
        conf.load(path, username, password, Utils.loadAsString(new FileInputStream(config)), null);
      }catch(Exception e){
        throw new BuildException("Could load configuration", e);
      }
    }finally{
      taskloader.resetThreadContextLoader();    
    }
  }
}
