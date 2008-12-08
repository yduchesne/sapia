package org.sapia.regis.gui;


import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.sapia.gumby.event.EventListener;
import org.sapia.gumby.event.EventManager;
import org.sapia.regis.Node;
import org.sapia.regis.Property;
import org.sapia.regis.gui.event.NewNodePropertyCreationEvent;
import org.sapia.regis.gui.event.NewNodeSelectionEvent;
import org.sapia.regis.gui.event.NodeChangeEvent;
import org.sapia.regis.gui.event.NodeCreatedEvent;
import org.sapia.regis.gui.event.NodeUpdatedEvent;
import org.sapia.regis.gui.event.PropertyCreationEvent;
import org.sapia.regis.gui.event.TaskEvent;
import org.sapia.regis.gui.model.ModelUtils;
import org.sapia.regis.gui.model.NodeModel;
import org.sapia.regis.gui.widgets.NewNodePanel;
import org.sapia.regis.gui.widgets.NodePanel;

public class Controller implements EventListener, GuiConsts{
  
  public Controller(){
    EventManager.getInstance().register(TaskEvent.class, this);    
    EventManager.getInstance().register(NodeChangeEvent.class, this);    
    EventManager.getInstance().register(NewNodeSelectionEvent.class, this);    
    EventManager.getInstance().register(PropertyCreationEvent.class, this);    
    EventManager.getInstance().register(NewNodePropertyCreationEvent.class, this);    
    EventManager.getInstance().register(NodeCreatedEvent.class, this);    
  }
  public void onEvent(Object event) {
    if(event instanceof TaskEvent){
      ((TaskEvent)event).execute();
    }    
    else if(event instanceof NodeChangeEvent){
      refreshCurrentNodePane(((NodeChangeEvent)event).getNode(), true);
    }
    else if(event instanceof NodeUpdatedEvent){
      refreshCurrentNodePane(((NodeUpdatedEvent)event).getNode(), true);
    }
    else if(event instanceof NewNodeSelectionEvent){
      refreshNewNodePane(true); 
    }
    else if(event instanceof NewNodePropertyCreationEvent){
      Property prop = ((NewNodePropertyCreationEvent)event).getProperty();
      GlobalContext.getInstance().getModelManager().getNewNodeModel().addProperty(prop);
      refreshNewNodePane(false); 
    }
    else if(event instanceof PropertyCreationEvent){
      Property prop = ((PropertyCreationEvent)event).getProperty();
      GlobalContext.getInstance().getModelManager().getNodeModel().addProperty(prop);
      refreshCurrentNodePane(
          GlobalContext.getInstance().getModelManager().getNodeModel().getNode(), 
          false);
    }
    else if(event instanceof NodeCreatedEvent){
      handleNodeCreation(((NodeCreatedEvent)event).getNode());
    }
  }
  
  private void refreshCurrentNodePane(Node node, boolean clear){
    if(clear){
      GlobalContext.getInstance().getModelManager().getNodeModel().setNode(node);
    }
    NodePanel panel = (NodePanel)GlobalContext.getInstance().getWidget(NODE_PANE);
    panel.fireCurrentNodeChange();
    
    JLabel title = (JLabel)GlobalContext.getInstance().getWidget(NODE_PANE_TITLE);
    title.setText("/"+node.getAbsolutePath().toString());
    
    JTabbedPane pane = (JTabbedPane)GlobalContext.getInstance().getWidget(TABBED_PANE);
    pane.setSelectedIndex(0);
  }
  
  private void refreshNewNodePane(boolean clear){
    NodeModel currentModel = GlobalContext.getInstance().getModelManager().getNodeModel();
    
    NewNodePanel panel = (NewNodePanel)GlobalContext.getInstance().getRenderContext().getEnv().get(GuiConsts.NEW_NODE_PANE);
    if(clear){
      panel.fireCreateNewNodeForm();
    }
    else{
      panel.fireUpdateNewNodeForm();
    }
        
    JLabel title = (JLabel)GlobalContext.getInstance().getRenderContext().getEnv().get(GuiConsts.NEW_NODE_PANE_TITLE);
    
    if(currentModel.getNode() == null){
      title.setText("/ ");
    }
    else{
      title.setText(currentModel.getNode().getAbsolutePath().toString() + "/ ");      
    }
  }  
  
  private void handleNodeCreation(Node node){
    JTree tree = (JTree)GlobalContext.getInstance().getWidget(TREE);

    synchronized(tree.getTreeLock()){
      DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
      TreePath path = tree.getSelectionPath();
      MutableTreeNode parent = null;
      if(path == null){
        parent = (MutableTreeNode)model.getRoot();
      }
      else{
        parent = (MutableTreeNode)path.getLastPathComponent(); 
      }
      DefaultMutableTreeNode child = ModelUtils.buildTreeNodeFrom(node);
      
      model.insertNodeInto(
          child, 
          parent,
          parent.getChildCount());
      
      
      tree.scrollPathToVisible(new TreePath(child.getPath()));
      JTabbedPane pane = (JTabbedPane)GlobalContext.getInstance().getWidget(TABBED_PANE);
      refreshCurrentNodePane(node, true);
      pane.setSelectedIndex(0);
      JTextField newNodeName = (JTextField)GlobalContext.getInstance().getWidget(NEW_NODE_PANE_NAME);
      newNodeName.setText("");
    }
  }

}
