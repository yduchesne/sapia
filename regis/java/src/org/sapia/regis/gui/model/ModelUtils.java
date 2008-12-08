package org.sapia.regis.gui.model;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.sapia.regis.Node;
import org.sapia.regis.gui.GlobalContext;
import org.sapia.regis.gui.GuiConsts;

public class ModelUtils implements GuiConsts{

  
  public static DefaultMutableTreeNode buildTreeNodeFrom(Node node){
    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
    treeNode.setAllowsChildren(true);
    treeNode.setUserObject(new NodeModel(node));
    return treeNode;
  }
  
  public static JTree buildTree(){
    return buildTreeFrom(new LazyNode(GlobalContext.getInstance().getRegistry().getRoot()));
  }  
  
  public static JTree buildTreeFrom(Node node){
    DefaultMutableTreeNode rootNode = buildTreeNodeFrom(node);
    JTree tree = new JTree(new DefaultTreeModel(rootNode));
    GlobalContext.getInstance().getRenderContext().getEnv().put(TREE, tree, WIDGETS_SCOPE);
    tree.putClientProperty("JTree.lineStyle", "None");
    TreeManager manager = new TreeManager();
    tree.setCellRenderer(new DefaulTreeCellRendererEx());
    tree.addTreeWillExpandListener(manager);
    tree.addTreeSelectionListener(manager);
    return tree;
  }  
  
  public static class DefaulTreeCellRendererEx extends DefaultTreeCellRenderer{
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      return super.getTreeCellRendererComponent(tree, value, sel, expanded, false,
          row, hasFocus);
    }
    
  }
}
