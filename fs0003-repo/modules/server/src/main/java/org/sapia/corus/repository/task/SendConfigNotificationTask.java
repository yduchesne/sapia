package org.sapia.corus.repository.task;

import java.util.Properties;
import java.util.Set;

import org.sapia.corus.client.services.cluster.Endpoint;
import org.sapia.corus.client.services.repository.ConfigNotification;
import org.sapia.corus.taskmanager.util.RunnableTask;

/**
 * Sends a {@link ConfigNotification} to targeted nodes. 
 * 
 * @author yduchesne
 *
 */
public class SendConfigNotificationTask extends RunnableTask {
  
  private Set<Endpoint> targets;
  
  public SendConfigNotificationTask(Set<Endpoint> targets) {
    this.targets = targets;
  }
  
  @Override
  public void run() {
    try {
      Properties props = context().getServerContext().getProcessProperties();
      Set<String> tags = context().getServerContext().getServices().getConfigurator().getTags();
      
      if (props.isEmpty() && tags.isEmpty()) {
        context().debug("No tags or properties to push to: " + targets);
      } else {
        context().debug("Sending configuration notification to: " + targets);
        ConfigNotification notif = new ConfigNotification();
        notif.getTargets().addAll(targets);
        notif.addProperties(props);
        notif.addTags(tags);
      
        context()
          .getServerContext()
          .getServices()
          .getClusterManager()
          .send(notif);
      }
    } catch (Exception e) {
      context().error("Could not send configuration to targets: " + targets, e);
    }
  }

}
