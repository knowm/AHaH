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
package com.mancrd.ahah.samples.classifier.censusincome;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mancrd.ahah.classifier.ClassificationEvaluator;
import com.mancrd.ahah.classifier.Classifier;
import com.mancrd.ahah.classifier.ClassifierOutput;
import com.xeiam.datasets.censusincome.CensusIncome;
import com.xeiam.datasets.censusincome.CensusIncomeDAO;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.SwingWrapper;

/**
 * @author alexnugent
 */
public class CensusIncomeOverfittingApp {

  public static DecimalFormat df = new DecimalFormat(".000");

  /**
   * This app takes the following arguments:
   * <ul>
   * <li>int numTrainingEpochs (5): number of training epochs to use</li>
   * 
   * @param args
   */
  public static void main(String[] args) {

    CensusIncomeOverfittingApp censusIncomeAnalysis = new CensusIncomeOverfittingApp();
    censusIncomeAnalysis.go(args);
  }

  private void go(String[] args) {

    int numTrainingEpochs = 50;
    try {
      numTrainingEpochs = Integer.parseInt(args[0]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    double[] ydata = new double[numTrainingEpochs];

    Classifier classifier = new Classifier();
    classifier.setLearningRate(.1f);
    classifier.setUnsupervisedConfidenceThreshold(10);
    classifier.setUnsupervisedEnabled(false);// prevents unsupervised learning. This allows us to test the performance due to "overfiting".

    CensusIncomeSpikeEncoder censusFeatureFactory = new CensusIncomeSpikeEncoder();

    // train
    System.out.println("training...");

    Set<String> labels = new HashSet<String>();
    labels.add("true");
    labels.add("false");
    List<CensusIncome> censusIncomeTrainData = CensusIncomeDAO.getShuffledTrainData();
    List<CensusIncome> censusIncomeTestData = CensusIncomeDAO.getShuffledTestData();

    for (int i = 0; i < numTrainingEpochs; i++) {

      // train
      for (CensusIncome censusIncome : censusIncomeTrainData) {
        String[] topicTrueLabels = new String[] { censusIncome.isIncomeLessThan50k() + "" };
        long[] spikes = censusFeatureFactory.encode(censusIncome);
        classifier.update(topicTrueLabels, spikes);
      }

      // test
      ClassificationEvaluator evaluator = new ClassificationEvaluator(labels);
      for (CensusIncome censusIncome : censusIncomeTestData) {
        long[] spikes = censusFeatureFactory.encode(censusIncome);
        ClassifierOutput classifierOutput = classifier.update(null, spikes);// passing null for labels. Since unsupervised learning is turned off, classifier cannot use this test data to improve
                                                                            // itself.
        String trueLabel = censusIncome.isIncomeLessThan50k() + "";
        // evaluator.update(trueLabels, classifierOutput.getSortedLabels(.3));
        evaluator.update(trueLabel, classifierOutput.getBestGuessLabelAboveThreshold(0));
      }

      System.out.println(" epoch " + (i + 1) + ", Classification Rate=" + evaluator.getAccuracyMicroAve());
      ydata[i] = evaluator.getAccuracyMicroAve();

    }

    Chart chart = new Chart(800, 600);
    chart.setChartTitle("Test Performance vs Number of Training Epochs");
    chart.setXAxisTitle("Trining Epochs");
    chart.setYAxisTitle("F1 Micro-Averaged Score @ Confidence Threshold=.25");

    Series accuracy_series = chart.addSeries("F1", null, ydata);
    accuracy_series.setMarker(SeriesMarker.NONE);

    new SwingWrapper(chart).displayChart();

  }
}
