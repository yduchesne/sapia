package org.sapia.regis;

public class RegisDebug {

  public static boolean enabled = false;
  
  public static void debug(Object src, Object msg){
    debug(src.getClass().getName(), msg);
  }
  
  public static void debug(String src, Object msg){
    if(enabled){
      System.out.println("[REGIS - " + src + " >> " + msg + "]");
    }
  }  
}
