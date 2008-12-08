package org.sapia.regis.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.sapia.gumby.MapScope;
import org.sapia.gumby.RenderContext;
import org.sapia.gumby.RenderContextFactory;
import org.sapia.gumby.event.EventManager;
import org.sapia.regis.RegisLog;
import org.sapia.regis.gui.event.NodeChangeEvent;

public class Main implements GuiConsts{


  public static void main(String[] args) throws Exception{

    RenderContext ctx = RenderContextFactory.newInstance();
    GlobalContext.getInstance().renderContext = ctx;
    
    JFrame frame = RegisFrameFactory.newFrame("Regis Admin Client");
    frame.setSize(800, 600);
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();
    int x = (screenSize.width - frame.getWidth()) / 2;
    int y = (screenSize.height - frame.getHeight()) / 2;    
    frame.setLocation(x, y);
    Controller ctrl = new Controller();
    //RegisLog.setDebug();
    MapScope scope = new MapScope();
    ctx.getEnv().addScope(APP_SCOPE, scope);
    ctx.getEnv().put(FRAME_KEY, frame, WIDGETS_SCOPE);
    JDialog dialog = (JDialog)GuiUtils.render(ctx, "login");  
      new JDialog(frame);    
    //dialog.setSize(300, 200);
    dialog.pack();
    GuiUtils.centerToScreen(dialog);
    dialog.setModal(true);
    dialog.setVisible(true);
    if(frame.isDisplayable()){
      frame.setVisible(true);
      while(true){
        Thread.sleep(100000);
      }      
    }
    System.exit(0);
  }
}
