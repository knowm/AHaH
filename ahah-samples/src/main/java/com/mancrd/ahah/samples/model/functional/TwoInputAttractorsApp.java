/**
 * Copyright (c) 2013 M. Alexander Nugent Consulting <i@alexnugent.name>
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
package com.mancrd.ahah.samples.model.functional;

import java.io.IOException;
import java.util.Set;

import com.mancrd.ahah.model.functional.AHaHNode;
import com.mancrd.ahah.model.functional.AHaHNodeBuilder;
import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesColor;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.SwingWrapper;

/**
 * Takes many two-input AHaH nodes and presents the spike patterns 01, 10, and 11. Plots the trajectory of the weights over time for each AHaH node, showing the converges to attractor states.
 * 
 * @author timmolter
 */
public class TwoInputAttractorsApp extends FunctionalModelExperiment {

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    TwoInputAttractorsApp twoInputAttractorsApp = new TwoInputAttractorsApp();
    twoInputAttractorsApp.go();
  }

  public void go() throws IOException {

    int trials = 50; // number of AHaH nodes to simulate
    int timeSteps = 5000; // number of time steps per ahah node

    // Weight Attractor Plot
    Chart chart = new Chart(300, 300, ChartTheme.Matlab);
    chart.setChartTitle("Attractor Basins - Functional");
    chart.setYAxisTitle("W1");
    chart.setXAxisTitle("W0");

    chart.getStyleManager().setLegendVisible(false);
    chart.getStyleManager().setAxisTicksVisible(false);

    double[] startW0 = new double[trials];
    double[] startW1 = new double[trials];

    double[] finalW0 = new double[trials];
    double[] finalW1 = new double[trials];

    for (int trial = 0; trial < trials; trial++) {

      int numBias = 1;

      AHaHNode ahahNode = new AHaHNodeBuilder().numInputs(2).numBiasInputs(numBias).isModelA(isModelA).build();

      double[] w0 = new double[timeSteps];
      double[] w1 = new double[timeSteps];
      double[] y = new double[timeSteps];

      startW0[trial] = ahahNode.getWeight(0);
      startW1[trial] = ahahNode.getWeight(1);

      for (int i = 0; i < timeSteps; i++) {
        Set<Integer> spikePattern = getThreePatternSpikePattern();
        y[i] = ahahNode.update(spikePattern);
        w0[i] = ahahNode.getWeight(0);
        w1[i] = ahahNode.getWeight(1);
      }

      finalW0[trial] = ahahNode.getWeight(0);
      finalW1[trial] = ahahNode.getWeight(1);

      // weight trace
      Series weightTrace = chart.addSeries("W0", w0, w1);
      weightTrace.setMarker(SeriesMarker.NONE);
      weightTrace.setLineColor(SeriesColor.BLUE);

    }

    // final weight value trace
    Series finalValueSeries = chart.addSeries("FinalValue", finalW0, finalW1);
    finalValueSeries.setMarker(SeriesMarker.CIRCLE);
    finalValueSeries.setMarkerColor(SeriesColor.RED);
    finalValueSeries.setLineStyle(SeriesLineStyle.NONE);

    // start weight value trace
    Series startValueSeries = chart.addSeries("StartValue", startW0, startW1);
    startValueSeries.setMarker(SeriesMarker.CIRCLE);
    startValueSeries.setMarkerColor(SeriesColor.GREEN);
    startValueSeries.setLineStyle(SeriesLineStyle.NONE);

    BitmapEncoder.savePNGWithDPI(chart, "./PLOS_AHAH/Figures/AHaH_Attractor_Functional.png", 300);
    new SwingWrapper(chart).displayChart();
  }

}
