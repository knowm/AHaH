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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author alexnugent
 */
public class LabelStatistics {

  private final Map<Integer, Integer> counts = new HashMap<Integer, Integer>();

  public int put(Integer labelID) {

    Integer count = counts.get(labelID);
    if (count == null) {
      count = 1;
    }
    else {
      count++;
    }
    counts.put(labelID, count);

    return count;

  }

  public int get(Integer labelID) {

    Integer count = counts.get(labelID);
    if (count == null) {
      return 0;
    }
    else {
      return count;
    }
  }

  public List<LabelCount> getCounts() {

    List<LabelCount> countArray = new ArrayList<LabelCount>();
    for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
      LabelCount labelCount = new LabelCount(entry.getKey(), entry.getValue());
      countArray.add(labelCount);
    }
    Collections.sort(countArray);
    return countArray;
  }

  public void printCounts() {

    Iterator<Integer> itt = counts.keySet().iterator();
    while (itt.hasNext()) {
      int key = itt.next();
      System.out.println(key + "-->" + counts.get(key));
    }

  }

}
