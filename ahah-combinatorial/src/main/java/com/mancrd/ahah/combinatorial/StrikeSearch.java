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
package com.mancrd.ahah.combinatorial;

/**
 * @author alexnugent
 */
public class StrikeSearch extends Search {

  private final boolean[] register;

  private float lastValue = 0;
  private float bestValue = Float.MAX_VALUE;
  private float aveValue = 0;

  StrikeNode strikeNode;

  /**
   * exponential moving average of solution value. If solution is better than average, nodes get Hebbian reward.
   * smaller values result in integration over longer time window. If k = 1, average value becomes last value.
   */
  private final float k = .4f;

  private final int prunePeriod = 10000;

  int sameCount = 0;
  int convergenceAttempts = 0;

  /**
   * Constructor
   * 
   * @param valuator
   * @param maxSteps
   * @param alpha
   * @param beta
   */
  public StrikeSearch(Valuator valuator, int maxSteps, float alpha, float beta) {

    this.valuator = valuator;
    this.register = new boolean[valuator.getMaxBitLength()];
    this.maxSteps = maxSteps;
    this.strikeNode = new StrikeNode();
    StrikeNode.set(alpha, beta); // static variable set for all future child nodes.
    strikeNode.read(register, 0); // initializes the register
    lastValue = aveValue = valuator.getConfigValue(register); // init the values

  }

  @Override
  public void run() {

    for (int i = 0; i < maxSteps; i++) {

      strikeNode.read(register, 0);
      float value = valuator.getConfigValue(register);
      if (value < aveValue) {
        strikeNode.write(true, register, 0);
        if (value < bestValue) {
          bestValue = value;
        }
      }

      if (i % prunePeriod == 0) {
        strikeNode.prune();
      }
      aveValue = (1 - k) * aveValue + k * value;

      if (value == lastValue) {
        sameCount++;
        if (sameCount > 5) {
          convergenceAttempts = i - 5;
          return;
        }
      }
      else {
        sameCount = 0;
      }
      lastValue = value;
    }
    convergenceAttempts = maxSteps;
  }

  @Override
  public int getConvergenceAttempts() {

    return convergenceAttempts;
  }

  @Override
  public float getBestValue() {

    return bestValue;
  }

  public int getNumNodes() {

    return strikeNode.getNumNodes();
  }

}
