package org.sapia.regis.gui.task;

import java.io.File;
import java.io.StringWriter;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.sapia.regis.Configurable;
import org.sapia.regis.Node;
import org.sapia.regis.gui.GlobalContext;
import org.sapia.regis.gui.GuiConsts;

public class LoadConfigTask implements Task, GuiConsts{

  public void execute() throws Exception {
    File configFile = GlobalContext.getInstance().getModelManager().getFileSystemModel().showFileDialog();
    if(configFile != null){
      SAXReader reader = new SAXReader();
      Document doc = reader.read(configFile);
      StringWriter writer = new StringWriter();
      doc.write(writer);
      Configurable registry = (Configurable)GlobalContext.getInstance().getRegistry();
      Node root = GlobalContext.getInstance().getRegistry().getRoot();
      registry.load(root.getAbsolutePath(), 
          GlobalContext.getInstance().getUsername(),
          GlobalContext.getInstance().getPassword(),
          writer.toString(),null);
      
      JTree tree = (JTree)GlobalContext.getInstance().getWidget(TREE);
      tree.clearSelection();
      DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getRoot();
      tree.collapsePath(new TreePath(((DefaultMutableTreeNode)model.getRoot()).getPath()));      
      node.removeAllChildren();
      
      GlobalContext.getInstance().success("Configuration file loaded successfully");
    }
  }
}
