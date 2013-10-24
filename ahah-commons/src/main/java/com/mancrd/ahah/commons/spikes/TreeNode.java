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

import gnu.trove.map.hash.TLongIntHashMap;

/**
 * A node within an AHaHTree. Multiplies in incoming spike pattern by a static random binary hyperplane, which reduces to a strict sum over the spike pattern.
 * Anti-Hebbian learning is used to adjust the bias to find a 50-50 split of incoming data. ID's of TreeNodes contained in the route up the tree is the spike code.
 * 
 * @author alexnugent
 */
class TreeNode {

  private static double LEARN_RATE = .01;

  TLongIntHashMap weights = new TLongIntHashMap();

  private double bias = 0;

  int update(long[] spikes) {

    double y = 0;
    for (long spike : spikes) {
      y += getWeight(spike);
    }
    y += bias;
    bias += (float) (-LEARN_RATE * y);

    int output = 1;

    if (y < 0) {
      output = -1;
    }

    return output;

  }

  private int getWeight(long spike) {

    if (weights.contains(spike)) {
      return weights.get(spike);
    }
    else {

      if (Math.random() > .5) {
        weights.put(spike, 1);
        return 1;
      }
      else {
        weights.put(spike, -1);
        return -1;
      }

    }

  }
}
