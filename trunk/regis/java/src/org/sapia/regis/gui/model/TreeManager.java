package org.sapia.regis.gui.model;

import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;

import org.sapia.gumby.event.EventManager;
import org.sapia.regis.RegisLog;
import org.sapia.regis.gui.event.NodeChangeEvent;

public class TreeManager implements TreeWillExpandListener, TreeSelectionListener{
  
  
  public TreeManager(){
  }
  
  public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
    /*
    JTree tree = (JTree)event.getSource();
    
    synchronized(tree.getTreeLock()){    
      DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
      if(!treeNode.isRoot()){
        treeNode.removeAllChildren();
      }
    }
    */
  }
  
  public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {

    /*
    JTree tree = (JTree)event.getSource();
    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
    NodeModel model = (NodeModel)treeNode.getUserObject();
    Iterator children = model.getNode().getChildren().iterator();
    while(children.hasNext()){
      treeNode.add(ModelUtils.buildTreeNodeFrom((Node)children.next()));
    }*/
  }
  
  public void valueChanged(TreeSelectionEvent event) {
    JTree tree = (JTree)event.getSource();
    synchronized(tree.getTreeLock()){
      DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
      if(treeNode != null && treeNode.getChildCount() == 0){
        NodeModel model = (NodeModel)treeNode.getUserObject();
        RegisLog.Timer timer = RegisLog.start(getClass(), "model.getNode().getChildrenNames()");
        Iterator names = model.getNode().getChildrenNames().iterator();
        timer.end(getClass(), "model.getNode().getChildrenNames()");
        while(names.hasNext()){
          String name = (String)names.next();
          LazyNode child = new LazyNode(model.getNode(), name);
          treeNode.add(ModelUtils.buildTreeNodeFrom(child));
        }
      }
      if(treeNode != null){
        NodeModel model = (NodeModel)treeNode.getUserObject();
        EventManager.getInstance().dispatchEvent(new NodeChangeEvent(model.getNode()));
      }
    }
  }
  
  

}
