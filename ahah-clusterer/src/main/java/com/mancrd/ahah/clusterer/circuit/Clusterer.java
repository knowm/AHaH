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
package com.mancrd.ahah.clusterer.circuit;

import java.util.HashSet;
import java.util.Set;

import com.mancrd.ahah.clusterer.Converter;
import com.mancrd.ahah.clusterer.IClusterer;
import com.mancrd.ahah.model.circuit.AHaH21Circuit;
import com.mancrd.ahah.model.circuit.AHaH21CircuitBuilder;

public class Clusterer implements IClusterer {

  private final AHaH21Circuit[] ahahNodes;

  /**
   * Constructor
   * 
   * @param builder
   */
  public Clusterer(ClustererBuilder builder) {

    ahahNodes = new AHaH21Circuit[builder.getNumAHaHNodes()];
    for (int i = 0; i < ahahNodes.length; i++) {
      ahahNodes[i] =
          new AHaH21CircuitBuilder().numInputs(builder.getMaxInputSpikes()).numBiasInputs(builder.getNumBias()).readPeriod(builder.getReadPeriod()).writePeriod(builder.getWritePeriod()).Vss(
              builder.getVss()).Vdd(builder.getVdd()).build();
    }
  }

  /**
   * spikes come in here
   * 
   * @param inputSpikes
   * @return
   */
  @Override
  public Integer put(Set<String> inputSpikes) {

    Set<Integer> inputSpikesIntegers = convertStringSpikes(inputSpikes);

    double[] nodeActivations = new double[ahahNodes.length];
    for (int i = 0; i < ahahNodes.length; i++) {
      nodeActivations[i] = ahahNodes[i].update(inputSpikesIntegers, 0);
    }
    return Converter.getSignature(nodeActivations);
  }

  /**
   * convert String-valued spike to integer-valued spikes
   * 
   * @param spikes
   * @return
   */
  private static Set<Integer> convertStringSpikes(Set<String> spikes) {

    Set<Integer> integerSpikes = new HashSet<Integer>();
    for (String s : spikes) {
      integerSpikes.add(Integer.parseInt(s));
    }
    return integerSpikes;
  }

}
