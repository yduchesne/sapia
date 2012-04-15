package org.sapia.ubik.rmi.server.stats;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sapia.ubik.rmi.server.stats.Stats;
import org.sapia.ubik.rmi.server.stats.Timer;

public class StatsTest {
  
  

  @Before
  public void setUp() throws Exception {
    Stats.getInstance().enable();
  }
  
  @After
  public void tearDown() throws Exception {
    Stats.getInstance().clear();
  }

  @Test
  public void testClear() {
    Stats.getInstance().createTimer(getClass(), "Test", "test timer");
    assertEquals(1, Stats.getInstance().getStatistics().size());
    Stats.getInstance().clear();
    assertEquals(0, Stats.getInstance().getStatistics().size());

  }

  @Test
  public void testDisableEnable() {
    Timer timer = Stats.getInstance().createTimer(getClass(), "Test", "test timer");
    assertTrue(timer.isEnabled());
    
    Stats.getInstance().disable();
    assertFalse(timer.isEnabled());
    assertFalse("Stats should not be enabled", Stats.getInstance().isEnabled());
    
    Stats.getInstance().enable();
    assertTrue(timer.isEnabled());
    assertTrue("Stats should be enabled", Stats.getInstance().isEnabled());

  }
  
  @Test
  public void testGetHitsBuilder() {
    Stats.getInstance()
      .getHitsBuilder(getClass(), "HitsPerSecond", "hits per second")
      .perSecond().build();
    Stats.getInstance().getStatisticFor(getClass(), "HitsPerSecond");
  }

  @Test
  public void testGetStatistics() {
    for(int i = 0; i < 5; i++) {
      Stats.getInstance().createTimer(getClass(), "Timer"+i, "timer");
      Stats.getInstance().getHitsBuilder(getClass(), "HitsPerSecond" + i, "hit per sec").build();
    }
    assertEquals(10, Stats.getInstance().getStatistics().size());
  }

}