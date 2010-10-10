package org.sapia.soto.log;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.util.EntityResolverImpl;
import org.w3c.dom.Document;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class Log4jService implements LogService, EnvAware {
  private Env    _env;
  private String _configPath;
  private Map _loggerDefs;

  public Log4jService() {
    _loggerDefs = new HashMap();
  }
  
  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
  }

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    if(_configPath == null) {
      BasicConfigurator.configure();
      Logger.getInstance(getClass()).debug(
          "XML config file not specified; using basic configurator");
    } else {
      DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
      fac.setValidating(false);

      DocumentBuilder builder = fac.newDocumentBuilder();
      InputStream is = _env.resolveResource(_configPath).getInputStream();
      builder.setEntityResolver(new EntityResolverImpl(_env));

      try {
        Document doc = builder.parse(is);
        DOMConfigurator.configure(doc.getDocumentElement());
        Logger.getInstance(getClass()).debug(
            "XML config file successfully loaded");
      } finally {
        is.close();
      }
    }
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }

  /**
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {
    _env = env;
  }

  public void setConfigPath(String configPath) {
    _configPath = configPath;
  }
  
  public void addLoggerDef(LoggerDef aDef) {
    _loggerDefs.put(aDef.getId(), aDef);
  }
  
  public LoggerDef getLoggerDef(String anId) {
    return (LoggerDef) _loggerDefs.get(anId);
  }
}
