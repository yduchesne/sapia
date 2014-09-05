package org.sapia.corus.client.services.processor;

import java.io.Serializable;

/**
 * Keeps information pertaining to the distribution a process "comes" from.
 * 
 * @author Yanick Duchesne
 */
public class DistributionInfo implements Serializable, Comparable<DistributionInfo> {

  static final long serialVersionUID = 1L;

  private static final int PRIME = 31;

  private String name;
  private String version;
  private String profile;
  private String processName;

  /**
   * Creates a new instance of this class with the given params.
   * 
   * @param name
   *          the distribution's name.
   * @param version
   *          the distribution's version.
   * @param profile
   *          the distribution's profile.
   * @param processName
   *          the distribution's process configuration name.
   */
  public DistributionInfo(String name, String version, String profile, String processName) {
    this.name = name;
    this.version = version;
    this.profile = profile;
    this.processName = processName;
  }

  /**
   * Returns this instance's profile.
   * 
   * @return a profile name.
   */
  public String getProfile() {
    return profile;
  }

  /**
   * Returns the name of the distribution to which this instance corresponds.
   * 
   * @return a distribution name.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the name of the process configuration to which this instance
   * corresponds.
   * 
   * @return a process name.
   */
  public String getProcessName() {
    return processName;
  }

  /**
   * Returns the version of the distribution to which this instance corresponds.
   * 
   * @return a version as a string.
   */
  public String getVersion() {
    return version;
  }

  @Override
  public int compareTo(DistributionInfo other) {
    int c = name.compareTo(other.getName());
    if (c == 0) {
      c = version.compareTo(other.getVersion());
    }
    if (c == 0) {
      c = processName.compareTo(other.getProcessName());
    }
    if (c == 0) {
      c = profile.compareTo(other.getProfile());
    }
    return c;
  }

  @Override
  public int hashCode() {
    return name.hashCode() * PRIME + processName.hashCode() * PRIME + profile.hashCode() * PRIME + version.hashCode() * PRIME + profile.hashCode()
        * PRIME;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DistributionInfo) {
      DistributionInfo other = (DistributionInfo) obj;
      return other.getName().equals(name) && other.getProcessName().equals(processName) && other.getProfile().equals(profile)
          && other.getVersion().equals(version) && other.getProfile().equals(profile);

    }
    return false;
  }

  /**
   * Return this instance's string representation.
   * 
   * @return this as a string.
   */
  public String toString() {
    return "[ dist=" + name + ", version=" + version + ", profile=" + profile + ", process name=" + processName + " ]";
  }
}
