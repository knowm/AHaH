/**
q * Copyright (c) 2013 M. Alexander Nugent Consulting <i@alexnugent.name>
 *
 * M. Alexander Nugent Consulting Research License Agreement
 * Non-Commercial Academic Use Only
 *
 * This Software is proprietary. By installing, copying, or otherwise using this
 * Software, you agree to be bound by the terms of this license. If you do not agree,
 * do not install, copy, or use the Software. The Software is protected by copyright
 * and other intellectual property laws.
 *
 * You may use the Software for non-commercial academic purpose, subject to the following
 * restrictions. You may copy and use the Software for peer-review and methods verification
 * only. You may not create derivative works of the Software. You may not use or distribute
 * the Software or any derivative works in any form for commercial or non-commercial purposes.
 *
 * Violators will be prosecuted to the full extent of the law.
 *
 * All rights reserved. No warranty, explicit or implicit, provided.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRßANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.mancrd.ahah.samples.plosahah.model;

import java.util.Map;

import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.CSVImporter;
import com.xeiam.xchart.CSVImporter.DataOrientation;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesColor;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.StyleManager.LegendPosition;
import com.xeiam.xchart.SwingWrapper;

/**
 * @author timmolter
 */
public class AgChalcHysteresisFigureA {

  public static void main(String[] args) throws Exception {

    // import chart from a folder containing CSV files
    Chart chart = CSVImporter.getChartFromCSVDir("./Results/Model/Circuit/AgChalcA", DataOrientation.Columns, 300, 270, ChartTheme.Matlab);
    chart.setYAxisTitle("Current [mA]");
    chart.setXAxisTitle("Voltage [V]");
    chart.getStyleManager().setLegendPosition(LegendPosition.InsideNW);
    chart.getStyleManager().setPlotGridLinesVisible(false);

    Map<String, Series> seriesMap = chart.getSeriesMap();

    Series series0 = seriesMap.get("Device");
    Series series1 = seriesMap.get("100 Hz");
    Series series2 = seriesMap.get("1000 Hz");
    Series series3 = seriesMap.get("10000 Hz");

    // series0 = seriesMap.get(0);
    series0.setLineStyle(SeriesLineStyle.NONE);
    series0.setMarker(SeriesMarker.CIRCLE);
    series0.setMarkerColor(SeriesColor.PINK);

    // series1 = seriesMap.get(1);
    series1.setMarker(SeriesMarker.NONE);
    series1.setLineColor(SeriesColor.BLUE);

    // series2 = seriesMap.get(2);
    series2.setMarker(SeriesMarker.NONE);
    series2.setLineStyle(SeriesLineStyle.DOT_DOT);
    series2.setLineColor(SeriesColor.ORANGE);

    // series3 = seriesMap.get(3);
    series3.setMarker(SeriesMarker.NONE);
    series3.setLineStyle(SeriesLineStyle.DASH_DASH);
    series3.setLineColor(SeriesColor.PURPLE);

    // Show it
    new SwingWrapper(chart).displayChart();
    BitmapEncoder.savePNGWithDPI(chart, "./PLOS_AHAH/Figures/AgChalcHysteresisA.png", 300);
  }
}
