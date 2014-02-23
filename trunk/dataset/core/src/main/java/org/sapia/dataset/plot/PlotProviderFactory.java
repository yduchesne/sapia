package org.sapia.dataset.plot;

import org.sapia.dataset.plot.gral.GralPlotProvider;

/**
 * This factory returns the currently configured {@link PlotProvider}.
 * 
 * @author yduchesne
 *
 */
public class PlotProviderFactory {

  private static PlotProvider provider = new GralPlotProvider();
 
  private PlotProviderFactory() {
  }
  
  /**
   * @return the currently configured {@link PlotProvider}
   */
  public static PlotProvider getProvider() {
    return provider;
  }
}
