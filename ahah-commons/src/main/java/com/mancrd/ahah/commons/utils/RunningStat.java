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
package com.mancrd.ahah.commons.utils;

/**
 * RunningStat keeps track of running average and standard deviation values for incoming values
 * 
 * @author timmolter
 */
public class RunningStat {

  private int count = 0;
  private double average = 0.0;
  private double pwrSumAvg = 0.0;
  private double stdDev = 0.0;

  /**
   * Incoming new values used to calculate the running statistics
   * 
   * @param value
   */
  public void put(double value) {

    count++;
    average += (value - average) / count;
    pwrSumAvg += (value * value - pwrSumAvg) / count;
    stdDev = Math.sqrt((pwrSumAvg * count - count * average * average) / (count - 1));

  }

  public double getAverage() {

    return average;
  }

  public double getStandardDeviation() {

    return Double.isNaN(stdDev) ? 0.0 : stdDev;
  }

}
