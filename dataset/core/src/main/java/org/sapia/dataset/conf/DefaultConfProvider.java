package org.sapia.dataset.conf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.sapia.dataset.util.Checks;
import org.sapia.dataset.util.Time;

/**
 * A default {@link ConfProvider} implementation.
 * 
 * @author yduchesne
 *
 */
public class DefaultConfProvider implements ConfProvider {
  
  public static final int DEFAULT_CELL_WIDTH    = 10;
  public static final int DEFAULT_DISPLAY_WIDTH = 80;
  public static final int DEFAULT_HEAD_LENGTH   = 25;
  public static final int DEFAULT_TAIL_LENGTH   = 25;
  public static final long DEFAULT_TASK_TIMEOUT = 30;
  
  private int              displayWidth = DEFAULT_DISPLAY_WIDTH;
  private int              cellWidth    = DEFAULT_CELL_WIDTH;
  private List<DateFormat> dateFormats  = new ArrayList<>();
  private int              headLength   = DEFAULT_HEAD_LENGTH;
  private int              tailLength   = DEFAULT_TAIL_LENGTH;
  private Time             taskTimeout  = new Time(DEFAULT_TASK_TIMEOUT, TimeUnit.SECONDS);
  
  {
    dateFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"));
    dateFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    dateFormats.add(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS"));
    dateFormats.add(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
    dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
    dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    dateFormats.add(new SimpleDateFormat("yyyyMMddHHmmssSSS"));
    dateFormats.add(new SimpleDateFormat("yyyyMMddHHmmss"));        
  }
  
  @Override
  public int getDisplayWidth() {
    return displayWidth;
  }
  
  /**
   * @param displayWidth the display width to use.
   */
  public void setDisplayWidth(int displayWidth) {
    Checks.isTrue(cellWidth > 0, "Display width must be greater than 0");
    this.displayWidth = displayWidth;
  }
  
  @Override
  public List<DateFormat> getDateFormats() {
    return Collections.unmodifiableList(dateFormats);
  }
  
  /**
   * @param format a {@link DateFormat}.
   */
  public void addDateFormat(DateFormat format) {
    dateFormats.add(format);
  }
  
  @Override
  public int getDefaultCellWidth() {
    return cellWidth;
  }
  
  /**
   * @param cellWidth the default cell width to use.
   */
  public void setDefaultCellWidth(int cellWidth) {
    Checks.isTrue(cellWidth > 0, "Cell width must be greater than 0");
    this.cellWidth = cellWidth;
  }
  
  @Override
  public int getHeadLength() {
    return headLength;
  }
  
  /**
   * @param headLength the head length to use.
   */
  public void setHeadLength(int headLength) {
    this.headLength = headLength;
  }
  
  @Override
  public int getTailLength() {
    return tailLength;
  }

  /**
   * @param tailLength the tail length to use.
   */
  public void setTailLength(int tailLength) {
    this.tailLength = tailLength;
  }
  
  @Override
  public Time getTaskTimeout() {
    return taskTimeout;
  }
  
  /**
   * @param taskTimeout the {@link Time} corresponding to the timeout for asynchronous tasks.
   */
  public void setTaskTimeout(Time taskTimeout) {
    this.taskTimeout = taskTimeout;
  }

}
