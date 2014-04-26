package org.sapia.archie;

import java.util.Iterator;


/**
 * This interface specifies the behavior of "nodes". Nodes hold child nodes that are bound
 * to their parent with a given name. Nodes also hold values, that are arbitrary objects
 * also bound to their parent node using a given name.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface Node {
  /**
   * Returns this node's value.
   *
   * @return an <code>Object</code>, or <code>null</code> if this node has
   * no value.
   */
  public Object getValue(NamePart name);

  /**
   * Internally removes the value that this node holds
   * and returns it.
   *
   * @return an <code>Object</code>, or <code>null</code> if this node has
   * no value.
   */
  public Object removeValue(NamePart name);

  /**
   * Put a value into this node; overwrites the existing value - if any.
   *
   * @param name the name under which to bind the given value.
   * @param value an <code>Object</code>.
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
   * @return an <code>Iterator</code> of <code>NamePart</code>s.
   */
  public Iterator getValueNames();

  /**
   * Returns the number of values that this instance contains.
   *
   * @return the number of values that this instance contains.
   */
  public int getValueCount();

  /**
   * Sets this node's name and parent node. Client applications <b>must not</b>
   * use this method - unless they know what they are doing.
   *
   * @param parent this instance's parent <code>Node</code>
   * @param nodeName a <code>NamePart</code>.
   */
  public void setUp(Node parent, NamePart nodeName);

  /**
   * Returns the full path to this node, starting from the root.
   *
   * @return a <code>Name</code>.
   */
  public Name getAbsolutePath();

  /**
   * Returns this instance's parent.
   *
   * @return a <code>Node</code>.
   */
  public Node getParent();

  /**
   * Return this instance's name.
   *
   * @return a <code>NamePart</code>.
   */
  public NamePart getName();

  /**
   * Creates the node corresponding to the given name and returns it.
   *
   * @param name a <code>NamePart</code>
   * @return a <code>Node</code>.
   * @throws DuplicateException if a node exists for the given name.
   */
  public Node createChild(NamePart name)
                   throws DuplicateException, ProcessingException;

  /**
   * Returns the node with the given name.
   *
   * @param name a <code>NamePart</code> corresponding to the name of an
   * existing child node.
   *
   * @return a <code>Node</code> or <code>null</code> if not child exists
   * for the given name.
   */
  public Node getChild(NamePart name);

  /**
   * Removes the node with the given name.
   *
   * @param name a <code>NamePart</code> corresponding to the name of an
   * existing child node.
   *
   * @return a <code>Node</code> or <code>null</code> if not child exists
   * for the given name.
   */
  public Node removeChild(NamePart name);

  /**
   * Returns this instance's child nodes.
   *
   * @return a <code>Iterator</code> of <code>Node</code>s.
   */
  public Iterator getChildren();

  /**
   * Returns the number of children that this instance contains.
   *
   * @return the number of children that this instance contains.
   */
  public int getChildrenCount();

  /**
   * @return an <code>Iterator</code> of <code>Entry</code> instances,
   * corresponding to the bindings that this instance holds.
   */
  public Iterator getEntries();

  /**
   * Returns the names of this instance's nodes.
   *
   * @return an <code>Iterator</code> of <code>NamePart</code>s.
   */
  public Iterator getChildrenNames();

  /**
   * Returns this implementation's name parser.
   *
   * @return a <code>NameParser</code>.
   */
  public NameParser getNameParser();

  /**
   * Traverses this instance starting from the root - notifies the given
   * visitor as nodes are encountered.
   *
   * @param visitor a <code>NodeVisitor</code> to notify.
   * @return <code>false</code> if traversal should be aborted.
   */
  public boolean accept(NodeVisitor visitor);
}
