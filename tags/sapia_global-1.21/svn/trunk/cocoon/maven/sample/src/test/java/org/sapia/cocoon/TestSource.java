package org.sapia.cocoon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceValidity;

public class TestSource implements Source{
  
  private File file;
  
  public TestSource(File f){
    file = f;
  }

  public boolean exists() {
    return file.exists();
  }
  
  public long getContentLength() {
    return file.length();
  }
  
  public InputStream getInputStream() throws IOException,
      SourceNotFoundException {
    return new FileInputStream(file);
  }
  
  public long getLastModified() {
    return file.lastModified();
  }
  
  public String getMimeType() {
    return "text/plain";
  }
  
  public String getScheme() {
    return "file";
  }
  
  public String getURI() {
    return file.toURI().toString();
  }
  
  public SourceValidity getValidity() {
    return new SourceValidity(){
      public int isValid() {
        return SourceValidity.VALID;
      }
      
      public int isValid(SourceValidity arg0) {
        return SourceValidity.VALID;
      }
    };
  }
  
  public void refresh() { 
  }
}
