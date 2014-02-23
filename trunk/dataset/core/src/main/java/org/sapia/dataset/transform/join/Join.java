package org.sapia.dataset.transform.join;

import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.util.Strings;

/**
 * Holds the {@link ColumnSet}s indicating which columns of the "left" 
 * and "right" dataset are part of the join.
 * 
 * @author yduchesne
 *
 */
public class Join {
  
  /**
   * Holds constants corresponding to the different join types.
   * 
   * @author yduchesne
   *
   */
  public enum Type {
    
    /**
     * Specifies an inner join: only rows in the left dataset that match rows in the
     * right dataset are joined and included in the dataset resulting from the join.
     */
    INNER,
    
    /**
     * Specifies an outer join: rows in the left dataset that match rows in the
     * right dataset and rows that don't match any row in the right dataset are 
     * joined and included in the dataset resulting from the join.
     */
    OUTER
  }
  
  // ==========================================================================

  private Type      type = Type.OUTER;
  private ColumnSet left, right;
  private String    leftAlias  = "left";
  private String    rightAlias = "right";
  
  public Join(ColumnSet left, ColumnSet right) {
    this.left  = left;
    this.right = right;
  }
  
  /**
   * @param leftAlias an alias to associate to the "left" dataset.
   */
  public void setLeftAlias(String leftAlias) {
    this.leftAlias = leftAlias;
  }
  
  /**
   * @return the alias associated to the "left" dataset.
   */
  public String getLeftAlias() {
    return leftAlias;
  }
  
  /**
   * @param rightAlias an alias to associate to the "right" dataset.
   */
  public void setRightAlias(String rightAlias) {
    this.rightAlias = rightAlias;
  }
  
  /**
   * @return the alias associated to the "right" dataset.
   */
  public String getRightAlias() {
    return rightAlias;
  }
  
  /**
   * @return the "left" {@link ColumnSet}.
   */
  public ColumnSet getLeft() {
    return left;
  }
  
  /**
   * @return the "right" {@link ColumnSet}.
   */
  public ColumnSet getRight() {
    return right;
  }
  
  /**
   * @return the join type.
   */
  public Type getType() {
    return type;
  }
  
  @Override
  public String toString() {
    return Strings.toString(
        "left", left, 
        "right", right, 
        "type", type, 
        "leftAlias", leftAlias,
        "rightAlias", rightAlias);
  }

}
