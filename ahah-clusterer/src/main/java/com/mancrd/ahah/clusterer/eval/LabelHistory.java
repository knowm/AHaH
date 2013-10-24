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
package com.mancrd.ahah.clusterer.eval;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * keeps track of data related to the Clusterer's spikes corresponding to a label
 * 
 * @author alexnugent
 */
public class LabelHistory {

  private int label;

  /** number of times this label has been generated */
  private int count = 0;

  /** number of time this feature has been associated with this label */
  private Map<Integer, Integer> spikes2CountMap = new HashMap<Integer, Integer>();

  /**
   * Constructor
   * 
   * @param labelId
   */
  public LabelHistory(int labelId) {

    this.label = labelId;
  }

  public void correspondingFeature(int featureId) {

    this.count++;

    Integer count = spikes2CountMap.get(featureId);
    if (count == null) {
      count = 1;
    }
    else {
      count++;
    }
    spikes2CountMap.put(featureId, count);
  }

  public int getCount() {

    return this.count;
  }

  public int getNumCorrespondingFeatures() {

    return this.spikes2CountMap.size();
  }

  @Override
  public String toString() {

    StringBuilder buf = new StringBuilder();

    buf.append(label + "(" + this.count + "):");
    Iterator<Integer> itr = spikes2CountMap.keySet().iterator();
    while (itr.hasNext()) {
      Integer featureId = itr.next();
      Integer featureCount = spikes2CountMap.get(featureId);
      buf.append(featureId + "(" + featureCount + "),");
    }
    buf.replace(buf.length() - 1, buf.length() - 1, "");// remove the last comma
    return buf.toString();
  }

}
