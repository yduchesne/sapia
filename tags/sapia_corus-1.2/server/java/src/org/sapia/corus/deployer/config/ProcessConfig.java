package org.sapia.corus.deployer.config;

import org.sapia.console.CmdLine;

import org.sapia.corus.LogicException;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

import java.util.ArrayList;
import java.util.List;


/**
 * This class corresponds to the <code>process</code> element in the
 * corus.xml file. This element provides process configuration parameters.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ProcessConfig implements java.io.Serializable, ObjectHandlerIF {
  public static final int DEFAULT_POLL_INTERVAL   = 10;
  public static final int DEFAULT_STATUS_INTERVAL = 30;
  private boolean         _invoke;
  private List            _starters               = new ArrayList();
  private List            _ports                  = new ArrayList();
  private int             _maxKillRetry           = -1;
  private int             _shutDownTimeout        = -1;
  private String          _name;
  private int             _statusInterval         = DEFAULT_STATUS_INTERVAL;
  private int             _pollInterval           = DEFAULT_POLL_INTERVAL;
  private boolean         _deleteOnKill           = false;
  /**
   * Sets this process config's name.
   *
   * @param a name.
   */
  public void setName(String name) {
    _name = name;
  }

  /**
   * Returns this process config's name.
   *
   * @return this process config's name.
   */
  public String getName() {
    return _name;
  }

  /**
   * Sets the maximum number of successive times the Corus
   * server should try to kill a process instance corresponding to
   * this object.
   *
   * @param maxRetry a number of maximum successive "kill" attempts.
   */
  public void setMaxKillRetry(int maxRetry) {
    _maxKillRetry = maxRetry;
  }
  
  /**
   * Indicates if the process directory should be deleted after being
   * terminated (defaults to true).
   */
  public void setDeleteOnKill(boolean delete){
    _deleteOnKill = delete;
  }
  
  /**
   * @return <code>true</code> if the process directory should be deleted after being
   * terminated
   */
  public boolean isDeleteOnKill(){
    return _deleteOnKill;
  }

  /**
   * Returns the maximum number of successive times the Corus
   * server should try to kill a process instance corresponding to
   * this object.
   *
   * @return a maximum number of successive "kill" attempts;
   * <code>-1</code> is returned if no value was specified.
   */
  public int getMaxKillRetry() {
    return _maxKillRetry;
  }

  /**
   * Sets the polling interval of the process corresponding to this instance.
   *
   * @param interval the interval (in seconds) at which the corresponding process
   * should poll the corus server - defaults to 10 seconds.
   */
  public void setPollInterval(int interval) {
    _pollInterval = interval;
  }

  /**
   * @return this instance's poll interval.
   * @see #setPollInterval(int)
   */
  public int getPollInterval() {
    return _pollInterval;
  }

  /**
   * Sets the status interval of the process corresponding to this instance.
   *
   * @param interval the interval (in seconds) at which the corresponding process
   * should send a status to the corus server - defaults to 25 seconds.
   */
  public void setStatusInterval(int interval) {
    _statusInterval = interval;
  }

  /**
   * @return this instance's status interval.
   * @see #setStatusInterval(int)
   */
  public int getStatusInterval() {
    return _statusInterval;
  }

  /**
   * Sets the number of seconds the Corus server should wait
   * for receiving process kill confirmation.
   *
   * @param shutDownTimeout a number in seconds.
   */
  public void setShutdownTimeout(int shutDownTimeout) {
    _shutDownTimeout = shutDownTimeout;
  }

  /**
   * Returns the number of seconds the Corus server should wait
   * for receiving process kill confirmation.
   *
   * @return a number of seconds, or <code>-1</code> if no value
   * was specified.
   */
  public int getShutdownTimeout() {
    return _shutDownTimeout;
  }

  /**
   * Sets if a process corresponding to this instance should be instantiated only if it has been invoked
   * explicitly by its name (in such a case, this method's param must
   * be <code>true</code>
   *
   * @param <code>true</code> if a process corresponding to this param should
   * be invoked explicitly by its name.
   */
  public void setInvoke(boolean invoke) {
    _invoke = invoke;
  }

  /**
   * Returns <code>true</code> if a process corresponding to this param should
   * be invoked explicitly by its name.
   */
  public boolean isInvoke() {
    return _invoke;
  }
  
  /**
   * @return a <code>Port</code> that is added to this instance and should
   * correspond to an existing port range.
   */
  public Port createPort(){
    Port p = new Port();
    _ports.add(p);
    return p;
  }
  
  /**
   * @return the <code>List<code> of this instance's <code>Port</code>.
   */
  public List getPorts(){
    return _ports;
  }

  /**
   * Returns a "command-line" representation of this instance.
   *
   * @return this instance as a <code>CmdLine</code> object, or
   * <code>null</code> if no <code>CmdLine</code> could be generated
   * for the profile contained in the passed in <code>Env</code>
   * instance.
   */
  public CmdLine toCmdLine(Env env) throws LogicException {
    Starter st = findFor(env.getProfile());

    if (st == null) {
      return null;
    }

    return st.toCmdLine(env);
  }

  /**
   * Returns the profiles that this instance contains.
   *
   * @return a <code>List</code> of profile names.
   */
  public List getProfiles() {
    List    profiles = new ArrayList();
    Starter st;

    for (int i = 0; i < _starters.size(); i++) {
      st = (Starter) _starters.get(i);
      profiles.add(st.getProfile());
    }

    return profiles;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(String, Object)
   */
  public void handleObject(String elementName, Object starter)
                    throws ConfigurationException {
    if (starter instanceof Starter) {
      _starters.add(starter);
    } else {
      throw new ConfigurationException(starter.getClass().getName() +
                                       " does not implement the " +
                                       Starter.class.getName() + " interface");
    }
  }

  private Starter findFor(String profile) {
    Starter toReturn = null;
    Starter current;

    for (int i = 0; i < _starters.size(); i++) {
      current = (Starter) _starters.get(i);

      if ((current.getProfile() == null) && (toReturn == null)) {
        toReturn = current;
      } else if ((current.getProfile() != null) &&
                   current.getProfile().equals(profile)) {
        toReturn = current;
      }
    }

    return toReturn;
  }

  public String toString() {
    return "[ name=" + _name + ", maxKillRetry=" + _maxKillRetry +
           ", shutDownTimeout=" + _shutDownTimeout + " java=" + _starters +
           ", deleteOnKill=" + _deleteOnKill + " ]";
  }
}