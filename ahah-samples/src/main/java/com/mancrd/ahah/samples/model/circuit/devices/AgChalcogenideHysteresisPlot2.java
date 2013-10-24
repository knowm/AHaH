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
import com.mancrd.ahah.model.circuit.mss.AgChalcMemristor2;
import com.mancrd.ahah.model.circuit.mss.MSSMemristor;
import com.xeiam.xchart.CSVExporter;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.SwingWrapper;

/**
 * Plots the current through two series-connected memristors as a function of voltage applied for a sinusoidal voltage source.
 * 
 * @author timmolter
 */
public class AgChalcogenideHysteresisPlot2 {

  /**
   * This app takes the following arguments:
   * <ul>
   * <li>frequency (100): frequency of voltage source
   * <li>timeStep (1E-4): time step of simulation
   * <li>amplitude (.25): amplitude of voltage source
   * <li>totalTime (5E-2): total simulation time.
   * 
   * @param args
   */
  public static void main(String[] args) {

    AgChalcogenideHysteresisPlot2 agChalcogenideHysteresisPlot = new AgChalcogenideHysteresisPlot2();
    agChalcogenideHysteresisPlot.go(args);
  }

  private void go(String[] args) {

    double frequency = 100;
    double timeStep = 1E-4;
    double amplitude = .37;
    double totalTime = 1E-2;

    try {
      frequency = Double.parseDouble(args[0]);
      timeStep = Double.parseDouble(args[1]);
      amplitude = Double.parseDouble(args[2]);
      totalTime = Double.parseDouble(args[3]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    MSSMemristor m1 = new AgChalcMemristor(.5);
    MSSMemristor m2 = new AgChalcMemristor2(.5);

    int numTimeSteps = (int) (totalTime / timeStep);

    double[] current = new double[numTimeSteps];
    double[] voltage = new double[numTimeSteps];
    double[] v1 = new double[numTimeSteps];
    double[] v2 = new double[numTimeSteps];
    double[] resistance = new double[numTimeSteps];
    double[] time = new double[numTimeSteps];

    for (int i = 0; i < numTimeSteps; i++) {

      time[i] = (i + 1) * timeStep;
      voltage[i] = amplitude * Math.sin(time[i] * 2 * Math.PI * frequency);
      current[i] = voltage[i] / (m1.getResistance() + m2.getResistance()) * 1000; // in mA

      v2[i] = current[i] / m2.getConductance() / 1000;
      v1[i] = voltage[i] - v2[i];
      resistance[i] = voltage[i] / current[i] * 1000; // in Ohm
      m1.dG(v1[i], timeStep);
      m2.dG(v2[i], timeStep);
    }

    // Create I/V Chart
    Chart chart = new Chart(600, 600, ChartTheme.Matlab);
    chart.setChartTitle("Hysteresis Loop " + frequency + " Hz");
    chart.setYAxisTitle("Current [mA]");
    chart.setXAxisTitle("Voltage [V]");
    // chart.getStyleManager().setLegendVisible(false);
    Series series1 = chart.addSeries("V", voltage, current);
    series1.setMarker(SeriesMarker.NONE);
    Series series2 = chart.addSeries("Va", v1, current);
    series2.setMarker(SeriesMarker.NONE);
    Series series3 = chart.addSeries("Vb", v2, current);
    series3.setMarker(SeriesMarker.NONE);
    new SwingWrapper(chart).displayChart();
    CSVExporter.writeCSVColumns(series1, "./Results/Model/Circuit/AgChalc2/");
    CSVExporter.writeCSVColumns(series2, "./Results/Model/Circuit/AgChalc2/");
    CSVExporter.writeCSVColumns(series3, "./Results/Model/Circuit/AgChalc2/");

  }
}
