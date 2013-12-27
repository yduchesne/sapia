package org.sapia.util.calc;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * 
 * @author Jean-Cedric Desrochers
 */
public class TimeAverageCalculator {

  /** Defines the value in milliseconds of 5 minutes. */
  public static final long FIVE_MINUTES_MILLIS = 5 * 60 * 1000;

  /** Defines the value in milliseconds of 1 minute. */
  public static final long ONE_MINUTE_MILLIS = 60 * 1000;

  /** Defines the value in milliseconds of 2 seconds. */
  public static final long TWO_SECONDS_MILLIS = 2 * 1000;

  /** The time period in millis of this calculator. */ 
  private long _timePeriodMillis;
  
  /** The list of entries of this calculator. */
  private LinkedList _entries;
  
  /**
   * Creates a new TimeAverageCalculator instance for a period of one minute.
   *
   */
  public TimeAverageCalculator() {
    this(ONE_MINUTE_MILLIS);
  }
  
  /**
   * Creates a new TimeAverageCalculator instance.
   * 
   * @param aTimePeriodMillis The period of time kept by this calculator.
   */
  public TimeAverageCalculator(long aTimePeriodMillis) {
    _timePeriodMillis = aTimePeriodMillis;
    _entries = new LinkedList();
  }
  
  /**
   * Adds the int value passed in to this calculator.
   *  
   * @param aValue The int value to add.
   */
  public void addValue(int aValue) {
    Entry entry = new Entry((double) aValue);
    addEntry(entry);
  }
  
  /**
   * Adds the double value passed in to this calculator.
   *  
   * @param aValue The double value to add.
   */
  public void addValue(double aValue) {
    Entry entry = new Entry(aValue);
    addEntry(entry);
  }
  
  /**
   * Internal method that adds the entry passed in to the internal list.
   * 
   * @param anEntry The entry to add.
   */
  protected void addEntry(Entry anEntry) {
    synchronized (_entries) {
      _entries.addLast(anEntry);
      trimEntries();
    }
  }

  /**
   * Internal method that removes all the entries at the begining of the list that have
   * a creation date greater than 1 minute ago. EVERY CALLER OF THIS METHOD SHOULD 
   * HAVE THE LOCK ON THE LIST OF ENTRIES TO ENSURE INTEGRITY.
   * 
   */
  protected void trimEntries() {
    long now = System.currentTimeMillis();
    boolean isTrimDone = false;

    while (!isTrimDone) {
      Entry entry = (Entry) _entries.getFirst();
      if ((entry.creationTimestamp + _timePeriodMillis) < now) {
        _entries.removeFirst();
      } else {
        isTrimDone = true;
      }
    }
  }
  
  /**
   * Internal method that calculates the statistics over the time perdiod of this calculator.
   *
   */
  protected PeriodStatistic calculatePeriodStatistics() {
    synchronized (_entries) {
      long now = System.currentTimeMillis();
      PeriodStatistic stats = new PeriodStatistic();

      boolean isComputationDone = false;
      for (Iterator it = _entries.iterator(); it.hasNext() && !isComputationDone; ) {
        Entry entry = (Entry) it.next();
        if ((entry.creationTimestamp + _timePeriodMillis) >= now) {
          stats.sum += entry.value;
          if (stats.count == 0) {
            stats.max = entry.value;
            stats.min = entry.value;
          } else {
            stats.max = Math.max(stats.max, entry.value);
            stats.min = Math.min(stats.min, entry.value);
          }
          stats.count++;
        } else {
          isComputationDone = true;
        }
      }
      
      return stats;
    }
  }

  /**
   * Returns the calculated average for the period of time of this calculator.
   * 
   * @return The calculated average for the period of time of this calculator.
   */
  public double getPeriodAverage() {
    return calculatePeriodStatistics().average();
  }
  
  /**
   * Returns the sum of the values for the period of time of this calculator.
   * 
   * @return The sum of the values for the period of time of this calculator.
   */
  public double getPeriodSum() {
    return calculatePeriodStatistics().sum;
  }
  
  /**
   * Returns the maximum value for the period of time of this calculator.
   * 
   * @return The maximum value for the period of time of this calculator.
   */
  public double getPeriodMaximum() {
    return calculatePeriodStatistics().max;
  }
  
  /**
   * Returns the minimum value for the period of time of this calculator.
   * 
   * @return The minimum value for the period of time of this calculator.
   */
  public double getPeriodMinimum() {
    return calculatePeriodStatistics().min;
  }

  /**
   * Inner class that definesan entry of the internal list of the calculator.
   * 
   * @author Jean-Cedric Desrochers
   */
  public static class Entry {
    protected long creationTimestamp;
    protected double value;
    protected Entry(double aValue) {
      creationTimestamp = System.currentTimeMillis();
      value = aValue;
    }
  }
  
  public class PeriodStatistic {
    protected double min;
    protected double max;
    protected double count;
    protected double sum;
    protected double average() {
      if (count > 0) {
        return sum / count;
      } else {
        return 0.0;
      }
    }
  }
}
