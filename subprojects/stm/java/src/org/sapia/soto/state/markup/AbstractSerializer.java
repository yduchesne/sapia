package org.sapia.soto.state.markup;

import org.sapia.soto.Settings;

/**
 * An abstract {@link org.sapia.soto.state.markup.MarkupSerializer} that provides
 * a convenient constructor accepting settings.
 * 
 * @author yduchesne
 *
 */
public abstract class AbstractSerializer implements MarkupSerializer{
  
  protected Settings settings;
  
  protected AbstractSerializer(Settings settings){
    this.settings = settings;
  }
  

}
