package org.sapia.cocoon.generation.chunk.template.content;


public class ContentParser {

  public enum ParserStatus{
    INITIAL,
    IN_VAR,
    END_DELIM,
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
    CompositeContent toReturn = new CompositeContent();
    ContentTokenizer ct = new ContentTokenizer(rawContent, '\\');
    ContentTokenizer.State st;
    StringBuilder temp = new StringBuilder();
    ParserStatus status = ParserStatus.INITIAL;
    while(ct.hasNext()){
      if(status == ParserStatus.INITIAL){
        st = ct.next(startDelim);
        if(st.isDelimFound()){
          if(st.isEscaped()){
            temp.append(st.token()).append(startDelim);
          }
          else{
            temp.append(st.token());
            StringContent content = new StringContent(temp.toString());
            toReturn.add(content);
            temp.delete(0, temp.length());
            status = ParserStatus.IN_VAR;
          }
        }
        else{
          temp.append(st.token());
        }
      }
      else if(status == ParserStatus.IN_VAR){
        st = ct.next(endDelim);
        if(st.isDelimFound()){
          if(st.isEscaped()){
            temp.append(st.token()).append(endDelim);
            status = ParserStatus.INITIAL;
          }
          else{
            toReturn.add(parseVar(st.token()));
            temp.delete(0, temp.length());
            status = ParserStatus.INITIAL;
          }
        }
        else{
          temp.append(st.token());
        }
      }      
    }
    if(temp.length() > 0){
      StringContent content = new StringContent(temp.toString());
      toReturn.add(content);
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
