package org.sapia.archie.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.sapia.archie.NamePart;
import org.sapia.archie.NodeFactory;
import org.sapia.archie.ProcessingException;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class AttributeNode extends MultiValueNode{
  
  protected AttributeNode(Map children, Map values, NodeFactory fac)  throws ProcessingException {
    super(new AttributeNameParser(), children, values, fac);
  }
  
  /**
   * @see org.sapia.archie.impl.MultiValueNode#getValue(org.sapia.archie.NamePart)
   */
  public final Object getValue(NamePart name) {
    List values = (List) _valueLists.get(name);

    if (values == null) {
      return null;
    }
    
    // selecting matching offers
    List matching = selectMatchingOffers(name, values);
    
    // if no match, return.
    if(matching.size() == 0){
      return null;
    }
    
    // select an offer among the ones that match.
    Offer selected = (Offer)onSelect(matching);
    
    // if the offer is not valid, remove it from internal list
    // and call this method recursively.
    if(!isValid(selected)){
      Offer other;
      for(int i = 0; i < values.size(); i++){
        other = (Offer)values.get(i);
        if(other.getId().equals(selected.getId())){
          values.remove(i);
          break;
        }
      }
      return getValue(name);
    }
    
    // else, return the object within the offer.
    Object toReturn = null;
    if(selected != null){
      toReturn = onRead(name, selected.getObject());
      selected.select();
    }
    return toReturn;  
  }
  
  /**
   * @see org.sapia.archie.Node#putValue(NamePart, Object, boolean)
   */
  public final boolean putValue(NamePart name, Object value, boolean overwrite) {
    List values = (List) _valueLists.get(name);

    if (values == null) {
      values = new ArrayList();
      _valueLists.put(name, values);
    }

    if (!overwrite && (values.size() != 0)) {
      return false;
    }

    Object toBind = onWrite(name, value);
    if(toBind != null){
      if(name instanceof AttributeNamePart){
        values.add(new Offer(((AttributeNamePart)name).getAttributes(), toBind));
      }
      else{
        values.add(new Offer(new Properties(), toBind));	
      }
    }
    return true;
  }  
  
  /**
   * @see org.sapia.archie.Node#removeValue(org.sapia.archie.NamePart)
   */
  public final Object removeValue(NamePart name) {
    Object val = getValue(name);
    List values = (List) _valueLists.get(name);
    if (values == null) {
      return val;
    }
    // selecting matching offers
    List matching = selectMatchingOffers(name, values);
    
    // if no match, return.
    if(matching.size() == 0){
      return val;
    }
    for(int i = 0; i < matching.size(); i++){
      removeOffer((Offer)matching.get(i), values);
    }
    if(values.size() == 0){
      _valueLists.remove(name);
    }
    return val;
  }  
  
  /**
   * Cannot be overridden.
   * 
   * @see #onSelectOffer(List)
   */
  protected final Object onSelect(List offers){
    return onSelectOffer(offers);
  }

  /**
   * Can be overridden to provide custom selection algorithm.
   * 
   * @param offers the <code>List</code> of <code>Offer</code>s to select from.
   * @return an <code>Offer</code>
   */
  protected Offer onSelectOffer(List offers){
    return SelectionHelper.selectLeastUsed(offers);
  }
  
  /**
   * This method is called after the <code>onSelectOffer()</code> method
   * is called. It can be overriden to provide custom validation behavior.
   * 
   * @param offer the <code>Offer</code> to validate.
   * @return <code>true</code> if the offer is valid.
   * 
   * @see #onSelectOffer(List)
   */
  protected boolean isValid(Offer offer){
    return true;
  }
  
  private void removeOffer(Offer offer , List offers){
    for(int i = 0; i < offers.size(); i++){
      if(((Offer)offers.get(i)).getId().equals(offer.getId())){
        offers.remove(i);
        i--;
      }
    }
  }
  
  static List selectMatchingOffers(NamePart name, List offers){
    List matching = new ArrayList();
    Offer offer;
    for(int i = 0; i < offers.size(); i++){
      offer = (Offer)offers.get(i);
      if(name instanceof AttributeNamePart){
        if(offer.matches(((AttributeNamePart)name).getAttributes())){
          matching.add(offer);
        }        
      }
      else{
        matching.add(offer);
      }
    }
    return matching;
  }
  
  
  
}
