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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.mancrd.ahah.commons.LRUCache;

/**
 * @author alexnugent
 */
public class KNearestNeighbors {

  private final LRUCache lruCache;
  private final Center[] centers;
  private final int maxCenters;
  private final double repRatio;

  private int idx = 0;

  /**
   * Constructor
   * 
   * @param maxCenters
   * @param inputDimension
   * @param repRatio
   */
  public KNearestNeighbors(int maxCenters, int inputDimension, double repRatio) {

    lruCache = new LRUCache(maxCenters);
    centers = new Center[maxCenters];
    this.maxCenters = maxCenters;
    this.repRatio = repRatio;
  }

  public Set<String> encode(double[] input, int numSpikes) {

    if (lruCache.size() < maxCenters) {
      Center newCenter = new Center(input, idx++ + "");
      centers[lruCache.put(newCenter.getId())] = newCenter;
      return new HashSet<String>();
    }

    double aveDistance = 0;
    for (int i = 0; i < centers.length; i++) {
      aveDistance += centers[i].setDistance(input);
    }
    aveDistance /= centers.length;
    Arrays.sort(centers);

    Set<String> spikes = new HashSet<String>();
    for (int i = 0; i < numSpikes; i++) {
      spikes.add(centers[i].getId());
      if (i > numSpikes) {
        break;
      }
    }

    double r = centers[0].getDistance() / aveDistance;

    if (r > repRatio) {
      Center newCenter = new Center(input, idx++ + "");
      centers[lruCache.put(newCenter.getId())] = newCenter;// replaces the least-recently-used
    }

    return spikes;
  }
}

class Center implements Comparable<Center> {

  private double[] center;
  private double distance;
  private final String id;

  public Center(double[] center, String id) {

    this.center = center;
    this.id = id;
  }

  public void setCenter(double[] center) {

    this.center = center;
  }

  public double getDistance() {

    return distance;
  }

  public String getId() {

    return id;
  }

  public double setDistance(double[] input) {

    distance = 0;
    for (int i = 0; i < input.length; i++) {
      distance += Math.pow((input[i] - center[i]), 2);
    }

    distance = Math.sqrt(distance);

    return distance;
  }

  @Override
  public int compareTo(Center o) {

    if (o.getDistance() < getDistance()) {
      return 1;
    }
    else if (o.getDistance() > getDistance()) {
      return -1;
    }
    return 0;
  }

  @Override
  public String toString() {

    return Arrays.toString(center) + ", " + distance + ", " + id;
    // return distance + ", " + id;
  }

}
