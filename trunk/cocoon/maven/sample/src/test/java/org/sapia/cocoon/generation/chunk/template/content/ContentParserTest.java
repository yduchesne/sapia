package org.sapia.cocoon.generation.chunk.template.content;

import org.sapia.cocoon.generation.chunk.template.TestTemplateContext;

import junit.framework.TestCase;

public class ContentParserTest extends TestCase {

  public ContentParserTest(String name) {
    super(name);
  }

  public void testParseSimpleVar() {
    ContentParser p = new ContentParser();
    TemplateContent tc = p.parse("$[someVar]");
    String s = tc.render(new TestTemplateContext().put("someVar", "foo"));
    assertEquals("foo", s);
  }
  
  public void testParseString() {
    ContentParser p = new ContentParser();
    TemplateContent tc = p.parse("foo");
    String s = tc.render(new TestTemplateContext());
    assertEquals("foo", s);
  }
  
  public void testParseVarAtBeginning() {
    ContentParser p = new ContentParser();
    TemplateContent tc = p.parse("$[someVar] bar");
    String s = tc.render(new TestTemplateContext().put("someVar", "foo"));
    assertEquals("foo bar", s);
  }
  
  public void testParseVarAtEnd() {
    ContentParser p = new ContentParser();
    TemplateContent tc = p.parse("foo $[someVar]");
    String s = tc.render(new TestTemplateContext().put("someVar", "bar"));
    assertEquals("foo bar", s);
  }
  
  public void testMixedContent1(){
    ContentParser p = new ContentParser();
    TemplateContent tc = p.parse("$[var1] sna $[var2] fu");
    String s = tc.render(new TestTemplateContext().put("var1", "foo").put("var2", "bar"));
    assertEquals("foo sna bar fu", s);
  }
  
  public void testMixedContent2(){
    ContentParser p = new ContentParser();
    TemplateContent tc = p.parse("sna $[var1] fu $[var2]");
    String s = tc.render(new TestTemplateContext().put("var1", "foo").put("var2", "bar"));
    assertEquals("sna foo fu bar", s);
  }
}
