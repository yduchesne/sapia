package org.sapia.dataset.plot.gral;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.sapia.dataset.Column;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.IndexedDataset;
import org.sapia.dataset.RowSet;
import org.sapia.dataset.VectorKey;
import org.sapia.dataset.plot.HistogramPlot;
import org.sapia.dataset.plot.PlotStyle;
import org.sapia.dataset.value.Values;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;

/**
 * Implements the {@link HistogramPlot} interface on top of the Gral {@link BarPlot} class.
 * 
 * @author yduchesne
 *
 */
public class GralHistogramPlotAdapter extends JFrame implements HistogramPlot {
  
  private static final long serialVersionUID = 1L;
  
  private PlotStyle    style;
  private Dataset      dataset;
  private String       classColumn;
  private int          tickSpacingX = 1, tickSpacingY = 1;
  
  @Override
  public void setStyle(PlotStyle style) {
    this.style = style;
  }
  
  @Override
  public void setDataset(Dataset dataset) {
    this.dataset = dataset;
  }
  
  @Override
  public void setColumn(String classColumn) {
    this.classColumn = classColumn;
  }
  
  @Override
  public void setTicksSpacingX(int spacing) {
    this.tickSpacingX = spacing;
  }
  
  @Override
  public void setTicksSpacingY(int spacing) {
    this.tickSpacingY = spacing;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void display() {
    Column col = dataset.getColumnSet().get(classColumn);

    IndexedDataset sortedByClassColumn = dataset.index(classColumn);
    
    DataTable data = new DataTable(Double.class, Integer.class);
    
    for (VectorKey k : sortedByClassColumn.getKeys()) {
      RowSet rowSet = sortedByClassColumn.getRowset(k);
      Object value = k.get(col.getIndex());
      data.add(Values.doubleValue(value), rowSet.size());
    }
    
    BarPlot plot = new BarPlot(data);
    plot.setInsets(
        new Insets2D.Double(
          style.getInsets().top, 
          style.getInsets().left, 
          style.getInsets().bottom, 
          style.getInsets().right
        )
    );
    if (style.getTitle() != null) {
      plot.setSetting(BarPlot.TITLE, style.getTitle());
    }
    plot.setSetting(BarPlot.BAR_WIDTH, 0.5);
    
    // Format x axis
    plot.getAxisRenderer(BarPlot.AXIS_X).setSetting(AxisRenderer.TICKS_ALIGNMENT, 0.0);
    plot.getAxisRenderer(BarPlot.AXIS_X).setSetting(AxisRenderer.TICKS_SPACING, tickSpacingX);
    plot.getAxisRenderer(BarPlot.AXIS_X).setSetting(AxisRenderer.TICKS_MINOR, false);
    
    // Format y axis
    plot.getAxisRenderer(BarPlot.AXIS_Y).setSetting(AxisRenderer.TICKS_ALIGNMENT, 0.0);
    plot.getAxisRenderer(BarPlot.AXIS_Y).setSetting(AxisRenderer.TICKS_SPACING, tickSpacingY);
    plot.getAxisRenderer(BarPlot.AXIS_Y).setSetting(AxisRenderer.TICKS_MINOR, false);
    plot.getAxisRenderer(BarPlot.AXIS_Y).setSetting(AxisRenderer.INTERSECTION, -5.0);
 
    // Format bars
    plot.getPointRenderer(data).setSetting(PointRenderer.COLOR, style.getForeground());
    plot.getPointRenderer(data).setSetting(PointRenderer.VALUE_DISPLAYED, false);
  
    getContentPane().add(new InteractivePanel(plot), BorderLayout.CENTER);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setMinimumSize(getContentPane().getMinimumSize());
    setSize(style.getDimension());
    setVisible(true);
  }
  

}
