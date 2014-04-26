package org.sapia.archie;

import java.util.Iterator;
import java.util.Map;

import org.sapia.archie.impl.DefaultNameParser;


/**
 * Abstract implementation of the {@link Node} interface.
 *
 * @author Yanick Duchesne
 */
public abstract class AbstractNode implements Node {
  protected Map<NamePart, Node> _children;
  protected NodeFactory _fac;
  private NamePart      _name;
  private Name          _absolutePath = new Name();
  private Node          _parent;
  private NameParser    _parser;

  protected AbstractNode(NameParser parser, Map<NamePart, Node> children, NodeFactory fac)
                  throws ProcessingException {
    _children = children;
    _fac      = fac;
    _parser   = parser;

    // TODO: this is a hack... Maybe name should be passed in ctor
    _name     = parser.parse("/").first();
    _absolutePath.add(_name);
  }

  protected AbstractNode(Map<NamePart, Node> children, NodeFactory fac)
                  throws ProcessingException {
    this(new DefaultNameParser(), children, fac);
  }

  /**
   * @see org.sapia.archie.Node#createChild(NamePart)
   */
  @Override
  public Node createChild(NamePart name)
                   throws DuplicateException, ProcessingException {
    if (_children.containsKey(name)) {
      throw new DuplicateException(name.asString());
    }

    Node n = _fac.newNode();
    n.setUp(this, name);
    _children.put(name, n);
    return n;
  }

  /**
   * @see org.sapia.archie.Node#getChild(NamePart)
   */
  @Override
  public Node getChild(NamePart name) {
    return _children.get(name);
  }

  /**
   * @see org.sapia.archie.Node#removeChild(org.sapia.archie.NamePart)
   */
  @Override
  public Node removeChild(NamePart name) {
    return _children.remove(name);
  }

  /**
   * @see org.sapia.archie.Node#getChildren()
   */
  @Override
  public Iterator<Node> getChildren() {
    return _children.values().iterator();
  }

  /**
   * @see org.sapia.archie.Node#getName()
   */
  @Override
  public NamePart getName() {
    return _name;
  }

  /**
   * @see Node#getAbsolutePath()
   */
  @Override
  public Name getAbsolutePath() {
    return (Name)_absolutePath.clone();
  }

  /**
   * @see Node#getChildrenCount()
   */
  @Override
  public int getChildrenCount() {
    return _children.size();
  }

  /**
   * @see Node#getChildrenNames()
   */
  @Override
  public Iterator<NamePart> getChildrenNames() {
    return _children.keySet().iterator();
  }

  /**
   * @see Node#getParent()
   */
  @Override
  public Node getParent() {
    return _parent;
  }

  /**
   * @see Node#getNameParser()
   */
  @Override
  public NameParser getNameParser() {
    return _parser;
  }

  /**
   * @see Node#accept(NodeVisitor).
   */
  @Override
  public boolean accept(NodeVisitor visitor) {
    if (visitor.visit(this)) {
      Iterator<NamePart> childrenNames = _children.keySet().iterator();
      while (childrenNames.hasNext()) {
        Node child = getChild(childrenNames.next());
        if (!child.accept(visitor)) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * @see Node#setUp(Node, NamePart)
   */
  @Override
  public void setUp(Node parent, NamePart name) {
    _parent       = parent;
    _absolutePath = new Name().add(parent.getAbsolutePath());
    _name         = name;
    _absolutePath.add(name);
  }

  @Override
  public String toString() {
    return _name.toString();
  }
}
