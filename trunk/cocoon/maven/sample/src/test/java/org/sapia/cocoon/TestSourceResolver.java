package org.sapia.cocoon;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;

public class TestSourceResolver implements SourceResolver{
  
  private File baseDir;

  public TestSourceResolver(){
    this(new File(System.getProperty("user.dir")));
  }
  
  public TestSourceResolver(File baseDir){
    this.baseDir = baseDir;
  }
  
  public void release(Source arg0) {
  }
  
  public Source resolveURI(String uri) throws MalformedURLException,
      IOException {
    File f = new File(baseDir, uri);
    return new TestSource(f);
  }
  
  public Source resolveURI(String uri, String arg1, Map arg2)
      throws MalformedURLException, IOException {
    return resolveURI(uri);
  }
  

}
