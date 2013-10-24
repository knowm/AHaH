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
package com.mancrd.ahah.samples.clusterer.sweep;

import java.util.ArrayList;
import java.util.List;

import com.mancrd.ahah.clusterer.eval.VergenceEvaluator;
import com.mancrd.ahah.commons.utils.FileUtils;
import com.mancrd.ahah.commons.utils.RunningStat;
import com.xeiam.xchart.CSVExporter;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.LegendPosition;
import com.xeiam.xchart.SwingWrapper;

/**
 * @author timmolter
 */
public abstract class Sweeper {

  public static final int LEARN_STEPS = 1000;
  public static final int EVAL_STEPS = 200;

  public final boolean isFunctional; // true if functional ahah node, false if circuit ahah node

  public abstract List<List<ClustererDriver>> getSweepMatrix(List<Number> orthoganolValues);

  public abstract List<Number> getXAxisValues();

  public abstract List<Number> getOrthoganolValues();

  public abstract String getChartTitle();

  public abstract String getOrthoganolName();

  public abstract Number getOrthoganolSweetSpotValue();

  /**
   * Constructor
   * 
   * @param isFunctional
   */
  public Sweeper(boolean isFunctional) {

    this.isFunctional = isFunctional;
  }

  /**
   * main entry point
   * 
   * @throws Exception
   */
  public void go() throws Exception {

    List<List<VergenceEvaluator>> sweeps = sweepOrthoganol();
    makeOrthogonalChart(sweeps);
  }

  /**
   * main entry point
   * 
   * @throws Exception
   */
  public void go(int numSweeps4Averaging) throws Exception {

    List<List<VergenceEvaluator>> sweeps = sweepSweetSpot(numSweeps4Averaging);
    makeSweetSpotChart(sweeps);
  }

  /**
   * @return
   */
  private List<List<VergenceEvaluator>> sweepSweetSpot(int numSweeps4Averaging) {

    List<List<VergenceEvaluator>> sweeps = new ArrayList<List<VergenceEvaluator>>();

    // make identical orthogonal sweep values from sweet spot value
    List<Number> orthogonalValues = new ArrayList<Number>();
    for (int i = 0; i < numSweeps4Averaging; i++) {
      orthogonalValues.add(getOrthoganolSweetSpotValue());
    }

    for (List<ClustererDriver> sweep : getSweepMatrix(orthogonalValues)) {

      List<VergenceEvaluator> results = new ArrayList<VergenceEvaluator>();
      System.out.println("New Sweep!");

      for (ClustererDriver clustererDriver : sweep) {

        VergenceEvaluator evaluator = this.trial(clustererDriver);
        // results.add(evaluator);
        System.out.println(evaluator.toCompactString());
        results.add(evaluator);
      }
      sweeps.add(results);
    }
    return sweeps;
  }

  /**
   * Do the primary and orthogonal sweeps
   * 
   * @return
   */
  public List<List<VergenceEvaluator>> sweepOrthoganol() {

    List<List<VergenceEvaluator>> sweeps = new ArrayList<List<VergenceEvaluator>>();

    for (List<ClustererDriver> sweep : getSweepMatrix(getOrthoganolValues())) {

      List<VergenceEvaluator> results = new ArrayList<VergenceEvaluator>();
      System.out.println("New Sweep!");

      for (ClustererDriver clustererDriver : sweep) {

        VergenceEvaluator evaluator = this.trial(clustererDriver);
        // results.add(evaluator);
        // System.out.println(evaluator.toCompactString());
        results.add(evaluator);
      }
      sweeps.add(results);
    }
    return sweeps;
  }

  /**
   * Run a sweep, teaching and testing
   * 
   * @param clustererDriver
   * @return
   */
  private VergenceEvaluator trial(ClustererDriver clustererDriver) {

    clustererDriver.learnRandom(LEARN_STEPS); // learn phase
    VergenceEvaluator evaluator = clustererDriver.testAllSpikes(EVAL_STEPS); // test phase

    return evaluator;
  }

  /**
   * make a chart, either a single one or a chart matrix. Save charts as PNGs.
   * 
   * @param sweeps
   * @throws Exception
   */
  private void makeOrthogonalChart(List<List<VergenceEvaluator>> sweeps) throws Exception {

    List<Chart> charts = new ArrayList<Chart>();

    for (int i = 0; i < sweeps.size(); i++) {
      List<VergenceEvaluator> result = sweeps.get(i);

      List<Number> vergenceValues = new ArrayList<Number>();
      List<Number> convergenceValues = new ArrayList<Number>();
      List<Number> divergenceValues = new ArrayList<Number>();

      for (VergenceEvaluator vergenceEvaluator : result) {
        vergenceValues.add(vergenceEvaluator.getVergence());
        convergenceValues.add(vergenceEvaluator.getConvergence());
        divergenceValues.add(vergenceEvaluator.getDivergence());
      }

      // Create Chart
      Chart chart;
      if (sweeps.size() == 1) {
        chart = new Chart(800, 600);
      }
      else {
        chart = new Chart(600, 400);
      }
      // Customize Chart
      String chartTitle;
      if (sweeps.size() == 1) {
        chartTitle = "Vergence vs. " + getChartTitle();
      }
      else {
        chartTitle = "Vergence vs. " + getChartTitle() + " (" + getOrthoganolName() + "=" + getOrthoganolValues().get(i) + ")";
      }
      chart.setChartTitle(chartTitle);
      chart.setXAxisTitle(getChartTitle());
      chart.setYAxisTitle("Vergence");

      Series series = chart.addSeries("Vergence", getXAxisValues(), vergenceValues);
      series.setMarker(SeriesMarker.NONE);
      Series series1 = chart.addSeries("Convergence", getXAxisValues(), convergenceValues);
      series1.setMarker(SeriesMarker.NONE);
      Series series2 = chart.addSeries("Divergence", getXAxisValues(), divergenceValues);
      series2.setMarker(SeriesMarker.NONE);

      charts.add(chart);
    }
    if (sweeps.size() == 1) {
      new SwingWrapper(charts.get(0)).displayChart();
    }
    else {
      new SwingWrapper(charts).displayChartMatrix();
    }
  }

  /**
   * Make a chart with Error Bars and average values
   * 
   * @param sweeps
   * @throws Exception
   */
  private void makeSweetSpotChart(List<List<VergenceEvaluator>> sweeps) throws Exception {

    List<Number> averageVergenceValues = new ArrayList<Number>();
    List<Number> stdVergenceValues = new ArrayList<Number>();
    List<Number> averageConergenceValues = new ArrayList<Number>();
    List<Number> stdConvergenceValues = new ArrayList<Number>();
    List<Number> averageDivergenceValues = new ArrayList<Number>();
    List<Number> stdDivergenceValues = new ArrayList<Number>();
    for (int i = 0; i < sweeps.get(0).size(); i++) {

      RunningStat rsVer = new RunningStat();
      RunningStat rsCon = new RunningStat();
      RunningStat rsDiv = new RunningStat();
      for (List<VergenceEvaluator> list : sweeps) { // for each sweep
        VergenceEvaluator result = list.get(i);
        rsVer.put(result.getVergence());
        rsCon.put(result.getConvergence());
        rsDiv.put(result.getDivergence());
      }

      averageVergenceValues.add(rsVer.getAverage());
      stdVergenceValues.add(rsVer.getStandardDeviation());
      averageConergenceValues.add(rsCon.getAverage());
      stdConvergenceValues.add(rsCon.getStandardDeviation());
      averageDivergenceValues.add(rsDiv.getAverage());
      stdDivergenceValues.add(rsDiv.getStandardDeviation());
    }

    Chart chart = new Chart(500, 500);
    // Customize Chart
    String chartTitle = "Vergence vs. " + getChartTitle() + " (" + getOrthoganolName() + "= " + getOrthoganolSweetSpotValue() + ", n= " + sweeps.size() + ")";
    chart.setChartTitle(chartTitle);
    chart.setXAxisTitle(getChartTitle());
    chart.setYAxisTitle("Vergence");
    chart.getStyleManager().setLegendPosition(LegendPosition.InsideSW);

    Series series = chart.addSeries(getOrthoganolName() + " = " + getOrthoganolSweetSpotValue(), getXAxisValues(), averageVergenceValues, stdVergenceValues);
    series.setMarker(SeriesMarker.NONE);
    // Series series1 = chart.addSeries("Convergence_" + getOrthoganolSweetSpotValue(), getXAxisValues(), averageConergenceValues, stdConvergenceValues);
    // series1.setMarker(SeriesMarker.NONE);
    // Series series2 = chart.addSeries("Divergence_" + getOrthoganolSweetSpotValue(), getXAxisValues(), averageDivergenceValues, stdDivergenceValues);
    // series2.setMarker(SeriesMarker.NONE);

    // Results
    String functionalOrCircuit = isFunctional ? "Functional" : "Circuit";
    FileUtils.mkDirIfNotExists("./Results/");
    FileUtils.mkDirIfNotExists("./Results/Clusterer/");
    FileUtils.mkDirIfNotExists("./Results/Clusterer/" + getChartTitle().replace(" ", "_") + "/");
    FileUtils.mkDirIfNotExists("./Results/Clusterer/" + getChartTitle().replace(" ", "_") + "/" + functionalOrCircuit + "/");
    CSVExporter.writeCSVColumns(series, "./Results/Clusterer/" + getChartTitle().replace(" ", "_") + "/" + functionalOrCircuit + "/");
    // CSVExporter.writeCSVColumns(series1, "./Results/Clusterer/" + getChartTitle().replace(" ", "_") + "/" + functionalOrCircuit + "/");
    // CSVExporter.writeCSVColumns(series2, "./Results/Clusterer/" + getChartTitle().replace(" ", "_") + "/" + functionalOrCircuit + "/");

    new SwingWrapper(chart).displayChart();

  }
}
