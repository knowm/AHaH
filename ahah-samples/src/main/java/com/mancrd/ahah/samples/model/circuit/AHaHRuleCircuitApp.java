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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.mancrd.ahah.commons.utils.FileUtils;
import com.mancrd.ahah.model.circuit.AHaH21Circuit;
import com.mancrd.ahah.model.circuit.AHaH21CircuitBuilder;
import com.xeiam.xchart.CSVExporter;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.SwingWrapper;

/**
 * @author timmolter
 */
public class AHaHRuleCircuitApp extends CircuitModelExperiment {

  /**
   * @param args
   */
  public static void main(String[] args) {

    AHaHRuleCircuitApp aHaHRule = new AHaHRuleCircuitApp();
    aHaHRule.go();
  }

  /**
   * Takes a four-input AHaH node and reconstructs the AHaH rule.
   */
  public void go() {

    int trials = 10; // number of AHaH nodes to simulate
    int timeSteps = 500; // number of time steps per ahah node to converge

    double Vdd = .5;
    double Vss = -.5;

    double readPeriod = 1E-6;
    double writePeriod = 1E-6;

    int numBias = 1;

    List<WeightUpdate> dw = new ArrayList<WeightUpdate>();
    for (int trial = 0; trial < trials; trial++) {

      AHaH21Circuit ahahNode = new AHaH21CircuitBuilder().numInputs(4).numBiasInputs(numBias).readPeriod(readPeriod).writePeriod(writePeriod).Vss(Vss).Vdd(Vdd).build();

      for (int i = 0; i < timeSteps; i++) {
        Set<Integer> spikePattern = getOrthogonalSpikeLogicSpikePattern();

        double[] beforeW = { ahahNode.getWeight(0), ahahNode.getWeight(1), ahahNode.getWeight(2), ahahNode.getWeight(3) };
        double beforeBiasW = ahahNode.getWeight(4);

        double y = ahahNode.update(spikePattern, 0);

        double dB = ahahNode.getWeight(4) - beforeBiasW;

        if (spikePattern.contains(0)) {
          dw.add(new WeightUpdate(i, y, ahahNode.getWeight(0) - beforeW[0], dB));
        }

        if (spikePattern.contains(1)) {
          dw.add(new WeightUpdate(i, y, ahahNode.getWeight(1) - beforeW[1], dB));
        }

        if (spikePattern.contains(2)) {
          dw.add(new WeightUpdate(i, y, ahahNode.getWeight(2) - beforeW[2], dB));
        }

        if (spikePattern.contains(3)) {
          dw.add(new WeightUpdate(i, y, ahahNode.getWeight(3) - beforeW[3], dB));
        }

      }

    }

    Collections.sort(dw);

    ahahPlot(0, 200, dw);
    // ahahPlot(200, 400, dw);
    // ahahPlot(400, 800, dw);
    // ahahPlot(0, 1000, dw);

  }

  public void ahahPlot(int startTime, int endTime, List<WeightUpdate> dwData) {

    int startIdx = -1;
    int endIdx = dwData.size();
    for (int i = 0; i < dwData.size(); i++) {
      if (startIdx == -1 && dwData.get(i).getTimeStep() == startTime) {
        startIdx = i;
      }
      if (endIdx == dwData.size() && dwData.get(i).getTimeStep() == endTime) {
        endIdx = i;
        break;
      }
    }

    System.out.println("startTime,endTime: " + startTime + "," + endTime);
    System.out.println("startIdx,endIdx: " + startIdx + "," + endIdx);

    double[] y = new double[endIdx - startIdx];
    double[] dw = new double[endIdx - startIdx];
    double[] db = new double[endIdx - startIdx];

    for (int i = startIdx; i < endIdx; i++) {
      y[i - startIdx] = dwData.get(i).getY();
      dw[i - startIdx] = dwData.get(i).getdW();
      db[i - startIdx] = dwData.get(i).getdB();
    }

    Chart chart = new Chart(500, 500, ChartTheme.Matlab);
    chart.setXAxisTitle("Y");
    chart.setYAxisTitle("dW");
    chart.setChartTitle("AHaH 21 Circuit AHaH Rule Reconstruction, T=" + startTime + ":" + endTime + "");
    chart.getStyleManager().setLegendVisible(false);

    Series dwseries = chart.addSeries("Circuit_Inputs", y, dw);
    dwseries.setLineStyle(SeriesLineStyle.NONE);
    dwseries.setMarker(SeriesMarker.CIRCLE);
    dwseries.setMarkerColor(new Color(0, 0, 255, 10));

    Series dwBiasSeries = chart.addSeries("Circuit_Bias", y, db);
    dwBiasSeries.setLineStyle(SeriesLineStyle.NONE);
    dwBiasSeries.setMarker(SeriesMarker.CIRCLE);
    dwBiasSeries.setMarkerColor(new Color(255, 0, 0, 10));

    new SwingWrapper(chart).displayChart();

    FileUtils.mkDirIfNotExists("./Results/");
    FileUtils.mkDirIfNotExists("./Results/Model/");
    FileUtils.mkDirIfNotExists("./Results/Model/AHaHRule/");
    FileUtils.mkDirIfNotExists("./Results/Model/AHaHRule/Circuit/");
    CSVExporter.writeCSVColumns(dwseries, "./Results/Model/AHaHRule/Circuit/");
    CSVExporter.writeCSVColumns(dwBiasSeries, "./Results/Model/AHaHRule/Circuit/");

  }
}
