package org.sapia.regis.gui.command;

import java.io.StringWriter;

import org.dom4j.Document;
import org.sapia.gumby.RenderContext;
import org.sapia.gumby.event.EventManager;
import org.sapia.regis.Configurable;
import org.sapia.regis.Registry;
import org.sapia.regis.gui.GlobalContext;
import org.sapia.regis.gui.event.NodeCreatedEvent;
import org.sapia.regis.gui.model.NewNodeModel;

public class NodeCreateCommand extends JellyCommand{
  
  private NewNodeModel _node;
  
  public NodeCreateCommand(NewNodeModel node){
    _node = node;
  }
  
  protected Object doExecute(RenderContext ctx, Document doc) throws Exception {
    if(_node.getParent().getChild(_node.getName()) != null){
      throw new IllegalStateException("Child node " + _node.getName() + " already exists");
    }
    StringWriter writer = new StringWriter();
    doc.write(writer);
    Registry reg = GlobalContext.getInstance().getRegistry();
    ((Configurable)reg).load(
        _node.getParent().getAbsolutePath(), 
        GlobalContext.getInstance().getUsername(),
        GlobalContext.getInstance().getPassword(),
        writer.toString(),
        null
        );
    EventManager.getInstance().dispatchEvent(new NodeCreatedEvent(_node.getParent().getChild(_node.getName())));
    return null;
  }

}
