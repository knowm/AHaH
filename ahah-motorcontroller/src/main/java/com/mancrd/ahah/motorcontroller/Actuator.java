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
package com.mancrd.ahah.motorcontroller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * This class encapsulates all of the learning
 * 
 * @author alexnugent
 */
public class Actuator {

  private final static double CONFIDENCE = 0;
  private final static double L = .1;
  private final static double ALPHA = L;
  private final static double BETA = 5 * L;
  private final static double NOISE_RATE_NODE = .01;
  private final static double STARTING_WEIGHT_MAG = 1E-9;

  private final int numJoints;
  private final int numFibers;
  private final int numNodes;

  private final Map<String, double[]> globalMap; // input line --> (weights)
  private final LearningBuffer buffer;
  private final Random random = new Random();

  /**
   * Constructor
   * 
   * @param labels
   * @param delay
   */
  public Actuator(int numJoints, int numFibers, int delay) {

    this.numJoints = numJoints;
    this.numFibers = numFibers;
    this.numNodes = numJoints * 2 * numFibers;

    globalMap = new HashMap<String, double[]>();
    buffer = new LearningBuffer(delay);
  }

  /**
   * @param spikes - a Collection of Objects. The internal map keys off of the Objects' toString() values, so make sure your Objects implement
   *          unique toString() implementations
   * @param value
   * @return - labels as ints
   */
  public int[] update(Set<String> spikes, double value) {

    // build feature Activation
    double[] yArray = new double[numNodes];
    String[] featureSetArray = new String[spikes.size()]; // this is used for buffering

    int idx = 0;
    for (String spike : spikes) { // loop through all of the features in the feature set

      featureSetArray[idx] = spike;

      double[] weights = getWeightVector(spike);
      weights[weights.length - 1] = 0;
      for (int i = 0; i < yArray.length; i++) {
        yArray[i] += weights[i];
        weights[weights.length - 1] += Math.abs(weights[i]); // this is used to store the total "link share" for each feature for rapid feedback
                                                             // later.
      }

      idx++;
    }

    // add noise
    for (int i = 0; i < yArray.length; i++) {
      yArray[i] += NOISE_RATE_NODE * random.nextGaussian();
    }

    // Learn delayed
    learnDelayed(featureSetArray, yArray, value);

    return getActuations(yArray);
  }

  private int[] getActuations(double[] yArray) {

    int[] actuations = new int[numJoints];

    if (yArray == null) {
      return actuations;
    }

    int idx = 0;
    for (int m = 0; m < numJoints; m++) {
      for (int mi = 0; mi < 2; mi++) {
        for (int f = 0; f < numFibers; f++) {

          if (yArray[idx] > 0) {
            if (mi == 0) {
              actuations[m]++;
            }
            else {
              actuations[m]--;
            }
          }
          idx++;
        }
      }
    }

    return actuations;
  }

  /**
   * @param feature
   * @param y
   * @param value
   */
  private void learnDelayed(String[] feature, double[] yArray, double value) {

    BufferElement bufferElement = this.buffer.put(feature, yArray, value); // learning takes place on buffered data, not live data!
    if (bufferElement != null) {
      double dV = value - bufferElement.getValue();
      double[] y = bufferElement.getY();
      double h = Math.signum(dV);
      double L = 1.0 / feature.length;
      double[] desired = getDesired(y, h);
      for (int i = 0; i < bufferElement.getFeature().length; i++) {
        double[] weights = getWeightVector(bufferElement.getFeature()[i]);
        for (int j = 0; j < y.length; j++) {
          weights[j] += -L * BETA * y[j];// anti-hebbian learning from access

          if (desired[j] * y[j] > 0) {
            weights[j] += Math.signum(y[j]) * L * ALPHA;// shot of reinformence
          }

        }
      }
    }

  }

  private double[] getDesired(double[] y, double h) {

    double[] desired = new double[y.length];
    int[] actuations = getActuations(y);

    int idx = 0;
    for (int m = 0; m < numJoints; m++) {
      for (int mi = 0; mi < 2; mi++) {
        for (int f = 0; f < numFibers; f++) {
          if (h > 0) {// better than expected. reward this
            if (mi == 0) {// positive collective
              desired[idx] = Math.signum(actuations[m]);
            }
            else {// negative going collective
              desired[idx] = -Math.signum(actuations[m]);
            }
          }
          else {// worse or equal to expected. should have done opposite
            if (mi == 0) {// positive collective
              desired[idx] = -Math.signum(actuations[m]);
            }
            else {// negative going collective
              desired[idx] = Math.signum(actuations[m]);
            }
          }

          idx++;
        }
      }
    }
    return desired;
  }

  /**
   * gets the weight and creates a new random weight if it does not exist.
   * 
   * @param spike
   * @return
   */
  private double[] getWeightVector(String spike) {

    double[] weights = globalMap.get(spike);
    if (weights == null) {
      weights = new double[numNodes];// the extra dimension is used to store the weight magnitude sum...
      for (int i = 0; i < weights.length; i++) {
        weights[i] = STARTING_WEIGHT_MAG * (2 * Math.random() - 1);
      }
      globalMap.put(spike, weights);
    }
    return weights;
  }

  double getConfidence() {

    return CONFIDENCE;
  }

  public int getNumUniqueLabels() {

    return globalMap.size();
  }

}
