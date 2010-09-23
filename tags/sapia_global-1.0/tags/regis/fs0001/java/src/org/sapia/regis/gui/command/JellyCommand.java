package org.sapia.regis.gui.command;

import java.net.URL;

import org.dom4j.Document;
import org.sapia.gumby.RenderContext;
import org.sapia.gumby.jelly.JellyHelper;

public abstract class JellyCommand implements Command{
  

  public Object execute(RenderContext ctx) throws Exception {
    String resource = getClass().getName().replace(".", "/") + ".jelly.xml";
    URL url = ctx.getSettings().resolveURL(resource);
    Document doc = JellyHelper.renderDocument(ctx, url);    
    return doExecute(ctx, doc);
  }
  
  protected abstract Object doExecute(RenderContext ctx, Document doc) throws Exception;
}
