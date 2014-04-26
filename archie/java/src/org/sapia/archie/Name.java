package org.sapia.archie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This interface provides an object reprentation of a "name". A {@link Name} is composed
 * of potentially multiple {@link NamePart}s.
 * <p>
 * An instance of this class is typically obtained from a string representation, using a
 * {@link NameParser}.
 * 
 * @see org.sapia.archie.NamePart
 * @see org.sapia.archie.NameParser
 * 
 * @author Yanick Duchesne
 */
public class Name implements Serializable{
  
  static final long serialVersionUID = 1L;
  
  private List<NamePart> _parts        = new ArrayList<>();
  private int  _currentIndex;

  protected Name(List<NamePart> parts) {
    _parts.addAll(parts);
  }
  
  public Name() {
  }

  /**
   * Returns the number of {@link NamePart}s in this name.
   *
   * @return a count.
   */
  public int count() {
    return _parts.size();
  }

  /**
   * Returns the internal iteration index.
   *
   * @return an index.
   */
  public int getCurrentIndex() {
    return _currentIndex;
  }

  /**
   * Sets the internal iteration index.
   *
   * @param i an index.
   */
  public void setCurrentIndex(int i) {
    if ((i < 0) || (i > (_parts.size() - 1))) {
      throw new IllegalArgumentException("Specified index exceeds bounds");
    }

    _currentIndex = i;
  }

  /**
   * Resets the internal iteration index.
   */
  public void reset() {
    _currentIndex = 0;
  }

  /**
   * Returns <code>true</code> if this instance has another
   * part to iterate on.
   *
   * @see #reset()
   * @see #getCurrentIndex()
   * @see #nextPart()
   */
  public boolean hasNextPart() {
    boolean hasNext = _currentIndex < _parts.size();

    if (!hasNext) {
      reset();
    }

    return hasNext;
  }

  /**
   * Iterates on the next {@link NamePart}.
   *
   * @see #hasNextPart()
   */
  public NamePart nextPart() {
    return (NamePart) _parts.get(_currentIndex++);
  }

  /**
   * Returns the {@link NamePart} at the given index.
   *
   * @return a {@link NamePart}.
   */
  public NamePart get(int i) {
    return (NamePart) _parts.get(i);
  }

  /**
   * Adds a {@link NamePart} to this instance.
   * 
   * @param part a {@link NamePart}
   * @return this instance (to allow chained invocations).
   */
  public Name add(NamePart part) {
    _parts.add(part);

    return this;
  }
  
  /**
   * @param pos the index at which to add the given {@link NamePart}.
   * @param part a {@link NamePart} to add to this instance.
   * @return this instance.
   */
  public Name addAt(int pos, NamePart part){
    assertPos(pos);
    _parts.add(pos, part);
    return this;
  }
  
  /**
   * @param pos the index of the part to remove.
   * @return the {@link NamePart} that was removed.
   */
  public NamePart removeAt(int pos){
    return (NamePart)_parts.remove(pos);
  }
  
  /**
   * @param other another {@link Name}.
   * @return <code>true</code> if this instance ends with the given {@link Name}.
   */
  public boolean endsWith(Name other){
    if(count() < other.count()) return false;
    for(int i = other.count() - 1; i > 0; i--){
      if(!((NamePart)other.get(i)).asString().equals(get(i).asString())){
        return false;
      }
    }
    return true;
  }
  
  /**
   * @param other another {@link Name}.
   * @return <code>true</code> if this instance starts with the given {@link Name}.
   */
  public boolean startsWith(Name other){
    if(count() < other.count()) return false;
    for(int i = 0; i < other.count(); i++){
      if(!((NamePart)other.get(i)).asString().equals(get(i).asString())){
        return false;
      }
    }
    return true;
  }  

  /**
   * @param n adds the given {@link Name} to this instance (actually
   * appends the {@link NamePart}s of the given name to this instance).
   * 
   * @return this instance.
   */
  public Name add(Name n) {
    _parts.addAll(n._parts);

    return this;
  }

  /**
   * Returns all the {@link NamePart}s that this name holds, up to the
   * given index (exclusively). Returns the {@link NamePart}s in a {@link Name}
   * instance.
   * 
   * @param to an upperbound index.
   * @return a {@link Name}.
   */
  public Name getTo(int to) {
    Name     n       = new Name();
    NamePart current;

    for (int i = 0; i < to; i++) {
      current = (NamePart) _parts.get(i);
      n._parts.add(current);
    }

    return n;
  }
  
  /**
   * Returns all the {@link NamePart}s that this name holds, starting from the
   * given index (inclusively). Returns the {@link NamePart}s in a {@link Name}
   * instance.
   * 
   * @param from a lowerbound index.
   * @return a {@link Name}.
   */

  public Name getFrom(int from) {
    Name     n       = new Name();
    NamePart current;

    for (int i = from; i < _parts.size(); i++) {
      current = (NamePart) _parts.get(i);
      n._parts.add(current);
    }

    return n;
  }

  /**
   * Returns the first part in this name.
   *
   * @return a {@link NamePart}.
   */
  public NamePart first() {
    return (NamePart) _parts.get(0);
  }

  /**
   * Returns the last part in this name.
   *
   * @return a {@link NamePart}.
   */
  public NamePart last() {
    return (NamePart) _parts.get(_parts.size() - 1);
  }

  /**
   * Chops the last part from this instance and returns it.
   *
   * @return a {@link NamePart}.
   */
  public NamePart chopLast() {
    return (NamePart) _parts.remove(_parts.size() - 1);
  }

  /**
   * Chops the first part from this instance and returns it.
   *
   * @return a {@link NamePart}.
   */
  public NamePart chopFirst() {
    return (NamePart) _parts.remove(0);
  }

  public Object clone() {
    return new Name(_parts);
  }

  /**
   * @return the string representation of this instance.
   */
  public String toString() {
    return _parts.toString();
  }
  
  public boolean equals(Object o){
    try{
      Name other = (Name)o;
      if(other.count() != count()){
        return false;
      }
      for(int i = 0; i < other.count(); i++){
        if(!other.get(i).asString().equals(get(i).asString())){
          return false;
        }
      }
      return true;
    }catch(ClassCastException e){
      return false;
    }
  }
  
  private void assertPos(int pos){
    if(pos < 0){
      throw new ArrayIndexOutOfBoundsException("Index too small: " + pos);
    }
    if(pos > _parts.size()){
      throw new ArrayIndexOutOfBoundsException("Index too large: " + pos);
    }
  }
}
