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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mancrd.ahah.classifier.ClassificationEvaluator;
import com.mancrd.ahah.classifier.ClassificationRate;
import com.mancrd.ahah.classifier.Classifier;
import com.mancrd.ahah.classifier.ClassifierOutput;
import com.xeiam.datasets.reuters21578.Reuters21578;
import com.xeiam.datasets.reuters21578.Reuters21578DAO;
import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.SwingWrapper;

/**
 * @author timmolter
 */
public class Reuters21578SemiSupervisedApp {

  public static DecimalFormat df = new DecimalFormat(".000");

  /**
   * <ul>
   * <li>double unsupervisedConfidenceThreshold (1.0): confidence threshold, above which unsupervised learning will take place
   * <li>double startAuto (.25): percentage through data when unsupervised learning starts;
   * <li>double learningRate (.5): classifier learning rate
   * 
   * @param args
   * @throws InterruptedException
   * @throws IOException
   */
  public static void main(String[] args) throws InterruptedException, IOException {

    Reuters21578SemiSupervisedApp rca = new Reuters21578SemiSupervisedApp();
    rca.go(args);
  }

  public void go(String[] args) throws IOException {

    double unsupervisedConfidenceThreshold = 1;
    double startAuto = .25;
    float learningRate = .5f;
    try {
      unsupervisedConfidenceThreshold = Double.parseDouble(args[0]);
      startAuto = Double.parseDouble(args[1]);
      learningRate = Float.parseFloat(args[2]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    Classifier classifier = new Classifier();
    classifier.setLearningRate(learningRate);
    classifier.setUnsupervisedConfidenceThreshold(unsupervisedConfidenceThreshold);
    classifier.setUnsupervisedEnabled(true);

    Reuters21578SpikeEncoder reuters21578SpikeEncoder = new Reuters21578SpikeEncoder();

    // only measure performance on top-10 labels (this is what most published benchmarks do).
    String[] labelArray = { "earn", "acq", "money-fx", "grain", "crude", "trade", "interest", "ship", "wheat", "corn" };
    Set<String> labels = new HashSet<String>(Arrays.asList(labelArray));
    ClassificationEvaluator evaluator = new ClassificationEvaluator(labels);

    System.out.println("training");
    List<Reuters21578> dataSet = Reuters21578DAO.selectModApte("TRAIN", true);
    dataSet.addAll(Reuters21578DAO.selectModApte("TEST", true));

    Collections.shuffle(dataSet);// this insures

    List<double[]> plotData = new ArrayList<double[]>();
    for (int i = 0; i < labelArray.length; i++) {
      plotData.add(new double[(dataSet.size())]);
    }

    for (int i = 0; i < dataSet.size(); i++) {
      Reuters21578 reuters21578 = dataSet.get(i);
      String[] topicTrueLabels = reuters21578.getTopics().split(",");
      long[] spikes = reuters21578SpikeEncoder.encode(reuters21578);
      ClassifierOutput classifierOutput;
      if ((double) i / dataSet.size() < startAuto) {
        classifierOutput = classifier.update(topicTrueLabels, spikes);
      }
      else {
        classifierOutput = classifier.update(null, spikes);// learn unsupervised. From
      }
      evaluator.update(new HashSet(Arrays.asList(topicTrueLabels)), classifierOutput.getSortedLabels(0.0));

      for (int j = 0; j < labelArray.length; j++) {
        ClassificationRate cr = evaluator.getClassificationRate(labelArray[j]);
        if (cr != null) {
          plotData.get(j)[i] = cr.getF1();
        }
      }
    }

    Chart chart = new Chart(600, 300, ChartTheme.Matlab);
    chart.setXAxisTitle("Sample Number");
    chart.setYAxisTitle("F1 Score");

    for (int j = 0; j < labelArray.length; j++) {
      Series series = chart.addSeries(labelArray[j], null, plotData.get(j));
      series.setMarker(SeriesMarker.NONE);
    }

    new SwingWrapper(chart).displayChart();
    BitmapEncoder.savePNGWithDPI(chart, "./PLOS_AHAH/Figures/Reuters_Semi_Supervised.png", 300);

  }

}
