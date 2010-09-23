package org.sapia.regis.gui;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

//import com.jgoodies.plaf.plastic.Plastic3DLookAndFeel;

public class RegisFrameFactory {

  public static JFrame newFrame(String name) {
    /*
    try {
      UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
    } catch(UnsupportedLookAndFeelException exception) {
      exception.printStackTrace();
    }*/
    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame frame = new JFrame(name);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    return frame;
  }  
}
