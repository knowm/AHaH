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
public class AgChalcogenidePulseTrainPlotC {

  public static void main(String[] args) {

    AgChalcogenidePulseTrainPlotC agChalcogenideHysteresisPlot = new AgChalcogenidePulseTrainPlotC();
    agChalcogenideHysteresisPlot.go(args);
  }

  private void go(String[] args) {

    // PulseDef pulseDef = new PulseDef(10, 10E-4, 1.6, -4.0);
    // PulseDef pulseDef = new PulseDef(10, 10E-4, .8, -2.0);
    // PulseDef pulseDef = new PulseDef(10, 10E-4, .2, -.25);
    // PulseDef pulseDef = new PulseDef(20, 20E-4, .8, -2.0);
    // PulseDef pulseDef = new PulseDef(20, 20E-4, 2.4, -6.0);
    PulseDef pulseDef = new PulseDef(5, 5E-4, .8, -2.0);

    MSSMemristor memristor = new AgChalcMemristor(0.5);

    int numTimeSteps = (int) (pulseDef.getTotalTime() / pulseDef.getPulseWidth()) + 1;

    double[] current = new double[numTimeSteps];
    double[] voltage = new double[numTimeSteps];
    double[] resistance = new double[numTimeSteps];
    double[] time = new double[numTimeSteps];

    final int numStepsBeforeSwitchingPolarity = 15;
    int numSteps = 0;
    boolean upPhase = false;
    for (int i = 0; i < numTimeSteps; i++) {

      time[i] = i;
      voltage[i] = getPolarity(upPhase, pulseDef);
      current[i] = memristor.getCurrent(voltage[i]) * 1000; // in mA
      if (current[i] != 0) {
        resistance[i] = voltage[i] / current[i] * 1000; // in Ohm
      }
      else {
        resistance[i] = 0;
      }

      // update memristor
      memristor.dG(voltage[i], pulseDef.getPulseWidth());

      if (numSteps++ == numStepsBeforeSwitchingPolarity) {
        upPhase = !upPhase;
        numSteps = 0;
      }
    }

    // Create IChart
    // Chart chart = new Chart(600, 600, ChartTheme.Matlab);
    // chart.setChartTitle("Pulse Train " + pulseWidth + " s, " + amplitude + " V");
    // chart.setYAxisTitle("Current [mA]");
    // chart.setXAxisTitle("Pulse Number");
    // chart.getStyleManager().setLegendVisible(false);
    // Series series = chart.addSeries("AgChalcPulseI", time, current);
    // // series.setMarker(SeriesMarker.NONE);
    // new SwingWrapper(chart).displayChart();
    // CSVExporter.writeCSVColumns(series, "./Results/Model/Circuit/");

    // resistance plot
    // String seriesName = "10 µs, 0.8V , -2.0V";
    String seriesName = pulseDef.toString();
    // String seriesName = "1 µs, 0.8V , -2.0V";
    Chart chart = new Chart(600, 600, ChartTheme.Matlab);
    chart.setChartTitle("Pulse Train " + pulseDef.getPulseWidth() + " s");
    chart.setYAxisTitle("Resistance [Ohm]");
    chart.setXAxisTitle("Pulse Number");
    chart.getStyleManager().setLegendVisible(false);
    Series series = chart.addSeries(seriesName, time, resistance);
    // series.setMarker(SeriesMarker.NONE);
    new SwingWrapper(chart).displayChart();
    CSVExporter.writeCSVColumns(series, "./Results/Model/Circuit/AgChalcC/");

  }

  private double getPolarity(boolean upPhase, PulseDef pulseDef) {

    if (upPhase) {
      return pulseDef.getUpPhaseVoltage();
    }
    else {
      return pulseDef.getDownPhaseVoltage();
    }
  }

  private class PulseDef {

    private final double pulseWidth;
    private final double totalTime;
    private final double upPhaseVoltage;
    private final double downPhaseVoltage;

    /**
     * Constructor
     * 
     * @param pulseWidth
     * @param amplitude
     * @param totalTime
     * @param upPhaseVoltage
     * @param downPhaseVoltage
     */
    public PulseDef(double pulseWidth, double totalTime, double upPhaseVoltage, double downPhaseVoltage) {

      this.pulseWidth = pulseWidth;
      this.totalTime = totalTime;
      this.upPhaseVoltage = upPhaseVoltage;
      this.downPhaseVoltage = downPhaseVoltage;
    }

    public double getPulseWidth() {

      return pulseWidth / 1000000;
    }

    public double getTotalTime() {

      return totalTime;
    }

    public double getUpPhaseVoltage() {

      return upPhaseVoltage;
    }

    public double getDownPhaseVoltage() {

      return downPhaseVoltage;
    }

    @Override
    public String toString() {

      return (int) pulseWidth + " µs, " + upPhaseVoltage + " V, " + downPhaseVoltage + " V";
    }

  }
}
