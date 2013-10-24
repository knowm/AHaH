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
package com.mancrd.ahah.model.functional;

import java.util.Random;
import java.util.Set;

public class AHaHNode {

  private final int numBiasInputs;
  private final int numInputs;
  private final boolean isModelA;
  private final double alpha;
  private final double beta;
  private final double noise;
  private final double decay;

  private final double[] w;// weights

  private Set<Integer> active;// driven inputs
  private double y = 0;
  Random random = new Random();

  /**
   * Constructor
   * 
   * @param numInputs
   * @param numBiasInputs
   * @param isModelA
   */
  public AHaHNode(AHaHNodeBuilder aHaHNodeBuilder) {

    this.numInputs = aHaHNodeBuilder.getNumInputs();
    this.numBiasInputs = aHaHNodeBuilder.getNumBiasInputs();
    this.isModelA = aHaHNodeBuilder.isModelA();
    this.alpha = aHaHNodeBuilder.getAlpha();
    this.beta = aHaHNodeBuilder.getBeta();
    this.noise = aHaHNodeBuilder.getNoise();
    this.decay = aHaHNodeBuilder.getDecay();

    w = new double[numInputs + numBiasInputs];

    for (int i = 0; i < numInputs + numBiasInputs; i++) {
      w[i] = .1 * random.nextGaussian();
    }
  }

  public double getWeight(int index) {

    return w[index];
  }

  public double update(Set<Integer> inputSpikes) {

    this.active = inputSpikes;
    read();
    return y;
  }

  private double read() {

    // compute output
    y = 0;

    for (Integer idx : active) {
      y += w[idx];
    }

    for (int i = 0; i < numBiasInputs; i++) {
      y += w[numInputs + i];
    }

    // weight update-->

    // input weights
    for (Integer idx : active) {
      w[idx] += alpha * Math.signum(y) - beta * y;

    }

    // bias weights
    for (int i = 0; i < numBiasInputs; i++) {
      if (isModelA) {
        w[numInputs + i] += -alpha * Math.signum(y) + beta * y;// Functional Model A. leads to strange attractors in weight space, likely due to conversion from differentials to real numbers.
      }
      else {
        w[numInputs + i] += -beta * y;// Functional Model B. Preferred functional model. No strange attractors and matches circuit better across tests. Not yet derived from ideal circuit, although its
                                      // intuitively clear why it works.
      }

    }

    for (int i = 0; i < numInputs + numBiasInputs; i++) {
      w[i] += noise * random.nextGaussian();
    }
    for (int i = 0; i < numInputs + numBiasInputs; i++) {
      w[i] *= 1 - decay;
    }
    return y;
  }

  public int getNumInputs() {

    return numInputs;
  }

  public double getAlpha() {

    return alpha;
  }

  public double getBeta() {

    return beta;
  }

}
