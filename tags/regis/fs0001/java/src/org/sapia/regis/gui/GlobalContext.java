package org.sapia.regis.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.sapia.gumby.RenderContext;
import org.sapia.gumby.event.EventManager;
import org.sapia.regis.Registry;
import org.sapia.regis.gui.event.TaskEvent;
import org.sapia.regis.gui.task.Task;
import org.sapia.regis.remote.Authenticating;

public class GlobalContext implements GuiConsts{
  
  static GlobalContext instance = new GlobalContext();

  String username, password;
  Registry registry;
  RenderContext renderContext;
  ModelManager modelManager = new ModelManager();
  
  public static GlobalContext getInstance(){
    return instance;
  }
  
  public void login(String username, String password, String host, int port, String jndiName) throws Exception{
    this.username = username;
    this.password = password;
    if((host == null || host.trim().length() == 0) &&
       (jndiName == null || jndiName.trim().length() == 0)){
      registry = RegistryFactory.newDebugInstance();
    }
    else{
      registry = RegistryFactory.newRemoteInstance(host, port, jndiName);
    }
    if(registry instanceof Authenticating){
      try{
        if(!((Authenticating)registry).authenticate(username, password)){
          GlobalContext.getInstance().error("Invalid username or password");
          registry.close();
          System.exit(0);
        }
      }catch(SecurityException e){
        GlobalContext.getInstance().error(e);
        registry.close();
        System.exit(0);
      }
    }
    
    GlobalContext.getInstance().getModelManager().getNodeModel().setNode(registry.getRoot());
  }

  public Registry getRegistry() {
    if(registry == null) throw new IllegalStateException("Registry not set");    
    return registry;
  }

  public RenderContext getRenderContext() {
    if(renderContext == null) throw new IllegalStateException("RenderContext not set");
    return renderContext;
  }

  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }

  public ModelManager getModelManager() {
    return modelManager;
  }
  
  public void error(Throwable e){
    JOptionPane.showMessageDialog(
        getFrame(),
        e.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE
        );
    e.printStackTrace();
  }
  
  public void error(String error){
    JOptionPane.showMessageDialog(
        null,
        error,
        "Error",
        JOptionPane.ERROR_MESSAGE
        );
  }
  
  public void success(String msg){
    JOptionPane.showMessageDialog(
        null,
        msg,
        "Success",
        JOptionPane.INFORMATION_MESSAGE
        );
  }    
  
  public void dispatchEvent(Object event){
    EventManager.getInstance().dispatchEvent(event);
  }
  
  public void dispatchTask(Task task){
    EventManager.getInstance().dispatchEvent(new TaskEvent(task));
  }  
  
  public Object getWidget(String name){
    Object w = renderContext.getEnv().get(name, WIDGETS_SCOPE);
    if(w == null){
      throw new IllegalStateException("No UI component found for " + name);
    }
    return w;
  }
  
  public  JFrame getFrame(){
    return (JFrame)getWidget(FRAME_KEY);
  }

}
