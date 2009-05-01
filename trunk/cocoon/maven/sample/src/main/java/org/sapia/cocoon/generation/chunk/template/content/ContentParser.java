package org.sapia.cocoon.generation.chunk.template.content;

public class ContentParser {

  public enum State{
    INITIAL,
    IN_VAR,
    END_DELIM_FOUND,
  }
  
  private String startDelim ="$[";
  private String endDelim = "]";

  public void setEndDelim(String endDelim) {
    this.endDelim = endDelim;
  }
  
  public void setStartDelim(String startDelim) {
    this.startDelim = startDelim;
  }
  
  public TemplateContent parse(String rawContent){
    int i = rawContent.indexOf(startDelim);
    if(i <= -1){
      return new StringContent(rawContent);
    }
    else{
      return doParse(rawContent);
    }
  }
  
  private TemplateContent doParse(String rawContent){
    int currentIndex = 0;
    CompositeContent toReturn = new CompositeContent();
    while(currentIndex < rawContent.length()){
      int i = rawContent.indexOf(startDelim, currentIndex);
      if(i > -1){
        int j = rawContent.indexOf(endDelim, currentIndex);
        if(j > i+startDelim.length()){
          if(i > currentIndex){
            StringContent c = new StringContent(rawContent.substring(currentIndex, i));
            toReturn.add(c);
          }
          VarContent var = this.parseVar(rawContent.substring(i+startDelim.length(),j));
          toReturn.add(var);
          currentIndex = j+endDelim.length();
        }
        else{
          StringContent c = new StringContent(rawContent.substring(i));
          toReturn.add(c);
          currentIndex = rawContent.length();
        }
      }
      else{
        StringContent c = new StringContent(rawContent.substring(currentIndex));
        toReturn.add(c);
        currentIndex = rawContent.length();
      }
    }
    return toReturn;
  }
  
  private VarContent parseVar(String var){
    int i = var.indexOf(":");
    if(i > 0){
      return new VarContent(var.substring(0, i), var.substring(i+1));
    }
    else{
      return new VarContent(null, var);
    }
  }
}
