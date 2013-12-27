package org.sapia.util.calc;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * 
 * @author Jean-Cedric Desrochers
 */
public class TimeAverageCalculatorTest extends TestCase {

  // Fixtures
  private TimeAverageCalculator _calculator;
  
  public static void main(String[] args) {
    TestRunner.run(TimeAverageCalculatorTest.class);
  }
  
  public TimeAverageCalculatorTest(String aName) {
    super(aName);
  }
  
  public void setUp() {
    _calculator = new TimeAverageCalculator(1000);
  }
  
  public void testAverage_Empty() throws Exception {
    assertTimeAverageCalculator(0.0, 0.0, 0.0, 0.0, _calculator);
  }
  
  public void testAverage_addOneValue() throws Exception {
    _calculator.addValue(5);
    assertTimeAverageCalculator(5.0, 5.0, 5.0, 5.0, _calculator);
    assertTimeAverageCalculator(5.0, 5.0, 5.0, 5.0, _calculator);
  }
  
  public void testAverage_addManyValues() throws Exception {
    _calculator.addValue(1);
    _calculator.addValue(2);
    _calculator.addValue(3);
    _calculator.addValue(4);
    _calculator.addValue(5);
    _calculator.addValue(6);
    _calculator.addValue(7);
    _calculator.addValue(8);
    _calculator.addValue(9);
    _calculator.addValue(10);
    assertTimeAverageCalculator(5.5, 55.0, 10.0, 1.0, _calculator);
    assertTimeAverageCalculator(5.5, 55.0, 10.0, 1.0, _calculator);
  }
  
  public void testDeletion() throws Exception {
    testAverage_addManyValues();
    Thread.sleep(1100);
    assertTimeAverageCalculator(0.0, 0.0, 0.0, 0.0, _calculator);
  }
  
  public void testNewValuesAfterDeletion() throws Exception {
    testAverage_addManyValues();
    Thread.sleep(1100);
    assertTimeAverageCalculator(0.0, 0.0, 0.0, 0.0, _calculator);
    testAverage_addManyValues();
  }
  
  public static void assertTimeAverageCalculator(double eAverageLastMin,
          double eSum, double eMax, double eMin, TimeAverageCalculator actual) {
    assertNotNull("The time average calculator passed in should not be null", actual);
    assertEquals("The time period average is not valid", eAverageLastMin, actual.getPeriodAverage(), 0.01);
    assertEquals("The time period sum is not valid", eSum, actual.getPeriodSum(), 0.01);
    assertEquals("The time period max is not valid", eMax, actual.getPeriodMaximum(), 0.01);
    assertEquals("The time period min is not valid", eMin, actual.getPeriodMinimum(), 0.01);
  }
}
