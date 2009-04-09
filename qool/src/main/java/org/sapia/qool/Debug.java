package org.sapia.qool;

import java.util.Calendar;

/**
 * Internal class used for generating debug traces (normally should be turned off, used
 * in the course of developing this framework).
 * @author yduchesne
 *
 */
public class Debug {
  
  public enum Level{
    
    DEBUG,
    INFO,
    WARNING,
    ERROR;
    
    public boolean isLoggable(Level other){
      return other.ordinal() >= ordinal();
    }
  }

  private Level level = Level.ERROR;
  
  private String name;
  
  Debug(String name) {
    this.name = name;
  }
  
  public Debug setLevel(Level level) {
    this.level = level != null ? level : Level.ERROR;
    return this;
  }
  
  public boolean isDebug(){
    return this.level.isLoggable(Level.DEBUG);
  }
  
  public boolean isInfo(){
    return this.level.isLoggable(Level.INFO);
  }
  
  public boolean isWarning(){
    return this.level.isLoggable(Level.WARNING);
  }
  
  public boolean isError(){
    return this.level.isLoggable(Level.ERROR);
  }
  
  public Level getLevel() {
    return level;
  }
  
  public Debug debug(String msg){
    return trace(msg, Level.DEBUG);
  }
  
  public Debug info(String msg){
    return trace(msg, Level.INFO);
  }
  
  public Debug warn(String msg){
    return trace(msg, Level.WARNING);
  }

  public Debug warn(String msg, Throwable err){
    return trace(msg, Level.WARNING, err);
  }  
  
  public Debug error(String msg){
    return trace(msg, Level.ERROR);
  }
  
  public Debug error(String msg, Throwable err){
    return trace(msg, Level.ERROR, err);
  }
  
  public Debug trace(String msg, Level l){
    return trace(msg, l, null);
  }
  public Debug trace(String msg, Level l, Throwable err){
    if(level.isLoggable(l)){
      Calendar cal = Calendar.getInstance();
      StringBuilder sb = new StringBuilder().append("[")
        .append(l.name()).append(" ")
        .append(name).append(" ")
        .append(cal.get(Calendar.HOUR_OF_DAY)).append(':')
          .append(cal.get(Calendar.MINUTE)).append(':')
          .append(cal.get(Calendar.SECOND)).append(':')
          .append(cal.get(Calendar.MILLISECOND)).append(" ")
        .append(Thread.currentThread().getName()).append("] ").append(msg);
      System.out.println(sb.toString());
      if(err != null){
        err.printStackTrace(System.out);
      }
    }
    return this;
  }
  
  public static Debug createInstanceFor(String name, Level level){
    return createInstanceFor(name, level);
  }
  
  public static Debug createInstanceFor(Object instance, Level level){
    return createInstanceFor(instance.getClass(), level);
  }
  
  public static Debug createInstanceFor(Class<?> clazz, Level level){
    return new Debug(clazz.getSimpleName()).setLevel(level);
  } 
  
}
