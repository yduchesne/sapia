package org.sapia.dataset.plot.gral;

import org.sapia.dataset.plot.HistogramPlot;
import org.sapia.dataset.plot.LinePlot;
import org.sapia.dataset.plot.PiePlot;
import org.sapia.dataset.plot.PlotProvider;
import org.sapia.dataset.plot.XYPlot;

/**
 * A {@link PlotProvider} implemented on top of the Gral framework.
 * 
 * @author yduchesne
 *
 */
public class GralPlotProvider implements PlotProvider {
  
  @Override
  public XYPlot newXYPlot() {
    return new GralXYPlotAdapter();
  }
  
  @Override
  public LinePlot newLinePlot() {
    return new GralLinePlotAdapter();
  }

  @Override
  public HistogramPlot newHistogram() {
    return new GralHistogramPlotAdapter();
  }
  
  @Override
  public PiePlot newPiePlot() {
    return new GralPiePlot();
  }
}
