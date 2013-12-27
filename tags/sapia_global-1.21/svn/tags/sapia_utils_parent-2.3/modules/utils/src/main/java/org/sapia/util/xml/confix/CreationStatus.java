package org.sapia.util.xml.confix;


/**
 * Models a "creation status". A creation status encapsulates:
 *
 * <ul>
 * <li>The object that is created by a given object factory;
 * <li>A flag that indicates if the created instance was assigned to its
 * parent - through a set/add method.
 * </ul>
 *
 * An instance of this class is not created through a constructor; rather, it
 * is created as such:
 *
 * <pre>
 * CreationStatus stat = CreationStatus.create(someObject);
 * </pre>
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CreationStatus {
  boolean _assigned = false;
  Object  _created;

  private CreationStatus(Object created) {
    _created = created;
  }

  /**
   * Returns the created object.
   *
   * @return an <code>Object</code>.
   */
  public Object getCreated() {
    //		if(_created != null && _created instanceof NullObject){
    //			return null;
    //		}
    return _created;
  }

  /**
   * Sets the object that was created.
   *
   * @param an <code>Object</code>.
   */
  public void setCreated(Object created) {
    _created = created;
  }

  /**
   * Returns <code>true</code> if the encapsulated object was
   * assigned to its parent.
   *
   * @return <code>true</code> if the encapsulated object was
   * assigned to its parent.
   */
  public boolean wasAssigned() {
    return _assigned;
  }

  /**
   * Sets this instance's "assigned" status.
   *
   * @param assigned if <code>true</code>, indicates that the object
   * encapsulated within this instance was assigned to its parent.
   */
  public CreationStatus assigned(boolean assigned) {
    _assigned = assigned;

    return this;
  }

  /**
   * Returns a <code>CreationStatus</code> that encapsulates the given
   * instance.
   *
   * @param created the created object.
   * @return a <code>CreationStatus</code>.
   */
  public static CreationStatus create(Object created) {
    return new CreationStatus(created);
  }
}
