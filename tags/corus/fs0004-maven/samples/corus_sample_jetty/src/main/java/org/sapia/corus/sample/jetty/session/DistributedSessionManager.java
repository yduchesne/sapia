package org.sapia.corus.sample.jetty.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.eclipse.jetty.server.session.AbstractSessionManager;

public class DistributedSessionManager extends AbstractSessionManager{
  
  private CacheManager caches;
  private Cache sessions;
  
  public DistributedSessionManager(File configFile) throws FileNotFoundException, IOException, CacheException{
    InputStream config = new FileInputStream(configFile);
    try{
      
      caches = CacheManager.create(config);
      sessions = caches.getCache("sessions");
    }finally{
      config.close();
    }
      
  }
  
  @Override
  protected void addSession(Session aSession) {
    Element sessionElement = new Element(aSession.getId(), aSession);
    sessions.put(sessionElement);
  }
  
  @Override
  public Session getSession(String id) {
    try{
      Element sessionElement = sessions.get(id);
      if(sessionElement == null){
        return null;
      }
      DistributedSession session = (DistributedSession)sessionElement.getValue();
      return session;
    }catch(CacheException e){
      return null;
    }
  }
  
  @Override
  protected Session newSession(HttpServletRequest request) {
    return new DistributedSession(request);
  }
  
  @Override
  public Map getSessionMap() {
    return new HashMap();
  }
  
  @Override
  protected void removeSession(String sessionId) {
    sessions.remove(sessionId);
  }
  
  @Override
  protected void invalidateSessions() {
  }
  
  /////////////////////////////////////////////////////////////////////
  
  class DistributedSession extends AbstractSessionManager.Session{
    
    DistributedSession(HttpServletRequest request){
      super(request);
    }
    
    DistributedSession(long created, String clusterId){
      super(created, clusterId);
    }
    
    @Override
    protected Map newAttributeMap() {
      return new HashMap();
    }
    
    
  }


}
