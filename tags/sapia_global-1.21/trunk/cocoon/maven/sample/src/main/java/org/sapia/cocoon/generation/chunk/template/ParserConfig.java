package org.sapia.cocoon.generation.chunk.template;

/**
 * Holds configuration about variable parsing.
 * 
 * @author yduchesne
 *
 */
public class ParserConfig {

  private String startVarDelim = "$[";
  private String endVarDelim = "]";

  /**
   * @return the start delimiter for identifying variables (defaults to <code>$[</code>).
   */
  public String getStartVarDelim() {
    return startVarDelim;
  }
  public void setStartVarDelim(String startVarDelim) {
    this.startVarDelim = startVarDelim;
  }

  /**
   * @return the end delimiter for identifying variables (defaults to <code>]</code>).
   */
  public String getEndVarDelim() {
    return endVarDelim;
  }
  public void setEndVarDelim(String endVarDelim) {
    this.endVarDelim = endVarDelim;
  }
}
