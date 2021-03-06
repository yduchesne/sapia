package org.sapia.corus.interop.client;

import org.sapia.corus.interop.Context;
import org.sapia.corus.interop.Status;
import org.sapia.corus.interop.Param;
import org.sapia.corus.interop.api.StatusRequestListener;

/**
 * @author Yanick Duchesne
 */
public class ClientStatusListener implements StatusRequestListener{
  
  public static final String CORUS_PROCESS_STATUS = "corus.process.status";
  public static final String TOTAL_MEMORY = "vm.totalMemory";
  public static final String FREE_MEMORY = "vm.freeMemory";
  public static final String MAX_MEMORY = "vm.maxMemory";  
  
  private static boolean isPlatformMBeansSupported;
  
  static{
    try{
      Class.forName("java.lang.management.RuntimeMXBean");
      isPlatformMBeansSupported = true;
    }catch(Exception e){
      isPlatformMBeansSupported = false;
    }
  }
 
  
  /**
   * @see org.sapia.corus.interop.client.StatusRequestListener#onStatus(org.sapia.corus.interop.Status)
   */
  public void onStatus(Status status) {
    Context t = new Context();
    t.setName(CORUS_PROCESS_STATUS);
    
    Param maxMem = new Param();
    maxMem.setName(MAX_MEMORY);
    maxMem.setValue(Long.toString(Runtime.getRuntime().maxMemory()));
    
    Param totalMem = new Param();
    totalMem.setName(TOTAL_MEMORY);
    totalMem.setValue(Long.toString(Runtime.getRuntime().totalMemory()));
    
    Param freeMem = new Param();
    freeMem.setName(FREE_MEMORY);
    freeMem.setValue(Long.toString(Runtime.getRuntime().freeMemory()));
    
    
    t.addParam(maxMem);
    t.addParam(freeMem);
    t.addParam(totalMem);
    if(isPlatformMBeansSupported){
      PlatformMBeansStatusHelper.process(t);
    }
    status.addContext(t);
  }

}
