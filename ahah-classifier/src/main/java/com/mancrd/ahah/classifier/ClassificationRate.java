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

/**
 * This class is used to hold performance evaluating statistics for classifications related to one class label. TP=TruePositive FP=FalsePositive
 * TN=TrueNegative FN=FalseNegative
 * 
 * @author timmolter
 */
public class ClassificationRate implements Comparable<ClassificationRate> {

  private final String label;

  private int truePositiveCount;
  private int falsePositiveCount;
  private int falseNegativeCount;
  private int trueNegativeCount;

  /**
   * Constructor
   */
  public ClassificationRate(String label) {

    this.label = label;
    truePositiveCount = 0;
    falsePositiveCount = 0;
  }

  public void incFalseNegativeCount() {

    falseNegativeCount++;
  }

  public void incTrueNegativeCount() {

    trueNegativeCount++;
  }

  public void incTruePositiveCount() {

    truePositiveCount++;
  }

  public void incFalsePositiveCount() {

    falsePositiveCount++;
  }

  public String getLabel() {

    return label;
  }

  /**
   * @return F1=TP/(2*TP+FP FN)
   */
  public double getF1() {

    double f1 = 2 * ((getPrecision() * getRecall()) / (getPrecision() + getRecall()));

    if (Double.isNaN(f1)) {
      return 0;
    }
    else {
      return f1;
    }

  }

  /**
   * @return Recall=TP/(TP+FN)
   */
  public double getRecall() {

    return (double) (truePositiveCount) / (double) (truePositiveCount + falseNegativeCount);
  }

  /**
   * @return Accuracy=(TP+TN)/(TP+FP+TN+FN)
   */
  public double getAccuracy() {

    return (double) (truePositiveCount + trueNegativeCount) / (double) (truePositiveCount + falsePositiveCount + trueNegativeCount + falseNegativeCount);
  }

  /**
   * @return Precision=TP/(TP+FP)
   */
  public double getPrecision() {

    return (double) (truePositiveCount) / (double) (truePositiveCount + falsePositiveCount);
  }

  @Override
  public int compareTo(ClassificationRate other) {

    if (other.getAccuracy() > getAccuracy()) {
      return 1;
    }
    else if (other.getAccuracy() < getAccuracy()) {
      return -1;
    }
    return 0;
  }

  public int getTruePositiveCount() {

    return truePositiveCount;
  }

  public void setTruePositiveCount(int truePositiveCount) {

    this.truePositiveCount = truePositiveCount;
  }

  public int getFalsePositiveCount() {

    return falsePositiveCount;
  }

  public void setFalsePositiveCount(int falsePositiveCount) {

    this.falsePositiveCount = falsePositiveCount;
  }

  public int getTotalCount() {

    return truePositiveCount + falsePositiveCount + trueNegativeCount + falseNegativeCount;
  }

  public int getFalseNegativeCount() {

    return falseNegativeCount;
  }

  public int getTrueNegativeCount() {

    return trueNegativeCount;
  }

  @Override
  public String toString() {

    DecimalFormat df = new DecimalFormat(".0000");
    StringBuffer buf = new StringBuffer();
    buf.append(label);
    buf.append(": ");
    buf.append("(");
    buf.append("TP=" + truePositiveCount + ",");
    buf.append("TN=" + trueNegativeCount + ",");
    buf.append("FP=" + falsePositiveCount + ",");
    buf.append("FN=" + falseNegativeCount + ",");
    buf.append("T=" + getTotalCount() + ",");
    buf.append("Ac=" + df.format(getAccuracy()) + ",");
    buf.append("Pr=" + df.format(getPrecision()) + ",");
    buf.append("Re=" + df.format(getRecall()) + ",");
    buf.append("F1=" + df.format(getF1()) + ")");
    return buf.toString();
  }

}
