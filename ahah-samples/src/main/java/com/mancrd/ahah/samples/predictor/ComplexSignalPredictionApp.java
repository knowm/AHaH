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
package com.mancrd.ahah.samples.predictor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mancrd.ahah.classifier.Classifier;
import com.mancrd.ahah.classifier.ClassifierOutput;
import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesColor;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.StyleManager.LegendPosition;
import com.xeiam.xchart.SwingWrapper;

/**
 * @author alexnugent
 */
public class ComplexSignalPredictionApp {

  private int timeSteps;
  private int testDuration;

  // define the complex signal
  private final double[] amplitudes = { .1, .5, .9, .5, .9 };
  private final double[] periods = { .05, .1, .2, .08, .15 };
  private final double[] phases = { .1, 0, .2, .9, .3 };
  private final double drift = 0; // .01;

  // parameters
  private final double reconstructionConfidenceTreshold = 0;
  private final int temporalBufferSize = 300; // higher for longer memory of past
  private final int spikeEncoderBinnerDepth = 4; // higher for more resolution

  private final double ke = .001; // exponential moving average of error.

  private double[] signalData;
  private double[] signalPredictionData;
  private double[] error;

  private final Classifier classifier = new Classifier();
  private final ComplexSignalGenerator signalGenerator = new ComplexSignalGenerator(amplitudes, periods, phases, drift);
  private final SimpleTemporalBufferSpikeEncoder simpleTemporalBufferSpikeEncoder = new SimpleTemporalBufferSpikeEncoder(temporalBufferSize, spikeEncoderBinnerDepth);

  /**
   * This app takes the following arguments:
   * <ul>
   * <li>int mode (1)
   * <li>int timeSteps (10000)
   * <li>int testDuration (300)
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    ComplexSignalPredictionApp complexSignalPrediction = new ComplexSignalPredictionApp();

    int mode = 1;
    int timeSteps = 10000;
    int testDuration = 300;

    try {
      mode = Integer.parseInt(args[0]);
      timeSteps = Integer.parseInt(args[1]);
      testDuration = Integer.parseInt(args[2]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    complexSignalPrediction.init(timeSteps, testDuration);

    if (mode == 0) {
      complexSignalPrediction.nonRecursivePredict();
    }
    else if (mode == 1) {
      complexSignalPrediction.recursivePredict();
    }
    else {
      throw new IllegalArgumentException("mode invalid");
    }

  }

  private void init(int timeSteps, int testDuration) {

    signalData = new double[timeSteps];
    signalPredictionData = new double[timeSteps];
    error = new double[timeSteps];
    this.timeSteps = timeSteps;
    this.testDuration = testDuration;
    this.classifier.setLearningRate(.1f);

  }

  private String[] getTrueLabels(double signal) {

    String[] labels = { simpleTemporalBufferSpikeEncoder.getBinner().put(signal) + "" };
    return labels;
  }

  private void nonRecursivePredict() throws IOException {

    double signalThen = 0;
    double signalNow = 0;
    for (int i = 0; i < timeSteps; i++) {

      signalNow = signalGenerator.getSignal();
      long[] signalSpikes = simpleTemporalBufferSpikeEncoder.encode(signalThen);

      String[] trueLabels = getTrueLabels(signalNow);

      // train the classifier with the old signal data to learn the current signal data
      ClassifierOutput classifierOutput = classifier.update(trueLabels, signalSpikes);

      // reconstruct the predicted spike code back into a real-value signal
      double signalPrediction = reconstruct(classifierOutput, reconstructionConfidenceTreshold);

      signalData[i] = signalNow;
      signalPredictionData[i] = signalPrediction;

      // error is computing as an exponential running average.
      if (i == 0) {
        error[i] = Math.abs(signalPrediction - signalNow);
      }
      else {
        error[i] = (1 - ke) * error[i - 1] + ke * Math.abs(signalPrediction - signalNow);
      }

      signalThen = signalNow;
    }
    plotError(error);
    plotSignal(signalData, signalPredictionData);
  }

  private void recursivePredict() throws IOException {

    double signalThen = 0.0;
    double signalNow = 0.0;

    for (int i = 0; i < timeSteps; i++) {

      if (i < timeSteps - testDuration) { // learn

        signalNow = signalGenerator.getSignal();
        String[] trueLabels = getTrueLabels(signalNow);

        long[] spikes = simpleTemporalBufferSpikeEncoder.encode(signalThen);
        ClassifierOutput classifierOutput = classifier.update(trueLabels, spikes);

        signalData[i] = signalNow;
        signalPredictionData[i] = reconstruct(classifierOutput, reconstructionConfidenceTreshold);
        // System.out.println("Spike Pattern Length = " + spikes.length);
      }
      else { // recursive prediction. feed output back as input.

        long[] signalSpikes = simpleTemporalBufferSpikeEncoder.encode(signalThen);
        ClassifierOutput classifierOutput = classifier.update(null, signalSpikes);
        signalNow = reconstruct(classifierOutput, reconstructionConfidenceTreshold);

        signalData[i] = signalGenerator.getSignal();
        signalPredictionData[i] = signalNow;
      }

      signalThen = signalNow;

    }
    System.out.println("Spike Pattern Space = " + simpleTemporalBufferSpikeEncoder.getSpikePatternSpace());

    plotSignal(signalData, signalPredictionData);

    System.out.println("Num AHaH Nodes = " + classifier.getNumUniqueLabels());
  }

  private double reconstruct(ClassifierOutput classifierOutput, double confidenceThreshold) {

    simpleTemporalBufferSpikeEncoder.getBinner();
    classifierOutput.getBestGuess();
    classifierOutput.getBestGuess().getLabelstring();

    return simpleTemporalBufferSpikeEncoder.getBinner().get(Integer.parseInt(classifierOutput.getBestGuess().getLabelstring()));
  }

  private void plotSignal(double[] signal, double[] signalPrediction) throws IOException {

    List<Number> predictionData1 = new ArrayList<Number>();
    List<Number> predictionData2 = new ArrayList<Number>();

    List<Number> signalData = new ArrayList<Number>();

    List<Number> xData0 = new ArrayList<Number>();
    List<Number> xData1 = new ArrayList<Number>();
    List<Number> xData2 = new ArrayList<Number>();

    for (int i = timeSteps - (int) (1.33 * testDuration); i < timeSteps; i++) {

      xData0.add(i);
      signalData.add(signal[i]);
      if (i < timeSteps - testDuration) {
        predictionData1.add(signalPrediction[i]);
        xData1.add(i);
      }
      else {
        predictionData2.add(signalPrediction[i]);
        xData2.add(i);
      }

    }

    Chart chart = new Chart(600, 300, ChartTheme.Matlab);
    chart.getStyleManager().setAxisTicksVisible(false);
    chart.getStyleManager().setPlotGridLinesVisible(false);
    chart.setXAxisTitle("Time Step");
    chart.setYAxisTitle("Signal Value");
    chart.getStyleManager().setAxisTitlesVisible(false);

    Series signal1_series = chart.addSeries("Signal", xData0, signalData);
    chart.getStyleManager().setLegendPosition(LegendPosition.InsideSW);
    signal1_series.setLineStyle(SeriesLineStyle.NONE);
    signal1_series.setMarkerColor(SeriesColor.PINK);

    Series signalPrediction1_series = chart.addSeries("Learning", xData1, predictionData1);
    signalPrediction1_series.setMarker(SeriesMarker.NONE);
    signalPrediction1_series.setLineColor(SeriesColor.BLACK);
    signalPrediction1_series.setLineStyle(SeriesLineStyle.DOT_DOT);

    Series signalPrediction2_series = chart.addSeries("Recursive Prediction", xData2, predictionData2);
    signalPrediction2_series.setMarker(SeriesMarker.NONE);
    signalPrediction2_series.setLineColor(SeriesColor.BLACK);

    BitmapEncoder.savePNGWithDPI(chart, "./PLOS_AHAH/Figures/Predictor_Mode_1.png", 300);
    new SwingWrapper(chart).displayChart();
  }

  private void plotError(double[] error) {

    Chart chart = new Chart(900, 350);
    chart.setChartTitle("Signal Prediction");
    chart.setXAxisTitle("Time Step");
    chart.setYAxisTitle("Error");

    Series error_series = chart.addSeries("Error", null, error);
    error_series.setMarker(SeriesMarker.NONE);

    new SwingWrapper(chart).displayChart();
  }

}
