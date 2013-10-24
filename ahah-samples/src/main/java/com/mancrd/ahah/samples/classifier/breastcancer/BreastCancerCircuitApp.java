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
package com.mancrd.ahah.samples.classifier.breastcancer;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mancrd.ahah.classifier.ClassificationEvaluator;
import com.mancrd.ahah.model.circuit.AHaH21Circuit;
import com.mancrd.ahah.model.circuit.AHaH21CircuitBuilder;
import com.mancrd.ahah.model.circuit.AHaH21CircuitBuilder.MemristorType;
import com.xeiam.datasets.breastcancerwisconsinorginal.BreastCancer;
import com.xeiam.datasets.breastcancerwisconsinorginal.BreastCancerDAO;
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
public class BreastCancerCircuitApp {

  public static DecimalFormat df = new DecimalFormat(".000");

  public static void main(String[] args) throws IOException {

    File tempDBFile = BreastCancerDAO.init(); // setup data
    BreastCancerCircuitApp breastCancerAnalysis = new BreastCancerCircuitApp();
    breastCancerAnalysis.go();
    BreastCancerDAO.release(tempDBFile); // release data resources
  }

  private void go() throws IOException {

    // initialize the ahahNode
    double readPeriod = 1E-5; // increase the learning rate from default
    double writePeriod = 1E-5;
    int numBias = 15;
    int numInputs = 70; // max number of inputs/spikes
    MemristorType memristorType = MemristorType.AgChalc;

    AHaH21Circuit ahahNodeCircuit = new AHaH21CircuitBuilder().numInputs(numInputs).numBiasInputs(numBias)

    .readPeriod(readPeriod).writePeriod(writePeriod).memristorType(memristorType).build();

    // initialize the feature factory. This code converts the raw data to a spiking representation.
    BreastCancerSpikeEncoder breastCancerSpikeEncoder = new BreastCancerSpikeEncoder();

    SpikeConverter spikeConverter = new SpikeConverter(); // converts the spike code produced by the feature factory to one that can be used by the circuit.

    // train the classifier--->
    List<BreastCancer> breastCancerTrainingData = BreastCancerDAO.selectTrainData();
    System.out.println("Learning...");

    for (BreastCancer breastCancer : breastCancerTrainingData) {
      String[] topicTrueLabels = new String[1];
      topicTrueLabels[0] = breastCancer.getCellClass() + "";
      long[] spikes = breastCancerSpikeEncoder.encode(breastCancer); // get the spike representation
      Set<Integer> convertedSpikes = spikeConverter.convert(spikes);

      int superviseSignal = 0;
      if (breastCancer.getCellClass() == 2) {
        superviseSignal = 1;
      }
      else {
        superviseSignal = -1;
      }
      System.out.println("Spike Pattern Length = " + convertedSpikes.size());
      // System.out.println("Spikes = " + Arrays.toString(convertedSpikes.toArray()));
      double y = ahahNodeCircuit.update(convertedSpikes, superviseSignal);
    }
    System.out.println("Spike Pattern Space = " + breastCancerSpikeEncoder.getSpikePatternSpace());

    // Test the classifier
    List<BreastCancer> breastCancerTestData = BreastCancerDAO.selectTestData();

    System.out.println("Testing...");
    Set<String> labels = new HashSet<String>(); // these are the labels we are evaluating the performance of
    labels.add("2");
    labels.add("4");

    // we are testing the performance for a range of confidence values, from 0 to 1.
    int numSteps = 100;
    double[] confidenceThresholds = new double[numSteps];
    double inc = .061 / numSteps;
    for (int i = 0; i < confidenceThresholds.length; i++) {
      confidenceThresholds[i] = i * inc;
    }

    // create evaluators for keeping track of performance
    ClassificationEvaluator[] evaluators = new ClassificationEvaluator[numSteps];
    for (int i = 0; i < evaluators.length; i++) {
      evaluators[i] = new ClassificationEvaluator(labels);
    }

    // run through the test set
    for (BreastCancer breastCancer : breastCancerTestData) {
      long[] spikes = breastCancerSpikeEncoder.encode(breastCancer);
      Set<Integer> convertedSpikes = spikeConverter.convert(spikes);

      double y = ahahNodeCircuit.update(convertedSpikes, 0); // NOTE: no supervised labels are passed here.

      // true labels for evaluation.
      Set<String> trueLabels = new HashSet<String>();
      trueLabels.add(breastCancer.getCellClass() + "");

      for (int i = 0; i < evaluators.length; i++) {

        List<String> labelOutput = new ArrayList<String>();

        if (y > confidenceThresholds[i]) {
          labelOutput.add("2");
        }
        else if (y < -confidenceThresholds[i]) {
          labelOutput.add("4");
        }

        evaluators[i].update(trueLabels, labelOutput);
      }
    }

    // plot results---->
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

    Chart chart = new Chart(300, 300, ChartTheme.Matlab);
    // chart.setChartTitle("Wisconsin Breast Cancer Benchmark - F1=" + df.format(maxF1) + " w/ConfidenceThreshold=" + df.format(confidenceThresholds[cIdx]));
    chart.setChartTitle("Breast Cancer - Circuit");
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

    BitmapEncoder.savePNGWithDPI(chart, "./PLOS_AHAH/Figures/Breast_Cancer_Circuit.png", 300);
    new SwingWrapper(chart).displayChart();

  }
}
