package org.sapia.dataset.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import org.sapia.dataset.util.Strings;

/**
 * Holds styling information for a given plot.
 * 
 * @author yduchesne
 *
 */
public class PlotStyle {

  /**
   * Defines legend configuration.
   */
  public static class Legend {

    /**
     * Holds constants pertaining to legend horizontal alignment (along the Y axis).
     */
    public enum HorizontalAlignment {
      TOP,
      BOTTOM,
      MIDDLE
    }

    /**
     * Holds constants pertaining to legend vertical alignment (along the X axis).
     */
    public enum VerticalAlignment {
      LEFT,
      RIGHT,
      CENTER
    }
    
    /**
     * Holds constants pertaining to legend orientation (indicating if legend labels
     * should be displayed horizontally or vertically).
     */
    public enum Orientation {
      VERTICAL,
      HORIZONTAL
    }
    
    // ------------------------------------------------------------------------
    
    private boolean             enabled     = true;
    private Orientation         orientation = Orientation.VERTICAL;
    private HorizontalAlignment halign      = HorizontalAlignment.TOP;
    private VerticalAlignment   valign      = VerticalAlignment.LEFT;
    
    /**
     * Sets this instance's <code>enabled</code> flag.
     * 
     * @param enabled <code>true</code> if display of this instance should be enabled.
     */
    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    /**
     * Sets this instance's <code>enabled</code> flag to <code>true</code>.
     * 
     * @return this instance.
     */
    public Legend enable() {
      enabled = true;
      return this;
    }
    
    /**
     * Sets this instance's <code>enabled</code> flag to <code>false</code>.
     * 
     * @return this instance.
     */
    public Legend disable() {
      enabled = false;
      return this;
    }
    
    /**
     * @return <code>true</code> if the legend data should be displayed - <code>false</code>
     * otherwise.
     */
    public boolean isEnabled() {
      return enabled;
    }
    
    /**
     * @param alignment the {@link HorizontalAlignment} to use.
     * @return this instance.
     */
    public Legend halign(HorizontalAlignment alignment) {
      this.halign = alignment;
      return this;
    }
    
    /**
     * @return this instance's {@link HorizontalAlignment}.
     */
    public HorizontalAlignment getHalign() {
      return this.halign;
    }
    
    /**
     * @param alignment the {@link VerticalAlignment} to use.
     * @return this instance.
     */
    public Legend valign(VerticalAlignment alignment) {
      this.valign = alignment;
      return this;
    }
    
    /**
     * @return this instance's {@link VerticalAlignment}.
     */
    public VerticalAlignment getValign() {
      return this.valign;
    }
    
    /**
     * @param orientation the {@link Orientation} to use.
     * @return this instance.
     */
    public Legend orientation(Orientation orientation) {
      this.orientation = orientation;
      return this;
    }
    
    /**
     * @return this instance's orientation.
     */
    public Orientation getOrientation() {
      return orientation;
    }
    
    @Override
    public String toString() {
      return Strings.toString(
          "enabled", enabled, 
          "halign", halign, 
          "valign", valign, 
          "orientation", orientation);
    }
  }
  
  // ==========================================================================
  
  private Color     foreground = new Color(55, 170, 200);
  private String    title;
  private Insets    insets     = new Insets(60, 60, 60, 60);
  private Dimension dimension  = new Dimension(500, 400); 
  private Legend    legend     = new Legend();
  
  private PlotStyle() {
  }
  
  
  /**
   * @return the plot {@link Legend}.
   */
  public Legend getLegend() {
    return legend;
  }
  
  /**
   * @param title the plot's intended title.
   * @return this instance.
   */
  public PlotStyle title(String title) {
    this.title = title;
    return this;
  }
  
  /**
   * @return the plot's title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param insets the plot's intended insets.
   * @return this instance.
   */
  public PlotStyle insets(Insets insets) {
    this.insets = insets;
    return this;
  }
  
  /**
   * @return the plot's insets.
   */
  public Insets getInsets() {
    return insets;
  }

  /**
   * @param dimension the plot's intended {@link Dimension}.
   */
  public PlotStyle dimension(Dimension dimension) {
    this.dimension = dimension;
    return this;
  }
  
  /**
   * @return the plot's dimension.
   */
  public Dimension getDimension() {
    return dimension;
  }
  
  /**
   * @param baseColor the base {@link Color} to use.
   * @return this instance.
   */
  public PlotStyle foreground(Color baseColor) {
    this.foreground = baseColor;
    return this;
  }
  
  /**
   * @return the base color.
   */
  public Color getForeground() {
    return foreground;
  }

  
  /**
   * @return the {@link PlotStyle} to use.
   */
  public static PlotStyle obj() {
    return new PlotStyle();
  }
}
