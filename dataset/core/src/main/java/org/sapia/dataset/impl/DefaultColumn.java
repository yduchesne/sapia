package org.sapia.dataset.impl;

import org.sapia.dataset.Column;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.format.DefaultFormat;
import org.sapia.dataset.format.Format;
import org.sapia.dataset.parser.Parser;
import org.sapia.dataset.parser.Parsers;
import org.sapia.dataset.util.Objects;

/**
 * Default implementation of the {@link Column} interface.
 * 
 * @author yduchesne
 *
 */
public class DefaultColumn implements Column {
 
  private Parser   parser;
  private Format   format = DefaultFormat.getInstance();
  private int      index;
  private Datatype type;
  private String   name;
  
  public DefaultColumn(int index, Datatype type, String name) {
    this.index = index;
    this.name = name;
    this.type = type;
    this.parser = Parsers.getParserFor(type);
  }
  
  @Override
  public Parser getParser() {
    return parser;
  }
  
  @Override
  public void setParser(Parser parser) {
    this.parser = parser;
  }
  
  @Override
  public Format getFormat() {
    return format;
  }
  
  @Override
  public void setFormat(Format format) {
    this.format = format;
  }
  
  @Override
  public int getIndex() {
    return index;
  }
  
  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public Datatype getType() {
    return type;
  }
  
  @Override
  public Column copy(int newIndex) {
    DefaultColumn copy = new DefaultColumn(newIndex, type, name);
    copy.setFormat(format);
    copy.setParser(parser);
    return copy;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Column) {
      Column other = (Column) obj;
      return 
          Objects.safeEquals(name, other.getName()) 
          && Objects.safeEquals(type, other.getType());
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return Objects.safeHashCode(name, type);
  }

}
