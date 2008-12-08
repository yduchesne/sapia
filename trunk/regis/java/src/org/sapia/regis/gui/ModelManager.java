package org.sapia.regis.gui;

import org.sapia.regis.gui.model.FileSystemModel;
import org.sapia.regis.gui.model.NewNodeModel;
import org.sapia.regis.gui.model.NodeModel;

public class ModelManager{

  private NodeModel nodeModel = new NodeModel(null);
  private NewNodeModel newNodeModel = new NewNodeModel(null); 
  private FileSystemModel fileSystemModel = new FileSystemModel();
  
  public NodeModel getNodeModel(){
    return nodeModel;
  }

  public NewNodeModel getNewNodeModel() {
    return newNodeModel;
  }

  public FileSystemModel getFileSystemModel() {
    return fileSystemModel;
  }
  
}
