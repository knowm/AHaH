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
package com.mancrd.ahah.samples.combinatorial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mancrd.ahah.combinatorial.RandomSearch;
import com.mancrd.ahah.combinatorial.StrikeSearch;
import com.mancrd.ahah.commons.utils.RunningStat;
import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.StyleManager.LegendPosition;
import com.xeiam.xchart.SwingWrapper;

/**
 * Compares an AHaH "strike" search to random guessing to show that its directed search. Shows relationship between learning rate and the time and quality of solution. Default variable result in a
 * test that takes about 30-60 minutes to run.
 * 
 * @author alexnugent
 */
public class StrikeSearchApp {

  private final int numAttempts = 200;
  private final int maxAttempts = 5000000; // stops the search if it does not terminate before this many steps.

  /**
   * optimization will terminate automatically when the same solution is generated 5 times.
   * if no convergence, optimization will terminate at this number of attempts.
   */
  private int maxSteps = 0;

  /** strike experiment */
  private final List<Number> strikeDistances = new ArrayList<Number>();
  private final List<Number> strikeConvergenceTimes = new ArrayList<Number>();
  private final List<Number> strikeLearnRates = new ArrayList<Number>();

  /** random experiment */
  private final List<Number> randDistances = new ArrayList<Number>();
  private final List<Number> randConvergenceTimes = new ArrayList<Number>();

  /** the network of cities */
  TravelingSalesman travelingSalesman;

  /**
   * This app takes the following arguments:
   * <ul>
   * <li>numCities (64) : smaller number of cities, faster this will run. Obviously.
   * <li>minLearningRate (.00015f) : minimum learning rate
   * <li>maxLearningRate (.0035f) : maximum learning rate
   * <p>
   * NOTE!! Add -Xms512m -Xmx1024m to VM args when running this.
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    StrikeSearchApp combOptApp = new StrikeSearchApp();
    combOptApp.go(args);
  }

  private void go(String[] args) throws IOException {

    int numCities = 64;
    float minLearningRate = .00015f;
    float maxLearningRate = .0035f;
    try {
      numCities = Integer.parseInt(args[0]);
      minLearningRate = Float.parseFloat(args[1]);
      maxLearningRate = Float.parseFloat(args[1]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    travelingSalesman = new TravelingSalesman(numCities);

    aHaHStrikeSearch(numCities, minLearningRate, maxLearningRate);
    randomSearch(numCities);

    plotAHaHAndRand();
    plotLearningRateVsValue();
    plotLearningRateVsTimeToSolution();
  }

  /**
   * finds solutions using the "strike" method
   */
  public void aHaHStrikeSearch(int numCities, float minLearningRate, float maxLearningRate) {

    RunningStat rs = new RunningStat();

    for (int i = 0; i < numAttempts; i++) {

      float learningRate = (float) (Math.random() * (maxLearningRate - minLearningRate) + minLearningRate);

      StrikeSearch strikeSearch = new StrikeSearch(travelingSalesman, maxAttempts, learningRate, learningRate);
      strikeSearch.run();

      strikeDistances.add(strikeSearch.getBestValue());
      strikeConvergenceTimes.add(strikeSearch.getConvergenceAttempts());
      strikeLearnRates.add(learningRate);

      if (strikeSearch.getConvergenceAttempts() > maxSteps) {
        maxSteps = strikeSearch.getConvergenceAttempts();
      }

      System.out.println("AHAH: " + i + " of " + numAttempts + " : " + strikeSearch.getBestValue() + ", " + strikeSearch.getConvergenceAttempts());
      rs.put(strikeSearch.getNumNodes());
      System.out.println("Num AHaH Nodes " + i + " of " + numAttempts + " : " + strikeSearch.getNumNodes());

    }
    System.out.println("Num AHaH Nodes Ave. = " + rs.getAverage() + ", " + rs.getStandardDeviation());

  }

  /**
   * finds random solutions. Alpha and beta are zero, resulting in guessing of random binary vectors.
   */
  public void randomSearch(int numCities) {

    for (int i = 0; i < numAttempts; i++) {

      int terminate = (int) (Math.random() * maxSteps); // sample from range

      TravelingSalesman travelingSalesman = new TravelingSalesman(numCities);
      RandomSearch randomSearch = new RandomSearch(travelingSalesman, terminate);
      randomSearch.run();
      randDistances.add(randomSearch.getBestValue());
      randConvergenceTimes.add(randomSearch.getConvergenceAttempts());
      System.out.println("RAND: " + i + " of " + numAttempts + " : " + randomSearch.getBestValue() + ", " + randomSearch.getConvergenceAttempts());
    }
  }

  private void plotAHaHAndRand() throws IOException {

    Chart chart = new Chart(600, 250, ChartTheme.Matlab);
    chart.setXAxisTitle("Convergence Time");
    chart.setYAxisTitle("Distance");
    chart.getStyleManager().setLegendPosition(LegendPosition.InsideNE);

    Series strikeSeries = chart.addSeries("AHaH", strikeConvergenceTimes, strikeDistances);
    strikeSeries.setLineStyle(SeriesLineStyle.NONE);

    Series randSeries = chart.addSeries("Random", randConvergenceTimes, randDistances);
    randSeries.setLineStyle(SeriesLineStyle.NONE);

    BitmapEncoder.savePNGWithDPI(chart, "./PLOS_AHAH/Figures/TSP_AHaHAndRandom.png", 300);
    new SwingWrapper(chart).displayChart();
  }

  private void plotLearningRateVsTimeToSolution() throws IOException {

    Chart chart = new Chart(300, 250, ChartTheme.Matlab);
    chart.setXAxisTitle("Learning Rate");
    chart.setYAxisTitle("Convergence Time");
    chart.getStyleManager().setLegendVisible(false);
    chart.getStyleManager().setLegendPosition(LegendPosition.InsideNE);

    Series fffSeries = chart.addSeries("Fractal Flow Fabric", strikeLearnRates, strikeConvergenceTimes);
    fffSeries.setLineStyle(SeriesLineStyle.NONE);

    BitmapEncoder.savePNGWithDPI(chart, "./PLOS_AHAH/Figures/TSP_ConvergenceTime.png", 300);
    new SwingWrapper(chart).displayChart();
  }

  private void plotLearningRateVsValue() throws IOException {

    Chart chart = new Chart(300, 250, ChartTheme.Matlab);
    chart.setXAxisTitle("Learning Rate");
    chart.setYAxisTitle("Distance");
    chart.getStyleManager().setLegendVisible(false);

    Series fffSeries = chart.addSeries("Fractal Flow Fabric", strikeLearnRates, strikeDistances);
    fffSeries.setLineStyle(SeriesLineStyle.NONE);

    BitmapEncoder.savePNGWithDPI(chart, "./PLOS_AHAH/Figures/TSP_MaxValue.png", 300);
    new SwingWrapper(chart).displayChart();
  }
}
