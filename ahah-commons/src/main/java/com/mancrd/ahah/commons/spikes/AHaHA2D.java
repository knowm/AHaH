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
package com.mancrd.ahah.commons.spikes;

import java.io.Serializable;

/**
 * An AHaHA2D instance is used to convert real-valued signals into a spike code. It is a decision tree nodes operating anti-hebbian plasticity. A AHaHA2D takes values in the update
 * method and bins the data set into the low or high bin recursively from the trunk
 * of the decision tree to the leaves. The update method returns a binary number encoded as an integer giving routing path up the tree. A call to "putAndParse" will convert this path to a spike code
 * consisting of the distinct nodes alone the route.
 * 
 * @author alexnugent
 */
public class AHaHA2D implements Serializable {

  private double lRate = .01;

  /** Each AHaHA2D (except for the leaves on the tree) are split into a high and low bin split at the average value the TreeBinner receives. Average is computed through Anti-Hebbian learning. */
  private AHaHA2D lowBin;
  private AHaHA2D highBin;

  private float w = 0;

  /**
   * Constructor
   * 
   * @param depth
   */
  public AHaHA2D(int depth) {

    if (depth > 0) {
      int d = depth - 1;
      highBin = new AHaHA2D(d);
      lowBin = new AHaHA2D(d);
    }
  }

  public int put(double input) {

    int path = 1;
    return getState(input, path);
  }

  public int[] putAndParse(double input) {

    int id = put(input);

    int numbits = getNumBits(id);
    int[] out = new int[numbits - 1];
    for (int i = 0; i < out.length; i++) {
      out[i] = id >> (numbits - i - 1);
    }

    return out;
  }

  public double get(int id) {

    return -getPrivate(id, getNumBits(id) - 2);
  }

  private double getPrivate(int id, int depth) {

    if (depth <= 0) {
      return w;
    }
    if (isZero(depth, id)) {
      return lowBin.getPrivate(id, depth - 1);
    }
    else {
      return highBin.getPrivate(id, depth - 1);
    }
  }

  private static boolean isZero(int position, int value) {

    return (value &= (1 << position)) == 0;
  }

  public static int getNumBits(int value) {

    return 32 - Integer.numberOfLeadingZeros(value);
  }

  /**
   * @param input
   * @param sb
   */
  private int getState(double input, int path) {

    double y = input + w;
    this.w -= lRate * y;// anti-hebbian learning

    if (y >= 0) {
      path = (path << 1) | 1;// took a right, add a one
      if (highBin == null) {
        return path;
      }
      else {
        return highBin.getState(input, path);
      }
    }
    else {
      path = (path << 1);// took a left, add a zero
      if (lowBin == null) {// terminal leaf
        return path;
      }
      else {
        return lowBin.getState(input, path);
      }
    }
  }

  public double getlRate() {

    return lRate;
  }

  public void setlRate(double lRate) {

    this.lRate = lRate;
  }

}
