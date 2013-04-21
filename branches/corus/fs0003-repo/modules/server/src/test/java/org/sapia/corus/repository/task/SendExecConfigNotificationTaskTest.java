package org.sapia.corus.repository.task;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sapia.corus.client.services.cluster.ClusterNotification;
import org.sapia.corus.client.services.cluster.Endpoint;
import org.sapia.corus.client.services.processor.ExecConfig;

public class SendExecConfigNotificationTaskTest extends AbstractRepoTaskTest {
  
  private List<ExecConfig>               execConfigs;
  private SendExecConfigNotificationTask task;

  @Before
  public void setUp() {
    super.doSetUp();
    execConfigs = mock(List.class);
    task = new SendExecConfigNotificationTask(execConfigs, new HashSet<Endpoint>());
  }
  
  @Test
  public void testNonEmptyConfigList() throws Throwable {
    when(execConfigs.isEmpty()).thenReturn(false);
    task.execute(taskContext, null);
    verify(cluster).send(any(ClusterNotification.class));
  }
  
  @Test
  public void testWithEmptyConfigList() throws Throwable {
    when(execConfigs.isEmpty()).thenReturn(true);
    task.execute(taskContext, null);
    verify(cluster, never()).send(any(ClusterNotification.class));
  }
}
