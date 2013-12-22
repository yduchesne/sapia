package org.sapia.corus.interop.client;

import org.sapia.corus.interop.ProcessEvent;
import org.sapia.corus.interop.api.ProcessEventListener;

public class TestProcessEventListener implements ProcessEventListener {
  
  boolean called;
  
  @Override
  public void onProcessEvent(ProcessEvent evt) {
    this.called = true;
  }

}
