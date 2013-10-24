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

import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntFloatProcedure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This DTO encapsulates an activation map from a classifier and has convenience methods to get at the relevant data
 * 
 * @author alexnugent
 */
public final class ClassifierOutput implements TIntFloatProcedure {

  /** the activation map */
  private LabelOutput[] labelOutputs = null;
  private final TIntFloatHashMap labelActivationMap;

  /**
   * Constructor
   * 
   * @param activations
   */
  public ClassifierOutput() {

    labelActivationMap = new TIntFloatHashMap();
  }

  public LabelOutput[] getLabelOutputs() {

    if (labelOutputs == null) {
      LabelOutputArrayProcedure labelOutputArrayProcedure = new LabelOutputArrayProcedure(labelActivationMap.size());
      labelActivationMap.forEachEntry(labelOutputArrayProcedure);
      labelOutputs = labelOutputArrayProcedure.getLabelOutputs();
      Arrays.sort(labelOutputs);
    }

    return labelOutputs;
  }

  public void translateLabels(TIntObjectHashMap<String> labelReverseMap) {

    if (getLabelOutputs() == null) {
      return;
    }
    for (int i = 0; i < getLabelOutputs().length; i++) {
      labelOutputs[i].setLabelstring(labelReverseMap.get(labelOutputs[i].getLabel()));
    }
  }

  /**
   * Returns the sum total activation for all labels in the output that exceed the given threshold.
   * 
   * @param activationThreshold
   * @return
   */
  public float getTotalConfidence(double confidenceThreshold) {

    float total = 0;
    for (int i = 0; i < getLabelOutputs().length; i++) {
      if (labelOutputs[i].getConfidence() < confidenceThreshold) {
        return total;
      }
      else {
        total += labelOutputs[i].getConfidence();
      }
    }
    return total;
  }

  /**
   * Gets the Activation objects sorted by activation above a given confidence threshold
   * 
   * @param activationThreshold
   * @return
   */
  public List<LabelOutput> getSortedLabelOutputs(double confidenceThreshold) {

    List<LabelOutput> output = new ArrayList<LabelOutput>();

    for (int i = 0; i < getLabelOutputs().length; i++) {
      if (labelOutputs[i].getConfidence() > confidenceThreshold) {
        output.add(labelOutputs[i]);
      }
      else {
        return output;
      }
    }
    return output;
  }

  /**
   * Gets the Activation objects sorted by activation above a given confidence threshold
   * 
   * @param activationThreshold
   * @return
   */
  public Set<LabelOutput> getLabelOutputSet(double confidenceThreshold) {

    Set<LabelOutput> output = new HashSet<LabelOutput>();

    for (int i = 0; i < getLabelOutputs().length; i++) {
      if (labelOutputs[i].getConfidence() > confidenceThreshold) {
        output.add(labelOutputs[i]);
      }
      else {
        return output;
      }
    }
    return output;
  }

  /**
   * Gets the Activation objects sorted by activation above a given confidence threshold
   * 
   * @param activationThreshold
   * @return
   */
  public List<String> getSortedLabels(double confidenceThreshold) {

    List<String> output = new ArrayList<String>();

    for (int i = 0; i < getLabelOutputs().length; i++) {
      if (labelOutputs[i].getConfidence() >= confidenceThreshold) {
        output.add(labelOutputs[i].getLabelstring());
      }
      else {
        return output;
      }
    }
    return output;
  }

  /**
   * Gets the Activation objects sorted by activation above a given confidence threshold
   * 
   * @param activationThreshold
   * @return
   */
  public Set<String> getLabelSet(double confidenceThreshold) {

    Set<String> output = new HashSet<String>();

    for (int i = 0; i < getLabelOutputs().length; i++) {
      if (labelOutputs[i].getConfidence() >= confidenceThreshold) {
        output.add(labelOutputs[i].getLabelstring());
      }
      else {
        return output;
      }
    }
    return output;
  }

  /**
   * Gets the Activation objects sorted by activation above a given confidence threshold
   * 
   * @param activationThreshold
   * @return
   */
  public List<String> getSortedLabels() {

    List<String> output = new ArrayList<String>();
    for (int i = 0; i < getLabelOutputs().length; i++) {
      output.add(labelOutputs[i].getLabelstring());
    }
    return output;
  }

  /**
   * Returns the highest ranked label or best-guess label
   * 
   * @return
   */
  public LabelOutput getBestGuess() {

    if (getLabelOutputs() != null && getLabelOutputs().length > 0) {
      return labelOutputs[0];
    }
    else
      return null;
  }

  public LabelOutput getLabelOutput(String label) {

    for (int i = 0; i < getLabelOutputs().length; i++) {
      if (labelOutputs[i].getLabelstring().equalsIgnoreCase(label)) {
        return labelOutputs[i];
      }
    }

    return null;
  }

  /**
   * Returns the highest ranked label above the given confidence threshold
   * 
   * @return
   */
  public LabelOutput getBestGuessLabelOuputAboveThreshold(double confidenceThreshold) {

    LabelOutput output = getBestGuess();
    if (output.getConfidence() >= confidenceThreshold) {
      return output;
    }
    else {
      return null;
    }
  }

  /**
   * Returns the highest ranked label above the given confidence threshold
   * 
   * @return
   */
  public String getBestGuessLabelAboveThreshold(double confidenceThreshold) {

    LabelOutput output = getBestGuess();
    if (output.getConfidence() >= confidenceThreshold) {
      return output.getLabelstring();
    }
    else {
      return null;
    }
  }

  @Override
  public boolean execute(int label, float weight) {

    if (!labelActivationMap.contains(label)) {
      labelActivationMap.put(label, weight);
    }
    else {
      float y = labelActivationMap.get(label);
      y += weight;
      labelActivationMap.put(label, y);
    }

    return true;// keep the itteration going
  }
}

class LabelOutputArrayProcedure implements TIntFloatProcedure {

  private final LabelOutput[] labelOutputs;
  private int idx = 0;

  public LabelOutputArrayProcedure(int size) {

    labelOutputs = new LabelOutput[size];
  }

  public LabelOutput[] getLabelOutputs() {

    return labelOutputs;
  }

  @Override
  public boolean execute(int label, float y) {

    labelOutputs[idx] = new LabelOutput(label, y);
    idx++;
    return true;
  }
}
