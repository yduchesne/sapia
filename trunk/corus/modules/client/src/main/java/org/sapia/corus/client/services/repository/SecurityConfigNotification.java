package org.sapia.corus.client.services.repository;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import org.sapia.corus.client.services.cluster.ClusterNotification;
import org.sapia.corus.client.services.security.ApplicationKeyManager.AppKeyConfig;
import org.sapia.corus.client.services.security.SecurityModule.RoleConfig;
import org.sapia.ubik.util.Strings;

/**
 * Holds the following security configuration elements:
 * <ul>
 *  <li>Roles and their corresponding permissions.
 *  <li>Application IDs, application keys, and their corresponding role.
 * </ul>
 * 
 * @author yduchesne
 *
 */
public class SecurityConfigNotification extends ClusterNotification {
  
  static final long serialVersionID = 1L;

  /**
   * The remote event type corresponding to an instance of this class.
   */
  public static final String EVENT_TYPE = "corus.event.repository.notif.security-config";
  
  private List<RoleConfig>   roleConfigurations;
  private List<AppKeyConfig> appKeyConfigurations;

  /**
   * Do not call: meant for externalization only.
   */
  public SecurityConfigNotification() {
  }

  public SecurityConfigNotification(List<RoleConfig> roleConfigurations, List<AppKeyConfig> appKeyConfigurations) {
    this.roleConfigurations   = roleConfigurations;
    this.appKeyConfigurations = appKeyConfigurations;
  }
  
  @Override
  public String getEventType() {
    return EVENT_TYPE;
  }
  
  /**
   * @return a {@link List} of {@link RoleConfig} instances.
   */
  public List<RoleConfig> getRoleConfigurations() {
    return roleConfigurations;
  }
  
  /**
   * @return a {@link List} of  {@link AppKeyConfig} instances.
   */
  public List<AppKeyConfig> getAppKeyConfigurations() {
    return appKeyConfigurations;
  }
  
  // --------------------------------------------------------------------------
  // Externalizable

  @SuppressWarnings("unchecked")
  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    roleConfigurations   = (List<RoleConfig>) in.readObject();
    appKeyConfigurations = (List<AppKeyConfig>) in.readObject();
    
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeObject(roleConfigurations);
    out.writeObject(appKeyConfigurations);
  }

  // --------------------------------------------------------------------------
  // Object overrides

  @Override
  public String toString() {
    return Strings.toStringFor(this, "roleConfigurations", roleConfigurations, "targeted", getTargets(), "visited", getVisited());
  }

}
