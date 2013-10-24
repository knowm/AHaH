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

import java.util.Arrays;
import java.util.Set;

import com.mancrd.ahah.commons.utils.FileUtils;
import com.mancrd.ahah.model.functional.AHaHNode;
import com.mancrd.ahah.model.functional.AHaHNodeBuilder;
import com.xeiam.xchart.CSVExporter;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.SwingWrapper;

/**
 * Shows occupation of spike-logic state attractors. Each AHaH node randomly falls into an attractor which is a logic function. This function is measured. This is repeated many times to build up a
 * histogram showing that all but the XOR functions are represented by AHaH attractor states.
 * 
 * @author timmolter
 */

public class SpikeLogicStateOccupationFrequencyApp extends FunctionalModelExperiment {

  /**
   * @param args
   */
  public static void main(String[] args) {

    SpikeLogicStateOccupationFrequencyApp spikeLogicStateOccupationFrequencyApp = new SpikeLogicStateOccupationFrequencyApp();
    spikeLogicStateOccupationFrequencyApp.go();
  }

  void go() {

    int trials = 5000;// number of AHaH nodes to simulate. Enough to get some good statistics.
    int timeSteps = 3000;// number of time steps per ahah node to converge. Run it out to make sure it has converged.

    int numBias = 1;

    double[] logicFunctionCounts = new double[16];

    for (int trial = 0; trial < trials; trial++) {

      AHaHNode ahahNode = new AHaHNodeBuilder().numInputs(4).numBiasInputs(numBias).isModelA(isModelA).build();

      for (int i = 0; i < timeSteps; i++) {
        Set<Integer> spikePattern = getThreePatternSpikeLogicSpikePattern();
        ahahNode.update(spikePattern);
      }

      int lf = testSpikeLogicState(ahahNode);

      logicFunctionCounts[lf]++;
    }

    // normalize histogram of logic functions
    for (int i = 0; i < logicFunctionCounts.length; i++) {
      logicFunctionCounts[i] /= trials;
    }

    // Plot the logic function histogram

    Chart chart =
        new ChartBuilder().chartType(ChartType.Bar).width(500).height(500).title("Functional AHaH Logic State Occupation Frequency").xAxisTitle("Logic Function").yAxisTitle("Frequency").theme(
            ChartTheme.Matlab).build();

    chart.getStyleManager().setLegendVisible(false);

    double[] x = new double[16];
    for (int i = 0; i < x.length; i++) {
      x[i] = i;
    }

    Series series = chart.addSeries("Functional", x, logicFunctionCounts);

    new SwingWrapper(chart).displayChart();

    System.out.println(Arrays.toString(logicFunctionCounts));

    // write to csv file
    FileUtils.mkDirIfNotExists("./Results/");
    FileUtils.mkDirIfNotExists("./Results/Model/");
    FileUtils.mkDirIfNotExists("./Results/Model/Logic/");
    CSVExporter.writeCSVColumns(series, "./Results/Model/Logic/");

  }

}
