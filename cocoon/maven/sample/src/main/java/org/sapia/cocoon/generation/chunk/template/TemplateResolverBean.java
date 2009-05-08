package org.sapia.cocoon.generation.chunk.template;

import java.io.IOException;

import org.apache.cocoon.environment.SourceResolver;
import org.sapia.cocoon.generation.chunk.exceptions.TemplateNotFoundException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.xml.sax.SAXException;

/**
 * This class implements the {@link TemplateResolver} interface and provides caching
 * behavior.
 * 
 * @author yduchesne
 *
 */
public class TemplateResolverBean implements 
  TemplateResolver, InitializingBean, DisposableBean{
  
  private TemplateCache cache = new TemplateCache();
  private ParserConfig config = new ParserConfig();
  
  /**
   * @param max the maximum of templates that should be tolerated in the cache - no
   * such limit by default.
   */
  public void setMaxTemplates(int max){
    cache.setMaxSize(max);
  }
  
  /**
   * @param reload if <code>true</code>, this instance will check for changes in template files and
   * reload them if required; otherwise, no checks will be done (<code>true</code> by default).
   */
  public void setReloadEnabled(boolean reload){
    cache.setReloadEnabled(reload);
  }
  
  /**
   * @param millis the amount of time of idleness (in millis) after which templates that
   * have not been used must be removed from the cache - by default, the idleness is not
   * taken into account: templates are kept in cache indefinitely.
   */
  public void setIdleTime(long millis){
    cache.setIdleTime(millis);
  }
  
  public void setIdleTimeSeconds(int secs){
    setIdleTime(secs * 1000);
  }
  
  public void setIdleTimeMinutes(int mins){
    setIdleTime(mins * 60);
  }
  
  /**
   * @param millis the interval (in millis) at which the internal cleanup thread runs
   * (defaults to 30 secs - or 30000 millis).
   */
  public void setCleanupInterval(long millis){
    cache.setCleanupInterval(millis);
  }
  
  /**
   * @param startDelim the "start" delimiter, identifying variables within templates
   * (defaults to <code>$[</code>.
   */
  public void setStartDelim(String startDelim){
    config.setStartVarDelim(startDelim);
  }
  
  /**
   * @param endDelim the "end" delimiter, identifying variables within templates
   * (defaults to <code>]</code>.
   */
  public void setEndDelim(String endDelim){
    config.setEndVarDelim(endDelim);
  }
  
  public void afterPropertiesSet() throws Exception {
    cache.start();
  }
  
  public void destroy() throws Exception {
    cache.stop();
  }
  
  public Template resolveTemplate(String uri, SourceResolver resolver) 
  throws TemplateNotFoundException, SAXException, IOException{
    return cache.get(config, uri, resolver);
  }

}
