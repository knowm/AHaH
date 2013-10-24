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
package com.mancrd.ahah.samples.model.circuit.devices;

import com.mancrd.ahah.model.circuit.mss.AgChalcMemristor;
import com.mancrd.ahah.model.circuit.mss.MSSMemristor;
import com.xeiam.xchart.CSVExporter;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.SwingWrapper;

/**
 * Plots the current through the memristor as a function of voltage pulses applied.
 * 
 * @author timmolter
 */
public class AgChalcogenidePulseTrainPlot {

  /**
   * This app takes the following arguments:
   * <ul>
   * <li>timeStep (1E-4): time step of simulation
   * <li>amplitude (.25): amplitude of voltage source
   * <li>totalTime (5E-2): total simulation time.
   * 
   * @param args
   */
  public static void main(String[] args) {

    AgChalcogenidePulseTrainPlot agChalcogenideHysteresisPlot = new AgChalcogenidePulseTrainPlot();
    agChalcogenideHysteresisPlot.go(args);
  }

  private void go(String[] args) {

    double pulseWidth = 1E-5;
    double amplitude = 1;
    double totalTime = 1E-3;

    try {
      pulseWidth = Double.parseDouble(args[1]);
      amplitude = Double.parseDouble(args[2]);
      totalTime = Double.parseDouble(args[3]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    MSSMemristor memristor = new AgChalcMemristor(0.5);

    int numTimeSteps = (int) (totalTime / pulseWidth);

    double[] current = new double[numTimeSteps];
    double[] voltage = new double[numTimeSteps];
    double[] resistance = new double[numTimeSteps];
    double[] time = new double[numTimeSteps];

    final int numStepsBeforeSwitchingPolarity = 15;
    int numSteps = 0;
    boolean upPhase = true;
    for (int i = 0; i < numTimeSteps; i++) {

      time[i] = i;
      voltage[i] = getPolarity(upPhase) * amplitude;
      current[i] = memristor.getCurrent(voltage[i]) * 1000; // in mA
      if (current[i] != 0) {
        resistance[i] = voltage[i] / current[i] * 1000; // in Ohm
      }
      else {
        resistance[i] = 0;
      }

      // update memristor
      memristor.dG(voltage[i], pulseWidth);

      if (numSteps++ == numStepsBeforeSwitchingPolarity) {
        upPhase = !upPhase;
        numSteps = 0;
      }
    }

    // Create IChart
    Chart chart = new Chart(600, 600, ChartTheme.Matlab);
    chart.setChartTitle("Pulse Train " + pulseWidth + " s, " + amplitude + " V");
    chart.setYAxisTitle("Current [mA]");
    chart.setXAxisTitle("Pulse Number");
    chart.getStyleManager().setLegendVisible(false);
    Series series = chart.addSeries("AgChalcPulseI", time, current);
    // series.setMarker(SeriesMarker.NONE);
    new SwingWrapper(chart).displayChart();
    CSVExporter.writeCSVColumns(series, "./Results/Model/Circuit/");

    // resistance plot
    chart = new Chart(600, 600, ChartTheme.Matlab);
    chart.setChartTitle("Pulse Train " + pulseWidth + " s");
    chart.setYAxisTitle("Resistance [Ohm]");
    chart.setXAxisTitle("Pulse Number");
    chart.getStyleManager().setLegendVisible(false);
    series = chart.addSeries("AgChalcPulseR", time, resistance);
    // series.setMarker(SeriesMarker.NONE);
    new SwingWrapper(chart).displayChart();
    CSVExporter.writeCSVColumns(series, "./Results/Model/Circuit/AgChalc3/");

  }

  private double getPolarity(boolean upPhase) {

    if (upPhase) {
      return -2.0;
    }
    else {
      return .8;
    }

  }
}
