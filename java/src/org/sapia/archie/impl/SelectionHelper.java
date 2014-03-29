package org.sapia.archie.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SelectionHelper {
  static final Comparator TIME_COMPARATOR       = new TimeComparator();
  static final Comparator OCCURRENCE_COMPARATOR = new OccurrenceComparator();  
  
  public static Offer selectLeastRecentlyUsed(List offers){
    Collections.sort(offers, TIME_COMPARATOR);
    return ((Offer)offers.get(0)).select();
  }
  
  public static Offer selectLeastUsed(List offers){
    Collections.sort(offers, OCCURRENCE_COMPARATOR);
    return ((Offer)offers.get(0)).select();
  }  
  
  // INNER CLASSES ///////////////////////////////////////////////
  
  static final class TimeComparator implements Comparator{
    
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object comparant, Object comparee) {
      Offer cmpt, cmpee;
      cmpt = (Offer)comparant;
      cmpee = (Offer)comparee;
      if(cmpt.getLastSelectTime() < cmpee.getLastSelectTime()){
        return -1;
      }
      if(cmpt.getLastSelectTime() > cmpee.getLastSelectTime()){
        return 1;
      }
      return 0;
    }  
  }
  
  static final class OccurrenceComparator implements Comparator{
    
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object comparant, Object comparee) {
      Offer cmpt, cmpee;
      cmpt = (Offer)comparant;
      cmpee = (Offer)comparee;      
      if(cmpt.getSelectCount() < cmpee.getSelectCount()){
        return -1;
      }
      if(cmpt.getSelectCount() > cmpee.getSelectCount()){
        return 1;
      }
      return 0;
    }  
  }  
}
