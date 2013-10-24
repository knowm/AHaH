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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.mancrd.ahah.commons.LinkWeight;

/**
 * @author alexnugent
 */
public abstract class SpikeEncoder<T> implements Serializable {

  private static final long idMask = 65535;// 0b1111111111111111;
  private final TIntObjectMap<String> reverseMap;

  /** abstract methods */
  public abstract Set<String> getSpikes(T data);

  public abstract short getUniquePositiveID();

  /**
   * Constructor
   */
  public SpikeEncoder() {

    reverseMap = new TIntObjectHashMap<String>();
  }

  public long[] encode(T data) {

    Set<String> stringSpikes = getSpikes(data);
    long[] spikes = new long[stringSpikes.size()];
    int idx = 0;
    for (String s : stringSpikes) {
      int iSpike = s.hashCode();
      if (!reverseMap.containsKey(iSpike)) {
        reverseMap.put(iSpike, s);
      }
      spikes[idx] = (long) iSpike << 16 | getUniquePositiveID();
      idx++;
    }

    return spikes;
  }

  public short getIdFromSpike(long compositeSpike) {

    return (short) (compositeSpike & idMask);
  }

  public int getOriginalSpikeIDFromComposite(long compositeSpike) {

    return (int) (compositeSpike >> 16);
  }

  public void setSpikeLabel(List<LinkWeight> linkWeights) {

    for (LinkWeight linkWeight : linkWeights) {
      linkWeight.setSpikeString(getSpikeLabel(getOriginalSpikeIDFromComposite(linkWeight.getSpike())));
    }
  }

  public String getSpikeLabel(int spike) {

    return reverseMap.get(spike);
  }

  public String getSpikeLabel(long spike) {

    if (getIdFromSpike(spike) == getUniquePositiveID()) {
      return reverseMap.get(getOriginalSpikeIDFromComposite(spike));
    }
    return null;

  }

  public TIntObjectMap<String> getReverseMap() {

    return reverseMap;
  }

  public int getSpikePatternSpace() {

    return reverseMap.size();
  }

}
