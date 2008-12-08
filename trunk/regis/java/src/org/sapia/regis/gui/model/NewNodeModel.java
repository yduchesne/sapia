package org.sapia.regis.gui.model;

import java.util.List;

import javax.swing.JTextField;

import org.sapia.gumby.RenderContext;
import org.sapia.regis.Node;
import org.sapia.regis.gui.GlobalContext;
import org.sapia.regis.gui.command.NodeCreateCommand;
import org.sapia.regis.gui.widgets.NewNodePanel;

public class NewNodeModel extends NodeModel{
  
  private Node parent;
  private String name;
  
  public NewNodeModel(Node node){
    super(node);
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Node getParent() {
    return parent;
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }
  
  public synchronized void create() throws Exception{
    NewNodePanel panel = (NewNodePanel)GlobalContext.getInstance()
      .getRenderContext().getEnv().get(NEW_NODE_PANE);
    
    JTextField txtField = (JTextField)GlobalContext.getInstance()
      .getRenderContext().getEnv().get(NEW_NODE_PANE_NAME);
    
    RenderContext child = GlobalContext.getInstance().getRenderContext().newChildInstance();
    List properties = panel.getProperties();
    child.getEnv().put("properties", properties, "local");
    if(txtField.getText() == null || txtField.getText().trim().length() == 0){
      GlobalContext.getInstance().error("No node name specified");
      return;
    }
    setName(txtField.getText());
    child.getEnv().put("name", txtField.getText(), "local");    
    NodeCreateCommand command = new NodeCreateCommand(this);
    command.execute(child);
    _props.clear();
  }

}
