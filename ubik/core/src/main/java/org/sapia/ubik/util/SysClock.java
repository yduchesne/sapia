package org.sapia.ubik.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstracts system time, convenient for unit testing.
 * 
 * @author yduchesne
 * 
 */
public interface SysClock {

  /**
   * Implements the {@link SysClock} interface over the {@link System} class.
   * 
   * @see System#currentTimeMillis()
   */
  public static class RealtimeClock implements SysClock {

    private static RealtimeClock instance = new RealtimeClock();

    private RealtimeClock() {
    }

    /**
     * Delegates to {@link System#currentTimeMillis()}.
     */
    @Override
    public long currentTimeMillis() {
      return System.currentTimeMillis();
    }

    @Override
    public long nanoTime() {
      return System.nanoTime();
    }

    /**
     * @return the {@link RealtimeClock} singleton.
     */
    public static RealtimeClock getInstance() {
      return instance;
    }

  }

  // ---------------------------------------------------------------------------

  /**
   * A mutable {@link SysClock} class: an instance of this class can have its time
   * explicitely set. Use this class for testing purposes only.
   * 
   */
  public static class MutableClock implements SysClock {

    private AtomicLong currentTime = new AtomicLong();

    public void setCurrentTimeMillis(long currentTime) {
      this.currentTime.set(currentTime);
    }

    public void setCurrentTime(long currentTime, TimeUnit timeunit) {
      this.currentTime.set(TimeUnit.MILLISECONDS.convert(currentTime, timeunit));
    }

    public void increaseCurrentTimeMillis(long amount) {
      currentTime.addAndGet(amount);
    }

    public void decreaseCurrentTimeMillis(long amount) {
      currentTime.addAndGet(-amount);
    }

    @Override
    public long currentTimeMillis() {
      return currentTime.get();
    }

    @Override
    public long nanoTime() {
      return TimeUnit.NANOSECONDS.convert(currentTime.get(), TimeUnit.MILLISECONDS);
    }

    public static MutableClock getInstance() {
      return new MutableClock();
    }
  }

  // ===========================================================================

  /**
   * @return the current time, in millis.
   */
  public long currentTimeMillis();

  /**
   * @return the nano time.
   */
  public long nanoTime();
}
