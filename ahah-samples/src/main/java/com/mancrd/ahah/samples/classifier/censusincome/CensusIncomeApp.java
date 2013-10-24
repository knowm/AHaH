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

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mancrd.ahah.classifier.ClassificationEvaluator;
import com.mancrd.ahah.classifier.Classifier;
import com.mancrd.ahah.classifier.ClassifierOutput;
import com.xeiam.datasets.censusincome.CensusIncome;
import com.xeiam.datasets.censusincome.CensusIncomeDAO;
import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.StyleManager.LegendPosition;
import com.xeiam.xchart.SwingWrapper;

/**
 * @author alexnugent
 */
public class CensusIncomeApp {

  public static DecimalFormat df = new DecimalFormat(".000");

  /**
   * This app takes the following arguments:
   * <ul>
   * <li>int numTrainingEpochs (5): number of training epochs to use</li>
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    File tempDBFile = CensusIncomeDAO.init(); // setup data
    CensusIncomeApp censusIncomeAnalysis = new CensusIncomeApp();
    censusIncomeAnalysis.go(args);
    CensusIncomeDAO.release(tempDBFile); // release data resources
  }

  private void go(String[] args) throws IOException {

    int numTrainingEpochs = 5;
    try {
      numTrainingEpochs = Integer.parseInt(args[0]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    Classifier classifier = new Classifier();
    // classifier.setLearningRate(.01f);
    CensusIncomeSpikeEncoder censusSpikeEncoder = new CensusIncomeSpikeEncoder();

    // train
    System.out.println("training...");

    Set<String> labels = new HashSet<String>();
    labels.add("true");
    labels.add("false");

    for (int i = 0; i < numTrainingEpochs; i++) {
      ClassificationEvaluator evaluator = new ClassificationEvaluator(labels);
      List<CensusIncome> censusIncomeTrainData = CensusIncomeDAO.getShuffledTrainData();

      for (CensusIncome censusIncome : censusIncomeTrainData) {
        String[] topicTrueLabels = new String[] { censusIncome.isIncomeLessThan50k() + "" };

        long[] spikes = censusSpikeEncoder.encode(censusIncome);
        ClassifierOutput classifierOutput = classifier.update(topicTrueLabels, spikes);
        evaluator.update(censusIncome.isIncomeLessThan50k() + "", classifierOutput.getBestGuessLabelAboveThreshold(0.0));
        // System.out.println("Spike Pattern Length = " + spikes.length);
      }
      System.out.println(" epoch " + (i + 1) + ", Classification Rate=" + evaluator.getAccuracyMicroAve());
    }

    System.out.println("totalClassificationTime= " + (double) classifier.getTotalClassificationTimeInNanoSeconds() / 1000000000 + " s");
    System.out.println("totalNumSpikesProcessed= " + classifier.getNumSpikesProcessed());
    System.out.println("averageSpikesProcessedPerUpdate= " + classifier.getNumSpikesProcessed() / (double) classifier.getNumUpdates());
    System.out.println("averageLabelsProcessedPerUpdate= " + classifier.getNumLabelsProcessed() / (double) classifier.getNumUpdates());
    System.out.println("spikesProcessingRate= " + classifier.getNumSpikesProcessed() / ((double) classifier.getTotalClassificationTimeInNanoSeconds() / 1000000000) + " spikes/s");
    System.out.println("Spike Pattern Space = " + censusSpikeEncoder.getSpikePatternSpace());

    // test
    System.out.println("testing...");
    List<CensusIncome> censusIncomeTestData = CensusIncomeDAO.getShuffledTestData();

    int numSteps = 100;
    double[] confidenceThresholds = new double[numSteps];
    double inc = 1.0 / numSteps;
    for (int i = 0; i < confidenceThresholds.length; i++) {
      confidenceThresholds[i] = i * inc;
    }

    ClassificationEvaluator[] evaluators = new ClassificationEvaluator[numSteps];
    for (int i = 0; i < evaluators.length; i++) {
      evaluators[i] = new ClassificationEvaluator(labels);
    }

    for (CensusIncome censusIncome : censusIncomeTestData) {

      long[] spikes = censusSpikeEncoder.encode(censusIncome);
      ClassifierOutput classifierOutput = classifier.update(null, spikes);

      Set<String> trueLabels = new HashSet<String>();
      trueLabels.add(censusIncome.isIncomeLessThan50k() + "");
      for (int i = 0; i < evaluators.length; i++) {
        evaluators[i].update(trueLabels, classifierOutput.getSortedLabels(confidenceThresholds[i]));
      }
    }

    // plot results
    double maxF1 = 0;
    int cIdx = 0;

    double[] accuracy = new double[numSteps];
    double[] precision = new double[numSteps];
    double[] recall = new double[numSteps];
    double[] f1 = new double[numSteps];
    for (int i = 0; i < numSteps; i++) {
      accuracy[i] = evaluators[i].getAccuracyMicroAve();
      precision[i] = evaluators[i].getPrecisionMicroAve();
      recall[i] = evaluators[i].getRecallMicroAve();
      f1[i] = evaluators[i].getF1MicroAve();

      if (f1[i] > maxF1) {// get the best confidence threshold
        maxF1 = f1[i];
        cIdx = i;
      }

    }

    // print the performance of all labels at best confidence threshold
    System.out.println(evaluators[cIdx].toString());

    Chart chart = new Chart(300, 300, ChartTheme.Matlab);
    chart.getStyleManager().setLegendPosition(LegendPosition.InsideSW);
    // chart.setChartTitle("Census Income App - F1=" + df.format(maxF1) + " w/ConfidenceThreshold=" + df.format(confidenceThresholds[cIdx]));
    chart.setChartTitle("Census Income - Functional");
    chart.setXAxisTitle("Confidence Threshold");
    chart.setYAxisTitle("Score");

    Series accuracy_series = chart.addSeries("Accuracy", confidenceThresholds, accuracy);
    accuracy_series.setMarker(SeriesMarker.NONE);
    Series precision_series = chart.addSeries("Precision", confidenceThresholds, precision);
    precision_series.setMarker(SeriesMarker.NONE);
    Series recall_series = chart.addSeries("Recall", confidenceThresholds, recall);
    recall_series.setMarker(SeriesMarker.NONE);
    Series f1_series = chart.addSeries("F1", confidenceThresholds, f1);
    f1_series.setMarker(SeriesMarker.NONE);

    BitmapEncoder.savePNGWithDPI(chart, "./PLOS_AHAH/Figures/Census_Income.png", 300);
    new SwingWrapper(chart).displayChart();

    System.out.println("Num AHaH Nodes = " + classifier.getNumUniqueLabels());

  }
}
