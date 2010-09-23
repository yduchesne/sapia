package org.sapia.cocoon.i18n;

public class i18nInfo {
  
  private String src;
  private String locale;
  private String extension;

  
  public static i18nInfo parse(String src){
    
    i18nInfo i18nSrc = new i18nInfo();
    int i = src.lastIndexOf('.'); 
    if(i > 0){
      setNameAndLocale(i18nSrc, src.substring(0, i));
      i18nSrc.extension = src.substring(i+1);
    }
    else{
      setNameAndLocale(i18nSrc, src);
    }
    return i18nSrc;
  }

  private static void setNameAndLocale(i18nInfo src, String str){
    int i = str.indexOf('_');
    if(i > 0){
      src.src = str.substring(0, i);
      src.locale = str.substring(i+1);
    }
    else{
      src.src = str;
    }
  }
  
  public String getExtension() {
    return extension;
  }

  public String getLocale() {
    return locale;
  }

  public String getSrc() {
    return src;
  }
  
  public String getSrcExt() {
    StringBuffer sb = new StringBuffer(src);
    if(extension != null){
      sb.append('.').append(extension);
    }
    return sb.toString();
  }  
  
  public String toString(){
    StringBuffer sb = new StringBuffer(src);
    if(locale != null){
      sb.append('_').append(locale);
    }
    if(extension != null){
      sb.append('.').append(extension);
    }
    return sb.toString();
  }
  
}


