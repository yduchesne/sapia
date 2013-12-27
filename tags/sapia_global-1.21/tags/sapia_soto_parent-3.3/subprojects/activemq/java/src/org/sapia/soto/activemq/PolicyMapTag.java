/**
 * 
 */
package org.sapia.soto.activemq;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.activemq.broker.region.policy.DispatchPolicy;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author Jean-Cedric Desrochers
 */
public class PolicyMapTag implements ObjectCreationCallback {

  private DispatchPolicy _dispatchPolicy;
  private String _queue;
  private String _topic;
  
  /**
   * Creates a new PolicyMapTag instance.
   */
  public PolicyMapTag() {
  }
  
  public void setDispatchPolicy(DispatchPolicy aDispatchPolicy) {
    _dispatchPolicy = aDispatchPolicy;
  }
  
  public void setQueue(String aName) {
    _queue = aName;
  }
  
  public void setTopic(String aName) {
    _topic = aName;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if (_dispatchPolicy == null) {
      throw new ConfigurationException("The dispatch policy of this PolicyMapTag is null");
    }
    
    PolicyMap policyMap = new PolicyMap();
    
    if (_queue == null && _topic == null) {
      PolicyEntry policyEntry = new PolicyEntry();
      policyEntry.setDispatchPolicy(_dispatchPolicy);
    } else {
      ArrayList policyEntries = new ArrayList();
      if (_queue != null) {
        for (StringTokenizer token = new StringTokenizer(_queue, ",", false); token.hasMoreTokens(); ) {
          String queueName = token.nextToken();
          PolicyEntry policyEntry = new PolicyEntry();
          policyEntry.setQueue(queueName);
          policyEntry.setDispatchPolicy(_dispatchPolicy);
          policyEntries.add(policyEntry);
        }
      }
      if (_topic != null) {
        for (StringTokenizer token = new StringTokenizer(_topic, ",", false); token.hasMoreTokens(); ) {
          String topicName = token.nextToken();
          PolicyEntry policyEntry = new PolicyEntry();
          policyEntry.setTopic(topicName);
          policyEntry.setDispatchPolicy(_dispatchPolicy);
          policyEntries.add(policyEntry);
        }
      }
      policyMap.setPolicyEntries(policyEntries);
    }
    
    return policyMap;
  }

}
