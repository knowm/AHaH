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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mancrd.ahah.commons.utils.FileUtils;
import com.mancrd.ahah.model.functional.AHaHNode;
import com.mancrd.ahah.model.functional.AHaHNodeBuilder;
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
public class AHaHRuleFunctionalApp extends FunctionalModelExperiment {

  /**
   * @param args
   */
  public static void main(String[] args) {

    AHaHRuleFunctionalApp aHaHRule = new AHaHRuleFunctionalApp();
    aHaHRule.go();
  }

  /**
   * Takes a four-input AHaH node and reconstructs the AHaH rule.
   */
  public void go() {

    int trials = 10; // number of AHaH nodes to simulate
    int timeSteps = 500; // number of time steps per ahah node to converge

    int numBias = 1;

    List<Number> dDdata = new ArrayList<Number>();
    List<Number> yData = new ArrayList<Number>();

    List<Number> dwBiasData = new ArrayList<Number>();
    List<Number> yBiasData = new ArrayList<Number>();

    for (int trial = 0; trial < trials; trial++) {

      AHaHNode ahahNode = new AHaHNodeBuilder().numInputs(4).numBiasInputs(numBias).isModelA(isModelA).noise(0.00005).build(); // reduce noise so rule can be seen

      for (int i = 0; i < timeSteps; i++) {

        Set<Integer> spikePattern = getOrthogonalSpikeLogicSpikePattern();

        double[] beforeW = { ahahNode.getWeight(0), ahahNode.getWeight(1), ahahNode.getWeight(2), ahahNode.getWeight(3) };

        double beforeBiasW = ahahNode.getWeight(4);

        double y = ahahNode.update(spikePattern);

        dwBiasData.add(ahahNode.getWeight(4) - beforeBiasW);
        yBiasData.add(y);

        if (spikePattern.contains(0)) {
          dDdata.add(ahahNode.getWeight(0) - beforeW[0]);
          yData.add(y);
        }

        if (spikePattern.contains(1)) {
          dDdata.add(ahahNode.getWeight(1) - beforeW[1]);
          yData.add(y);
        }

        if (spikePattern.contains(2)) {
          dDdata.add(ahahNode.getWeight(2) - beforeW[2]);
          yData.add(y);
        }

        if (spikePattern.contains(3)) {
          dDdata.add(ahahNode.getWeight(3) - beforeW[3]);
          yData.add(y);
        }

      }

    }

    // Plot the ahah rule
    Chart chart = new Chart(500, 500, ChartTheme.Matlab);
    chart.setXAxisTitle("Y");
    chart.setYAxisTitle("dW");
    chart.setChartTitle("AHaH 21 Functional");
    chart.getStyleManager().setLegendVisible(false);

    Series dwseries = chart.addSeries("Functional_Inputs", yData, dDdata);
    dwseries.setLineStyle(SeriesLineStyle.NONE);
    dwseries.setMarker(SeriesMarker.CIRCLE);
    dwseries.setMarkerColor(new Color(0, 0, 255, 10));

    Series dwBiasSeries = chart.addSeries("Functional_Bias", yBiasData, dwBiasData);
    dwBiasSeries.setLineStyle(SeriesLineStyle.NONE);
    dwBiasSeries.setMarker(SeriesMarker.CIRCLE);
    dwBiasSeries.setMarkerColor(new Color(255, 0, 0, 10));

    new SwingWrapper(chart).displayChart();

    FileUtils.mkDirIfNotExists("./Results/");
    FileUtils.mkDirIfNotExists("./Results/Model/");
    FileUtils.mkDirIfNotExists("./Results/Model/AHaHRule/");
    FileUtils.mkDirIfNotExists("./Results/Model/AHaHRule/Functional/");
    CSVExporter.writeCSVColumns(dwseries, "./Results/Model/AHaHRule/Functional/");
    CSVExporter.writeCSVColumns(dwBiasSeries, "./Results/Model/AHaHRule/Functional/");
  }

}
