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
package com.mancrd.ahah.model.circuit.mss;

import java.util.Random;

/**
 * This memristor is based on Alex's "collection of meta-stable switches" memristor model
 * 
 * @author timmolter
 */
public abstract class MSSMemristor {

  /** Boltzman's constant */
  private static final double K = 1.3806503E-23;

  /** electron charge */
  private static final double Q = 1.60217646E-19;

  /** temperature in Kelvin */
  private static final double TEMP = 298.0;

  private static final double BETA = Q / (K * TEMP);

  /** thermal voltage */
  private static final double VT = 1.0 / BETA;

  /** between 0 and 1, representing lowest and highest possible resistance */
  private double nAtoNRatio;

  /** characteristic time scale of the device */
  private final double tc;

  /** the number of MSS's */
  private final double n;

  /** conductance contributed from each MSS */
  private final double Ga;
  private final double Gb;

  /** between 0 and 1, what percentage of the current comes from the MSS model? the rest comes from schottkey barrier exponential current */
  private final double phi;

  private final double schottkeyAlpha;
  private final double schottkeyBeta;
  private final double schottkeyReverseAlpha;
  private final double schottkeyReverseBeta;

  /** barrier potentials */
  private final double vA;
  private final double vB;

  // DEVICE DYNAMIC VARIABLES

  /** Nb is the number of MSS's in the B state */
  private double Nb = 0;

  private final static Random RANDOM = new Random();

  /**
   * Constructor
   * 
   * @param memristance
   * @param tc
   * @param n
   * @param wOff
   * @param wOn
   * @param vA
   * @param vB
   * @param d
   * @param phi
   * @param schottkeyAlpha
   * @param schottkeyBeta
   * @param schottkeyReverseAlpha
   * @param schottkeyReverseBeta
   */
  public MSSMemristor(double memristance, double tc, double n, double wOff, double wOn, double vA, double vB,

  double phi, double schottkeyAlpha, double schottkeyBeta, double schottkeyReverseAlpha, double schottkeyReverseBeta) {

    if (memristance > 1.0 || memristance < 0.0) {
      throw new IllegalArgumentException("Memristance must be between 0 and 1, inclusive!!!");
    }
    this.nAtoNRatio = memristance;
    this.tc = tc;
    this.n = n;
    Ga = wOff / n;
    Gb = wOn / n;
    this.vA = vA;
    this.vB = vB;

    this.phi = phi;

    this.schottkeyAlpha = schottkeyAlpha;
    this.schottkeyBeta = schottkeyBeta;
    this.schottkeyReverseAlpha = schottkeyReverseAlpha;
    this.schottkeyReverseBeta = schottkeyReverseBeta;

    setNb();
  }

  /**
   * update device
   * 
   * @param voltage
   * @param dt
   */
  public void dG(double voltage, double dt) {

    double Pa = Pa(voltage, dt);
    double Pb = Pb(voltage, dt);

    double u_a = (n - Nb) * Pa;
    double u_b = (Nb) * Pb;

    double stdv_a = Math.sqrt((n - Nb) * Pa * (1 - Pa));
    double stdv_b = Math.sqrt((Nb) * Pb * (1 - Pb));

    double Gab = G(u_a, stdv_a);
    double Gba = G(u_b, stdv_b);

    Nb += (Gab - Gba);

    if (Nb > n) {
      Nb = n;
    }
    else if (Nb < 0) {
      Nb = 0;
    }

  }

  /**
   * the probability that the MSS will transition from the A state to the B state
   * 
   * @param voltage - the voltage across the device
   * @param dt
   * @return
   */
  private double Pa(final double voltage, final double dt) {

    double exponent = -1 * (voltage - vA) / VT;
    double alpha = getAlpha(dt);
    double Pa = alpha / (1.0 + Math.exp(exponent));

    if (Pa > 1.0) {
      Pa = 1.0;
    }
    else if (Pa < 0.0) {
      Pa = 0.0;
    }
    return Pa;
  }

  /**
   * The probability that the MSS will transition from the B state to the A state
   * 
   * @param v
   * @param dt
   * @return
   */
  private double Pb(final double v, final double dt) {

    double exponent = -1 * (v + vB) / VT;
    double alpha = getAlpha(dt);
    double Pb = alpha * (1.0 - 1.0 / (1.0 + Math.exp(exponent)));

    if (Pb > 1.0) {
      Pb = 1.0;
    }
    else if (Pb < 0.0) {
      Pb = 0.0;
    }

    return Pb;
  }

  /**
   * dt should be less than tc.
   * 
   * @param dt
   * @return
   */
  private double getAlpha(double dt) {

    return dt / tc;
  }

  private double G(double u, double stdv) {

    double G = stdv * RANDOM.nextGaussian() + u;
    return G;
  }

  /**
   * get the conductance
   * 
   * @return
   */
  public double getConductance() {

    double conductance = Nb * (Gb - Ga) + n * Ga;
    nAtoNRatio = 1 - Nb / n;
    return conductance;
  }

  public double getResistance() {

    return 1 / getConductance();
  }

  private void setNb() {

    if (nAtoNRatio > 1) {
      nAtoNRatio = 1;
    }
    else if (nAtoNRatio < 0) {
      nAtoNRatio = 0;
    }
    Nb = (1 - nAtoNRatio) * n; // note: (1- memristance) so that a memristance of 1 give a higher resitance than memristance of 0.
  }

  /**
   * Get the current thru this memristor
   * 
   * @return
   */
  public double getCurrent(double voltage) {

    double mssCurrent = voltage * getConductance();
    double schottkeyCurrent = getDiodeCurrent(voltage);

    return phi * mssCurrent + (1 - phi) * schottkeyCurrent;
  }

  private double getDiodeCurrent(double voltage) {

    return schottkeyReverseAlpha * (-1 * Math.exp(-1 * schottkeyReverseBeta * voltage)) + schottkeyAlpha * (Math.exp(schottkeyBeta * voltage));

  }

}
