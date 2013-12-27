package org.sapia.soto.log;

import org.sapia.soto.Service;

/**
 * 
 *
 * @author Jean-Cedric Desrochers
 */
public interface LogService extends Service {

  public void addLoggerDef(LoggerDef aLoggerDef);
  
  public LoggerDef getLoggerDef(String anId);
}
