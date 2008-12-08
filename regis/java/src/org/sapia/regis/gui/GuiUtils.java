package org.sapia.regis.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.sapia.gumby.RenderContext;

public class GuiUtils {

  public static Dimension computeScreenCenter(JComponent comp){
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();
    int x = (screenSize.width - comp.getWidth()) / 2;
    int y = (screenSize.height - comp.getHeight()) / 2;
    return new Dimension(x, y);
  }
  
  public static Dimension computeScreenCenter(JDialog dia){
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();
    int x = (screenSize.width - dia.getWidth()) / 2;
    int y = (screenSize.height - dia.getHeight()) / 2;
    return new Dimension(x, y);
  }  
  
  public static Dimension computeScreenCenter(JFrame frame){
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();
    int x = (screenSize.width - frame.getWidth()) / 2;
    int y = (screenSize.height - frame.getHeight()) / 2;
    return new Dimension(x, y);
  }
  
  public static void centerToScreen(JFrame frame){
    Dimension dim = computeScreenCenter(frame);
    frame.setLocation((int)dim.getWidth(), (int)dim.getHeight());
  }    
  
  public static void centerToScreen(JComponent comp){
    Dimension dim = computeScreenCenter(comp);
    comp.setLocation((int)dim.getWidth(), (int)dim.getHeight());
  }  
  
  public static void centerToScreen(JDialog dia){
    Dimension dim = computeScreenCenter(dia);
    dia.setLocation((int)dim.getWidth(), (int)dim.getHeight());
  }
  
  public static Object render(RenderContext ctx, String resource) throws Exception{
    InputStream is = Thread.currentThread()
    .getContextClassLoader().getResourceAsStream("org/sapia/regis/gui/screens/" + resource + ".xml");
    if(is == null){
      throw new FileNotFoundException(resource);
    }
    try{
      return ctx.render(is);
    }finally{
      is.close();
    }
  }
}


