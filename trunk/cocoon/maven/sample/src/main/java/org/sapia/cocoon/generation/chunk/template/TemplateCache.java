package org.sapia.cocoon.generation.chunk.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;
import org.xml.sax.SAXException;

/**
 * This class implements template caching.
 * 
 * @author yduchesne
 *
 */
public class TemplateCache {
  
  static final int  NO_MAX = -1;
  static final long NO_IDLE_TIME = -1;
  static final long CLEANUP_INTERVAL = 30000;
  
  private Map<String, TemplateEntry> entries = new ConcurrentHashMap<String, TemplateEntry>();
  
  private int maxSize = NO_MAX;
  private long idleTime = NO_IDLE_TIME;
  private long cleanupInterval = CLEANUP_INTERVAL;
  
  private Thread cleaner;
  
  public TemplateCache() {
  }
  
  public void setMaxSize(int max){
    maxSize = max;
  }
  
  public void setCleanupInterval(long interval){
    cleanupInterval = interval;
  }
  
  public void setIdleTime(long idleTime) {
    this.idleTime = idleTime;
  }
  
  public synchronized void start(){
    cleaner = new Thread(
        new Runnable(){
          public void run() {
            
            while(true){
              try {
                Thread.sleep(cleanupInterval <= 0 ? CLEANUP_INTERVAL : cleanupInterval);
                flushStaleEntries();
                flushOverflowEntries();
              } catch (InterruptedException e) {
                break;
              }
            }
          }
          
          private void flushStaleEntries(){
            if(idleTime <= NO_IDLE_TIME) return;
            Collection<TemplateEntry> toCheck = entries.values();
            for(TemplateEntry e:toCheck){
              if(e.isStale(idleTime)){
                removeEntry(e.getUri());
              }
            }
          }
          
          private void flushOverflowEntries(){
            if(entries.size() < maxSize || maxSize <= NO_MAX) return;
            Collection<TemplateEntry> toCheck = entries.values();
            List<TemplateEntry> sorted = new ArrayList<TemplateEntry>(toCheck);
            
            // sorting : least recently used at end
            Collections.sort(sorted, new Comparator<TemplateEntry>(){
              public int compare(TemplateEntry o1, TemplateEntry o2) {
                long diff = o1.lastAccess() - o2.lastAccess();
                if(diff == 0) return 0;
                else if(diff < 0) return 1;
                else return -1;
              };
            });
            
            // remove least recently used.
            List<TemplateEntry> toRemove = sorted.subList(maxSize, sorted.size());
            synchronized(entries){
              for(TemplateEntry t:toRemove){
                removeEntry(t.getUri());
              }
            }
          }          
        },
        "template-cache-cleaner"
    );
    cleaner.setDaemon(true);
    cleaner.start();
  }
  
  public synchronized void stop(){
    int maxAttempts = 3;
    int attempt = 0;
    while(cleaner != null && cleaner.isAlive() && attempt < maxAttempts){
      cleaner.interrupt();
      try{
        cleaner.join(5000);
        attempt++;
      }catch(InterruptedException e){
        break;
      } 
    }
    cleaner = null;
  }
  
  public Template get(ParserConfig config, String uri, SourceResolver resolver) 
  throws SAXException, IOException{
    Source src = resolver.resolveURI(uri);
    TemplateEntry entry = entries.get(uri);
    if(entry == null || src.getLastModified() != entry.lastModified()){
      TemplateParser parser = new TemplateParser(config);
      Template t = parser.parse(src);
      entry = new TemplateEntry(uri, t, src.getLastModified());
      entries.put(uri, entry);
      onLoad(entry);
    }
    onGet(entry);
    return entry.get();
  }

  protected void onGet(TemplateEntry entry){}
  
  protected void onLoad(TemplateEntry entry){}
  
  protected TemplateEntry getEntry(String uri)
    throws IOException{
    return entries.get(uri);
  }
  
  protected void removeEntry(String uri){
    entries.remove(uri);
  }
  
  //////////////////////// inner classes ////////////////////////////
  
  static class TemplateEntry{
    private volatile long lastModified;
    private AtomicLong lastAccess;
    private Template template;
    private String uri;
    
    TemplateEntry(String uri, Template t, long lastModified){
      this.uri = uri;
      template = t;
      this.lastModified = lastModified;
      this.lastAccess = new AtomicLong(System.currentTimeMillis());
    }
    
    String getUri() {
      return uri;
    }
    
    Template get(){
      lastAccess.set(System.currentTimeMillis());
      return template;
    }
    
    long lastModified(){
      return lastModified;
    }
    
    long lastAccess(){
      return lastAccess.get();
    }
    
    void touch(){
      lastModified = System.currentTimeMillis();
    }
    
    boolean isStale(long idleTime){
      return System.currentTimeMillis() - lastAccess.get() > idleTime;
    }
  }
  
}
