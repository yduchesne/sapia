package org.sapia.cocoon.groovy;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyResourceLoader;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;


public class GroovyBean implements InitializingBean, ResourceLoaderAware{

  private ResourceLoader _resourceLoader;
  private GroovyClassLoader _classLoader;
  private String _classPath;
  
  public void setResourceLoader(ResourceLoader loader) {
    _resourceLoader = loader;
  }
  
  public void setClasspath(String path){
    _classPath = path;
  }
  
  public void afterPropertiesSet() throws Exception {
    _classLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
    if(_classPath != null){
      _classLoader .setResourceLoader(new GroovyResourceLoaderImpl(parseResources(_classPath)));
    }
  }
  
  private List<Resource> parseResources(String path) throws IOException{
    List<Resource> resources = new ArrayList<Resource>();
    String[] parts = path.split(";");
    for(String p:parts){
      if(!p.endsWith("/")){
        p = p+"/";
      }      
      Resource source = _resourceLoader.getResource("classpath:/COB-INF/" + p);
      if(!source.exists()){
        throw new IllegalArgumentException("Invalid path (does not exist): " + source.getFile().getAbsolutePath());
      }      
      resources.add(source);
    }
    return resources;
  }
  
  public GroovyClassLoader getClassLoader(){
    return _classLoader;
  }
  
  static class GroovyResourceLoaderImpl implements GroovyResourceLoader{
    
    private List<Resource> _roots;

    public GroovyResourceLoaderImpl(List<Resource> roots){
      _roots = roots;
    }
    
    public URL loadGroovySource(String src) throws MalformedURLException {
      try{
        src = src.replace(".", File.separator) + ".groovy";
        for(Resource root:_roots){
          Resource file = root.createRelative(src);
          if(file.exists()){
            return file.getURL();
          }
        }
        return null;
        //throw new MalformedURLException("No resource found for: " + src);
      }catch(MalformedURLException e){
        throw e;
      }catch(IOException e){
        throw new MalformedURLException(e.getMessage());
      }
    }
  }

}
