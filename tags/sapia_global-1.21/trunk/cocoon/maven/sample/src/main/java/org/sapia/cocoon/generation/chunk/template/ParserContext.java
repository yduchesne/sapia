package org.sapia.cocoon.generation.chunk.template;

import org.sapia.cocoon.generation.chunk.template.content.ContentParser;

/**
 * Encapsulates data relevant to a {@link TemplateParser}.
 * 
 * @author yduchesne
 *
 */
public class ParserContext {

  private ContentParser contentParser;
  
  /**
   * @param parser the {@link ContentParser} that this instance must use
   * to parse variables in XML fragments.
   */
  ParserContext(ContentParser parser){
    this.contentParser = parser;
  }
  
  /**
   * @return this instance's {@link ContentParser}.
   */
  public ContentParser getContentParser() {
    return contentParser;
  }
  
  
}
