package org.sapia.site.plugins.documentation.template.markdown;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.markdown4j.Markdown4jProcessor;
import org.markdown4j.Plugin;

public class ParaPlugin extends Plugin implements ProcessorAware {
  
  private Markdown4jProcessor processor;
  
  @Override
  public void setProcessor(Markdown4jProcessor processor) {
    this.processor = processor;
  }
  
  public ParaPlugin() {
    super("para");
  }
  @Override
  public void emit(StringBuilder out, List<String> lines, Map<String, String> args) {
    int count = 0;
    StringBuilder buf = new StringBuilder();
    for (String line : lines) {
      if (count++ > 0) {
        buf.append(" ");
      }
      buf.append(line);
    }
    try {
      String result = processor.process(buf.toString()).trim();
      /*if (result.startsWith("<p>")) {
        result = result.substring("<p>".length());
      }
      if (result.endsWith("</p>")) {
        result = result.substring(0, result.length() - "</p>".length());
      }*/
      out.append(result);
    } catch (IOException e) {
      throw new IllegalArgumentException("'para' plugin could not process paragraph content", e);
    }
  }

}
