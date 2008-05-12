package org.sapia.cocoon.php;

import java.io.File;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.caucho.quercus.Quercus;
import com.caucho.vfs.FilePath;

public class QuercusBean extends Quercus implements ResourceLoaderAware, InitializingBean{
  
  public static final String DEFAULT_WORKDIR = ".";
  public static final String DEFAULT_PWD = ".";
  
  private String _pwdDir;
  private ResourceLoader _loader;
  
  public void setResourceLoader(ResourceLoader loader) {
    _loader = loader;
  }
  
  public void setPwdDir(String dir){
    _pwdDir = dir;
  }
  
  public void afterPropertiesSet() throws Exception {
    Resource source = _loader.getResource("classpath:/COB-INF/" + (_pwdDir != null ? _pwdDir : DEFAULT_PWD));
    File f = source.getFile();
    if(!f.exists()){
      throw new IllegalArgumentException("Invalid path (does not exist): " + f.getAbsolutePath());
    }
    setPwd(new QuercusPath(f.getAbsolutePath())); 
  }
  
  public static class QuercusPath extends FilePath{
    public QuercusPath(String path){
      super(path);
    }
  }
  
  
  

}
