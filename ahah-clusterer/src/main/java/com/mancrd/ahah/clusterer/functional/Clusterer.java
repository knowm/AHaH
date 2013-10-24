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
package com.mancrd.ahah.clusterer.functional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.mancrd.ahah.clusterer.Converter;
import com.mancrd.ahah.clusterer.IClusterer;
import com.mancrd.ahah.commons.LRUCache;

/**
 * @author alexnugent
 */
public final class Clusterer implements IClusterer {

  private final Converter converter = new Converter();

  /************** Core Parameters **************/

  /** number of input lines */
  private final int numInputs;

  /** number of nodes */
  private final int numAhahNodes;

  /** available energy. "learning rate" */
  private final double learningRate;

  /** used to inititalize the weights */
  private final double initWeightMag;

  /************** Internal State **************/

  /** weight matrix */
  private final double[][] ahahNodeWeightMatrix;

  /** AHAH node bias weights */
  private final double[] ahahNodeBiasWeights;

  private final LRUCache lineIDLRUCache;

  /**
   * Constructor
   * 
   * @param builder
   */
  public Clusterer(ClustererBuilder builder) {

    this.numInputs = builder.getmaxInputSpike();
    this.numAhahNodes = builder.getNumAhahNodes();
    this.learningRate = builder.getLearningRate();
    this.initWeightMag = builder.getInitWeightMag();

    ahahNodeWeightMatrix = new double[numAhahNodes][numInputs];
    ahahNodeBiasWeights = new double[numAhahNodes];

    initWeightsRandom();

    lineIDLRUCache = new LRUCache(builder.getmaxInputSpike());
  }

  /**
   * initialize the weights to random values, scaled by the structure constant
   */
  private void initWeightsRandom() {

    Random random = new Random();

    for (int i = 0; i < ahahNodeWeightMatrix.length; i++) {
      for (int j = 0; j < ahahNodeWeightMatrix[0].length; j++) {
        ahahNodeWeightMatrix[i][j] = random.nextGaussian() * initWeightMag;
      }
    }
  }

  /**
   * Spikes come in here. A label is returned.
   * 
   * @param spikes
   * @return Integer - A label
   */
  @Override
  public Integer put(Set<String> spikes) {

    if (spikes.size() > this.numInputs) {
      throw new IllegalArgumentException("feature length cannot be greater than the number of inputs into the core!");
    }

    // 1. compute activations
    double[] ahahNodeActivations = new double[this.numAhahNodes]; // this is the basis for the base-10 Integer signature, nodeActivation = y

    for (String lineId : spikes) {

      for (int j = 0; j < this.numAhahNodes; j++) {
        ahahNodeActivations[j] += ahahNodeWeightMatrix[j][lineIDLRUCache.put(lineId)];
      }
    }

    // 2. bias activations. Bias inputs are all active (+1) so its equivalent to just adding bias weight
    for (int j = 0; j < this.numAhahNodes; j++) {
      ahahNodeActivations[j] += ahahNodeBiasWeights[j];
    }

    // 3. get output AHAH output address. This performs pooling operation
    Integer signature = converter.getSignature(ahahNodeActivations);

    // 4. Update ahah nodes
    ahah(spikes, ahahNodeActivations);

    return signature;
  }

  /**
   * @param spikes
   * @param nodeActivations
   */
  private void ahah(Set<String> spikes, double[] nodeActivations) {

    // 1. update nodeWeightMatrix
    for (String lineId : spikes) {
      for (int j = 0; j < nodeActivations.length; j++) {
        ahahNodeWeightMatrix[j][lineIDLRUCache.put(lineId)] += fy(nodeActivations[j]);

      }
    }

    // 2. update nodeBiasWeights to prevent the null state, most stable states are the ones that most stable split the state
    for (int j = 0; j < nodeActivations.length; j++) {
      ahahNodeBiasWeights[j] -= learningRate * nodeActivations[j]; // anti-hebbian learning

    }

  }

  /**
   * AHaH unsupervised update
   * 
   * @param sumOfWeights (y)
   * @return
   */
  private double fy(double y) {

    return learningRate * (Math.signum(y) - y);
  }

  /**
   * get a feature, given a signature
   * 
   * @param signature - the signature
   * @param featureLength - the length of the features
   * @return - spikes corresponding to a given signature
   */
  Set<String> reverseLookup(Integer signature) {

    // 1. determine node activations array, ex. 1,-1,-1,1,1,1
    double[] nodeActivations = getNodeActivations(signature);

    // 2. gets something
    double[] inputLines = new double[ahahNodeWeightMatrix[0].length];

    for (int i = 0; i < nodeActivations.length; i++) {
      for (int j = 0; j < inputLines.length; j++) {
        inputLines[j] += (nodeActivations[i] + ahahNodeBiasWeights[i]) * ahahNodeWeightMatrix[i][j];
      }
    }

    // 3. create sorted input line activations
    List<InputLineActivation> inputLineActivations = new ArrayList<InputLineActivation>();
    for (int i = 0; i < inputLines.length; i++) {
      inputLineActivations.add(new InputLineActivation(i, inputLines[i]));
    }
    Collections.sort(inputLineActivations);

    // double sum = 0;
    int cutoffIdx = 0;
    for (int i = 0; i < inputLineActivations.size(); i++) {

      if (inputLineActivations.get(i).getActivation() <= 0) {
        cutoffIdx = i;
        break;
      }

    }

    // 4. crop inputLineActivations
    List<InputLineActivation> croppedInputLineActivations = inputLineActivations.subList(0, cutoffIdx);

    // 5. create feature
    Set<String> spikes = new HashSet<String>();
    for (InputLineActivation inputLineActivation : croppedInputLineActivations) {
      spikes.add(inputLineActivation.getInputId().toString());
    }

    return spikes;
  }

  /**
   * Given a signature (base-10 int), get nodeActivations
   * 
   * @param signature
   * @return
   */
  double[] getNodeActivations(int signature) {

    // 1. convert base-10 int to binary
    String bitPattern = Integer.toBinaryString(signature);

    // 2. fill in missing zeros in front
    while (bitPattern.length() < numAhahNodes) {
      bitPattern = "0" + bitPattern;
    }

    // 3. determine node activations array, ex. 1,-1,-1,1,1,1
    double[] nodeActivations = new double[numAhahNodes];
    for (int i = 0; i < nodeActivations.length; i++) {
      if (bitPattern.charAt(i) == '1') {
        nodeActivations[i] = 1;
      }
      else {
        nodeActivations[i] = -1;
      }
    }
    return nodeActivations;
  }

  /**
   * Creates a String showing all the weights in matrix form
   * 
   * @return - A String showing all the weights in matrix form
   */
  String weightMatrixToString() {

    DecimalFormat df = new DecimalFormat("#0.000000");

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < ahahNodeWeightMatrix.length; i++) {
      for (int j = 0; j < ahahNodeWeightMatrix[i].length; j++) {
        sb.append((ahahNodeWeightMatrix[i][j] > 0.0 ? " " : "") + df.format(ahahNodeWeightMatrix[i][j]) + " : ");
      }
      sb.append("\n");
    }
    return sb.toString();
  }

}
