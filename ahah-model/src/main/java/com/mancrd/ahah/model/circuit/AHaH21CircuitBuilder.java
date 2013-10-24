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

/**
 * Builder for AHaH21Circuit
 */
public class AHaH21CircuitBuilder {

  public enum MemristorType {
    AgChalc, WOx, AIST, GST
  }

  private int numInputs;
  private int numBiasInputs;
  private double Vdd = .5; // Drain voltage
  private double Vss = -Vdd; // Source voltage
  private double readPeriod = 1E-6; // seconds
  private double writePeriod = 1E-6; // seconds
  private MemristorType memristorType = MemristorType.AgChalc; // seconds

  /**
   * return fully built object
   * 
   * @return a AHaH21Circuit
   */
  public AHaH21Circuit build() {

    return new AHaH21Circuit(this);
  }

  public AHaH21CircuitBuilder numInputs(int numInputs) {

    this.numInputs = numInputs;
    return this;
  }

  public AHaH21CircuitBuilder numBiasInputs(int numBiasInputs) {

    this.numBiasInputs = numBiasInputs;
    return this;
  }

  public AHaH21CircuitBuilder Vdd(double Vdd) {

    this.Vdd = Vdd;
    return this;
  }

  public AHaH21CircuitBuilder Vss(double Vss) {

    this.Vss = Vss;
    return this;
  }

  public AHaH21CircuitBuilder readPeriod(double readPeriod) {

    this.readPeriod = readPeriod;
    return this;
  }

  public AHaH21CircuitBuilder writePeriod(double writePeriod) {

    this.writePeriod = writePeriod;
    return this;
  }

  public AHaH21CircuitBuilder memristorType(MemristorType memristorType) {

    this.memristorType = memristorType;
    return this;
  }

  public int getNumInputs() {

    return numInputs;
  }

  public int getNumBiasInputs() {

    return numBiasInputs;
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

  public MemristorType getMemristorType() {

    return memristorType;
  }
}
