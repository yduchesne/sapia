package org.sapia.corus.processor;

import org.sapia.corus.deployer.config.Distribution;
import org.sapia.corus.deployer.config.ProcessConfig;

public class ProcessRef implements Comparable<ProcessRef> {

  private Distribution dist;
  private ProcessConfig processConf;
  private String profile;
  private int index;
  private int instanceCount = 1;

  public ProcessRef(Distribution dist, ProcessConfig conf, String profile,
      int index) {
    this.dist = dist;
    this.processConf = conf;
    this.profile = profile;
    this.index = index;
  }
  
  public int getInstanceCount() {
    return instanceCount;
  }

  public String getProfile() {
    return profile;
  }

  public Distribution getDist() {
    return dist;
  }

  public ProcessConfig getProcessConfig() {
    return processConf;
  }

  public int hashCode() {
    return processConf.hashCode() ^ dist.hashCode();
  }

  public boolean equals(Object other) {
    if (other instanceof ProcessRef) {
      ProcessRef otherRef = (ProcessRef) other;
      return dist.equals(otherRef.dist)
          && processConf.equals(otherRef.processConf);
    } else {
      return false;
    }
  }

  public int compareTo(ProcessRef other) {
    return -(index - other.index);
  }
  
  @Override
  public String toString() {
    return new StringBuilder("[")
      .append("dist=").append(dist)
      .append("processConf=").append(processConf)
      .append("]")
      .toString();
  }

}
