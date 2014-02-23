package org.sapia.dataset.plot;

import java.awt.Color;

/**
 * Specifies the behavior of pie plots.
 * 
 * @author yduchesne
 *
 */
public interface PiePlot extends Plot {
  
  /**
   * @param name the name of the column used for the pie plot's sections.
   */
  public void setColumn(String name);

  /**
   * @param color the "start" {@link Color} of the plot's gradient.
   */
  public void setGradientStart(Color color);
  
  /**
   * @param color the "end" {@link Color} of the plot's gradient.
   */
  public void setGradientEnd(Color color);
}
