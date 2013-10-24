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
package com.mancrd.ahah.samples.classifier.mnist;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mancrd.ahah.classifier.ClassificationEvaluator;
import com.mancrd.ahah.classifier.Classifier;
import com.mancrd.ahah.classifier.ClassifierOutput;
import com.xeiam.datasets.mnist.Mnist;
import com.xeiam.datasets.mnist.MnistDAO;
import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.StyleManager.LegendPosition;
import com.xeiam.xchart.SwingWrapper;

/**
 * @author alexnugent
 */
public class MnistApp {

  public static DecimalFormat df = new DecimalFormat(".000");

  /**
   * This app takes the following arguments:
   * <ul>
   * <li>int poolSize (~8): pool size in pixels</li>
   * <li>int patchSize = (~8): size of patch to feed into the AHaH tree</li>
   * <li>int encoderResolution = (~10): number of features produced per tree=2^depth</li>
   * <li>int numEncoders = (~3): more trees-->more features-->better performance-->longer run-time. Those last few tenths of a percent will cost you dearly</li>
   * <li>int numTrainingEpochs = (1): not much point in running more than 2 or 3 times. 1 is epoch is ok.</li>
   * <li>int numTrainingSamples = (60000): 60000 is max</li>
   * <li>int numTestSamples = (10000): 10000 is max</li>
   * <p>
   * NOTE!! Add -Xms512m -Xmx1024m to VM args when running this.
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    File tempDBFile = MnistDAO.init(); // setup data
    MnistApp mnistExample = new MnistApp();
    mnistExample.go(args);
    MnistDAO.release(tempDBFile); // release data resources
  }

  private void go(String[] args) throws IOException {

    int poolSize = 8;
    int patchSize = 8;
    int encoderResolution = 10;
    int numEncoders = 3;
    int numTrainingEpochs = 1;
    int numTrainingSamples = 60000;
    int numTestSamples = 10000;
    try {
      poolSize = Integer.parseInt(args[0]);
      patchSize = Integer.parseInt(args[1]);
      encoderResolution = Integer.parseInt(args[2]);
      numEncoders = Integer.parseInt(args[3]);
      numTrainingEpochs = Integer.parseInt(args[4]);
      numTrainingSamples = Integer.parseInt(args[5]);
      numTestSamples = Integer.parseInt(args[6]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    if (numTrainingSamples > 60000) {
      throw new IllegalArgumentException("Training samples limited to 60,000.");
    }
    if (numTestSamples > 10000) {
      throw new IllegalArgumentException("Training samples limited to 10,000.");
    }

    MnistSpikeEncoder mnistSpikeEncoder = new MnistSpikeEncoder(poolSize, patchSize, encoderResolution, numEncoders);
    Classifier classifier = new Classifier();
    // classifier.setLearningRate(.3);// this speeds it up a little.

    // train
    System.out.println("Training...");
    Set<String> labelSet = new HashSet<String>(Arrays.asList(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" }));

    List<Number> trainingDataX = new ArrayList<Number>();
    List<Number> trainingDataY = new ArrayList<Number>();

    long totalSpikeEncoderTime = 0L;
    long totalClassificationTime = 0L;

    for (int e = 0; e < numTrainingEpochs; e++) {
      ClassificationEvaluator trainingEvaluator = new ClassificationEvaluator(labelSet);
      for (int i = 0; i < numTrainingSamples; i++) {
        Mnist mnistData = MnistDAO.selectSingle(i);
        String[] trueLabels = new String[] { mnistData.getLabel() + "" };

        // get spikes
        long spikeStartTime = System.currentTimeMillis();
        long[] spikes = mnistSpikeEncoder.encode(mnistData.getImageMatrix());
        totalSpikeEncoderTime += System.currentTimeMillis() - spikeStartTime;

        // classify
        long classifiyStartTime = System.currentTimeMillis();
        ClassifierOutput classifierOutput = classifier.update(trueLabels, spikes);
        totalClassificationTime += System.currentTimeMillis() - classifiyStartTime;

        trainingEvaluator.update(mnistData.getLabel() + "", classifierOutput.getBestGuessLabelAboveThreshold(0));
        if (i % 500 == 0) {
          System.out.println("sample=" + i + ", F1 = " + trainingEvaluator.getF1MicroAve() + ", numLinks=" + classifier.getNumLinks());
          trainingDataX.add(i);
          trainingDataY.add(trainingEvaluator.getF1MicroAve());
          trainingEvaluator = new ClassificationEvaluator(labelSet);
        }
        // System.out.println("Spike Pattern Length = " + spikes.length);

      }
    }

    System.out.println("totalSpikeEncoderTime= " + totalSpikeEncoderTime / 1000 + " s");
    System.out.println("totalClassificationTime= " + totalClassificationTime / 1000 + " s");
    System.out.println("-----------");
    System.out.println("totalClassificationTime= " + (double) classifier.getTotalClassificationTimeInNanoSeconds() / 1000000000 + " s");
    System.out.println("totalNumSpikesProcessed= " + classifier.getNumSpikesProcessed());
    System.out.println("averageSpikesProcessedPerUpdate= " + classifier.getNumSpikesProcessed() / (double) classifier.getNumUpdates());
    System.out.println("averageLabelsProcessedPerUpdate= " + classifier.getNumLabelsProcessed() / (double) classifier.getNumUpdates());
    System.out.println("spikesProcessingRate= " + classifier.getNumSpikesProcessed() / ((double) classifier.getTotalClassificationTimeInNanoSeconds() / 1000000000) + " spikes/s");
    System.out.println("Spike Pattern Space = " + mnistSpikeEncoder.getSpikePatternSpace());

    // test
    System.out.println("Testing...");

    // all confidence threshold we would like to evaluate
    int numSteps = 100;
    double[] confidenceThresholds = new double[numSteps];
    double inc = 1.0 / numSteps;
    for (int i = 0; i < confidenceThresholds.length; i++) {
      confidenceThresholds[i] = (i - 50) * inc;
    }
    // evaluators for keeping track of performance for each confidence threshold
    ClassificationEvaluator[] evaluators = new ClassificationEvaluator[numSteps];
    for (int i = 0; i < evaluators.length; i++) {
      evaluators[i] = new ClassificationEvaluator(labelSet);
    }

    ClassificationEvaluator testEvaluator = new ClassificationEvaluator(labelSet);
    for (int i = 60000; i < 60000 + numTestSamples; i++) {
      Mnist mnistData = MnistDAO.selectSingle(i);

      long[] spikes = mnistSpikeEncoder.encode(mnistData.getImageMatrix());
      ClassifierOutput classifierOutput = classifier.update(null, spikes);

      Set<String> trueLabels = new HashSet<String>();
      trueLabels.add(mnistData.getLabel() + "");
      for (int k = 0; k < evaluators.length; k++) {
        evaluators[k].update(mnistData.getLabel() + "", classifierOutput.getBestGuessLabelAboveThreshold(confidenceThresholds[k]));
      }

      testEvaluator.update(mnistData.getLabel() + "", classifierOutput.getBestGuess().getLabelstring());
      if (i % 200 == 0) {
        System.out.println("sample=" + i + ", F1=" + testEvaluator.getF1MicroAve());
      }
    }
    double bestF1 = 0;
    int bestF1idx = 0;

    // Plot the performance as a function of confidence threshold--->
    double[] accuracy = new double[numSteps];
    double[] precision = new double[numSteps];
    double[] recall = new double[numSteps];
    double[] f1 = new double[numSteps];
    for (int i = 0; i < numSteps; i++) {
      accuracy[i] = evaluators[i].getAccuracyMicroAve();
      precision[i] = evaluators[i].getPrecisionMicroAve();
      recall[i] = evaluators[i].getRecallMicroAve();
      f1[i] = evaluators[i].getF1MicroAve();
      if (f1[i] > bestF1) {// get the best confidence threshold
        bestF1 = f1[i];
        bestF1idx = i;
      }
    }

    Chart chart = new Chart(300, 300, ChartTheme.Matlab);
    // chart.setChartTitle("MNIST Classification App - F1=" + df.format(bestF1) + " w/ConfidenceThreshold=" + df.format(confidenceThresholds[bestF1idx]));
    chart.setChartTitle("MNIST - Functional");
    chart.setXAxisTitle("Confidence Threshold");
    chart.setYAxisTitle("Score");
    chart.getStyleManager().setLegendPosition(LegendPosition.InsideSW);

    Series accuracy_series = chart.addSeries("Accuracy", confidenceThresholds, accuracy);
    accuracy_series.setMarker(SeriesMarker.NONE);
    Series precision_series = chart.addSeries("Precision", confidenceThresholds, precision);

    precision_series.setMarker(SeriesMarker.NONE);
    Series recall_series = chart.addSeries("Recall", confidenceThresholds, recall);
    recall_series.setMarker(SeriesMarker.NONE);
    Series f1_series = chart.addSeries("F1", confidenceThresholds, f1);
    f1_series.setMarker(SeriesMarker.NONE);

    new SwingWrapper(chart).displayChart();
    BitmapEncoder.savePNGWithDPI(chart, "./PLOS_AHAH/Figures/MNIST.png", 300);

    // Plot the F1 scores of each digit-->
    Chart chart2 = new Chart(300, 300, ChartTheme.Matlab);
    chart2.setChartTitle("MNIST Digit F1 Scores");
    chart2.setXAxisTitle("Digit");
    chart2.setYAxisTitle("F1 Score");
    chart2.getStyleManager().setChartType(ChartType.Bar);
    chart2.getStyleManager().setLegendPosition(LegendPosition.InsideSE);
    chart2.getStyleManager().setYAxisMin(.90);
    chart2.getStyleManager().setYAxisMax(1.0);
    chart2.getStyleManager().setLegendVisible(false);

    double[] digitF1Scores = new double[10];
    double[] x = new double[10];
    for (int i = 0; i < digitF1Scores.length; i++) {
      digitF1Scores[i] = evaluators[bestF1idx].getClassificationRate("" + i).getF1();
      x[i] = i;
    }
    chart2.addSeries("Digit F1 Scores", x, digitF1Scores);

    new SwingWrapper(chart2).displayChart();
    BitmapEncoder.savePNGWithDPI(chart2, "./PLOS_AHAH/Figures/MNIST_Digit_Scores.png", 300);

    // plot the training data--->
    Chart chart3 = new Chart(600, 400);
    chart3.setChartTitle("MNIST Classification App - Training");
    chart3.setXAxisTitle("Sample Number");
    chart3.setYAxisTitle("F1 Score");
    chart3.getStyleManager().setLegendPosition(LegendPosition.InsideSE);

    chart3.addSeries("Training", trainingDataX, trainingDataY);
    new SwingWrapper(chart3).displayChart();

    System.out.println("Num AHaH Nodes = " + classifier.getNumUniqueLabels());

  }
}
