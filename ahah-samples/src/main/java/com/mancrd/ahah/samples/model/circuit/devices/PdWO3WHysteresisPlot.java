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

import com.mancrd.ahah.model.circuit.driver.Driver;
import com.mancrd.ahah.model.circuit.driver.Triangle;
import com.mancrd.ahah.model.circuit.mss.MSSMemristor;
import com.mancrd.ahah.model.circuit.mss.PdWO3WMemristor;
import com.mancrd.ahah.samples.model.circuit.ChartHelper;
import com.xeiam.xchart.CSVExporter;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.StyleManager.LegendPosition;
import com.xeiam.xchart.SwingWrapper;

/**
 * Plots the current through the memristor as a function of voltage applied for a sinusoidal voltage source.
 * <p>
 * Note: we do not know what the driving frequency is in [Chang 2011]
 * 
 * @author timmolter
 */
public class PdWO3WHysteresisPlot {

  private static final int CHART_DIM = 400;

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

    PdWO3WHysteresisPlot pdWO3WHysteresisPlot = new PdWO3WHysteresisPlot();
    pdWO3WHysteresisPlot.go(args);
  }

  private void go(String[] args) {

    double frequency = 100;
    double timeStep = 2E-4;
    double amplitude = 1.3;
    double totalTime = 5E-2;

    try {
      frequency = Double.parseDouble(args[0]);
      timeStep = Double.parseDouble(args[1]);
      amplitude = Double.parseDouble(args[2]);
      totalTime = Double.parseDouble(args[3]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    MSSMemristor memristor = new PdWO3WMemristor(.8);

    int numTimeSteps = (int) (totalTime / timeStep);

    double[] current = new double[numTimeSteps];
    double[] voltage = new double[numTimeSteps];
    double[] time = new double[numTimeSteps];
    double[] resistance = new double[numTimeSteps];

    Driver driver = new Triangle("V", 0, 0, amplitude, frequency);

    for (int i = 0; i < numTimeSteps; i++) {
      time[i] = (i + 1) * timeStep;
      voltage[i] = driver.getSignal(time[i]);
      // voltage[i] = amplitude * Math.sin(time[i] * 2 * Math.PI * frequency);
      current[i] = memristor.getCurrent(voltage[i]) * 1000000; // in uA
      memristor.dG(voltage[i], timeStep);
      resistance[i] = voltage[i] / current[i] * 1000000; // in Ohm
    }

    // Create Chart
    Chart chart = new Chart(CHART_DIM, CHART_DIM, ChartTheme.Matlab);
    chart.setChartTitle("Hysteresis Loop " + frequency + " Hz");
    chart.setYAxisTitle("Current [uA]");
    chart.setXAxisTitle("Voltage [V]");
    chart.getStyleManager().setLegendPosition(LegendPosition.InsideSE);
    Series series = chart.addSeries("PdWO3WDevice", voltage, current);
    series.setMarker(SeriesMarker.NONE);
    new SwingWrapper(chart).displayChart();
    CSVExporter.writeCSVColumns(series, "./Results/Model/Circuit/");

    saveScaledData("WOx", voltage, current);
  }

  private void saveScaledData(String name, double[] voltage, double[] current) {

    double[] voltageScaled = ChartHelper.getScaledArray(voltage);
    double[] currentScaled = ChartHelper.getScaledArray(current);

    Chart chart = new Chart(CHART_DIM, CHART_DIM, ChartTheme.Matlab);
    Series series = chart.addSeries(name, voltageScaled, currentScaled);

    CSVExporter.writeCSVColumns(series, "./Results/Model/Circuit/Combo/");
  }
}
