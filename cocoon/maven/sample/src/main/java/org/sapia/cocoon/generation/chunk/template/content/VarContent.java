package org.sapia.cocoon.generation.chunk.template.content;

import org.sapia.cocoon.generation.chunk.exceptions.MissingValueException;
import org.sapia.cocoon.generation.chunk.template.TemplateContext;

public class VarContent implements TemplateContent{
  
  private String prefix, name;
  private static final String EMPTY_STRING = "";
  
  public VarContent(String prefix, String name) {
    this.prefix = prefix;
    this.name = name;
  }
  
  public String render(TemplateContext ctx) {
    Object val = ctx.getValue(prefix, name);
    if(val == null){
      if(ctx.isLenient()){
        return EMPTY_STRING;
      }
      else{
        throw new MissingValueException("No value found for " +
          (prefix == null ? name : prefix+":"+name));
      }
    }
    else{
      if(val instanceof String){
        return (String)val;
      }
      else{
        return val.toString();
      }
    }
  }
  
}
