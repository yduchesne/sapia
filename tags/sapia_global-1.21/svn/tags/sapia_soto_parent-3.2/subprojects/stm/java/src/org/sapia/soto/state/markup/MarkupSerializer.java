package org.sapia.soto.state.markup;

import java.io.PrintWriter;

import org.sapia.soto.state.Context;

/**
 * This interface abstracts markup output behavior.
 * 
 * @see org.sapia.soto.state.markup.MarkupSerializerFactory
 * 
 * @author yduchesne
 *
 */
public interface MarkupSerializer {
  
  /**
   * This method is called for the corresponding start tag of the given markup.
   * 
   * @param info some <code>MarkupInfo</code>
   * @param ctx a <code>Context</code>
   * @param pw a <code>PrintWriter</code> - to be used by this instance if needed,
   * when serializing the markup data. 
   * @throws Exception
   */
  public void start(MarkupInfo info, Context ctx, PrintWriter pw) throws Exception;
  
  /**
   * @param txt some text.
   * @param ctx a <code>Context</code>
   * @param pw a <code>PrintWriter</code> - to be used by this instance if needed,
   * when serializing the given text.
   * @throws Exception
   */
  public void text(String txt, Context ctx, PrintWriter pw) throws Exception;  

  /**
   * This method is called for the corresponding start tag of the given markup.
   * 
   * @param info some <code>MarkupInfo</code>
   * @param ctx a <code>Context</code>
   * @param pw a <code>PrintWriter</code> - to be used by this instance if needed,
   * when serializing the markup data. 
   * @throws Exception
   */  
  public void end(MarkupInfo info, Context ctx, PrintWriter pw) throws Exception;  

}
