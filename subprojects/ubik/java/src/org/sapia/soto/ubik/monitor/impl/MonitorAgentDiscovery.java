package org.sapia.soto.ubik.monitor.impl;

import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.Env;
import org.sapia.soto.Layer;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.ServiceSelector;
import org.sapia.soto.ubik.monitor.MonitorAgent;

/**
 * Utility class that discovers <code>MonitorAgent</code>s
 * within a Soto container.
 * 
 * @author yduchesne
 *
 */
public class MonitorAgentDiscovery{
  
  private Env _env;
  
  public MonitorAgentDiscovery(Env env){
    _env = env;
  }
  
  public List discover(){
    List metadatas = new ArrayList(); 
    
    List tmp = _env.lookup(
        new ServiceSelector(){
          public boolean accepts(ServiceMetaData meta) {
            if(meta.getService() instanceof MonitorAgent){
              return true;
            }
            List layers = meta.getLayers();
            if(layers != null){
              for(int i = 0; i < layers.size(); i++){
                Object layer = layers.get(i);
                if(!(layer instanceof MonitorAgent)){
                  return false;
                }
              }
              return true;
            }
            else{
              return false;
            }
          }
        }, true);
    metadatas.addAll(tmp);
    
    List agents = new ArrayList();
    for(int i = 0; i < metadatas.size(); i++){
      ServiceMetaData meta = (ServiceMetaData)metadatas.get(i);
      if(meta.getService() instanceof MonitorAgent){
        agents.add(meta.getService());
      }
      else{
        List layers = meta.getLayers();
        for(int j = 0; j < layers.size(); j++){
          Layer layer = (Layer)layers.get(j);
          if(layer instanceof MonitorAgent){
            agents.add(layer);
          }
        }
      }
    }
    return agents;
  }

}
