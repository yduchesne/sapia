/**
 * 
 */
package org.sapia.soto.activemq.cluster;

import javax.jms.JMSException;

import org.apache.activecluster.ClusterFactory;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author Jean-Cedric Desrochers
 */
public class ActiveClusterRef implements ObjectCreationCallback {

  /** The cluster factory to create. */
  private ClusterFactory _clusterFactory;
  
  /** The groupd name of the destination cluster. */
  private String _groupName;
  
  /**
   * Creates a new ActiveClusterRef instance.
   */
  public ActiveClusterRef() {
  }
  
  public void setClusterFactory(ClusterFactory aFactory) {
    _clusterFactory = aFactory;
  }
  
  public void setGroupName(String aName) {
    _groupName = aName;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if (_clusterFactory == null) {
      throw new ConfigurationException("Could not create a cluster object - the cluster factory is not set");
    } else if (_groupName == null) {
      throw new ConfigurationException("Could not create a cluster object - the cluster group name is not set");
    }
    
    try {
      return _clusterFactory.createCluster(_groupName);
    } catch (JMSException jmse) {
      throw new ConfigurationException("Error creating new cluster instance", jmse);
    }
  }

}
