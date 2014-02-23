package org.sapia.dataset.plot;

import java.awt.Color;
import java.awt.Insets;
import java.util.List;
import java.util.Map;

import org.sapia.dataset.Dataset;
import org.sapia.dataset.help.Doc;
import org.sapia.dataset.help.Example;
import org.sapia.dataset.help.SettingsDoc;
import org.sapia.dataset.plot.PlotStyle.Legend;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.util.Settings;
import org.sapia.dataset.util.Strings;

/**
 * Holds factory methods to generate {@link Plots}. 
 * 
 * @author yduchesne
 *
 */
@Doc("Holds factory methods to create plots")
public class Plots {
  
  private static final Settings COMMON_SETTINGS = Settings.obj()
    .setting()
      .name("title")
      .type(String.class)
      .description("plot title")
    .setting()
      .name("insets_top")
      .type(Integer.class)
      .description("top inset value")
    .setting()
      .name("insets_left")
      .type(Integer.class)
      .description("left inset value")
    .setting()
      .name("insets_bottom")
      .type(Integer.class)
      .description("bottom inset value")
    .setting()
      .name("insets_right")
      .type(Integer.class)
      .description("right inset value")
    .setting()
      .name("legend_halign")
      .type(String.class)
      .description("specifies legend alignment along the Y-axis (can be: 'top', 'middle', 'bottom')")
    .setting()
      .name("legend_valign")
      .type(String.class)
      .description("specifies legend alignment along the X-axis (can be: 'left', 'center', 'middle')")
    .finish();
    
  private static final Settings XYPLOT_SETTINGS = Settings.obj(COMMON_SETTINGS)
    .setting()
      .name("x")
      .type(String.class)
      .mandatory()
      .description("name of the column used for the X-axis")
    .setting()
      .name("y")
      .type(String[].class)
      .mandatory()
      .description("array of names of the columns used for the Y-axis")
    .setting()
      .name("label_x")
      .type(String.class)
      .description("text used for the X-axis label")
    .setting()
      .name("label_y")
      .type(String.class)
      .description("text used for the Y-axis label")
    .finish();
  
  private static final Settings LINEPLOT_SETTINGS = Settings.obj(XYPLOT_SETTINGS);
  
  private static final Settings PIEPLOT_SETTINGS = Settings.obj(COMMON_SETTINGS)
    .setting()
      .name("column")
      .type(String.class)
      .mandatory()
      .description("name of the column whose values will be used for the plot")
    .setting()
      .name("gradient_start")
      .type(List.class)
      .description("a list holding 3 rgb color values for the gradient start color")
    .setting()
      .name("gradient_end")
      .type(List.class)
      .description("a list holding 3 rgb color values for the gradient end color")
    .finish();

  
  private static final Settings HISTOGRAM_SETTINGS = Settings.obj(COMMON_SETTINGS)
    .setting()
      .name("column")
      .type(String.class)
      .mandatory()
      .description("name of the column whose frequencies will be computed")
    .setting()
      .name("ticks_spacing_x")
      .type(Double.class)
      .description("spacing to use for X-axis ticks (defaults to 1)")
    .setting()
      .name("ticks_spacing_y")
      .type(Double.class)
      .description("spacing to use for Y-axis ticks (defaults to 1)")
    .finish();


  private Plots() {
  }
  
  // --------------------------------------------------------------------------
  // Factory methods
  
  /**
   * @param dataset a {@link Dataset}.
   * @param x_axis_columnName the name of the dataset column used for the X-axis values.
   * @param y_axis_columnNames the names of the dataset columns used for the Y-axis values.
   * @return a new {@link XYPlot}.
   */
  @Doc("Creates a XYPlot, given a dataset column names indicating which columns to use for the X/Y axis")
  public static final XYPlot xyplot(
      @Doc("dataset to plot") Dataset dataset, 
      @Doc("name of the column to use for X axis values") String x_axis_columnName, 
      @Doc("names of the columns to use for Y axis values") String...y_axis_columnNames) {
    XYPlot plot = PlotProviderFactory.getProvider().newXYPlot();
    plot.setDataset(dataset);
    plot.setX(x_axis_columnName);
    plot.setY(y_axis_columnNames);
    return plot;
  }
  
  /**
   * @param dataset a {@link Dataset}.
   * @param config a {@link Map} holding the setting values for the {@link XYPlot}.
   * @return a new {@link XYPlot}.
   */
  @Doc(
      value = "Creates a XYPlot, given a dataset and plot settings",
      examples =  {
          @Example(caption="Basic usage - x and y axis columns", content="xyplot(dataset, settings(\"x\", \"car_cyl\", \"y\", array(\"car_mpg\")))"),
          @Example(caption="With title and axis labels", content="xyplot(dataset, settings(\"x\", \"car_cyl\", \"y\", array(\"car_mpg\")" + 
                    ", \"label_x\", \"CYL\", \"label_y\", \"MPG\"\")))")
      })
  public static final XYPlot xyplot(
      @Doc("dataset to plot") Dataset dataset, 
      @Doc("plot settings")  @SettingsDoc("XYPLOT_SETTINGS") Map<String, Object> config) {
    
    XYPlot plot = PlotProviderFactory.getProvider().newXYPlot();
    plot.setDataset(dataset);
    setStyle(plot, XYPLOT_SETTINGS, config);
    plot.setX(XYPLOT_SETTINGS.get("x").get(config, String.class));
    plot.setY(XYPLOT_SETTINGS.get("y").get(config, String[].class));
    plot.setLabelX(XYPLOT_SETTINGS.get("label_x").get(config, Strings.BLANK, String.class));
    plot.setLabelY(XYPLOT_SETTINGS.get("label_y").get(config, Strings.BLANK, String.class));
    return plot;
  }
  
  /**
   * @param dataset a {@link Dataset}.
   * @param config a {@link Map} holding the setting values for the {@link LinePlot}.
   * @return a new {@link LinePlot}.
   */
  @Doc(
      value = "Creates a LinePlot, given a dataset and plot settings",
      examples =  {
          @Example(caption="Basic usage - x and y axis columns", content="lineplot(dataset, settings(\"x\", \"car_cyl\", \"y\", array(\"car_mpg\")))"),
          @Example(caption="With title and axis labels", content="lineplot(dataset, settings(\"x\", \"car_cyl\", \"y\", array(\"car_mpg\")" + 
                    ", \"label_x\", \"CYL\", \"label_y\", \"MPG\"\")))")
      })
  public static final LinePlot lineplot(
      @Doc("dataset to plot") Dataset dataset, 
      @Doc("plot settings")  @SettingsDoc("LINEPLOT_SETTINGS") Map<String, Object> config) {
    
    LinePlot plot = PlotProviderFactory.getProvider().newLinePlot();
    plot.setDataset(dataset);
    setStyle(plot, LINEPLOT_SETTINGS, config);
    plot.setX(LINEPLOT_SETTINGS.get("x").get(config, String.class));
    plot.setY(LINEPLOT_SETTINGS.get("y").get(config, String[].class));
    plot.setLabelX(LINEPLOT_SETTINGS.get("label_x").get(config, Strings.BLANK, String.class));
    plot.setLabelY(LINEPLOT_SETTINGS.get("label_y").get(config, Strings.BLANK, String.class));
    return plot;
  }
  
  public static final HistogramPlot histogram(       
      @Doc("dataset to plot") Dataset dataset, 
      @Doc("plot settings")  @SettingsDoc("HISTOGRAM_SETTINGS") Map<String, Object> config) {
    
    HistogramPlot plot = PlotProviderFactory.getProvider().newHistogram();
    plot.setDataset(dataset);
    plot.setColumn(HISTOGRAM_SETTINGS.get("column").get(config, String.class));
    plot.setTicksSpacingX(HISTOGRAM_SETTINGS.get("ticks_spacing_x").get(config, 1, Integer.class));
    plot.setTicksSpacingY(HISTOGRAM_SETTINGS.get("ticks_spacing_y").get(config, 1, Integer.class));
    setStyle(plot, HISTOGRAM_SETTINGS, config);
    return plot;
  }
  
  @SuppressWarnings("unchecked")
  public static final PiePlot pieplot(
      @Doc("dataset to plot") Dataset dataset, 
      @Doc("plot settings")  @SettingsDoc("HISTOGRAM_SETTINGS") Map<String, Object> config) {
    
    PiePlot plot = PlotProviderFactory.getProvider().newPiePlot();
    plot.setDataset(dataset);
    plot.setColumn(PIEPLOT_SETTINGS.get("column").get(config, String.class));
    if (config.containsKey("gradient_start")) {
      List<Integer> rgb = PIEPLOT_SETTINGS.get("gradient_start").get(config, List.class);
      Checks.isTrue(rgb.size() == 3, "Gradient color must have 3 RGB components, got: %s", rgb);
      Color c = new Color(rgb.get(0), rgb.get(1), rgb.get(2));
      plot.setGradientStart(c);
    }
    if (config.containsKey("gradient_end")) {
      List<Integer> rgb = PIEPLOT_SETTINGS.get("gradient_end").get(config, List.class);
      Checks.isTrue(rgb.size() == 3, "Gradient color must have 3 RGB components, got: %s", rgb);
      Color c = new Color(rgb.get(0), rgb.get(1), rgb.get(2));
      plot.setGradientEnd(c);
    }
     
    setStyle(plot, HISTOGRAM_SETTINGS, config);
    return plot;
  }
  
  
  private static final void setStyle(Plot plot, Settings settings, Map<String, Object> config) {
    PlotStyle style = PlotStyle.obj();
    style.title(settings.get("title").get(config, Strings.BLANK, String.class));
    style.getLegend().halign(settings.get("legend_halign").getConstant(
        config, Legend.HorizontalAlignment.TOP, Legend.HorizontalAlignment.class));
    style.getLegend().valign(settings.get("legend_valign").getConstant(
        config, Legend.VerticalAlignment.LEFT, Legend.VerticalAlignment.class));
  
    String[] criteria = Data.array("insets_top", "insets_left", "insets_bottom", "insets_right");
    if (Data.containsAny(criteria, config.keySet())) {
      Insets insets = new Insets(
          settings.get("insets_top").get(config, 0, Integer.class), 
          settings.get("insets_left").get(config, 0, Integer.class), 
          settings.get("insets_bottom").get(config, 0, Integer.class), 
          settings.get("insets_right").get(config, 0, Integer.class) 
      );
      style.insets(insets);
    }
    plot.setStyle(style);
    
  }
}
