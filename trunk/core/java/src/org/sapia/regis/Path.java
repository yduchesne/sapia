package org.sapia.regis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class Path implements Serializable{
  
  static final long serialVersionUID = 1L;
  
  protected List _tokens;
  
  public Path(List tokens){
    _tokens = tokens;
  }
  
  public Iterator tokens(){
    return _tokens.iterator();
  }
  
  public boolean isRoot(){
    return _tokens.size() == 0 ||
      (_tokens.size() == 1 && _tokens.get(0).equals(Node.ROOT_NAME));
  }
  
  public int tokenCount(){
    return _tokens.size();
  }
  
  public Path getClone(){
    return new Path(new ArrayList(_tokens));
  }
  
  public static Path parse(String path){
    return parse(path, "/");
  }
  
  public static Path parse(String path, String delim){
    StringTokenizer tokenizer = new StringTokenizer(path, delim);
    ArrayList tokens = new ArrayList();
    while(tokenizer.hasMoreTokens()){
      tokens.add(tokenizer.nextToken());
    }
    tokens.trimToSize();
    return new Path(tokens);
  }  
  
  public String toString(char delim){
    StringBuffer buf = new StringBuffer();
    for(int i = 0; i < _tokens.size(); i++){
      String token = (String)_tokens.get(i);
      buf.append(token);
      if(i < _tokens.size() - 1){
        buf.append(delim);
      }
    }
    return buf.toString();
  }

  
  public String toString(){
    return toString('/');
  }  

}
