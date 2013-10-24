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
package com.mancrd.ahah.samples.predictor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.mancrd.ahah.commons.spikes.AHaHA2D;
import com.mancrd.ahah.commons.spikes.SpikeEncoder;

/**
 * @author alexnugent
 */
public class SimpleTemporalBufferSpikeEncoder extends SpikeEncoder<Double> {

  private final AHaHA2D binner;
  private final LinkedList<Integer> buffer;
  private final int bufferSize;

  /**
   * Constructor
   * 
   * @param bufferSize
   * @param binnerDepth
   */
  public SimpleTemporalBufferSpikeEncoder(int bufferSize, int binnerDepth) {

    this.bufferSize = bufferSize;
    buffer = new LinkedList<Integer>();
    binner = new AHaHA2D(binnerDepth);

  }

  @Override
  public Set<String> getSpikes(Double signal) {

    buffer.add(binner.put(signal));
    if (buffer.size() > bufferSize) {
      buffer.removeFirst();
    }

    String[] spikes = new String[buffer.size()];

    if (buffer.size() < bufferSize) {
      return array2Set(spikes);
    }

    for (int i = 0; i < spikes.length; i++) {
      spikes[i] = i + "." + buffer.get(i);
    }

    // System.out.println(Arrays.toString(spikes));
    return array2Set(spikes);
  }

  private Set<String> array2Set(String[] spikes) {

    Set<String> spikesSet = new HashSet<String>();
    for (int i = 0; i < spikes.length; i++) {
      if (spikes[i] == null) {
        spikesSet.add("0");
      }
      else {
        spikesSet.add(spikes[i]);
      }
    }
    return spikesSet;
  }

  public AHaHA2D getBinner() {

    return binner;
  }

  @Override
  public short getUniquePositiveID() {

    // TODO Auto-generated method stub
    return 0;
  }

}
