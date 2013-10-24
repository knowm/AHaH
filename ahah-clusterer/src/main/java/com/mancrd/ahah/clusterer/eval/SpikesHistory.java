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
 * Keeps track of data related to the Clusterer's labels generated as a response to spikes
 * 
 * @author alexnugent
 */
public class SpikesHistory {

  private int spikesId;

  /** number of times this feature has been inputted */
  private int count = 0;

  /** number of time these spikes have been associated with the labels */
  private Map<Integer, Integer> label2CountMap = new HashMap<Integer, Integer>();

  /**
   * Constructor
   * 
   * @param spikesId
   */
  public SpikesHistory(int spikesId) {

    this.spikesId = spikesId;
  }

  /**
   * Here a label corresponding to the spikes coming in to be recorded.
   * 
   * @param labelId
   */
  public void correspondingLabel(int labelId) {

    // 1. increment feature count
    this.count++;

    // 2. increment label count
    Integer count = label2CountMap.get(labelId);
    if (count == null) {
      count = 1;
    }
    else {
      count++;
    }
    label2CountMap.put(labelId, count);
  }

  public int getCount() {

    return this.count;
  }

  public int getNumCorrespondingLabels() {

    return this.label2CountMap.size();
  }

  @Override
  public String toString() {

    StringBuilder buf = new StringBuilder();

    buf.append(spikesId + "(" + this.count + "):");
    Iterator<Integer> itr = label2CountMap.keySet().iterator();
    while (itr.hasNext()) {
      Integer labelId = itr.next();
      Integer labelCount = label2CountMap.get(labelId);
      buf.append(labelId + "(" + labelCount + "),");
    }
    buf.replace(buf.length() - 1, buf.length() - 1, "");// remove the last comma
    return buf.toString();
  }

}
