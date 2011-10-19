/*
 * FileType.java
 *
 * Created on June 28, 2005, 7:57 PM
 */

package org.sapia.soto.config.types;

import java.io.File;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author yduchesne
 */
public class FileType implements ObjectCreationCallback{
  
  private File _file;
  private boolean _create;
  
  /** Creates a new instance of FileType */
  public FileType() {
  }
  
  public void setName(String name){
    _file = new File(name);
  }
  
  public void setCreate(boolean create){
    _create = create;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_file == null){
      throw new ConfigurationException("File name not set");
    }
    if(_create && !_file.exists()){
      if(!_file.mkdirs()){
        throw new ConfigurationException("Could not create file " + _file.getAbsolutePath());
      }
    }
    return _file;
  }
  
}
