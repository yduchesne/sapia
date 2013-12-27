package org.sapia.archie.sync;

import org.sapia.archie.Name;
import org.sapia.archie.NamePart;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface Synchronizer {
  
  /**
   * Called by a <code>SynchronizedNode</code> when a value could not be found
   * for a given name. This method should return the object that corresponds
   * to the given name, or <code>null</code> if it cannot find an object for
   * the given name.
   * 
   * @param nodeAbsolutePath the <code>Name</code> corresponding to the absolute path of the node that
   * could not find a value for the given name.
   * 
   * @param valueName the <code>NamePart</code> corresponding to the name of the searched object.
   * @return the <code>Object</code> corresponding to the given name, or <code>null</code> if
   * no object could be found for the given name.
   * 
   * @see org.sapia.archie.Node#getValue(NamePart)
   * 
   */
  public Object onGetValue(Name nodeAbsolutePath, NamePart valueName);
  
  /**
   * Called when the <code>putValue()</code> method has been called on the
   * node that owns this instance.
   * 
   * @param nodeAbsolutePath the <code>Name</code> corresponding to the absolute path of the node 
   * in which a value was put.
   * 
   * @param valueName the <code>NamePart</code> corresponding to the name of object that was put
   * into the node.
   * 
   * @param obj the <code>Object</code> that was put into the node.
   * 
   * @see org.sapia.archie.Node#putValue(NamePart, Object, boolean)
   */
  public void onPutValue(Name nodeAbsolutePath, NamePart valueName, Object obj, boolean overwrite);
  
  /**
   * Called when the <code>removeValue()</code> method has been called on the
   * node that owns this instance.
   * 
   * @param nodeAbsolutePath the <code>Name</code> corresponding to the absolute path of the node 
   * from which a value was removed.
   * 
   * @param valueName the <code>NamePart</code> corresponding to the name of object that was removed
   * from the node.
   * 
   * @see org.sapia.archie.Node#removeValue(NamePart)
   */
  public void onRemoveValue(Name nodeAbsolutePath, NamePart valueName);
}
