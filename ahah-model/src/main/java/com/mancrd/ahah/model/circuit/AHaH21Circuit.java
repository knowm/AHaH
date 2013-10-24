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
package com.mancrd.ahah.model.circuit;

import java.util.Set;

import com.mancrd.ahah.model.circuit.mss.AgChalcMemristor;
import com.mancrd.ahah.model.circuit.mss.AgInSbTeMemristor;
import com.mancrd.ahah.model.circuit.mss.GSTMemristor;
import com.mancrd.ahah.model.circuit.mss.MSSMemristor;
import com.mancrd.ahah.model.circuit.mss.PdWO3WMemristor;

/**
 * This class represents the AHaH 2-1 Circuit Model
 */
public class AHaH21Circuit {

  private final int numBiasInputs;
  private final int numInputs;
  private final double Vdd;
  private final double Vss;
  private final double readPeriod; // seconds
  private final double writePeriod; // seconds
  private final MSSMemristor[] memristorsA; // memristor array for A side
  private final MSSMemristor[] memristorsB; // memristor array for B side

  /** runtime variables */
  private double Vy = 0; // output voltage

  /**
   * Constructor
   * 
   * @param aHaH21CircuitBuilder
   */
  public AHaH21Circuit(AHaH21CircuitBuilder aHaH21CircuitBuilder) {

    this.numInputs = aHaH21CircuitBuilder.getNumInputs();
    this.numBiasInputs = aHaH21CircuitBuilder.getNumBiasInputs();
    this.Vdd = aHaH21CircuitBuilder.getVdd();
    this.Vss = aHaH21CircuitBuilder.getVss();
    this.readPeriod = aHaH21CircuitBuilder.getReadPeriod();
    this.writePeriod = aHaH21CircuitBuilder.getWritePeriod();

    memristorsA = new MSSMemristor[numInputs + numBiasInputs];
    memristorsB = new MSSMemristor[numInputs + numBiasInputs];

    for (int i = 0; i < numInputs + numBiasInputs; i++) {
      switch (aHaH21CircuitBuilder.getMemristorType()) {
      case AgChalc:
        memristorsA[i] = new AgChalcMemristor(.1 * Math.random());
        memristorsB[i] = new AgChalcMemristor(.1 * Math.random());
        break;

      case WOx:
        memristorsA[i] = new PdWO3WMemristor(.1 * Math.random());
        memristorsB[i] = new PdWO3WMemristor(.1 * Math.random());
        break;

      case AIST:
        memristorsA[i] = new AgInSbTeMemristor(.1 * Math.random());
        memristorsB[i] = new AgInSbTeMemristor(.1 * Math.random());
        break;

      case GST:
        memristorsA[i] = new GSTMemristor(.1 * Math.random());
        memristorsB[i] = new GSTMemristor(.1 * Math.random());
        break;

      default:
        break;
      }
    }
  }

  public double getSpikeInputWeightMagnitudeSum(Set<Integer> inputSpikes) {

    double s = 0;
    for (Integer idx : inputSpikes) {
      s += getWeightConjugate(idx);
    }

    for (int idx = numInputs; idx < numInputs + numBiasInputs; idx++) {
      s += getWeightConjugate(idx);
    }

    return s;
  }

  public double getSpikeInputWeightSum(Set<Integer> inputSpikes) {

    double s = 0;
    for (Integer idx : inputSpikes) {
      s += getWeight(idx);
    }

    for (int idx = numInputs; idx < numInputs + numBiasInputs; idx++) {
      s += getWeight(idx);
    }

    return s;
  }

  public double getWeightConjugate(int index) {

    return (Vdd * memristorsA[index].getConductance() - Vss * memristorsB[index].getConductance());
  }

  public double getWeight(int index) {

    return (Vdd * memristorsA[index].getConductance() + Vss * memristorsB[index].getConductance());

  }

  /**
   * provides unsupervised or supervised AntiHebbian (read) and Hebbian (write) learning.
   * 
   * @param inputSpikes a list of input lines that are active.
   * @param superviseSignal +1 or -1 for supervised. 0 for unsupervised
   * @return
   */
  public double update(Set<Integer> inputSpikes, int superviseSignal) {

    read(inputSpikes);
    write(inputSpikes, superviseSignal);
    return Vy;
  }

  private double read(Set<Integer> inputSpikes) {

    // Compute Output Voltage---->
    double conductanceA = 0.0;
    double conductanceB = 0.0;

    for (Integer idx : inputSpikes) {
      conductanceA += memristorsA[idx].getConductance();
      conductanceB += memristorsB[idx].getConductance();
    }

    for (int i = 0; i < numBiasInputs; i++) {
      conductanceA += memristorsA[numInputs + i].getConductance();
      conductanceB += memristorsB[numInputs + i].getConductance();
    }

    // get output voltage. KCL at Node y.
    Vy = (Vdd * conductanceA + Vss * conductanceB) / (conductanceA + conductanceB);

    // Update Memristors---->

    // input weights
    for (int i = 0; i < numInputs; i++) {
      if (inputSpikes.contains(i)) {// non-floating active inputs
        memristorsA[i].dG(Vdd - Vy, readPeriod);
        memristorsB[i].dG(Vy - Vss, readPeriod);
      }
      else {// floating inputs
        memristorsA[i].dG(0, readPeriod);
        memristorsB[i].dG(0, readPeriod);
      }
    }

    // bias weights. polarity is reversed
    for (int i = 0; i < numBiasInputs; i++) {
      memristorsA[numInputs + i].dG(-(Vdd - Vy), readPeriod);
      memristorsB[numInputs + i].dG(-(Vy - Vss), readPeriod);
    }

    return Vy;
  }

  private void write(Set<Integer> inputSpikes, int supervisedSignal) {

    if (supervisedSignal == 1 || (Vy > 0 && supervisedSignal == 0)) {
      // evaluation was positive. Decay negative losers. Hebbian.

      // B side inputs weights
      for (int i = 0; i < numInputs; i++) {
        if (inputSpikes.contains(i)) { // non-floating active inputs.
          memristorsB[i].dG(-(Vdd - Vss), writePeriod);
        }
        else {// floating inputs
          memristorsB[i].dG(0, writePeriod);
        }

        // A side input weights. Rewarded by not being decayed.
        memristorsA[i].dG(0, writePeriod); // all floating
      }

      // bias weights, A and B side.
      for (int i = 0; i < numBiasInputs; i++) {
        memristorsB[numInputs + i].dG(Vdd - Vss, writePeriod); // write. polarity is reversed from input weights
        memristorsA[numInputs + i].dG(0, writePeriod); // floating
      }

    }
    else if (supervisedSignal == -1 || (Vy < 0 && supervisedSignal == 0)) {
      // evaluation was negative. Decay positive losers. Hebbian

      // A side input weights
      for (int i = 0; i < numInputs; i++) {
        if (inputSpikes.contains(i)) { // non-floating active inputs.
          memristorsA[i].dG(-(Vdd - Vss), writePeriod);
        }
        else {// floating inputs
          memristorsA[i].dG(0, writePeriod);
        }
      }

      // B side input weights. Rewarded by not being decayed
      for (int i = 0; i < numInputs; i++) {
        memristorsB[i].dG(0, writePeriod);// all floating
      }
      // Bias weights, A and B sides
      for (int i = 0; i < numBiasInputs; i++) {
        memristorsA[numInputs + i].dG(Vdd - Vss, writePeriod); // write. Polarity is reversed from input weights
        memristorsB[numInputs + i].dG(0, writePeriod); // floating
      }
    }
  }

  public double getVdd() {

    return Vdd;
  }

  public double getVss() {

    return Vss;
  }

  public double getReadPeriod() {

    return readPeriod;
  }

  public double getWritePeriod() {

    return writePeriod;
  }

  public MSSMemristor[] getMemristorsA() {

    return memristorsA;
  }

  public MSSMemristor[] getMemristorsB() {

    return memristorsB;
  }

}
