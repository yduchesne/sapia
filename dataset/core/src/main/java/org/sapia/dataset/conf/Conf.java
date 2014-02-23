package org.sapia.dataset.conf;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.sapia.dataset.util.DefaultRef;
import org.sapia.dataset.util.Ref;
import org.sapia.dataset.util.Time;

/**
 * Centralizes global configuration access.
 * 
 * @author yduchesne
 *
 */
public class Conf {

  private static Ref<ConfProvider> provider = new DefaultRef<ConfProvider>(new DefaultConfProvider());
  
  private Conf() {
  }
  
  /**
   * @param provider the {@link Ref} that supplies the {@link ConfProvider} to use.
   */
  public static void setProvider(Ref<ConfProvider> provider) {
    Conf.provider = provider;
  }
  
  /**
   * @return the default cell width.
   */
  public static int getCellWidth() {
    return provider.get().getDefaultCellWidth();
  }
  
  /**
   * @return the screen display width.
   */
  public static int getDisplayWidth() {
    return provider.get().getDisplayWidth();
  }
  
  /**
   * @return the {@link DateFormat} to use when parsing strings into {@link Date}s.
   */
  public static List<DateFormat> getDateFormats() {
    return provider.get().getDateFormats();
  }
  
  /**
   * @return the head length.
   */
  public static int getHeadLength() {
    return provider.get().getHeadLength();
  }
  
  /**
   * @return the tail length.
   */
  public static int getTailLength() {
    return provider.get().getTailLength();
  }
  
  /**
   * @return the {@link Time} indicating the timeout or async tasks.
   */
  public static Time getTaskTimeout() {
    return provider.get().getTaskTimeout();
  }
}
