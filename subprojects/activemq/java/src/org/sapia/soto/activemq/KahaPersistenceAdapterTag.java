/**
 * 
 */
package org.sapia.soto.activemq;

import java.io.File;
import java.io.IOException;

import org.apache.activemq.store.kahadaptor.KahaPersistenceAdapter;
//import org.apache.activemq.store.kahadaptor.KahaPersistentAdaptor;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author Jean-C�dric Desrochers
 */
public class KahaPersistenceAdapterTag implements ObjectCreationCallback {

  private String _dirName;
  private long _maxDataFileLength;
  
  /**
   * Creates a new KahaPersistenceAdapterTag instance.
   */
  public KahaPersistenceAdapterTag() {
    _maxDataFileLength = 0;
    _dirName = System.getProperty("user.dir");
  }
  
  public void setDir(String aDirName) {
    _dirName = aDirName;
  }
  
  public void setMaxDataFileLength(long aMaxLength) {
    _maxDataFileLength = aMaxLength;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    try {
      File dataDirFile = new File(_dirName);
    
//    Commented out code for ActiveMQ 4.0
//      KahaPersistentAdaptor kaha = new KahaPersistentAdaptor(dataDirFile);

// Commented out code for ActiveMQ 4.1
      KahaPersistenceAdapter kaha = new KahaPersistenceAdapter(dataDirFile);
      if (_maxDataFileLength > 0) {
        kaha.setMaxDataFileLength(_maxDataFileLength);
      }
      
      return kaha;
      
    } catch (IOException ioe) {
      throw new ConfigurationException("Error creating Kaha persistent adapter class", ioe);
    }
  }

}
