package org.sapia.corus.repository.task;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sapia.corus.client.services.cluster.ClusterNotification;
import org.sapia.corus.client.services.cluster.Endpoint;
import org.sapia.corus.client.services.configurator.Configurator.PropertyScope;
import org.sapia.corus.client.services.configurator.Property;
import org.sapia.corus.client.services.configurator.Tag;
import org.sapia.ubik.util.Collects;

public class SendConfigNotificationTaskTest extends AbstractRepoTaskTest {
  
  private List<Property> propList;
  private Set<Tag> tags;
  private SendConfigNotificationTask task;

  @Before
  public void setUp() {
    super.doSetUp();
    Set<Endpoint> endpoints = new HashSet<Endpoint>();
    task     = new SendConfigNotificationTask(repoConfig, endpoints);
    propList = Collects.arrayToList(new Property("test", "testValue", null));
    tags     = new HashSet<Tag>();
    tags.add(new Tag("testTag"));
  }
  
  @Test
  public void testWithTagsAndProperties() throws Throwable {
    when(configurator.getAllPropertiesList(PropertyScope.PROCESS)).thenReturn(propList);
    when(configurator.getTags()).thenReturn(tags);
    task.execute(taskContext, null);
    verify(cluster).send(any(ClusterNotification.class));
  }
  
  @Test
  public void testWithEmptyTagsAndProperties() throws Throwable {
    propList.clear();
    tags.clear();
    when(configurator.getAllPropertiesList(PropertyScope.PROCESS)).thenReturn(propList);
    when(configurator.getTags()).thenReturn(tags);
    task.execute(taskContext, null);
    verify(cluster, never()).send(any(ClusterNotification.class));
  }

  @Test
  public void testWithEmptyTags() throws Throwable {
    tags.clear();
    when(configurator.getAllPropertiesList(PropertyScope.PROCESS)).thenReturn(propList);
    when(configurator.getTags()).thenReturn(tags);
    task.execute(taskContext, null);
    verify(cluster).send(any(ClusterNotification.class));
  }    

  @Test
  public void testWithEmptyProperties() throws Throwable {
    propList.clear();
    when(configurator.getAllPropertiesList(PropertyScope.PROCESS)).thenReturn(propList);
    when(configurator.getTags()).thenReturn(tags);
    task.execute(taskContext, null);
    verify(cluster).send(any(ClusterNotification.class));
  }    
  
}
