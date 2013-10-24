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
package com.mancrd.ahah.samples.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author timmolter
 */
public class ModelExperiment {

  /**
   * @return
   */
  public Set<Integer> getThreePatternSpikeLogicSpikePattern() {

    Set<Integer> spikes = new HashSet<Integer>();

    double x0 = Math.random();
    double x1 = Math.random();

    if (x0 < .5) {
      spikes.add(0);
    }
    else {
      spikes.add(1);
    }

    if (x1 < .5) {
      spikes.add(2);
    }
    else {
      spikes.add(3);
    }

    return spikes;
  }

  public Set<Integer> getOrthogonalSpikeLogicSpikePattern() {

    Set<Integer> spikes = new HashSet<Integer>();

    double x0 = Math.random();

    if (x0 < .5) {
      spikes.add(0);
    }
    else {
      spikes.add(1);
    }

    if (x0 > .5) {
      spikes.add(2);
    }
    else {
      spikes.add(3);
    }

    return spikes;
  }

  public Set<Integer> getThreePatternSpikePattern() {

    double p = Math.random();
    Set<Integer> spikes = new HashSet<Integer>();
    if (p < .333) {
      spikes.add(0);
      return spikes;
    }
    else if (p < .666) {
      spikes.add(1);
      return spikes;
    }
    else {
      spikes.add(0);
      spikes.add(1);
      return spikes;
    }
  }

  public int H(double y) {

    if (y > 0) {
      return 1;
    }
    else {
      return 0;
    }
  }
}
