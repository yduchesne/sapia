package org.sapia.corus.deployer.config;

/**
 * A helper class that encapsulates various process startup parameters.
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Env {
  private String     _distDir;
  private String     _commonDir;
  private String     _processDir;
  private String     _profile;
  private Property[] _props;

  /**
   * Constructor for Env.
   * 
   * @param profile the name of the profile under which a process is to be started.
   * @param distDir the path to the distribution directory of the process to start.
   * @param commonDir the path to the "common" directory of the process to start - corresponds to user.dir.
   * @param processDir the path to the process directory.
   * @param props a <code>Property[]</code> instance that corresponds to the properties that will
   * be dynamically passed to the started process.
   */
  public Env(String profile, String distDir, String commonDir, String processDir,
             Property[] props) {
    _distDir    = distDir;
    _processDir = processDir;
    _props      = props;
    _profile    = profile;
    _commonDir  = commonDir;
  }

  /**
   * @return the distribution directory of the process to start.
   */
  public String getDistDir() {
    return _distDir;
  }

  /**
   * @return the "common" directory of the process to start.
   */
  public String getCommonDir() {
    return _commonDir;
  }

  /**
   * @return the process directory of the process to start.
   */
  public String getProcessDir() {
    return _processDir;
  }
  
  /**
   * @return the name of the profile under which to start the process.
   */
  public String getProfile() {
    return _profile;
  }

  /**
   * @return the properties to pass to the started process.
   */
  public Property[] getProperties() {
    return _props;
  }
}
