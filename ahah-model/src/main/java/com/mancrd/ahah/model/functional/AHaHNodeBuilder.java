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

/**
 * Builder for AHaH21Circuit
 */
public class AHaHNodeBuilder {

  private int numInputs;
  private int numBiasInputs;
  private boolean isModelA = true;
  private double alpha = .001;
  private double beta = .001;
  private double noise = .0005;
  private double decay = .0005;

  /**
   * return fully built object
   * 
   * @return a AHaHNode
   */
  public AHaHNode build() {

    return new AHaHNode(this);
  }

  public AHaHNodeBuilder decay(double decay) {

    this.decay = decay;
    return this;
  }

  public AHaHNodeBuilder noise(double noise) {

    this.noise = noise;
    return this;
  }

  public AHaHNodeBuilder numInputs(int numInputs) {

    this.numInputs = numInputs;
    return this;
  }

  public AHaHNodeBuilder numBiasInputs(int numBiasInputs) {

    this.numBiasInputs = numBiasInputs;
    return this;
  }

  public AHaHNodeBuilder isModelA(boolean isModelA) {

    this.isModelA = isModelA;
    return this;
  }

  public AHaHNodeBuilder alpha(double alpha) {

    this.alpha = alpha;
    return this;
  }

  public AHaHNodeBuilder beta(double beta) {

    this.beta = beta;
    return this;
  }

  public int getNumInputs() {

    return numInputs;
  }

  public int getNumBiasInputs() {

    return numBiasInputs;
  }

  public boolean isModelA() {

    return isModelA;
  }

  public double getAlpha() {

    return alpha;
  }

  public double getBeta() {

    return beta;
  }

  public double getNoise() {

    return noise;
  }

  public double getDecay() {

    return decay;
  }
}
