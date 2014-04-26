package org.sapia.archie;

import java.util.Iterator;


/**
 * This interface specifies the behavior of "nodes". Nodes hold child nodes that are bound
 * to their parent with a given name. Nodes also hold values, that are arbitrary objects
 * also bound to their parent node using a given name.
 *
 * @author Yanick Duchesne
 */
public interface Node {
  /**
   * Returns this node's value.
   *
   * @return an {@link Object}, or <code>null</code> if this node has
   * no value.
   */
  public Object getValue(NamePart name);

  /**
   * Internally removes the value that this node holds
   * and returns it.
   *
   * @return an {@link Object}, or <code>null</code> if this node has
   * no value.
   */
  public Object removeValue(NamePart name);

  /**
   * Put a value into this node; overwrites the existing value - if any.
   *
   * @param name the name under which to bind the given value.
   * @param value an {@link Object}.
   * @param overwrite if <code>true</code>, overwrites the already existing value
   * for the given name - it such is the case.
   *
   * @return <code>true</code> if the given value was added. Returns <code>false</code>
   * if <code>overwrite</code> is <code>false</code> and a value already exists for the
   * given name.
   */
  public boolean putValue(NamePart name, Object value, boolean overwrite);

  /**
   * Returns the names of this instance's values.
   *
   * @return an {@link Iterator} of {@link NamePart}s.
   */
  public Iterator<NamePart> getValueNames();

  /**
   * @return the number of values that this instance contains.
   */
  public int getValueCount();

  /**
   * Sets this node's name and parent node. Client applications <b>must not</b>
   * use this method - unless they know what they are doing.
   *
   * @param parent this instance's parent {@link Node}.
   * @param nodeName a {@link NamePart}.
   */
  public void setUp(Node parent, NamePart nodeName);

  /**
   * Returns the full path to this node, starting from the root.
   *
   * @return a {@link Name}.
   */
  public Name getAbsolutePath();

  /**
   * Returns this instance's parent.
   *
   * @return a {@link Node}.
   */
  public Node getParent();

  /**
   * Return this instance's name.
   *
   * @return a {@link NamePart}.
   */
  public NamePart getName();

  /**
   * Creates the node corresponding to the given name and returns it.
   *
   * @param name a {@link NamePart}.
   * @return a {@link Node}.
   * @throws DuplicateException if a node exists for the given name.
   */
  public Node createChild(NamePart name)
                   throws DuplicateException, ProcessingException;

  /**
   * Returns the node with the given name.
   *
   * @param name a {@link NamePart} corresponding to the name of an
   * existing child node.
   *
   * @return a {@link Node} or <code>null</code> if not child exists
   * for the given name.
   */
  public Node getChild(NamePart name);

  /**
   * Removes the node with the given name.
   *
   * @param name a {@link NamePart} corresponding to the name of an
   * existing child node.
   *
   * @return a {@link Node} or <code>null</code> if not child exists
   * for the given name.
   */
  public Node removeChild(NamePart name);

  /**
   * Returns this instance's child nodes.
   *
   * @return a {@link Iterator} of {@link Node}s.
   */
  public Iterator<Node> getChildren();

  /**
   * @return the number of children that this instance contains.
   */
  public int getChildrenCount();

  /**
   * @return an {@link Iterator} of {@link Entry} instances,
   * corresponding to the bindings that this instance holds.
   */
  public Iterator<Entry> getEntries();

  /**
   * Returns the names of this instance's nodes.
   *
   * @return an {@link Iterator} of {@link NamePart} s.
   */
  public Iterator<NamePart> getChildrenNames();

  /**
   * Returns this implementation's name parser.
   *
   * @return a {@link NameParser}.
   */
  public NameParser getNameParser();

  /**
   * Traverses this instance starting from the root - notifies the given
   * visitor as nodes are encountered.
   *
   * @param visitor a {@link NodeVisitor} to notify.
   * @return <code>false</code> if traversal should be aborted.
   */
  public boolean accept(NodeVisitor visitor);
}
