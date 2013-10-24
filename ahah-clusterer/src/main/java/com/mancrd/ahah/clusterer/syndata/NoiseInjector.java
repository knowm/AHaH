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
package com.mancrd.ahah.clusterer.syndata;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * @author timmolter
 */
public class NoiseInjector {

  /** A Random object used for creating random numbers */
  private Random random = new Random();

  public enum Noise {

    ADDITIVE, SUBTRACTIVE, ADD_SUBTRACT, FLIP;
  }

  /**
   * @param spikePatterns
   * @param numInputs
   * @param noise
   * @param numNoiseBitsPerFeature
   */
  public void injectNoise(Set<String> spikePatterns, Integer numInputs, Noise noise, int numNoiseBitsPerFeature) {

    switch (noise) {
    case ADDITIVE:
      additiveNoise(spikePatterns, numInputs, numNoiseBitsPerFeature);
      break;
    case SUBTRACTIVE:
      subtractiveNoise(spikePatterns, numNoiseBitsPerFeature);
      break;
    case ADD_SUBTRACT:
      subtractiveNoise(spikePatterns, numNoiseBitsPerFeature);
      additiveNoise(spikePatterns, numInputs, numNoiseBitsPerFeature);
      break;
    case FLIP:
      flipNoise(spikePatterns, numInputs, numNoiseBitsPerFeature);
      break;
    default:
      break;
    }
  }

  /**
   * randomly adds inputs to a feature
   * 
   * @param spikePatterns
   * @param numInputs
   * @param numNoiseBitsPerFeature
   */
  private void additiveNoise(Set<String> spikePatterns, int numInputs, int numNoiseBitsPerFeature) {

    for (int i = 0; i < numNoiseBitsPerFeature; i++) {
      boolean added = false;
      do {
        added = spikePatterns.add(new Integer(random.nextInt(numInputs)).toString());
      } while (!added && spikePatterns.size() < numInputs);
    }
  }

  /**
   * randomly subtracts inputs to a feature
   * 
   * @param spikePatterns
   * @param numNoiseBitsPerFeature
   */
  private void subtractiveNoise(Set<String> spikePatterns, int numNoiseBitsPerFeature) {

    for (int i = 0; i < numNoiseBitsPerFeature; i++) {
      if (spikePatterns.size() <= 0) {
        throw new IllegalArgumentException("Too much subtractive noise added to feature, size()=0!");
      }
      int index = this.random.nextInt(spikePatterns.size());
      Iterator<String> itr = spikePatterns.iterator();
      itr.next();
      while (index > 0) {
        itr.next();
        index--;
      }
      itr.remove();
    }
  }

  /**
   * randomly flips bits of a spikePattern
   * 
   * @param spikePatterns
   * @param numInputs
   * @param numNoiseBitsPerFeature
   */
  private void flipNoise(Set<String> spikePatterns, int numInputs, int numNoiseBitsPerFeature) {

    for (int i = 0; i < numNoiseBitsPerFeature; i++) {
      String sensorId = new Integer(random.nextInt(numInputs)).toString();
      if (spikePatterns.contains(sensorId)) {
        spikePatterns.remove(sensorId);
      }
      else {// add it
        spikePatterns.add(sensorId);
      }
    }
  }

}
