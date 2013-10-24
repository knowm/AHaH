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

import com.mancrd.ahah.clusterer.IClusterer;
import com.mancrd.ahah.clusterer.syndata.RandomSpikeGeneratorDefaultValues;

public class ClustererBuilder {

  private int maxInputSpikes = RandomSpikeGeneratorDefaultValues.MAX_INPUT_SPIKES;
  private int numAhahNodes = IClusterer.NUM_AHAH_NODES;
  private double learningRate = com.mancrd.ahah.clusterer.functional.ClustererDefaultValues.LEARNING_RATE;
  private double initWeightMag = com.mancrd.ahah.clusterer.functional.ClustererDefaultValues.INIT_WEIGHT_MAG;

  public ClustererBuilder numInputs(int numInputs) {

    this.maxInputSpikes = numInputs;
    return this;
  }

  public ClustererBuilder ahahNodes(int numAhahNodes) {

    this.numAhahNodes = numAhahNodes;
    return this;
  }

  public ClustererBuilder learningRate(double learningRate) {

    this.learningRate = learningRate;
    return this;
  }

  public ClustererBuilder maxInitWeight(double initWeightMag) {

    this.initWeightMag = initWeightMag;
    return this;
  }

  /**
   * return fully built object
   * 
   * @return a Clusterer
   */
  public Clusterer build() {

    return new Clusterer(this);
  }

  public int getmaxInputSpike() {

    return maxInputSpikes;
  }

  public int getNumAhahNodes() {

    return numAhahNodes;
  }

  public double getLearningRate() {

    return learningRate;
  }

  public double getInitWeightMag() {

    return initWeightMag;
  }

}
