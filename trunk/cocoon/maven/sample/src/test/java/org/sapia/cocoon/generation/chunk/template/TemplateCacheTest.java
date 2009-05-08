package org.sapia.cocoon.generation.chunk.template;

import java.io.File;

import org.sapia.cocoon.TestSourceResolver;

import junit.framework.TestCase;

public class TemplateCacheTest extends TestCase {

  private TestCache cache;
  private TestSourceResolver sources;
  
  public TemplateCacheTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    sources = new TestSourceResolver(new File("etc/template"));
    cache = new TestCache();
    cache.setCleanupInterval(1000);
    cache.setIdleTime(800);
    cache.start();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
    cache.stop();
  }
  
  public void testRefresh() throws Exception{
    ParserConfig conf = new ParserConfig();
    String uri = "testTemplate.xml";
    cache.get(conf, uri, sources);
    Thread.sleep(100);
    cache.getEntry(uri, sources).touch();
    cache.get(conf, uri, sources);
    assertEquals(2, cache.refreshCount);
    cache.get(conf, uri, sources);
    assertEquals(2, cache.refreshCount);
  }
  
  public void testCleanStaleEntries() throws Exception{
    ParserConfig conf = new ParserConfig();
    String uri = "testTemplate.xml";
    cache.get(conf, uri, sources);
    assertTrue(cache.containsEntry(uri, sources));
    Thread.sleep(1500);
    assertTrue(!cache.containsEntry(uri, sources));
  }

  static class TestCache extends TemplateCache{
    
    public int refreshCount;
    public TemplateEntry entry;
    @Override
    protected void onLoad(TemplateEntry entry) {
      refreshCount++;
    }
  }
}
