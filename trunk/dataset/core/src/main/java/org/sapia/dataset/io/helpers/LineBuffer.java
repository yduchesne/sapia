package org.sapia.dataset.io.helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * An instance of this class buffers up {@link Line} instances, which have
 * be read from a dataset input.
 * 
 * @author yduchesne
 *
 */
public class LineBuffer {
  
  private List<Line> lines = new ArrayList<>();
  
  /**
   * @param line a {@link Line} to buffer.
   */
  public void append(Line line) {
    lines.add(line);
  }
  
  /**
   * @return this instance's {@link Line}s, in the order in which they were added.
   */
  public List<Line> getLines() {
    return lines;
  }
}
