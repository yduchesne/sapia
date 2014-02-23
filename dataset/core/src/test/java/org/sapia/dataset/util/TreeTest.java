package org.sapia.dataset.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.util.Tree.Node;

public class TreeTest {

  private Tree<String, String> tree;
  
  @Before
  public void setUp() {
    tree = new Tree<>();
  }
  
  @Test
  public void testBind() {
    Node<String, String>  lastNode = tree.bind(Data.list(KVPair.obj("k1", "v1"), KVPair.obj("k2", "v2"), KVPair.obj("k3", "v3")));
    assertEquals("k3", lastNode.getKey());
    assertEquals("v3", lastNode.getValue());
    assertEquals(3, lastNode.getLevel());
    
    Node<String, String> n = tree.getRoot().get("k1");
    assertEquals("v1", n.getValue());
    assertEquals(1, n.getLevel());
    
    n = n.get("k2");
    assertEquals("v2", n.getValue());
    assertEquals(2, n.getLevel());
    
    n = n.get("k3");
    assertEquals("v3", n.getValue());
    assertEquals(3, n.getLevel());
  }
  
  @Test
  public void testBindWithPath() {
    Node<String, String>  lastNode = tree.bind(Data.list("k1", "k2", "k3"), "v3");
    assertEquals("k3", lastNode.getKey());
    assertEquals("v3", lastNode.getValue());
    
    Node<String, String> n = tree.getRoot().get("k1");
    assertNull(n.getValue());
    assertEquals(1, n.getLevel());
    
    n = n.get("k2");
    assertNull(n.getValue());
    assertEquals(2, n.getLevel());
    
    n = n.get("k3");
    assertEquals("v3", n.getValue());
    assertEquals(3, n.getLevel());
  }

  @Test
  public void testLookup() {
    tree.bind(Data.list(KVPair.obj("k1", "v1"), KVPair.obj("k2", "v2"), KVPair.obj("k3", "v3")));
    
    Node<String, String> n = tree.lookup(Data.list("k1"));
    assertEquals("v1", n.getValue());
    
    n = tree.lookup(Data.list("k1", "k2"));
    assertEquals("v2", n.getValue());
    
    n = tree.lookup(Data.list("k1", "k2", "k3"));
    assertEquals("v3", n.getValue());

  }

}
