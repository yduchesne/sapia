package org.sapia.regis.gui.model;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.sapia.regis.gui.GlobalContext;
import org.sapia.regis.gui.GuiConsts;

public class FileSystemModel implements GuiConsts{

  File _lastDir = new File(System.getProperty("user.dir"));
  
  
  public File showFileDialog() throws Exception{
    JFrame frame = (JFrame)GlobalContext.getInstance().getWidget(FRAME_KEY);
    JFileChooser chooser = new JFileChooser(_lastDir);
    chooser.showOpenDialog(frame);
    File selectedFile = chooser.getSelectedFile();
    if(selectedFile != null){
      if(selectedFile.isDirectory()){
        throw new IllegalStateException("Chosen resource must be a file");
      }
      else{
        _lastDir = chooser.getCurrentDirectory();
      }
    }
    return selectedFile;
  }
}
