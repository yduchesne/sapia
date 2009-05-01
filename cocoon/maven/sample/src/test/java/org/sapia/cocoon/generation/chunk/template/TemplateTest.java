package org.sapia.cocoon.generation.chunk.template;

import java.io.File;
import java.util.HashMap;

import junit.framework.TestCase;

import org.sapia.cocoon.TestSourceResolver;
import org.sapia.cocoon.util.StdoutContentHandler;

public class TemplateTest extends TestCase {

  private TestTemplateContext context;
  public TemplateTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    TestSourceResolver sources = new TestSourceResolver(new File("etc/template"));
    TemplateCache cache = new TemplateCache();
    context = new TestTemplateContext(
        new HashMap<String, Object>(),
        new StdoutContentHandler(),
        cache, sources
    );
    
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }
 
  public void testParseTemplate() throws Exception{
    context.put("from", "John Smith");
    context.put("to", "Fred Flintstone");
    context.put("subject", "meeting");
    Template template = context.resolveTemplate("testTemplate.xml");
    template.render(context);
  }

}
