package org.sapia.regis.gui.command;

import java.io.StringWriter;

import org.dom4j.Document;
import org.sapia.gumby.RenderContext;
import org.sapia.gumby.event.EventManager;
import org.sapia.regis.Configurable;
import org.sapia.regis.Registry;
import org.sapia.regis.gui.GlobalContext;
import org.sapia.regis.gui.event.NodeUpdatedEvent;
import org.sapia.regis.gui.model.NodeModel;

public class NodeUpdateCommand extends JellyCommand{
  
  private NodeModel _node;
  
  public NodeUpdateCommand(NodeModel node){
    _node = node;
  }
  
  protected Object doExecute(RenderContext ctx, Document doc) throws Exception {
    StringWriter writer = new StringWriter();
    doc.write(writer);
    Registry reg = GlobalContext.getInstance().getRegistry();
    ((Configurable)reg).load(
        _node.getNode().getAbsolutePath(), 
        GlobalContext.getInstance().getUsername(),
        GlobalContext.getInstance().getPassword(),
        writer.toString(),
        null
        );
    EventManager.getInstance().dispatchEvent(new NodeUpdatedEvent(_node.getNode()));
    return null;
  }

}
