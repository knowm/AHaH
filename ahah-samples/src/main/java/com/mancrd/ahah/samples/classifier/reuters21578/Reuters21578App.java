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
package com.mancrd.ahah.samples.classifier.reuters21578;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mancrd.ahah.classifier.ClassificationEvaluator;
import com.mancrd.ahah.classifier.Classifier;
import com.mancrd.ahah.classifier.ClassifierOutput;
import com.xeiam.datasets.reuters21578.Reuters21578;
import com.xeiam.datasets.reuters21578.Reuters21578DAO;
import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.StyleManager.LegendPosition;
import com.xeiam.xchart.SwingWrapper;

/**
 * @author timmolter
 */
public class Reuters21578App {

  public static DecimalFormat df = new DecimalFormat(".000");

  /**
   * This app takes the following arguments:
   * <ul>
   * <li>int numTrainingEpochs = (5)
   * 
   * @param args
   * @throws InterruptedException
   * @throws IOException
   */
  public static void main(String[] args) throws InterruptedException, IOException {

    File tempDBFile = Reuters21578DAO.init(); // setup data
    Reuters21578App rca = new Reuters21578App();
    rca.go(args);
    Reuters21578DAO.release(tempDBFile); // release data resources
  }

  public void go(String[] args) throws IOException {

    int numTrainingEpoch = 3;
    try {
      numTrainingEpoch = Integer.parseInt(args[0]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    Classifier classifier = new Classifier();
    Reuters21578SpikeEncoder reuters21578SpikeEncoder = new Reuters21578SpikeEncoder();

    System.out.println("getting data from DB...");
    List<Reuters21578> trainSet = Reuters21578DAO.selectModApte("TRAIN", true);
    // Collections.shuffle(trainSet);
    System.out.println("trainSet size =" + trainSet.size());

    System.out.println("training...");
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < numTrainingEpoch; i++) {
      System.out.println(" epoch " + (i + 1));
      for (Reuters21578 reuters21578 : trainSet) {
        String[] topicTrueLabels = reuters21578.getTopics().split(",");
        long[] spikes = reuters21578SpikeEncoder.encode(reuters21578);
        classifier.update(topicTrueLabels, spikes);
        // System.out.println("Spike Pattern Length = " + spikes.length);
      }
    }

    System.out.println("time elapsed training= " + (System.currentTimeMillis() - startTime));
    System.out.println("runtime total memory: " + Runtime.getRuntime().totalMemory());

    System.out.println("totalClassificationTime= " + (double) classifier.getTotalClassificationTimeInNanoSeconds() / 1000000000 + " s");
    System.out.println("totalNumSpikesProcessed= " + classifier.getNumSpikesProcessed());
    System.out.println("averageSpikesProcessedPerUpdate= " + classifier.getNumSpikesProcessed() / (double) classifier.getNumUpdates());
    System.out.println("averageLabelsProcessedPerUpdate= " + classifier.getNumLabelsProcessed() / (double) classifier.getNumUpdates());
    System.out.println("spikesProcessingRate= " + classifier.getNumSpikesProcessed() / ((double) classifier.getTotalClassificationTimeInNanoSeconds() / 1000000000) + " spikes/s");
    System.out.println("Spike Pattern Space = " + reuters21578SpikeEncoder.getSpikePatternSpace());

    // test
    System.out.println("testing");

    // only measure performance on top-10 labels (this is what most published benchmarks do).
    Set<String> labels = new HashSet<String>();
    labels.add("earn");
    labels.add("acq");
    labels.add("money-fx");
    labels.add("grain");
    labels.add("crude");
    labels.add("trade");
    labels.add("interest");
    labels.add("ship");
    labels.add("wheat");
    labels.add("corn");

    int numSteps = 100;
    double[] confidenceThresholds = new double[numSteps];
    double inc = 1.0 / numSteps;
    for (int i = 0; i < confidenceThresholds.length; i++) {
      confidenceThresholds[i] = i * inc;
    }

    // evaluators for keeping track of performance for each confidence threshold
    ClassificationEvaluator[] evaluators = new ClassificationEvaluator[numSteps];
    for (int i = 0; i < evaluators.length; i++) {
      evaluators[i] = new ClassificationEvaluator(labels);
    }

    List<Reuters21578> testSet = Reuters21578DAO.selectModApte("TEST", true);
    System.out.println("testSet size =" + testSet.size());

    for (Reuters21578 reuters21578 : testSet) {
      String[] topicTrueLabels = reuters21578.getTopics().split(",");
      long[] spikes = reuters21578SpikeEncoder.encode(reuters21578);
      ClassifierOutput classifierOutput = classifier.update(topicTrueLabels, spikes);

      for (int i = 0; i < evaluators.length; i++) {
        evaluators[i].update(new HashSet(Arrays.asList(topicTrueLabels)), classifierOutput.getSortedLabels(confidenceThresholds[i]));
      }
    }

    double maxF1 = 0;
    int cIdx = 0;

    // plot results---->

    // performance vs confidence thresholds
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

    // print the performance of all labels at best confidence threshold-->
    System.out.println(evaluators[cIdx].toString());

    // classifierDB.close();

    Chart chart = new Chart(300, 300, ChartTheme.Matlab);
    chart.getStyleManager().setLegendPosition(LegendPosition.InsideSW);
    // chart.setChartTitle("Reuters21578 App - F1=" + df.format(maxF1) + " w/ConfidenceThreshold=" + df.format(confidenceThresholds[cIdx]));
    chart.setChartTitle("Reuters21578 - Functional");
    chart.setXAxisTitle("Confidence Threshold");
    chart.setYAxisTitle("Score");
    // chart.getStyleManager().setXAxisMax(1.01);

    Series accuracy_series = chart.addSeries("Accuracy", confidenceThresholds, accuracy);
    accuracy_series.setMarker(SeriesMarker.NONE);
    Series precision_series = chart.addSeries("Precision", confidenceThresholds, precision);
    precision_series.setMarker(SeriesMarker.NONE);
    Series recall_series = chart.addSeries("Recall", confidenceThresholds, recall);
    recall_series.setMarker(SeriesMarker.NONE);
    Series f1_series = chart.addSeries("F1", confidenceThresholds, f1);
    f1_series.setMarker(SeriesMarker.NONE);

    BitmapEncoder.savePNGWithDPI(chart, "./PLOS_AHAH/Figures/Reuters_Supervised.png", 300);
    new SwingWrapper(chart).displayChart();

    System.out.println("Num AHaH Nodes = " + classifier.getNumUniqueLabels());

  }
}
