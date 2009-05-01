package org.sapia.cocoon.generation.chunk.template.content;

import org.sapia.cocoon.generation.chunk.template.TemplateContext;

public class StringContent implements TemplateContent{
  
  private String content;
  
  public StringContent(String content){
    this.content = content;
  }
  
  public String render(TemplateContext ctx) {
    return content;
  }

}
