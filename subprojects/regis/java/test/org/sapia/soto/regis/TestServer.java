package org.sapia.soto.regis;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class TestServer {
  
  private static TestServer _INSTANCE;
  private String _profile;
  private String _quality;
  private List _providers;
  
  public static TestServer getInstance() {
    return _INSTANCE;
  }
  
  public TestServer() {
    _INSTANCE = this;
    _providers = new ArrayList();
  }
  
  /**
   * Returns the profile value.
   *
   * @return The profile value.
   */
  public String getProfile() {
    return _profile;
  }
  
  /**
   * Changes the value of the profile.
   *
   * @param aProfile The new profile value.
   */
  public void setProfile(String aProfile) {
    _profile = aProfile;
  }
  
  /**
   * Returns the quality value.
   *
   * @return The quality value.
   */
  public String getQuality() {
    return _quality;
  }
  
  /**
   * Changes the value of the quality.
   *
   * @param aQuality The new quality value.
   */
  public void setQuality(String aQuality) {
    _quality = aQuality;
  }
  
  public void addProvider(TestProvider aProvider) {
    _providers.add(aProvider);
  }
}
