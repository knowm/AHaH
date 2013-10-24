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
package com.mancrd.ahah.clusterer.circuit;

import com.mancrd.ahah.clusterer.IClusterer;
import com.mancrd.ahah.clusterer.syndata.RandomSpikeGeneratorDefaultValues;

/**
 * Builder for IdealCircuitClusterer
 */
public class ClustererBuilder {

  private int numAHaHNodes = IClusterer.NUM_AHAH_NODES;
  private int maxInputSpikes = RandomSpikeGeneratorDefaultValues.MAX_INPUT_SPIKES;
  private int numBias = ClustererDefaultValues.NUM_BIAS;
  private double Vss = ClustererDefaultValues.VSS;
  private double Vdd = ClustererDefaultValues.VDD;
  private double readPeriod = ClustererDefaultValues.READ_PERIOD;
  private double writePeriod = ClustererDefaultValues.WRITE_PERIOD;

  public Clusterer build() {

    return new Clusterer(this);
  }

  public ClustererBuilder ahahNodes(int numAhahNodes) {

    this.numAHaHNodes = numAhahNodes;
    return this;
  }

  public ClustererBuilder numInputs(int numInputs) {

    this.maxInputSpikes = numInputs;
    return this;
  }

  public ClustererBuilder numBias(int numBias) {

    this.numBias = numBias;
    return this;
  }

  public ClustererBuilder Vss(int Vss) {

    this.Vss = Vss;
    return this;
  }

  public ClustererBuilder Vdd(int Vdd) {

    this.Vdd = Vdd;
    return this;
  }

  public ClustererBuilder readPeriod(int readPeriod) {

    this.readPeriod = readPeriod;
    return this;
  }

  public ClustererBuilder writePeriod(int writePeriod) {

    this.writePeriod = writePeriod;
    return this;
  }

  public int getNumAHaHNodes() {

    return numAHaHNodes;
  }

  public int getMaxInputSpikes() {

    return maxInputSpikes;
  }

  public int getNumBias() {

    return numBias;
  }

  public double getVss() {

    return Vss;
  }

  public double getVdd() {

    return Vdd;
  }

  public double getReadPeriod() {

    return readPeriod;
  }

  public double getWritePeriod() {

    return writePeriod;
  }

}
