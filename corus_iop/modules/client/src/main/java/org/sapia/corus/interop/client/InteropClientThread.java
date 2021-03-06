package org.sapia.corus.interop.client;

import java.util.ArrayList;
import java.util.List;

import org.sapia.corus.interop.AbstractCommand;
import org.sapia.corus.interop.Ack;
import org.sapia.corus.interop.Shutdown;
import org.sapia.corus.interop.Status;
import org.sapia.corus.interop.api.Consts;
import org.sapia.corus.interop.soap.FaultException;


/**
 * @author Yanick Duchesne
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 */
class InteropClientThread extends Thread {

  private long          _statusInterval = 0;
  private long          _pollInterval = 0;
  private long          _analysisInterval = 0;
  private long          _lastStatus = System.currentTimeMillis();
  private long          _lastPoll = _lastStatus;
  private InteropClient _parent;

  /**
   * Creates a new InteropClientThread instance.
   * 
   * @param parent
   * @throws IllegalStateException
   */
  InteropClientThread(InteropClient parent) throws IllegalStateException {
    super.setDaemon(true);
    _parent = parent;

    try {
      _pollInterval = (long) (Float.parseFloat(System.getProperty(Consts.CORUS_POLL_INTERVAL)) * 1000.0);
      _parent._log.info("Poll interval set to: " + _pollInterval);
    } catch (NumberFormatException e) {
      throw new NumberFormatException("Poll interval interval must be valid long value");
    } catch (NullPointerException e) {
      throw new NumberFormatException("Poll interval interval must be specified as system properties");
    }

    try {
      _statusInterval = (long) (Float.parseFloat(System.getProperty(Consts.CORUS_STATUS_INTERVAL)) * 1000.0);
      _parent._log.info("Status interval set to: " + _statusInterval);
    } catch (NumberFormatException e) {
      _parent._log.warn("The status interval is not set - no status information will be sent!");
    } catch (NullPointerException e) {
      _parent._log.warn("The status interval is not set - no status information will be sent!");
    }

    try {
      _analysisInterval = (long) (Float.parseFloat(System.getProperty(Consts.CORUS_CLIENT_ANALYSIS_INTERVAL)) * 1000.0);
      if (_analysisInterval <= 0) {
        _parent._log.warn("Client analysis interval could not be zero - setting default value to 1000 ");
        _analysisInterval = 1000;
      } else {
        _parent._log.info("Client analysis interval set to: " + _analysisInterval);
      }
    } catch (NumberFormatException e) {
      _parent._log.warn("The client analysis interval is not set - setting default value to 1000");
      _analysisInterval = 1000;
    } catch (NullPointerException e) {
      _parent._log.warn("The client analysis interval is not set - setting default value to 1000");
      _analysisInterval = 1000;
    }
  }

  public void run() {
    if (!_parent.isDynamic()) {
      return;
    }

    if(_parent._log.isDebugEnabled())
      _parent._log.debug("Starting interop client thread");

    while (true) {
	  try {
	    if(_parent._log.isDebugEnabled())
	      _parent._log.debug(String.format("Sleeping for %s...", _analysisInterval));
	    
		  Thread.sleep(_analysisInterval);
	  } catch (InterruptedException e) {
	    _parent._log.warn("Interop thread was interrupted");
		  break;
	  }      
	  
  	  if(_parent._proto == null){
  	    _parent._log.info("Protocol not set on InteropClient; will not be polling Corus");
  	  }
  	  else {
        try {
          if(_parent._log.isDebugEnabled())
            _parent._log.debug("Checking if polling is due...");
          
          List<AbstractCommand> response = new ArrayList<AbstractCommand>();
          Status status = null;
          long currentTime = System.currentTimeMillis();

          if (_statusInterval > 0 && ((currentTime - _lastStatus) >= _statusInterval)) {
            _lastStatus = currentTime;
            status = new Status();
            _parent.processStatus(status);
          }
          
          if (_pollInterval > 0 && ((currentTime - _lastPoll) >= _pollInterval)) {
            _lastPoll = currentTime;
            
            if (status == null) {
              if(_parent._log.isDebugEnabled())
                _parent._log.debug("Polling");
              response = _parent._proto.poll();
            } else {
              if(_parent._log.isDebugEnabled())
                _parent._log.debug("Polling and sending status");
              response = _parent._proto.pollAndSendStatus(status);
            }
          } else if (status != null) {
            if(_parent._log.isDebugEnabled())
              _parent._log.debug("Sending status");
            response = _parent._proto.sendStatus(status);
          }

          AbstractCommand command;

          for (int i = 0; i < response.size(); i++) {
            command = (AbstractCommand) response.get(i);
            if(_parent._log.isDebugEnabled())
              _parent._log.debug("Command received from corus server; command ID is: " +
                                 command.getCommandId());

            if (command instanceof Ack) {
              if(_parent._log.isDebugEnabled())
                _parent._log.debug("Command was an Ack - ignoring");

              // in this case, noop.
              // according to the interop spec, an
              // Ack is not accompanied by other commands.
              break;
            } else if (command instanceof Shutdown) {
              Shutdown sh = (Shutdown) command;
              _parent._log.info("Shutdown requested from: " +
                                sh.getRequestor());
              _parent.shutdown();

              break;
            }
          }
        } catch (FaultException e) {
          _parent._log.info("corus server generated a SOAP fault", e);
        } catch (Exception e) {
          _parent._log.info("Error caught sending SOAP request to corus server", e);
        } 
      }
    }

    _parent._log.debug("Terminating interop client thread");
  }
}
