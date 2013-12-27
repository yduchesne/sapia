package org.sapia.cocoon.generation.chunk.template.content;

import junit.framework.TestCase;

public class ContentTokenizerTest extends TestCase {

  public ContentTokenizerTest(String name) {
    super(name);
  }


  public void testTokenizer(){
    
    String s = "this is a $[tokenization] \\$[test] that $[should be] successful and generate no $[errors";
    
    ContentTokenizer tokenizer = new ContentTokenizer(s, '\\');
    ContentTokenizer.State st = tokenizer.next("$[");
    assertTrue(st.isDelimFound());
    assertEquals("this is a ", st.token());
    
    st = tokenizer.next("]");
    assertTrue(st.isDelimFound());
    assertEquals("tokenization", st.token());

    st = tokenizer.next("$[");
    assertTrue(st.isDelimFound());
    assertTrue(st.isEscaped());
    assertEquals(" ", st.token());

    st = tokenizer.next("]");
    assertTrue(st.isDelimFound());
    assertEquals("test", st.token());

    st = tokenizer.next("$[");
    assertTrue(st.isDelimFound());
    assertEquals(" that ", st.token());

    st = tokenizer.next("]");
    assertTrue(st.isDelimFound());
    assertEquals("should be", st.token());

    st = tokenizer.next("$[");
    assertTrue(st.isDelimFound());
    assertEquals(" successful and generate no ", st.token());

    st = tokenizer.next("]");
    assertTrue(!st.isDelimFound());
    assertEquals("errors", st.token());
    
    assertTrue(!tokenizer.hasNext());
    
  }
}
