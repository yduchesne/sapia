package org.sapia.cocoon.generation.chunk.template.content;

import java.util.ArrayList;
import java.util.List;

import org.sapia.cocoon.generation.chunk.template.TemplateContext;

public class CompositeContent implements TemplateContent{
  
  private List<TemplateContent> elements = new ArrayList<TemplateContent>();

  public void add(TemplateContent tc){
    elements.add(tc);
  }
  
  public String render(TemplateContext ctx) {
    StringBuilder b = new StringBuilder();
    for(TemplateContent e:elements){
      b.append(e.render(ctx));
    }
    return b.toString();
  }
}
