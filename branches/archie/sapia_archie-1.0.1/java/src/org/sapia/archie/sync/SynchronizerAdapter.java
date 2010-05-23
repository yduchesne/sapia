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
public class SynchronizerAdapter implements Synchronizer{
  
  /**
   * @see org.sapia.archie.sync.Synchronizer#onGetValue(org.sapia.archie.Name, org.sapia.archie.NamePart)
   */
  public Object onGetValue(Name nodeAbsolutePath, NamePart valueName) {
    return null;
  }
  /**
   * @see org.sapia.archie.sync.Synchronizer#onPutValue(org.sapia.archie.Name, org.sapia.archie.NamePart, java.lang.Object, boolean)
   */
  public void onPutValue(Name nodeAbsolutePath, NamePart valueName, Object obj,
      boolean overwrite) {
  }
  /**
   * @see org.sapia.archie.sync.Synchronizer#onRemoveValue(org.sapia.archie.Name, org.sapia.archie.NamePart)
   */
  public void onRemoveValue(Name nodeAbsolutePath, NamePart valueName) {
  }
}
