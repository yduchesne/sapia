package org.sapia.site.plugins.documentation.template.jmte;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.sapia.site.plugins.documentation.Functions;

import com.floreysoft.jmte.NamedRenderer;
import com.floreysoft.jmte.RenderFormatInfo;

public class ClassLinkRenderer implements NamedRenderer {

  @Override
  public String getName() {
    return "classlink";
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
      throw new IllegalArgumentException("'class' must be specified in classlink plugin");
    }
    String base = args.length > 1 ? args[1] : null;
    if (base == null) {
       out.append(Functions.javadocLink(className));
    } else {
      out.append(Functions.javadocLink(className, base));
    }
  }

}
