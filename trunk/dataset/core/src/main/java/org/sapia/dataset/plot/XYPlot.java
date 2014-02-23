package org.sapia.dataset.plot;


/**
 * An instance of this interface is expected to plot dataset column values on
 * an XY chart.
 * 
 * @author yduchesne
 *
 */
public interface XYPlot extends Plot {
  
  /**
   * @param columnName the name of the dataset column used for the X axis.
   */
  public void setX(String columnName);
  
  /**
   * @param columnNames the names of the dataset columns used for the Y axis.
   */
  public void setY(String...columnNames);

  /**
   * @param label the label to use for the X axis.
   */
  public void setLabelX(String label);
  
  /**
   * @param label the label to use for the Y axis.
   */
  public void setLabelY(String label);
  /**
   * Displays this instance.
   */
  public void display();
  
}
