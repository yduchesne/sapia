package org.sapia.archie.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Yanick Duchesne
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
