package org.sapia.dataset.io.table;

/**
 * Holds column styling information.
 * 
 * @author yduchesne
 *
 */
public class ColumnStyle {
  
  /**
   * Holds constants defining the different column alignment styles.
   */
  public enum Alignment {
    LEFT,
    RIGHT,
    CENTER
  }
  
  // ==========================================================================
  
  private static final String DEFAULT_SEPARATOR = "|";
  private String separator = DEFAULT_SEPARATOR;

  private String leftPadding   = " ";
  private String rightPadding  = " ";
  private Alignment alignment = Alignment.RIGHT;
  private int       width;
  
  ColumnStyle width(int width) {
    if (width > this.width) {
      this.width = width;
    }
    return this;
  }
  
  /**
   * @param sep a column separator {@link String}.
   * @return this instance.
   */
  public ColumnStyle separator(String sep) {
    separator = sep;
    return this;
  }
  
  /**
   * @return this instance's column separator.
   */
  public String getSeparator() {
    return separator;
  }

  /**
   * @return this instance's width.
   */
  public int getWidth() {
    return width;
  }
  
  /**
   * @param alignment a column {@link Alignment}.
   * @return this.
   */
  public ColumnStyle alignment(Alignment alignment) {
    this.alignment = alignment;
    return this;
  }
  
  /**
   * @return this instance.
   */
  public ColumnStyle alignLeft() {
    this.alignment = Alignment.LEFT;
    return this;
  }
  
  /**
   * @return this instance.
   */
  public ColumnStyle alignRight() {
    this.alignment = Alignment.RIGHT;
    return this;
  }
  
  /**
   * @return this instance.
   */
  public ColumnStyle alignCenter() {
    this.alignment = Alignment.CENTER;
    return this;
  }
  
  /**
   * @return this instance's column {@link Alignment}.
   */
  public Alignment getAlignment() {
    return alignment;
  }
  
  /**
   * @param padding the left padding to use.
   * @return this instance.
   */
  public ColumnStyle leftPadding(String padding) {
    this.leftPadding = padding;
    return this;
  }
  
  /**
   * @return this instance's left padding.
   */
  public String getLeftPadding() {
    return leftPadding;
  }
  
  /**
   * @param padding the right padding to use.
   * @return this instance.
   */
  public ColumnStyle rightPadding(String padding) {
    this.rightPadding = padding;
    return this;
  }
  
  /**
   * @return this instance's right padding.
   */
  public String getRightPadding() {
    return rightPadding;
  }

}
