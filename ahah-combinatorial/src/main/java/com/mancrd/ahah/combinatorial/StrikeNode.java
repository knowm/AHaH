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

/**
 * @author alexnugent
 */
public class StrikeNode {

  protected static Random rand = new Random();

  protected static float alpha;
  protected static float beta;
  protected static float alphaOverBeta;

  protected static float noise = .025f;
  protected static float prune = .01f;

  private StrikeNode up;
  private StrikeNode dwn;

  private double ktbit = 0; // i.e. the weight.

  private double y = 0;

  /**
   * Constructor
   */
  public StrikeNode() {

  }

  public static void set(float alpha, float beta) {

    StrikeNode.alpha = alpha;
    StrikeNode.beta = beta;
    StrikeNode.alphaOverBeta = alpha / beta;
  }

  public void countChildren(CountObject count) {

    if (up != null) {
      count.inc();
      up.countChildren(count);
    }

    if (dwn != null) {
      count.inc();
      dwn.countChildren(count);
    }
  }

  public void prune() {

    if (Math.abs(ktbit) < alphaOverBeta * prune) {
      dwn = null;
      up = null;
    }
    else {

      if (up != null) {
        up.prune();
      }
      if (dwn != null) {
        dwn.prune();
      }

    }

  }

  public void write(boolean z, boolean[] register, int idx) {

    if (idx == register.length) {
      return;
    }
    if (z) {
      if (register[idx]) {
        ktbit += alpha;
        up.write(z, register, idx + 1);
      }
      else {
        ktbit -= alpha;
        dwn.write(z, register, idx + 1);
      }
    }
    else {
      if (register[idx]) {
        ktbit -= alpha;
        up.write(z, register, idx + 1);
      }
      else {
        ktbit += alpha;
        dwn.write(z, register, idx + 1);
      }
    }

  }

  public void read(boolean[] register, int idx) {

    if (idx == register.length) {
      return;
    }

    y = ktbit + rand.nextGaussian() * noise;
    if (y > 0) {
      register[idx] = true;
      if (up == null) {
        up = new StrikeNode();
      }
      up.read(register, idx + 1);
    }
    else {
      register[idx] = false;
      if (dwn == null) {
        dwn = new StrikeNode();
      }
      dwn.read(register, idx + 1);
    }

    ktbit -= beta * y; // anti-hebbian read
  }

  /**
   * How many nodes are in the tree below this node
   * 
   * @return
   */
  public int getNumNodes() {

    CountObject co = new CountObject();
    countChildren(co);
    return co.getCount();
  }

}
