package org.sapia.cocoon.generation.chunk.template.content;

public class ContentTokenizer {
  
  private String content;
  private int position;
  private int escapeChar = -1;
  
  public ContentTokenizer(String content){
    this.content = content;
  }

  public ContentTokenizer(String content, char escapeChar){
    this.content = content;
    this.escapeChar = (int)escapeChar;
  }
  
  public boolean hasNext(){
    return position < content.length();
  }
  
  public State next(String delim){
    if(position >= content.length()){
      throw new IllegalStateException("End of string reached");
    }
    State st = null;
    while(position < content.length()){
      int i = content.indexOf(delim, position);
      if(i >-1 ){
        if(i >= position){
          boolean isEscaped = i > position ? isEscapeChar(content.charAt(i-1)) : false;
          String token = null;
          if(isEscaped) 
            token = content.substring(position, i-1);
          else
            token = content.substring(position, i);
          position = i+delim.length();
          st = new State();
          st.token = token;
          st.delimFound = true;
          st.isEscaped = isEscaped;
          break;
        }
      }
      else{
        String token = content.substring(position);
        st = new State();
        st.token = token;
        position = content.length();
        break;
      }
    }
    return st;
  }
  
  private boolean isEscapeChar(char c){
    return escapeChar > 0 && escapeChar == (int)c;
  }
  
  public static class State{
    private String token;
    private boolean delimFound, isEscaped;
   
    public boolean isDelimFound(){
      return delimFound;
    }
    
    public boolean isEscaped() {
      return isEscaped;
    }
    
    public String token(){
      return token;
    }
  }

}
