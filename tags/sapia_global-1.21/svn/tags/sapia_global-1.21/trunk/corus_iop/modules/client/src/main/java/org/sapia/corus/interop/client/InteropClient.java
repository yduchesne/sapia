package org.sapia.corus.interop.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sapia.corus.interop.Status;
import org.sapia.corus.interop.api.Consts;
import org.sapia.corus.interop.api.Implementation;
import org.sapia.corus.interop.api.InteropLink;
import org.sapia.corus.interop.api.ShutdownListener;
import org.sapia.corus.interop.api.StatusRequestListener;
import org.sapia.corus.interop.soap.FaultException;


/**
 * This class implements a singleton that synchronizes with a corus server according
 * to the corus Interoperability spec.
 * <p>
 * The singleton expects the following system properties to be set:
 *
 * <ul>
 *   <li>corus.process.id</li>
 *   <li>corus.process.poll.interval</li>
 *   <li>corus.process.status.interval</li>
 * </ul>
 *
 * See the corus Interop spec. for more info on these properties.
 * <p>
 * This class implements a client that communicates with a given corus
 * server using an implementation of <code>InteropProtocol</code>. An implementation
 * thereof as in charge of relaying commands back and forth over a specific wire
 * protocol that it abstracts from the singleton. The latter's purpose is twofolds:
 * first, to allow distributed VMs to be managed by a corus server; second,
 * to provide an API that applications can use in order to affect the interaction
 * with the corus server. For example, an application could request a restart of
 * its VM using the <code>restart()</code> method on this class.
 * <p>
 * The <code>InteropClient</code> singleton must be initialized with an
 * <code>InteropProtocol</code> instance before it can communicate with
 * its corus server. This step goes as follows:
 * <pre>
 * InteropClient.getInstance().setProtocol(new HttpProtocol());
 * </pre>
 * <p>
 * The <code>setProtocol()</code> method can only be called once. If the client is not
 * given a protocol, then client applications will still be able to invoke the singleton's
 * API, yet no corresponding commands will be sent to the corus server.
 * <p>
 * To trigger a restart of their VM, applications invoke the following:
 * 
 * <pre>
 * InteropClient.getInstance().restart();
 * </pre>
 * <p>
 * To force the shutdown of the VM of which they are part, applications invoke the following:
 * 
 * <pre>
 * InteropClient.getInstance().shutdown();
 * </pre>
 * The above method internally goes through all <code>ShutdownListener</code>s, then sends
 * a shutdown confirmation to the corus server, and exits the VM through a <code>System.exit()</code>
 * call. 
 * 
 * <b>IMPORTANT</b>: it is important that corus-aware applications terminate using the above approach -
 * and not <code>System.exit()</code>. This is to avoid an auto-restart by the corus server, that will then
 * not know about the VM's shutdown.
 *s   
 * </p>
 * 
 *
 * @see org.sapia.corus.interop.http.HttpProtocol
 *
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class InteropClient implements Consts, Implementation {
  public static final int UNDEFINED_PORT = -1;
  static InteropClient    _instance;
  InteropProtocol         _proto;
  boolean                 _dynamic = System.getProperty(Consts.CORUS_PID) != null;
  List<SoftReference<ShutdownListener>>      _shutdownListeners    = new ArrayList<SoftReference<ShutdownListener>>();
  List<SoftReference<StatusRequestListener>> _statusListeners      = new ArrayList<SoftReference<StatusRequestListener>>();
  boolean                 _exitSystemOnShutdown = true;
  boolean                 _isShutdownInProgress = false;
  InteropClientThread     _thread;
  Log                     _log;
  ClientStatusListener    _listener;
  

  private InteropClient() {
    StdoutLog log = new StdoutLog();
    setLogLevel(log);
    _log = log;
    _log.debug("isDynamic: " + _dynamic);
    _log.debug("CORUS_PROCESS_DIR: " + System.getProperty(CORUS_PROCESS_DIR));
    if (_dynamic) {
      // Process the process directory
      String processDir = System.getProperty(CORUS_PROCESS_DIR);
      if (processDir != null && processDir.charAt(0) == '"') {
        processDir = processDir.substring(1, (processDir.length() - 1));
        System.setProperty(CORUS_PROCESS_DIR, processDir);
        _log.debug("Renamed CORUS_PROCESS_DIR to : " + processDir);
      }
      _statusListeners.add(new SoftReference<StatusRequestListener>(_listener = new ClientStatusListener()));
      redirectOutput();
      _log.debug("Starting interop client thread...");

      _thread = new InteropClientThread(this);
      _thread.start();
      _thread.setPriority(Thread.MAX_PRIORITY);
    } else {
      _log.warn("No corus process ID found; VM was not started dynamically");
    }
    
    Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
            _exitSystemOnShutdown = false;
            _log.info("System shutdown hook called");
          
            if (_isShutdownInProgress) {
                doLogThreadDumpCallingSystemExit();
                doConfirmShutdownWithServer();
            } else {
                doShutdown();
            }
        }
    });
  }
  
  /**
   * Returns the <code>InteropClient</code> singleton.
   *
   * @return an <code>InteropClient</code>
   */
  public static synchronized InteropClient getInstance() {
    if (_instance == null) {
      _instance = new InteropClient();
    }
    InteropLink.setImpl(_instance);

    return _instance;
  }

  /**
   * Sets this client's corus interoperability protocol implementation.
   * The passed in protocol instance is given the <code>Log</code> of this
   * client.
   *
   * @param proto a <code>InteropProtocol</code> implementation.
   * @throws ProtocolAlreadySetException if this instance's {@link InteropProtocol} has
   * already been set.
   */
  public void setProtocol(InteropProtocol proto) throws ProtocolAlreadySetException {
    if (_proto != null) {
      throw new ProtocolAlreadySetException();
    }

    _proto = proto;
    _proto.setLog(_log);
    _proto = proto;
  }

  /**
   * Sets this client's log. By default, this client logs to stdout.
   *
   * @param log a <code>Log</code> instance.
   */
  public void setLog(Log log) {
    _log = log;

    if (_proto != null) {
      _proto.setLog(log);
    }
  }

  /**
   * Returns the corus process identifier corresponding to the VM.
   *
   * @return the dymamo process ID of this VM, or <code>null</code>
   * if this VM was not started by a corus server.
   */
  public String getCorusPid() {
    return System.getProperty(Consts.CORUS_PID);
  }

  /**
   * Returns the corus distribution name of this VM.
   *
   * @return the corus distribution name of this VM, or <code>null</code>
   * if this VM was not started by a corus server.
   */
  public String getDistributionName() {
    return System.getProperty(Consts.CORUS_DIST_NAME);
  }

  /**
   * Returns the version of this VM's distribution.
   *
   * @return the version of this VM's distribution, or <code>null</code>
   * if this VM was not started by a corus server.
   */
  public String getDistributionVersion() {
    return System.getProperty(Consts.CORUS_DIST_VERSION);
  }

  /**
   * Returns the root directory of this VM's distribution.
   *
   * @return the root directory of this VM's distribution, or <code>null</code>
   * if this VM was not started by a corus server.
   */
  public String getDistributionDir() {
    return System.getProperty(Consts.CORUS_DIST_DIR);
  }

  /**
   * @return the host of the corus server that started this VM, or <code>null</code>
   * if this VM was not started by a corus server.
   */
  public String getCorusHost() {
    return System.getProperty(Consts.CORUS_SERVER_HOST);
  }

  /**
   * @return the main port of the corus server that started this VM, or <code>-1</code>
   * if this VM was not started by a corus server.
   */
  public int getCorusPort() {
    if (System.getProperty(Consts.CORUS_SERVER_PORT) != null) {
      return Integer.parseInt(System.getProperty(Consts.CORUS_SERVER_PORT));
    }

    return UNDEFINED_PORT;
  }

  /**
   * @return <code>true</code> if the VM was started by a corus server.
   */
  public boolean isDynamic() {
    return _dynamic;
  }

  /**
   * Sends a restart request to the corus server that started this
   * VM. The corus server will trigger a clean shut down of this VM
   * and restart it.
   */
  public synchronized void restart() {
    if (_proto != null) {
      try {
        _proto.restart();
      } catch (FaultException e) {
        _log.fatal("SOAP fault generated by corus server", e);
      } catch (IOException e) {
        _log.fatal("Could not send restart command to corus server", e);
      }
    }
  }

  /**
   * Shuts down this client. And terminates this VM. Internally
   * calls this client's <code>ShutdownListener</code>s so that the
   * latter can cleanly shut down.
   */
  public synchronized void shutdown() {
      _isShutdownInProgress = true;
      
      if ((_thread != null) && (Thread.currentThread() == _thread)) {
          _log.info("corus server initiated a shutdown");
      }

      if (_thread != null) {
          _thread.interrupt();
      }

      doShutdown();
      doConfirmShutdownWithServer();
    
      _thread = null;
      _proto = null;
      _instance = null;
      _isShutdownInProgress = false;

      if (_exitSystemOnShutdown) {
          _log.info("quitting virtual machine");
          System.exit(0);
      }
  }
  
  private void doConfirmShutdownWithServer() {
      if (_proto != null) {
          try {
              _proto.confirmShutdown();
              _log.info("shutdown confirmed to server");
              Thread.sleep(1000);    // give time to complete shutdown confirmation
              
          } catch (Exception e) {
              _log.warn("Unable to confirm shutdown with server", e);
          }
      }
  }
  
  private void doShutdown() {
    ShutdownListener listener;

    for (int i = 0; i < _shutdownListeners.size(); i++) {
      SoftReference<ShutdownListener> ref = _shutdownListeners.get(i);
      listener = ref.get();

      if (listener == null) {
        _shutdownListeners.remove(i);
        i--;
      } else {
        listener.onShutdown();
      }
    }
  }

  private void doLogThreadDumpCallingSystemExit() {
    Map<Thread, StackTraceElement[]> threadDumps = Thread.getAllStackTraces(); 
    for (Thread t: threadDumps.keySet()) {
      StringBuilder builder = new StringBuilder().
              append("Detecting thread calling System.exit() while performing shutdown...\n").
              append(t.toString()).append("\n");
      
      for (StackTraceElement ste: threadDumps.get(t)) {
        builder.append("\tat ").append(ste.toString()).append("\n");
      }
      
      if (builder.indexOf("java.lang.System.exit(") >= 0) {
        _log.warn(builder.toString());
      }
    }
  }
  
  /**
   * Adds a <code>ShutdownListener</code> to this client. The listener
   * is internally kept in a <code>SoftReference</code>, so client applications
   * should keep a reference on the given listener in order to spare the
   * latter from being GC'ed.
   *
   * @param listener a <code>ShutdownListener</code>.
   */
  public synchronized void addShutdownListener(ShutdownListener listener) {
    _shutdownListeners.add(new SoftReference<ShutdownListener>(listener));
  }

  /**
   * Adds a <code>StatusRequestListener</code> to this client. The listener
   * is internally kept in a <code>SoftReference</code>, so client applications
   * should keep a reference on the given listener in order to spare the
   * latter from being GC'ed.
   *
   * @param listener a <code>StatusRequestListener</code>.
   */
  public synchronized void addStatusRequestListener(StatusRequestListener listener) {
    _statusListeners.add(new SoftReference<StatusRequestListener>(listener));
  }

  /**
   * Internally goes through this client's <code>StatusRequestListener</code>
   * in order to collect status information.
   *
   * @param stat a <code>Status</code> instance.
   */
  public void processStatus(Status stat) {
    StatusRequestListener listener;

    for (int i = 0; i < _statusListeners.size(); i++) {
      SoftReference<StatusRequestListener> ref = _statusListeners.get(i);
      listener = (StatusRequestListener) ref.get();

      if (listener == null) {
        _statusListeners.remove(i);
        i--;
      } else {
        listener.onStatus(stat);
      }
    }
  }

  /**
   * THIS METHOD IS PROVIDED FOR TESTING PURPOSES ONLY.
   *
   * @param exit if <code>true</code>, this instance will call
   * <code>System.exit(0)</code> upon its <code>shutdown()</code>
   * method being called.
   */
  public void setExitSystemOnShutdown(boolean exit) {
    _exitSystemOnShutdown = exit;
  }

  /**
   * THIS METHOD IS PROVIDED FOR TESTING PURPOSES ONLY.
   * @param th the <code>InteropClientThread</code> with which to
   * replace the current one.
   */
  public void setThread(InteropClientThread th) {
    _thread.interrupt();
    _thread = th;
  }
  
  private void redirectOutput(){
    try{
      _log.debug("Redirecting stdout and stderr output...");
      
      if(System.getProperty(CORUS_PROCESS_DIR) != null){
        File procDir = new File(System.getProperty(CORUS_PROCESS_DIR).replace("\"", ""));
        File errFile  = new File(procDir, "stderr.txt");
        File outFile  = new File(procDir, "stdout.txt");

        PrintStream errStream = new PrintStream(new FileOutputStream(errFile), true);
        PrintStream outStream = new PrintStream(new FileOutputStream(outFile), true);        
        _log.debug("stdout --> " + outFile.getAbsolutePath());        
        _log.debug("stderr --> " + errFile.getAbsolutePath());        
        System.setErr(errStream);
        System.setOut(outStream);
        Date date = new Date();
        String header = "[" + getClass().getName() + " : " + date.toString() + "]";
        _log.info(header + " starting interop client");
      }
      else{
        _log.warn("System property not set: " + CORUS_PROCESS_DIR + 
          "; not redirecting process output to file");
      }
    }catch(Exception e){
      _log.warn("Could not redirect stdout and stderr to file", e);
    }
  }  
  
  private void setLogLevel(StdoutLog log){
    String levelName = System.getProperty(CORUS_PROCESS_LOG_LEVEL, LOG_LEVEL_FATAL);
    int level = StdoutLog.FATAL;
    if(levelName.equalsIgnoreCase(LOG_LEVEL_DEBUG)){
      level = StdoutLog.DEBUG;
    }
    else if(levelName.equalsIgnoreCase(LOG_LEVEL_INFO)){
      level = StdoutLog.INFO;
    }    
    else if(levelName.toLowerCase().startsWith(LOG_LEVEL_WARN)){
      level = StdoutLog.WARNING;
    }    
    else if(levelName.toLowerCase().startsWith(LOG_LEVEL_FATAL)){
      level = StdoutLog.FATAL;
    }        
    log.setLevel(level);
  }
}
