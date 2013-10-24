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
package com.mancrd.ahah.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Used to keep track of classification performance.
 * 
 * @author alexnugent
 */
public class ClassificationEvaluator {

  /** score keeper for classifier classifications */
  private final Map<String, ClassificationRate> classificationRateMap;

  private boolean isDeclared = false;

  public ClassificationEvaluator() {

    classificationRateMap = new HashMap<String, ClassificationRate>();
  }

  /**
   * Constructor Must provide a set of labels that will be used for evaluation. Other labels will be ignored for evaluation.
   */
  public ClassificationEvaluator(Set<String> labels) {

    classificationRateMap = new HashMap<String, ClassificationRate>();
    declareLabels(labels);
    isDeclared = true;
  }

  /**
   * @param trueLabel: the actual (supervised) label.
   * @param label: the label given by the classifier.
   */
  public void update(String trueLabel, String label) {

    Set<String> trueLabels = new HashSet<String>();
    trueLabels.add(trueLabel);

    List<String> labels = new ArrayList<String>();
    labels.add(label);
    update(trueLabels, labels);
  }

  /**
   * declare the labels that should be evaluated.
   * 
   * @param labels
   */
  private void declareLabels(Set<String> labels) {

    for (String label : labels) {
      if (label.length() == 0 || label.equalsIgnoreCase("")) {
        continue;
      }
      ClassificationRate classificationRate = classificationRateMap.get(label);
      if (classificationRate == null) {
        classificationRate = new ClassificationRate(label);
        classificationRateMap.put(label, classificationRate);
      }
    }
  }

  /**
   * Here's where results come in
   * 
   * @param trueLabels: the supervised labels
   * @param labels: the labels given by the classifier.
   */
  public void update(Set<String> trueLabels, List<String> labels) {

    if (isDeclared) {

      Set<String> allLabels = classificationRateMap.keySet();
      for (String s : allLabels) {
        ClassificationRate classificationRate = classificationRateMap.get(s);

        if (classificationRate == null) {
          throw new RuntimeException("Label as not previously been declared");
        }

        if (trueLabels.contains(s) && labels.contains(s)) {// TRUE POSITIVE
          classificationRate.incTruePositiveCount();
        }
        else if (trueLabels.contains(s) && !labels.contains(s)) {// FALSE NEGATIVE
          classificationRate.incFalseNegativeCount();
        }
        else if (!trueLabels.contains(s) && labels.contains(s)) {// FALSE POSITIVE
          classificationRate.incFalsePositiveCount();
        }
        else if (!trueLabels.contains(s) && !labels.contains(s)) {// TRUE NEGATIVE
          classificationRate.incTrueNegativeCount();
        }
      }
    }
    else {

      for (String trueLabel : trueLabels) {
        if (labels.contains(trueLabel)) {// true positive
          classificationRateMap.get(trueLabel).incTruePositiveCount();
        }
        else {// false-negative
          classificationRateMap.get(trueLabel).incFalseNegativeCount();
        }
      }
      for (String label : labels) {
        if (!trueLabels.contains(label)) {// false positive
          classificationRateMap.get(label).incFalsePositiveCount();
        }
      }

    }

  }

  /**
   * calculates the classification rate for a single trueLabelId
   * 
   * @param trueLabel
   * @return double - the classification for a single trueLabel.
   */
  public ClassificationRate getClassificationRate(String trueLabel) {

    return classificationRateMap.get(trueLabel);

  }

  /**
   * @return micro-averaged F1 score. F1=2*TP/(2*TP+FP FN)
   */
  public double getF1MicroAve() {

    int[] c = getCounts();

    return 2 * ((double) c[0]) / (2 * c[0] + c[1] + c[3]);
  }

  /**
   * @return micro-averaged recall score. Recall=TP/(TP+FN)
   */
  public double getRecallMicroAve() {

    int[] c = getCounts();
    if (c[0] == 0) {
      return 0.0;
    }
    return (double) (c[0]) / (double) (c[0] + c[3]);
  }

  /**
   * @return micro-averaged precision score. Precision=TP/(TP+FP)
   */
  public double getPrecisionMicroAve() {

    int[] c = getCounts();
    if (c[0] == 0) {
      return 0.0;
    }
    return (double) (c[0]) / (double) (c[0] + c[1]);

  }

  /**
   * @return micro averaged accuracy. Accuracy=(TP+TN)/(TP+FP+TN+FN)
   */
  public double getAccuracyMicroAve() {

    int[] c = getCounts();
    return (double) (c[0] + c[2]) / (double) (c[0] + c[1] + c[2] + c[3]);
  }

  /**
   * @return macro-averaged F1 score. F1=TP/(2*TP+FP FN)
   */
  public double getF1MacroAve() {

    double v = 0;
    for (ClassificationRate classificationRate : classificationRateMap.values()) {
      v += classificationRate.getF1();
    }
    return v / classificationRateMap.size();
  }

  /**
   * @return macro-averaged recall score. Recall=TP/(TP+FN)
   */
  public double getRecallMacroAve() {

    double v = 0;
    for (ClassificationRate classificationRate : classificationRateMap.values()) {
      v += classificationRate.getRecall();
    }
    return v / classificationRateMap.size();
  }

  /**
   * @return macro-averaged precision score. Precision=TP/(TP+FP)
   */
  public double getPrecisionMacroAve() {

    double v = 0;
    for (ClassificationRate classificationRate : classificationRateMap.values()) {
      v += classificationRate.getPrecision();
    }
    return v / classificationRateMap.values().size();
  }

  /**
   * @return macro averaged accuracy. Accuracy=(TP+TN)/(TP+FP+TN+FN)
   */
  public double getAccuracyMacroAve() {

    double v = 0;
    for (ClassificationRate classificationRate : classificationRateMap.values()) {
      v += classificationRate.getAccuracy();
    }
    return v / classificationRateMap.values().size();
  }

  private int[] getCounts() {

    int totalTruePositiveCount = 0;
    int totalFalsePositiveCount = 0;
    int totalTrueNegativeCount = 0;
    int totalFalseNegativeCount = 0;

    for (ClassificationRate classificationRate : classificationRateMap.values()) {
      totalTruePositiveCount += classificationRate.getTruePositiveCount();
      totalFalsePositiveCount += classificationRate.getFalsePositiveCount();
      totalTrueNegativeCount += classificationRate.getTrueNegativeCount();
      totalFalseNegativeCount += classificationRate.getFalseNegativeCount();
    }
    int[] counts = { totalTruePositiveCount, totalFalsePositiveCount, totalTrueNegativeCount, totalFalseNegativeCount };
    return counts;
  }

  /**
   * @return a List of all ClassificationRate objects holding data for each label.
   */
  public List<ClassificationRate> getSortedClassificationRates() {

    List<ClassificationRate> classificationRateList = new ArrayList<ClassificationRate>(classificationRateMap.values());
    Collections.sort(classificationRateList);
    return classificationRateList;
  }

  @Override
  public String toString() {

    DecimalFormat df = new DecimalFormat(".0000");

    StringBuilder sb = new StringBuilder();

    sb.append(System.getProperty("line.separator"));

    for (ClassificationRate classificationRate : getSortedClassificationRates()) {
      sb.append(classificationRate.toString());
      sb.append(System.getProperty("line.separator"));
    }

    sb.append("OVERALL F1 (micro/macro) = " + df.format(getF1MicroAve()) + "/" + df.format(getF1MacroAve()));
    sb.append(System.getProperty("line.separator"));
    sb.append("OVERALL Precision (micro/macro) = " + df.format(getPrecisionMicroAve()) + "/" + df.format(getPrecisionMacroAve()));
    sb.append(System.getProperty("line.separator"));
    sb.append("OVERALL Recal (micro/macro) = " + df.format(getRecallMicroAve()) + "/" + df.format(getRecallMacroAve()));
    sb.append(System.getProperty("line.separator"));
    sb.append("OVERALL ACCURACY (micro/macro) = " + df.format(getAccuracyMicroAve()) + "/" + df.format(getAccuracyMacroAve()));
    sb.append(System.getProperty("line.separator"));

    return sb.toString();
  }
}
