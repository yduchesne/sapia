package org.sapia.regis.gui.command;

import org.sapia.gumby.RenderContext;

public interface Command {

  public Object execute(RenderContext ctx) throws Exception;
}
