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
package com.mancrd.ahah.samples.spikeencoding;

import java.util.HashSet;
import java.util.Set;

import com.mancrd.ahah.model.circuit.AHaH21Circuit;
import com.mancrd.ahah.model.circuit.AHaH21CircuitBuilder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.SwingWrapper;

public class VoltageToSpikes {

  private static Set<Integer> inputSpikes = new HashSet<Integer>();
  static {
    inputSpikes.add(0);
  }

  public static void main(String[] args) {

    test0();
  }

  public static void test0() {

    AHaH21Circuit ahahNode = new AHaH21CircuitBuilder().numInputs(1).numBiasInputs(1).build();

    int T = 500;
    double[] vD = new double[T];
    double[] yD = new double[T];

    for (int i = 0; i < T; i++) {

      double v = Math.sin(i * .1) + 1;
      vD[i] = v;
      double y = ahahNode.update(inputSpikes, 0);
      yD[i] = y;
      System.out.println(v + "," + y);

    }

    // Create Chart
    Chart chart = new Chart(1000, 600, ChartTheme.Matlab);
    chart.setChartTitle("V and Y");
    chart.setYAxisTitle("");
    chart.setXAxisTitle("");
    // Series series1 = chart.addSeries("V", null, vD);
    Series series2 = chart.addSeries("Y", null, yD);
    // series1.setMarker(SeriesMarker.NONE);
    series2.setMarker(SeriesMarker.NONE);
    new SwingWrapper(chart).displayChart();

  }
}
