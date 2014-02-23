package org.sapia.dataset.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.sapia.dataset.func.NoArgFunction;

/**
 * A generic tree structure.
 * 
 * @author yduchesne
 *
 * @param <K> the generic type for tree keys.
 * @param <V> the generic type for tree values.
 */
public class Tree<K, V> {

  /**
   * This interface specifies visitor behavior.
   * 
   * @author yduchesne
   */
  public interface Visitor<K, V> {
    
    /**
     * @param node the currently visited node.
     */
    public void visit(Node<K, V> node);
    
  }
  
  public static class Node<K, V> {
    private int level;
    private K key;
    private V value;
    private Map<K, Node<K, V>> nodes;
    
    public Node(int level, K key, V value, NoArgFunction<Map<K, Node<K, V>>> mapFunc) {
      this.level = level;
      this.key  = key;
      this.value = value;
      this.nodes = mapFunc.call();
    }
    
    public int getLevel() {
      return level;
    }
    
    public K getKey() {
      return key;
    }
    
    public V getValue() {
      return value;
    }
    
    private Node<K, V> addIfNotExists(K key, V value, NoArgFunction<Map<K, Node<K, V>>> mapFunc) {
      Node<K, V> node = nodes.get(key);
      if (node == null) {
        node = new Node<K, V>(level + 1, key, value, mapFunc);
        nodes.put(key, node);
      }
      return node;
    }
    
    public int getNodeCount() {
      return this.nodes.size();
    }
    
    public boolean isLeaf() {
      return this.nodes.size() == 0;
    }
    
    public Node<K, V> get(K key) {
      return nodes.get(key);
    }
    
    public Collection<K> keySet() {
      return nodes.keySet();
    }
    
    public Collection<Node<K, V>> values() {
      return nodes.values();
    }
    
    public void acceptBreadthFirst(Visitor<K, V> visitor) {
      visitor.visit(this);
      for (Node<K, V> child : nodes.values()) {
        child.acceptBreadthFirst(visitor);
      }
    }
    
    public void acceptDepthFirst(Visitor<K, V> visitor) {
      for (Node<K, V> child : nodes.values()) {
        child.acceptDepthFirst(visitor);
      }
      visitor.visit(this);
    }

  }
  
  private Node<K, V>                         root;
  private NoArgFunction <Map<K, Node<K, V>>> mapFunc = new NoArgFunction<Map<K, Node<K,V>>>() {
    @Override
    public Map<K, Node<K, V>> call() {
      return new HashMap<>();
    }
  };

  /**
   * Creates a {@link HashMap}-based instance. 
   */
  public Tree() {
    this(new NoArgFunction<Map<K, Node<K,V>>>() {
      @Override
      public Map<K, Node<K, V>> call() {
        return new HashMap<>();
      }
    });
  }

  /**
   * A {@link NoArgFunction} that generates {@link Map}s used internally for tree {@link Node}s.
   */
  public Tree(NoArgFunction <Map<K, Node<K, V>>> mapFunc) {
    this.mapFunc = mapFunc;
    root = new Node<K, V>(0, null, null, mapFunc);
  }
  
  /**
   * @return this instance's root node.
   */
  public Node<K, V> getRoot() {
    return root;
  }

  /**
   * @param pairs a {@link Collection} of {@link KVPair}s, corresponding a branch in
   * the tree.
   */
  public Node<K, V> bind(Collection<KVPair<K, V>> pairs) {
    Node<K, V> current = root;
    for (KVPair<K, V> p : pairs) {
      current = current.addIfNotExists(p.getKey(), p.getValue(), mapFunc);
    }
    return current;
  }
  
  /**
   * @param path a {@link Collection} of keys corresponding to the path to the node
   * with to which the given value should be set.
   * @param value a value.
   * @return the {@link Node} to which the given value was set.
   */
  public Node<K, V> bind(Collection<K> path, V value) {
    Node<K, V> current = root;
    int count = 0;
    for (K key : path) {
      if (Objects.safeEquals(current.key, key)) {
        return current;
      } else {
        if (count == path.size() - 1) {
          current = current.addIfNotExists(key, value, mapFunc);
        } else {
          current = current.addIfNotExists(key, null, mapFunc);
        }
      }
      count++;
    }
    return current;
  }
  
  /**
   * @param keys a {@link Collection} of keys.
   * @return the {@link Node} at the given path, or <code>null</code> if no such node
   * exists.
   */
  public Node<K, V> lookup(Collection<K> path) {
    Node<K, V> current = root;
    for (K key : path) {
      if (current == null) {
        return null;
      } else if (Objects.safeEquals(current.key, key)) {
        return current;
      } else {
        current = current.get(key);
        if (current == null) {
          return null;
        }
      }
    }
    return current;
  }
  
  /**
   * @param visitor the {@link Visitor} to use for traversal.
   */
  public void acceptBreadthFirst(Visitor<K, V> visitor) {
    root.acceptBreadthFirst(visitor);
  }
  
  /**
   * @param visitor the {@link Visitor} to use for traversal.
   */
  public void acceptDepthFirst(Visitor<K, V> visitor) {
    root.acceptDepthFirst(visitor);
  }
}
