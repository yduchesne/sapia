package org.sapia.dataset.plot.gral;

import static de.erichseifert.gral.plots.XYPlot.AXIS_X;
import static de.erichseifert.gral.plots.XYPlot.AXIS_Y;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import org.sapia.dataset.Dataset;
import org.sapia.dataset.plot.PlotStyle;
import org.sapia.dataset.plot.XYPlot;
import org.sapia.dataset.util.Checks;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.Plot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.legends.Legend;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;

/**
 * Gral-based implementation of the {@link XYPlot} interface. 
 */
public class GralXYPlotAdapter extends JFrame implements XYPlot {
  
  private static final int BASE_COLOR_COMPONENT   = 100;
  private static final int RANDOM_COLOR_COMPONENT = 110;
  
  private static final long serialVersionUID = 1L;

  private   Random    random = new Random(System.nanoTime());
  protected PlotStyle style  = PlotStyle.obj();
  protected Dataset   dataset;
  protected String    x, label_x, label_y;
  protected String[]  y;
  
  @Override
  public void setStyle(PlotStyle style) {
    this.style = style;
  }
  
  @Override
  public void setDataset(Dataset dataset) {
    this.dataset = dataset;
  }
  
  @Override
  public void setX(String columnName) {
    this.x = columnName;
  }
  
  @Override
  public void setY(String... columnNames) {
    this.y = columnNames;
  }
  
  @Override
  public void setLabelX(String label) {
    this.label_x = label;
  }
  
  @Override
  public void setLabelY(String label) {
    this.label_y = label;
  }
  
  @Override
  public void display() {
    Checks.notNull(dataset, "Dataset not set");
    Checks.notNull(x, "X axis column name not set");
    Checks.notNull(y, "Y axis column name(s) not set");
    
    DataSource datasource = new DatasetDataSourceAdapter(dataset);
    List<DataSeries> seriesList = new ArrayList<>(y.length);
    for (int i = 0; i < y.length; i++) {
      DataSeries series = new DataSeries(
          dataset.getColumnSet().get(y[i]).getName(),
          datasource, 
          dataset.getColumnSet().get(x).getIndex(), 
          dataset.getColumnSet().get(y[i]).getIndex() 
      );    
      seriesList.add(series);
    }
 
    de.erichseifert.gral.plots.XYPlot plot = new de.erichseifert.gral.plots.XYPlot(
        seriesList.toArray(new DataSeries[seriesList.size()])
    );

    if (style.getTitle() != null) {
      plot.setSetting(BarPlot.TITLE, style.getTitle());
    }
    
    plot.setInsets(new Insets2D.Double(
        style.getInsets().top, 
        style.getInsets().left, 
        style.getInsets().bottom, 
        style.getInsets().right));
   
    if (label_x != null) {
      plot.getAxisRenderer(AXIS_X).setSetting(AxisRenderer.LABEL, label_x);
    }
    
    if (label_y != null) {
      plot.getAxisRenderer(AXIS_Y).setSetting(AxisRenderer.LABEL, label_y);
    }
    
    plot.getAxisRenderer(AXIS_X).setSetting(AxisRenderer.INTERSECTION, -(Double.MAX_VALUE));
    plot.getAxisRenderer(AXIS_Y).setSetting(AxisRenderer.INTERSECTION, -(Double.MAX_VALUE));
    
    if (style.getLegend().isEnabled()) {
      plot.setSetting(Plot.LEGEND, true);
      switch (style.getLegend().getOrientation()) {
        case HORIZONTAL:
          plot.getLegend().setSetting(Legend.ORIENTATION, Orientation.HORIZONTAL);
          break;
        case VERTICAL:
          plot.getLegend().setSetting(Legend.ORIENTATION, Orientation.VERTICAL);
          break;
        default:
          throw new IllegalArgumentException("Unknown orientation: " + style.getLegend().getOrientation());
      }
      switch (style.getLegend().getHalign()) {
        case BOTTOM:
          plot.getLegend().setSetting(Legend.ALIGNMENT_Y, 1);
          break;
        case MIDDLE:
          plot.getLegend().setSetting(Legend.ALIGNMENT_Y, 0.5);
          break;
        case TOP:
          plot.getLegend().setSetting(Legend.ALIGNMENT_Y, 0);
      }
      
      switch (style.getLegend().getValign()) {
        case LEFT:
          plot.getLegend().setSetting(Legend.ALIGNMENT_X, 0);
          break;
        case CENTER:
          plot.getLegend().setSetting(Legend.ALIGNMENT_X, 0.5);
          break;
        case RIGHT:
          plot.getLegend().setSetting(Legend.ALIGNMENT_X, 1);
      }

    }
        
    customize(plot, datasource, seriesList);
    
    getContentPane().add(new InteractivePanel(plot), BorderLayout.CENTER);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setMinimumSize(getContentPane().getMinimumSize());
    setSize(style.getDimension());
    setVisible(true);
  }
  
  protected void customize(
      de.erichseifert.gral.plots.XYPlot plot, 
      DataSource datasource,
      List<DataSeries> series) {
    Color c = style.getForeground();
    for (DataSeries s : series) {
      PointRenderer point = new DefaultPointRenderer2D();
      plot.setPointRenderer(s, point);
      point.setSetting(DefaultPointRenderer2D.COLOR, c);
      c = randomColor();
    }
  }
  
  protected Color randomColor() {
    int red   = BASE_COLOR_COMPONENT + random.nextInt(RANDOM_COLOR_COMPONENT);
    int green = BASE_COLOR_COMPONENT + random.nextInt(RANDOM_COLOR_COMPONENT);
    int blue  = BASE_COLOR_COMPONENT + random.nextInt(RANDOM_COLOR_COMPONENT);
    return new Color(red, green, blue);
  }
}
