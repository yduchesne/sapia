package org.sapia.dataset.conf;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.sapia.dataset.Dataset;
import org.sapia.dataset.util.Time;

/**
 * Specifies the behavior of the configuration backend.
 * 
 * @author yduchesne
 *
 */
public interface ConfProvider {

  /**
   * @return the default cell width (in numbers of characters), if no cell-specific width is defined.
   */
  public int getDefaultCellWidth();
  
  /**
   * @return the console display width, in numbers of characters.
   */
  public int getDisplayWidth();
  
  /**
   * @return the {@link List} of {@link DateFormat}s to use to parse strings into {@link Date}.
   */
  public List<DateFormat> getDateFormats();
  
  /**
   * @return the number of rows in dataset heads.
   * 
   * @see Dataset#head()
   */
  public int getHeadLength();
  
  /**
   * @return the number of rows in dataset tails.
   * 
   * @see Dataset#tail()
   */
  public int getTailLength();
  
  /**
   * @return the {@link Time} indicating the timeout for async tasks.
   */
  public Time getTaskTimeout();
}
