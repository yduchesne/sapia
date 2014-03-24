package org.sapia.site.plugins.documentation.template.markdown;

import java.util.List;
import java.util.Map;

import org.markdown4j.Plugin;

public class ParaPlugin extends Plugin {
  
  public ParaPlugin() {
    super("para");
  }
  @Override
  public void emit(StringBuilder out, List<String> lines, Map<String, String> args) {
    int count = 0;
    for (String line : lines) {
      if (count > 0) {
        out.append(" ");
      }
      out.append(line);
    }
  }

}
