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
package com.mancrd.ahah.samples.model.circuit;

/**
 * 
 */
public class WeightUpdate implements Comparable<WeightUpdate> {

  private final int timeStep;
  private final double y;
  private final double dW;
  private final double dB;

  /**
   * Constructor
   * 
   * @param timeStep
   * @param y
   * @param dW
   * @param dB
   */
  public WeightUpdate(int timeStep, double y, double dW, double dB) {

    this.timeStep = timeStep;
    this.y = y;
    this.dW = dW;
    this.dB = dB;
  }

  @Override
  public int compareTo(WeightUpdate o) {

    if (o.getTimeStep() > this.getTimeStep()) {
      return -1;
    }
    else if (o.getTimeStep() < this.getTimeStep()) {
      return 1;
    }

    return 0;
  }

  public int getTimeStep() {

    return timeStep;
  }

  public double getY() {

    return y;
  }

  public double getdW() {

    return dW;
  }

  public double getdB() {

    return dB;
  }
}
