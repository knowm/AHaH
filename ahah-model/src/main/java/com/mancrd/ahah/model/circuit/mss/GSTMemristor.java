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

/**
 * This memristor is a specific MSSMemristor with parameters tweaked to match the GST device from Li 2013
 * 
 * @author timmolter
 */
public class GSTMemristor extends MSSMemristor {

  /** characteristic time scale of the device */
  private static final double TC = 0.00042;

  /** the number of MSS's */
  private static final double N = 1000000;

  private static final double G_OFF = .12E-2;
  private static final double G_ON = .12E-3;

  /** barrier potentials */
  private static final double VA = .9; // lower is pointier
  private static final double VB = .6;

  final static double schottkeyAlpha = 0.00005;
  final static double schottkeyBeta = 3.0;
  final static double phi = 0.7;

  /**
   * Constructor
   * 
   * @param memristance
   */
  public GSTMemristor(double memristance) {

    super(memristance, TC, N, G_OFF, G_ON, VA, VB, phi, schottkeyAlpha, schottkeyBeta, schottkeyAlpha, schottkeyBeta);
  }

}
