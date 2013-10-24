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
 * This class keeps track of a Clusterer's feature --> label mappings for analysis.
 * 
 * @author alexnugent
 */
public class VergenceEvaluator {

  /** A Map mapping features to its history */
  private Map<Integer, SpikesHistory> spikes2SpikesHistoryMap = new HashMap<Integer, SpikesHistory>();

  /** A Map mapping labels to its history */
  private Map<Integer, LabelHistory> label2LabelHistoryMap = new HashMap<Integer, LabelHistory>();

  /**
   * Entry points for recording labels the Clusterer produces corresponding to the spikeId
   * 
   * @param spikeId - The feature Id. Given a feature list of size M, the id is between 0 and M-1
   * @param label - the generated label produced by the Clusterer
   */
  public void update(int spikeId, int label) {

    SpikesHistory spikesHistory = spikes2SpikesHistoryMap.get(spikeId);
    if (spikesHistory == null) {
      spikesHistory = new SpikesHistory(spikeId);
    }
    spikesHistory.correspondingLabel(label);
    spikes2SpikesHistoryMap.put(spikeId, spikesHistory);

    LabelHistory labelHistory = label2LabelHistoryMap.get(label);
    if (labelHistory == null) {
      labelHistory = new LabelHistory(label);
    }
    labelHistory.correspondingFeature(spikeId);
    label2LabelHistoryMap.put(label, labelHistory);
  }

  /**
   * @return - the vergence of the Clusterer
   */
  public double getVergence() {

    return (getConvergence() + getDivergence()) / 2;
  }

  /**
   * @return - the convergence of the Clusterer
   */
  public double getConvergence() {

    Iterator<Integer> itr = label2LabelHistoryMap.keySet().iterator();
    double m = 0;
    while (itr.hasNext()) {
      Integer labelId = itr.next();
      m += label2LabelHistoryMap.get(labelId).getNumCorrespondingFeatures();
    }
    m /= label2LabelHistoryMap.size();

    return 1 / m;
  }

  /**
   * @return - the divergence of the Clusterer
   */
  public double getDivergence() {

    Iterator<Integer> itr = spikes2SpikesHistoryMap.keySet().iterator();
    double m = 0;
    while (itr.hasNext()) {
      Integer featureId = itr.next();
      m += spikes2SpikesHistoryMap.get(featureId).getNumCorrespondingLabels();
    }
    m /= spikes2SpikesHistoryMap.size();

    return 1 / m;
  }

  /**
   * prints the vergence of this Evaluation object
   */
  public String toVergenceString() {

    StringBuilder sb = new StringBuilder();
    sb.append("Convergence(ave. Spikes per Label) = " + getConvergence() + "\n");
    sb.append("Divergence(ave. Labels per Spikes) = " + getDivergence() + "\n");
    sb.append("Vergence                           = " + getVergence() + "\n");
    return sb.toString();
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();

    sb.append("*****SPIKES COUNTS*****" + "\n");
    sb.append("Spike ID(count): LabelId0(count),LabelId1(count),..." + "\n");
    Iterator<Integer> itr = spikes2SpikesHistoryMap.keySet().iterator();
    while (itr.hasNext()) {
      Integer featureId = itr.next();
      SpikesHistory featureHistory = spikes2SpikesHistoryMap.get(featureId);
      sb.append(featureHistory.toString() + "\n");
    }
    sb.append("*****LABEL COUNTS*****" + "\n");
    sb.append("Label ID(count): SpikeId0(count),SpikeId1(count),..." + "\n");
    itr = label2LabelHistoryMap.keySet().iterator();
    while (itr.hasNext()) {
      Integer labelId = itr.next();
      LabelHistory labelHistory = label2LabelHistoryMap.get(labelId);
      sb.append(labelHistory.toString() + "\n");
    }
    return sb.toString();
  }

  public String toCompactString() {

    StringBuilder sb = new StringBuilder();
    sb.append(getVergence() + " : ");
    sb.append(getConvergence() + " : ");
    sb.append(getDivergence());
    return sb.toString();
  }

}
