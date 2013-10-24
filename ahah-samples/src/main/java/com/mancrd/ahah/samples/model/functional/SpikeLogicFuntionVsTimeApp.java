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

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mancrd.ahah.model.functional.AHaHNode;
import com.mancrd.ahah.model.functional.AHaHNodeBuilder;
import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.SwingWrapper;

/**
 * Determines the logic function of attractor states of a spike-encoded binary input. Each AHaH node will randomly converge to a stable attractor state. The purpose of this experiment is to show
 * that the AHaH attractor states are stable.
 * "Logical 0"-->Spike Pattern {1,z}
 * "Logical 1"-->Spike Pattern {z,1}
 * 'z' is a floating input
 * Examples:
 * Binary Logic-->Spike Logic
 * 00-->1z1z
 * 01-->1zz1
 * 10-->z11z
 * 11-->z1z1
 * NOTE!! Add -Xms512m -Xmx1024m to VM args when running this.
 * 
 * @author timmolter
 */
public class SpikeLogicFuntionVsTimeApp extends FunctionalModelExperiment {

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    SpikeLogicFuntionVsTimeApp spikeLogicFuntionVsTimeApp = new SpikeLogicFuntionVsTimeApp();
    spikeLogicFuntionVsTimeApp.go();

  }

  public void go() throws IOException {

    int trials = 100; // Number of AHaH nodes to simulate.
    int timeSteps = 50000; // number of time steps

    int numBias = 1;

    List<double[]> nodeLogicData = new ArrayList<double[]>();

    for (int i = 0; i < trials; i++) {
      nodeLogicData.add(new double[timeSteps]);
    }

    for (int trial = 0; trial < trials; trial++) {

      AHaHNode ahahNode = new AHaHNodeBuilder().numInputs(4).numBiasInputs(numBias).isModelA(isModelA).build();

      for (int i = 0; i < timeSteps; i++) {
        nodeLogicData.get(trial)[i] = testSpikeLogicState(ahahNode);
      }
    }

    // Plot the ahah rule
    Chart chart = new Chart(200, 300, ChartTheme.Matlab);

    chart.getStyleManager().setLegendVisible(false);

    chart.setXAxisTitle("Time Step");
    chart.setYAxisTitle("Logic Function");

    for (int i = 0; i < nodeLogicData.size(); i++) {
      Series dw_series = chart.addSeries("node logic " + i, null, nodeLogicData.get(i));
      dw_series.setLineStyle(SeriesLineStyle.SOLID);
      dw_series.setMarker(SeriesMarker.NONE);
      dw_series.setLineColor(Color.blue);
    }

    BitmapEncoder.savePNGWithDPI(chart, "./PLOS_AHAH/Figures/AHaH_Logic_Vs_Time_Functional.png", 300);
    new SwingWrapper(chart).displayChart();

  }

}
