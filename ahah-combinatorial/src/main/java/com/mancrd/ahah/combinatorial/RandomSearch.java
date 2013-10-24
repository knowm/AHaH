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

import java.util.Random;

public class RandomSearch extends Search {

  private final boolean[] register;

  private float bestValue = Float.MAX_VALUE;

  /**
   * Just randomly guesses bit patterns. No directed search. Used as a control to show that fractal flow fabric is performing a directed search.
   * 
   * @param valuator
   * @param maxSteps
   */
  public RandomSearch(Valuator valuator, int maxSteps) {

    this.valuator = valuator;
    this.register = new boolean[valuator.getMaxBitLength()];
    this.maxSteps = maxSteps;

    setRegister();
  }

  @Override
  public void run() {

    for (int i = 0; i < maxSteps; i++) {

      setRegister();
      float value = valuator.getConfigValue(register);

      if (value < bestValue) {
        bestValue = value;
      }

    }
  }

  private void setRegister() {

    Random rand = new Random();

    for (int i = 0; i < register.length; i++) {
      register[i] = rand.nextBoolean();
    }

  }

  @Override
  public int getConvergenceAttempts() {

    return maxSteps; // for random search, all attempts are used
  }

  @Override
  public float getBestValue() {

    return bestValue;
  }

}
