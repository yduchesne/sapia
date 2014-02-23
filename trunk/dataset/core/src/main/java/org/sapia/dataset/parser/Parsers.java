package org.sapia.dataset.parser;

import org.sapia.dataset.Datatype;

/**
 * A factory of {@link Parser}s.
 * 
 * @author yduchesne
 *
 */
public class Parsers {

  private static final Parser NUMERIC = new NumericParser();
  private static final Parser DATE    = new DateParser();
  private static final Parser STRING  = new StringParser();
  private static final Parser WKT     = new WktParser();
  
  private Parsers() {
  }
  
  /**
   * @param datatype a Datatype.
   * @return the {@link Parser} corresponding to the given data type.
   */
  public static Parser getParserFor(Datatype datatype) {
    switch (datatype) {
      case DATE: 
        return DATE;
      case GEOMETRY: 
        return WKT;
      case NUMERIC: 
        return NUMERIC;
      case STRING: 
        return STRING;
      default:
        throw new IllegalArgumentException("No parser found for datatype: " + datatype);
    }
  }
}
