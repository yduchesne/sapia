package org.sapia.soto.osgi.deployer;

import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
class DeploymentDb implements Externalizable{
  
  static final long serialVersionUID = 1L;
  static final String FILE_NAME = ".deployments.ser";
  
  private transient File _basedir;
  private Set _fileSet = new HashSet();
  
  /**
   * Do not call. Used for externalization only.
   */
  public DeploymentDb(){
    
  }
  DeploymentDb(File basedir){
    _basedir = basedir;
  }
  
  synchronized void load() throws IOException{
    File f = new File(_basedir, FILE_NAME);
    if(f.exists()){
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
      try{
        DeploymentDb db = (DeploymentDb)ois.readObject(); 
        _fileSet = db._fileSet;
      }catch(ClassNotFoundException e){
        throw new IOException("Class not found - " + e.getMessage()); 
      }finally{
        try{
          ois.close();
        }catch(IOException e){}
      }
    }
  }
  
  synchronized void save() throws IOException{
    File f = new File(_basedir, FILE_NAME);
    ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream(f));
    try{
      ois.writeObject(this); 
    }finally{
      try{
        ois.close();
      }catch(IOException e){}
    }  
  }
  
  synchronized boolean contains(File f){
    return _fileSet.contains(f);
  }
  
  synchronized void remove(File f){
    _fileSet.remove(f);
  }
  
  synchronized void add(File f){
    _fileSet.add(f);
  }
  
  public synchronized File[] getFiles(){
    return (File[])_fileSet.toArray(new File[_fileSet.size()]);
  }
  
  /**
   * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
   */
  public void readExternal(ObjectInput in) throws IOException,
      ClassNotFoundException {
    _fileSet = (Set)in.readObject();
  }
  
  /**
   * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
   */
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(_fileSet);
  }
  
}
