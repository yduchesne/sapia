package org.sapia.dataset.plot;

/**
 * Service provider interface pertaining to the creation of plots.
 * 
 * @author yduchesne
 *
 */
public interface PlotProvider {

  /**
   * @return a new {@link XYPlot} instance.
   */
  public XYPlot newXYPlot();
  
  /**
   * @return a new {@link LinePlot} instance.
   */
  public LinePlot newLinePlot();
  
  /**
   * @return a new {@link HistogramPlot}.
   */
  public HistogramPlot newHistogram();
  
  /**
   * @return a new {@link PiePlot}.
   */
  public PiePlot newPiePlot();
}
