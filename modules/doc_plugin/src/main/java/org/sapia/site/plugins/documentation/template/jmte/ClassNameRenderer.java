package org.sapia.site.plugins.documentation.template.jmte;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.floreysoft.jmte.NamedRenderer;
import com.floreysoft.jmte.RenderFormatInfo;

public class ClassNameRenderer implements NamedRenderer {

  @Override
  public String getName() {
    return "classname";
  }
  
  @Override
  public RenderFormatInfo getFormatInfo() {
    return null;
  }
  
  @Override
  public Class<?>[] getSupportedClasses() {
    return new Class<?>[] { NullObject.class };
  }
  
  @Override
  public String render(Object model, String link, Locale locale) {
    StringBuilder out = new StringBuilder();
    doRender(out, StringUtils.split(link, ','));
    return out.toString();
  }

  private void doRender(StringBuilder out, String[] args) {
    if (args.length == 0) {
      throw new IllegalArgumentException("Expected class name");
    }
    String className = args[0];
    if (className == null) {
      throw new IllegalArgumentException("'class' must be specified in classname plugin");
    }
    out.append("<span style=\"font-family: courier, courier new; color: black;\">");
    out.append(className);
    out.append("</span>");
  }

}
