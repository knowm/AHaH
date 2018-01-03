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
package com.mancrd.ahah.samples.model.circuit;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mancrd.ahah.model.circuit.AHaH21Circuit;
import com.mancrd.ahah.model.circuit.AHaH21CircuitBuilder;
import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.SwingWrapper;

/**
 * Shows the values [Wa-Wb] (red) and [Wa+Wb] (blue) over time for a number of AHaH nodes. Demonstrates that the quantity [Wa+Wb] is constant and can thus be factored out in the derivations of the
 * ideal circuit model. This enables a linear sum: sum(Wa-Wb) as a representation of "y" in the functional model.
 * 
 * @author timmolter
 */
public class DifferentialWeightApp extends CircuitModelExperiment {

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    DifferentialWeightApp differentialWeightApp = new DifferentialWeightApp();
    differentialWeightApp.go();
  }

  void go() throws IOException {

    int timeSteps = 1100; // number of time steps per ahah node to converge
    int numTrials = 5;

    int numBias = 1;

    // Plot the weights rule
    Chart chart = new Chart(300, 300, ChartTheme.Matlab);

    chart.setXAxisTitle("Time");
    chart.setChartTitle("Weight and Weight Conjugate");
    chart.getStyleManager().setLegendVisible(false);

    for (int i = 0; i < numTrials; i++) {

      AHaH21Circuit ahahNode = new AHaH21CircuitBuilder().numInputs(4).numBiasInputs(numBias).build();

      List<Number> w = new ArrayList<Number>();
      List<Number> w_conjugate = new ArrayList<Number>();

      for (int t = 0; t < timeSteps; t++) {
        Set<Integer> spikePattern = getOrthogonalSpikeLogicSpikePattern();
        ahahNode.update(spikePattern, 0);
        w.add(ahahNode.getSpikeInputWeightSum(spikePattern));
        w_conjugate.add(ahahNode.getSpikeInputWeightMagnitudeSum(spikePattern));
      }

      Series w_series = chart.addSeries("sum(wa - wb)_"+i, null, w);
      w_series.setLineStyle(SeriesLineStyle.SOLID);
      w_series.setMarker(SeriesMarker.NONE);
      w_series.setLineColor(new Color(255, 0, 0, 50));

      Series wc_series = chart.addSeries("sum(wa + wb)_"+i, null, w_conjugate);
      wc_series.setLineStyle(SeriesLineStyle.SOLID);
      wc_series.setMarker(SeriesMarker.NONE);
      wc_series.setLineColor(new Color(0, 0, 255, 50));

    }

    BitmapEncoder.savePNGWithDPI(chart, "./PLOS_AHAH/Figures/AHaH_Weight_and_Weight_Conjugate.png", 300);
    new SwingWrapper(chart).displayChart();
  }

}
