package org.sapia.dataset.format;

import org.sapia.dataset.Datatype;

/**
 * This interface is to be implemented by classes whose instance "know" how to format their
 * internal representation so that it is displayable in human-readable format.
 * 
 * @author yduchesne
 *
 */
public interface SelfFormattable {

  /**
   * @param type the {@link Datatype} associated to this instance.
   * @param f the {@link Format} associated to this instance's column.
   * @return a formatted {@link String}.
   */
  public String format(Datatype type, Format f);
}
