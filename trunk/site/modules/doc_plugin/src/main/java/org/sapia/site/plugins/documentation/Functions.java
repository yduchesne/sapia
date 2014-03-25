package org.sapia.site.plugins.documentation;

public class Functions {

  private static final String DEFAULT_JAVADOC_BASE = "maven/api";
  
  private Functions() {
  }
  
  public static String javadocClassName(String className) {
    return className.replace(".", "/") + ".html";
  }
  
  public static String javadocLink(String className) {
    return javadocLink(className, DEFAULT_JAVADOC_BASE);
  }
    
  public static String javadocLink(String className, String base) {

    StringBuilder out = new StringBuilder();
    out.append("<span style=\"font-family: courier, courier new; color: black;\">");
    out.append("<a href=\"");
    out.append(base).append("/").append(className.replace(".", "/")).append(".html").append("\"");
    out.append(" target=\"").append(className).append("\">");
    out.append(className).append("</a>");
    out.append("</span>");  
  
    return out.toString();
  }
}
