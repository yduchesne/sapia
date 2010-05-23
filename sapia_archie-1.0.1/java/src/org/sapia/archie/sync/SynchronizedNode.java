package org.sapia.archie.sync;

import java.util.Iterator;

import org.sapia.archie.DuplicateException;
import org.sapia.archie.Name;
import org.sapia.archie.NameParser;
import org.sapia.archie.NamePart;
import org.sapia.archie.Node;
import org.sapia.archie.ProcessingException;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SynchronizedNode implements Node{
  
  private Node         _node;
  private Synchronizer _sync = new SynchronizerAdapter();
  
  /**
   * @param node the <code>Node</code> that this instance encapsulates.
   */
  public SynchronizedNode(Node node){
    _node = node;
  }
  
  /**
   * @param sync a <code>Synchronizer</code> instance.
   */
  public void setSynchronizer(Synchronizer sync){
    _sync = sync;
  }
  
  /**
   * @return the <code>Synchronizer</code> that this instance uses.
   */
  public Synchronizer getSynchronizer(){
    return _sync;
  }
  
  /**
   * This method should be called when needing to synchronize the
   * content of this node with a "putValue" that occurred at another
   * node.
   * 
   * @param valueName the <code>NamePart</code> corresponding to the
   * name of the value to synchronize.
   * @param toSync the value to put into this node, under the given name.
   * 
   */
  public void synchronizePut(NamePart valueName, Object toSync, boolean overwrite){
    _node.putValue(valueName, toSync, overwrite);
  }

  /**
   * This method should be called when needing to synchronize the
   * content of this node with a "removeValue" that occurred at another
   * node.
   * 
   * @param valueName the <code>NamePart</code> corresponding to the
   * name of the value to remove.
   * 
   */
  public void synchronizeRemove(NamePart valueName){
    _node.removeValue(valueName);
  }
  
  /**
   * This method should be called when needing to synchronize a lookup that
   * occurred at another node.
   * 
   * @param valueName the <code>NamePart</code> corresponding to the
   * name of the value to look up.
   * 
   * @return the <code>Object</code> corresponding to the given name, or
   * <code>null</code> if no object exists for that name.
   */
  public Object synchronizeGet(NamePart valueName){
    return _node.getValue(valueName);
  }
  
  /**
   * @see org.sapia.archie.Node#createChild(org.sapia.archie.NamePart)
   */
  public Node createChild(NamePart name) throws DuplicateException,
      ProcessingException {
    return _node.createChild(name);
  }
  /**
   * @see org.sapia.archie.Node#getAbsolutePath()
   */
  public Name getAbsolutePath() {
    return _node.getAbsolutePath();
  }
  /**
   * @see org.sapia.archie.Node#getChild(org.sapia.archie.NamePart)
   */
  public Node getChild(NamePart name) {
    return _node.getChild(name);
  }
  /**
   * @see org.sapia.archie.Node#getChildren()
   */
  public Iterator getChildren() {
    return _node.getChildren();
  }
  /**
   * @see org.sapia.archie.Node#getChildrenCount()
   */
  public int getChildrenCount() {
    return _node.getChildrenCount();
  }
  /**
   * @see org.sapia.archie.Node#getChildrenNames()
   */
  public Iterator getChildrenNames() {
    return _node.getChildrenNames();
  }
  /**
   * @see org.sapia.archie.Node#getEntries()
   */
  public Iterator getEntries() {
    return _node.getEntries();
  }
  /**
   * @see org.sapia.archie.Node#getName()
   */
  public NamePart getName() {
    return _node.getName();
  }
  /**
   * @see org.sapia.archie.Node#getNameParser()
   */
  public NameParser getNameParser() {
    return _node.getNameParser();
  }
  /**
   * @see org.sapia.archie.Node#getParent()
   */
  public Node getParent() {
    return _node.getParent();
  }
  /**
   * @see org.sapia.archie.Node#getValue(org.sapia.archie.NamePart)
   */
  public Object getValue(NamePart name) {
    Object toReturn = _node.getValue(name);
    if(toReturn == null){
      toReturn = _sync.onGetValue((Name)getAbsolutePath().clone(), name);
    }
    return toReturn;
  }
  /**
   * @see org.sapia.archie.Node#getValueCount()
   */
  public int getValueCount() {
    return _node.getValueCount();
  }
  /**
   * @see org.sapia.archie.Node#getValueNames()
   */
  public Iterator getValueNames() {
    return _node.getValueNames();
  }
  /**
   * @see org.sapia.archie.Node#putValue(org.sapia.archie.NamePart, java.lang.Object, boolean)
   */
  public boolean putValue(NamePart name, Object value, boolean overwrite) {
    boolean put =  _node.putValue(name, value, overwrite);
    _sync.onPutValue((Name)getAbsolutePath().clone(), name, value, overwrite);
    return put;
  }
  /**
   * @see org.sapia.archie.Node#removeChild(org.sapia.archie.NamePart)
   */
  public Node removeChild(NamePart name) {
    return _node.removeChild(name);
  }
  /**
   * @see org.sapia.archie.Node#removeValue(org.sapia.archie.NamePart)
   */
  public Object removeValue(NamePart name) {
    Object toReturn = _node.removeValue(name);
    _sync.onRemoveValue((Name)getAbsolutePath().clone(), name);
    return toReturn;
  }
  /**
   * @see org.sapia.archie.Node#setUp(org.sapia.archie.Node, org.sapia.archie.NamePart)
   */
  public void setUp(Node parent, NamePart nodeName) {
    _node.setUp(parent, nodeName);
  }
}
