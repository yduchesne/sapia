package org.sapia.cocoon.ruby;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class JRubyBean implements InitializingBean, DisposableBean, ResourceLoaderAware{
  
  private String _path, _home;
  private ResourceLoader _loader;
  private Ruby _runtime;
  private List<String> _loadPath;
  
  public void setResourceLoader(ResourceLoader loader) {
    _loader = loader;
  }
  
  public void destroy() throws Exception {
    if(_runtime != null){
      JavaEmbedUtils.terminate(_runtime);
    }
  }
  
  public void afterPropertiesSet() throws Exception {
    _loadPath = new ArrayList<String>();
    if(_path != null){
      String[] paths = _path.split(";");
      for(String path:paths){
        Resource source = _loader.getResource("classpath:/COB-INF/" + path);
        File f = source.getFile();
        if(!f.exists()){
          throw new IllegalArgumentException("Invalid path (does not exist): " + f.getAbsolutePath());
        }        
        else{
          String aPath = f.getAbsolutePath();
          if(aPath.endsWith(File.separator)){
            aPath = aPath.substring(0, aPath.length()-1);
          }
          _loadPath.add(f.getAbsolutePath());
        }
      }
    }
    _runtime = Ruby.newInstance();
  }
  
  public void setJrubyHome(String home){
    _home = home;
  }
  
  public void setPath(String path){
    _path = path;
  }
  
  public Ruby newInstance(){
    Ruby instance = Ruby.newInstance(_runtime.getInstanceConfig());
    if(_home != null){
      instance.setJRubyHome(_home);
    }
    if(_loadPath.size() > 0){
      instance.getLoadService().init(_loadPath);
    }
    return instance;
  }
}
