package org.sapia.corus.repository.task;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sapia.corus.client.services.cluster.ClusterNotification;
import org.sapia.corus.client.services.cluster.Endpoint;
import org.sapia.corus.client.services.configurator.Tag;

public class SendConfigNotificationTaskTest extends AbstractRepoTaskTest {
  
  private Properties properties;
  private Set<Tag> tags;
  private SendConfigNotificationTask task;

  @Before
  public void setUp() {
    super.doSetUp();
    Set<Endpoint> endpoints = new HashSet<Endpoint>();
    task = new SendConfigNotificationTask(repoConfig, endpoints);
    properties = new Properties();
    properties.setProperty("test", "testValue");
    tags = new HashSet<Tag>();
    tags.add(new Tag("testTag"));
  }
  
  @Test
  public void testWithTagsAndProperties() throws Throwable {
    when(serverContext.getProcessProperties()).thenReturn(properties);
    when(configurator.getTags()).thenReturn(tags);
    task.execute(taskContext, null);
    verify(cluster).send(any(ClusterNotification.class));
  }
  
  @Test
  public void testWithEmptyTagsAndProperties() throws Throwable {
    properties.clear();
    tags.clear();
    when(serverContext.getProcessProperties()).thenReturn(properties);
    when(configurator.getTags()).thenReturn(tags);
    task.execute(taskContext, null);
    verify(cluster, never()).send(any(ClusterNotification.class));
  }

  @Test
  public void testWithEmptyTags() throws Throwable {
    tags.clear();
    when(serverContext.getProcessProperties()).thenReturn(properties);
    when(configurator.getTags()).thenReturn(tags);
    task.execute(taskContext, null);
    verify(cluster).send(any(ClusterNotification.class));
  }    

  @Test
  public void testWithEmptyProperties() throws Throwable {
    properties.clear();
    when(serverContext.getProcessProperties()).thenReturn(properties);
    when(configurator.getTags()).thenReturn(tags);
    task.execute(taskContext, null);
    verify(cluster).send(any(ClusterNotification.class));
  }    
  
}
