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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author alexnugent
 */
public class BinarySetConverter {

  public static void main(String[] args) {

    int setSize = 8;
    boolean[] randomPath = getRandomPath(getNumEncodingBits(setSize));
    System.out.println(binaryArrayToString(randomPath));

    int[] set = getSet(setSize, randomPath);

    System.out.println(Arrays.toString(set));
  }

  public static String binaryArrayToString(boolean[] p) {

    StringBuilder s = new StringBuilder();
    for (int i = 0; i < p.length; i++) {
      if (p[i]) {
        s.append("1");
      }
      else {
        s.append("0");
      }
    }

    return s.toString();
  }

  public static boolean[] getRandomPath(int size) {

    Random rand = new Random();
    boolean[] rb = new boolean[size];
    for (int i = 0; i < rb.length; i++) {
      rb[i] = rand.nextBoolean();
    }

    return rb;
  }

  public static int[] getSet(int setSize, boolean[] b) {

    List<Integer> a = new LinkedList<Integer>();
    for (int i = 0; i < setSize; i++) {
      a.add(i);
    }
    int[] set = new int[setSize];
    int idx = 0;
    for (int i = 0; i < set.length; i++) {
      int numBits = (int) Math.ceil(logBase2(a.size()));
      int setIndx = getSetIndex(a.size(), Arrays.copyOfRange(b, idx, idx + numBits));
      set[i] = a.get(setIndx);
      a.remove(setIndx);
      idx += numBits;
    }

    return set;
  }

  public static int getNumEncodingBits(int setSize) {

    int numBits = 0;
    for (int i = setSize; i > 0; i--) {
      numBits += (int) Math.ceil(logBase2(i));
    }

    return numBits;

  }

  public static int getSetIndex(int setSize, boolean[] path) {

    double n = Math.ceil(logBase2(setSize));
    if (path.length < n) {
      throw new RuntimeException("path[] length must exceed " + n + " for a setSize of " + setSize);
    }
    return (int) Math.round(setSize * booleanArrayToInt(path) / Math.pow(2, path.length));
  }

  public static double logBase2(double x) {

    return Math.log(x) / Math.log(2);
  }

  static int booleanArrayToInt(boolean[] arr) {

    int n = 0;
    for (boolean b : arr)
      n = (n << 1) | (b ? 1 : 0);
    return n;
  }

  private static boolean[] toBinary(int number, int base) {

    final boolean[] ret = new boolean[base];
    for (int i = 0; i < base; i++) {
      ret[base - 1 - i] = (1 << i & number) != 0;
    }
    return ret;
  }

}
