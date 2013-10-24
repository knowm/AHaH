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
package com.mancrd.ahah.samples.motorcontroller.roboticarm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mancrd.ahah.commons.utils.RunningStat;
import com.mancrd.ahah.samples.motorcontroller.roboticarm.data.RoboticArmRaw;
import com.mancrd.ahah.samples.motorcontroller.roboticarm.data.RoboticArmRawDAO;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.SwingWrapper;

/**
 * @author timmolter
 */
public class RoboticArmResultsPlotter {

  public void plotLevelSummary() {

    List<Chart> charts = new ArrayList<Chart>();

    List<RoboticArmRaw> roboticArmRaws = RoboticArmRawDAO.selectAll();

    Map<Integer, RunningStat> energyData = new HashMap<Integer, RunningStat>();
    Map<Integer, RunningStat> timeData = new HashMap<Integer, RunningStat>();

    for (RoboticArmRaw roboticArmRaw : roboticArmRaws) {

      // Energy
      RunningStat rs = energyData.get(roboticArmRaw.getLevel());
      if (rs == null) {
        rs = new RunningStat();
      }
      rs.put(roboticArmRaw.getEnergy());
      energyData.put(roboticArmRaw.getLevel(), rs);

      // Time
      rs = timeData.get(roboticArmRaw.getLevel());
      if (rs == null) {
        rs = new RunningStat();
      }
      rs.put(roboticArmRaw.getTime());
      timeData.put(roboticArmRaw.getLevel(), rs);

    }

    String title = roboticArmRaws.get(0).getAlgo();
    int numJoints = roboticArmRaws.get(0).getNumjoints();

    Collection<Number> xData = new ArrayList<Number>();
    Collection<Number> yData = new ArrayList<Number>();
    Collection<Number> errorBars = new ArrayList<Number>();

    // ///////////////////////

    for (Entry<Integer, RunningStat> entrySet : energyData.entrySet()) {
      int level = entrySet.getKey();
      RunningStat stat = entrySet.getValue();
      xData.add(level);
      yData.add(stat.getAverage());
      errorBars.add(stat.getStandardDeviation());
      System.out.println("level: " + level + ", ave: " + stat.getAverage() + ", std: " + stat.getStandardDeviation());
      System.out.println(stat.getAverage());
      System.out.println(stat.getStandardDeviation());
    }

    // Create Chart
    Chart chart = new Chart(800, 220);

    // Customize Chart
    chart.setChartTitle(title + " (Num Joints = " + numJoints + ")");
    chart.setXAxisTitle("Level");
    chart.setYAxisTitle("Required Activation Energy");
    chart.getStyleManager().setLegendVisible(false);

    // Series
    Series series = chart.addSeries("requiredActivationEnergy", xData, yData, errorBars);
    series.setLineStyle(SeriesLineStyle.NONE);
    charts.add(chart);

    // ////////////////

    xData = new ArrayList<Number>();
    yData = new ArrayList<Number>();
    errorBars = new ArrayList<Number>();

    for (Entry<Integer, RunningStat> entrySet : timeData.entrySet()) {
      int pillId = entrySet.getKey();
      RunningStat stat = entrySet.getValue();
      xData.add(pillId);
      yData.add(stat.getAverage());
      errorBars.add(stat.getStandardDeviation());
    }

    // Create Chart
    chart = new Chart(800, 220);

    // Customize Chart
    chart.setXAxisTitle("Level");
    chart.setYAxisTitle("Elapsed Time (s)");
    chart.getStyleManager().setLegendVisible(false);
    chart.getStyleManager().setChartTitleVisible(false);

    // Series
    series = chart.addSeries("elapsedTime", xData, yData, errorBars);
    series.setLineStyle(SeriesLineStyle.NONE);
    charts.add(chart);

    new SwingWrapper(charts, 2, 1).displayChartMatrix();

  }

  public void plotSimple() {

    List<Chart> charts = new ArrayList<Chart>();

    Collection<Number> xData = new ArrayList<Number>();
    Collection<Number> yData = new ArrayList<Number>();

    List<RoboticArmRaw> roboticArmRaws = RoboticArmRawDAO.selectAll();

    for (RoboticArmRaw roboticArmRaw : roboticArmRaws) {
      xData.add(roboticArmRaw.getLevel());
      yData.add(roboticArmRaw.getEnergy());
    }

    String title = roboticArmRaws.get(0).getAlgo();
    int numJoints = roboticArmRaws.get(0).getNumjoints();

    // Create Chart
    Chart chart = new Chart(800, 220);

    // Customize Chart
    chart.setChartTitle(title + " (Num Joints = " + numJoints + ")");
    chart.setXAxisTitle("Level");
    chart.setYAxisTitle("Robotic Arm App - Required Activation Energy");
    chart.getStyleManager().setLegendVisible(false);

    // Series 1
    Series series1 = chart.addSeries("requiredActivationEnergy", xData, yData);
    series1.setLineStyle(SeriesLineStyle.NONE);
    charts.add(chart);

    // ////////////////

    xData = new ArrayList<Number>();
    yData = new ArrayList<Number>();

    for (RoboticArmRaw roboticArmRaw : roboticArmRaws) {
      xData.add(roboticArmRaw.getLevel());
      yData.add(roboticArmRaw.getTime());
    }

    // Create Chart
    chart = new Chart(800, 220);

    // Customize Chart
    chart.setXAxisTitle("Level");
    chart.setYAxisTitle("Elapsed Time (s)");
    chart.getStyleManager().setLegendVisible(false);
    chart.getStyleManager().setChartTitleVisible(false);

    // Series 1
    series1 = chart.addSeries("elapsedTime", xData, yData);
    series1.setLineStyle(SeriesLineStyle.NONE);
    charts.add(chart);

    new SwingWrapper(charts, 2, 1).displayChartMatrix();

  }

}
