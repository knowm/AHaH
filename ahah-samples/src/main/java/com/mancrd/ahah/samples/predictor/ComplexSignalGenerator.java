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
package com.mancrd.ahah.samples.predictor;

import java.util.Arrays;

/**
 * @author alexnugent
 */
public class ComplexSignalGenerator {

  private double[] amplitudes;
  private double[] periods;
  private double[] phases;
  private double drift;

  private double t = 0;

  public ComplexSignalGenerator(int numSources) {

    this.amplitudes = new double[numSources];
    this.periods = new double[numSources];
    this.phases = new double[numSources];

    for (int i = 0; i < periods.length; i++) {
      amplitudes[i] = Math.random();
      phases[i] = Math.random();
      periods[i] = Math.random();
    }

    drift = 0;// Math.random();

    System.out.println(Arrays.toString(amplitudes));
    System.out.println(Arrays.toString(phases));
    System.out.println(Arrays.toString(periods));

  }

  /**
   * Constructor
   * 
   * @param amplitudes
   * @param periods
   * @param phases
   * @param drift
   */
  public ComplexSignalGenerator(double[] amplitudes, double[] periods, double[] phases, double drift) {

    this.amplitudes = amplitudes;
    this.periods = periods;
    this.phases = phases;
    this.drift = drift;
  }

  public double getSignal() {

    double z = 0;
    for (int i = 0; i < amplitudes.length; i++) {
      z += amplitudes[i] * Math.sin(periods[i] * t - phases[i]);
    }

    t++;
    return z + drift * t;
  }

  public double[] getAmplitudes() {

    return amplitudes;
  }

  public void setAmplitudes(double[] amplitudes) {

    this.amplitudes = amplitudes;
  }

  public double[] getPeriods() {

    return periods;
  }

  public void setPeriods(double[] periods) {

    this.periods = periods;
  }

  public double[] getPhases() {

    return phases;
  }

  public void setPhases(double[] phases) {

    this.phases = phases;
  }

  public double getDrift() {

    return drift;
  }

  public void setDrift(double drift) {

    this.drift = drift;
  }

}