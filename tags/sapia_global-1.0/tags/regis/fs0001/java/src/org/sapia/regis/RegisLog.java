package org.sapia.regis;

import java.util.Calendar;
import java.util.Properties;

public class RegisLog {
  
  public static final String LOG_PROP = "sapia.regis.log";
  
  public static final int DEBUG   = 0;
  public static final int WARNING = 1;
  public static final int ERROR   = 2;
  public static final int NONE    = 3;  
  
  public static final String[] LEVELS = 
    new String[]{
      "DEBUG", "WARNING", "ERROR", "NONE"
    };
  
  private static int _level = NONE;
  
  public static void init(Properties props){
    String prop = props.getProperty(LOG_PROP);
    if(prop != null){
      _level = NONE;
      for(int i = 0; i < LEVELS.length; i++){
        if(prop.equalsIgnoreCase(LEVELS[i])){
          _level = i;
          break;
        }
      }
    }
  }
  
  public static void setDebug(){
    _level = DEBUG;
  }
  
  public static void setWarning(){
    _level = WARNING;
  }
  
  public static void setError(){
    _level = ERROR;
  }  
  
  public static void setNone(){
    _level = NONE;
  }    
  
  public static void debug(Class source, String msg){
    log(DEBUG, source, msg);
  }
  
  public static void warning(Class source, String msg){
    log(WARNING, source, msg);
  }
  
  public static void error(Class source, String msg){
    log(ERROR, source, msg);
  }  
  
  public static Timer start(Class source, String msg){
    return new Timer().start(DEBUG, source, msg);
  }

  public static Timer start(int level, Class source, String msg){
    return new Timer().start(level, source, msg);
  }  
  
  public static void log(int level, Class source, String msg){
    if(level < 0 || level >= LEVELS.length){
      throw new IllegalArgumentException("Invalid log level: " + level);
    }
    if(level >= _level){
      StringBuffer buf = new StringBuffer();
      buf.append("[").append(LEVELS[level])
        .append(" - ").append(source != null ? source.getName() : "<unknown source>").append("] ").append(msg);
      System.out.println(buf.toString());
    }
  }

  public static class Timer{
    
    private int logLevel;
    private Calendar cal;
    
    public Timer start(int logLevel, Class source, String msg){
      cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      StringBuffer buf = new StringBuffer("( >> start: ");
      appendTime(buf, cal);
      this.logLevel = logLevel;
      buf.append(") ").append(msg);
      RegisLog.log(logLevel, source, buf.toString());
      return this;
    }
    
    public void end(Class source, String msg){
      if(cal == null){
        throw new IllegalStateException("start() method must be called first");
      }

      long end = System.currentTimeMillis();
      long interval = end - cal.getTimeInMillis();
      cal.setTimeInMillis(end);
      StringBuffer buf = new StringBuffer("( << end: ");      
      appendTime(buf, cal);
      buf.append(" - interval: ").append(interval);
      buf.append(") ").append(msg);
      RegisLog.log(logLevel, source, buf.toString());      
    }
    
  }
  
  public static void main(String[] args) {
    RegisLog.setDebug();
    RegisLog.debug(null, "THIS IS A TEST");
    RegisLog.start(null, "doTest()").end(null, "doTest()");
  }
  
  private static void appendTime(StringBuffer buf, Calendar cal){
    buf.append(cal.get(Calendar.HOUR_OF_DAY))
       .append(":")
       .append(cal.get(Calendar.MINUTE))
       .append(":")
       .append(cal.get(Calendar.SECOND))
       .append(":")
       .append(cal.get(Calendar.MILLISECOND));
  }
}
