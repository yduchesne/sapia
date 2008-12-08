package org.sapia.regis.remote;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.sapia.regis.RegisDebug;
import org.sapia.ubik.rmi.interceptor.Interceptor;
import org.sapia.ubik.rmi.server.gc.GcEvent;
import org.sapia.ubik.rmi.server.invocation.ServerPreInvokeEvent;

import com.mentorgen.tools.profile.runtime.Profile;

public class ProfilingInterceptor implements Interceptor{
  
  private long lastMemDump = System.currentTimeMillis();
  private long memDumpInterval = 15000;
  private Date startDate = new Date();
  private File profileOutputDir = new File("etc/profiling/output");
  private SimpleDateFormat format;
  
  private Object lock = new Object();
  
  class ShutDownHook extends Thread{
    
    public void run() {
      if(RegisDebug.enabled){
        Profile.stop();
        Profile.shutdown();
      }
    }
  }
  
  public ProfilingInterceptor(){
    profileOutputDir.mkdirs();
    format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    if(RegisDebug.enabled){
      /*
      Profile.clear();
      File prof = new File(profileOutputDir, makeFileName("profiling_stats"));
      File mem = new File(profileOutputDir, makeFileName("memory_stats"));
      prof.delete();
      mem.delete();
      Profile.setFileName(prof.getAbsolutePath());      
      Profile.start();
      Runtime.getRuntime().addShutdownHook(new ShutDownHook());
      */
    }
  }
  
  public void onGcEvent(GcEvent evt){
    RegisDebug.debug(this, "============== DGC at: " + format.format(new Date()) + "==============");
    RegisDebug.debug(this, "Count  :" + evt.getCleanedCount());    
  }
  
  public void onServerPreInvokeEvent(ServerPreInvokeEvent evt){
    if(RegisDebug.enabled){
      synchronized(lock){
        if(System.currentTimeMillis() - memDumpInterval > lastMemDump){
          Date currentDate = new Date();
          
          double freemem   = toMegs(Runtime.getRuntime().freeMemory());
          double totalmem  = toMegs(Runtime.getRuntime().totalMemory());
          double maxmem    = toMegs(Runtime.getRuntime().maxMemory());
          
          RegisDebug.debug(this, "============== Memory Stats at: " + format.format(currentDate) + "==============");          
          RegisDebug.debug(this, "Free  memory  :" + freemem);
          RegisDebug.debug(this, "Total memory  :" + totalmem);
          RegisDebug.debug(this, "Max   memory  :" + maxmem);
          
          String output = format.format(currentDate) + "," + freemem + "," + totalmem + "," + maxmem;
          
          output("memory_stats", output);
          
          lastMemDump = System.currentTimeMillis();
        }
      }
    }
  }

  private double toMegs(long bytes){
    return ((double)(bytes / 1000)/1000);
  }
  
  private String makeFileName(String base){
    return base +  "-" + new SimpleDateFormat("yyyy-MM-dd").format(startDate) + ".txt";
  }
  
  private void output(String fileName, String output){
    PrintWriter pw = null;
    try{
      pw = new PrintWriter(
          new FileOutputStream(
            new File(profileOutputDir, makeFileName(fileName)), true
          )
        );            
      pw.println(output);
    }catch(IOException e){
    }finally{
      if(pw != null){
        pw.flush();
        pw.close();
      }
    }    
  }

}
