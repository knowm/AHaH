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
package com.mancrd.ahah.samples.clusterer.sweep;

import java.util.List;
import java.util.Set;

import com.mancrd.ahah.clusterer.IClusterer;
import com.mancrd.ahah.clusterer.eval.VergenceEvaluator;
import com.mancrd.ahah.clusterer.syndata.RandomSpikePatternGenerator;

/**
 * Given a IClusterer and a RandomSpikesGenerator, this class is used to teach and test a Clusterer.
 * 
 * @author timmolter
 */
public class ClustererDriver {

  IClusterer clusterer;
  RandomSpikePatternGenerator randomSpikesGenerator;

  /**
   * Constructor
   */
  public ClustererDriver(IClusterer clusterer, RandomSpikePatternGenerator randomSpikesGenerator) {

    this.clusterer = clusterer;
    this.randomSpikesGenerator = randomSpikesGenerator;
  }

  /**
   * Inputs only a single spikes in the spikes list randomly picked for each trial. Features are cloned with noise. Total inputs will be N, where
   * N=numTrials
   * 
   * @param numTrials
   */
  public void learnRandom(int numTrials) {

    for (int i = 0; i < numTrials; i++) {
      int spikesId = randomSpikesGenerator.getRandomSpikeId();
      Set<String> spikes = randomSpikesGenerator.getClonedSpikePatternWithNoise(spikesId);
      this.clusterer.put(spikes);
    }
  }

  /**
   * Inputs all spikes in the spikes list randomly picked for each trial. Features are cloned with noise. Total inputs will be N*numSpikes, where
   * N=numTrials
   * 
   * @param numTrials
   */
  public void learnAll(int numTrials) {

    for (int i = 0; i < numTrials; i++) {
      for (int j = 0; j < randomSpikesGenerator.getSpikePatterns().size(); j++) {
        Set<String> spikes = randomSpikesGenerator.getClonedSpikePatternWithNoise(j);
        this.clusterer.put(spikes);
      }
    }
  }

  /**
   * Tests every single spikes in the spikes list for each trial. Features are cloned with noise. Total inputs will be M*N, where M=numFeatures and
   * N=numTrials
   * 
   * @param numTrials
   */
  public VergenceEvaluator testAllSpikes(int numTrials) {

    VergenceEvaluator evaluator = new VergenceEvaluator();

    List<Set<String>> spikesList = randomSpikesGenerator.getSpikePatterns();

    for (int t = 0; t < numTrials; t++) {
      for (int spikeId = 0; spikeId < spikesList.size(); spikeId++) {
        Set<String> spikes = randomSpikesGenerator.getClonedSpikePatternWithNoise(spikeId);
        int labelId = this.clusterer.put(spikes);
        evaluator.update(spikeId, labelId);
      }
    }
    return evaluator;
  }

  /**
   * Tests only a single spikes in the spikes list randomly picked for each trial. Features are cloned with noise. Total inputs will be N, where
   * N=numTrials
   * 
   * @param numTrials
   */
  public VergenceEvaluator testRandomSpikes(int numTrials) {

    VergenceEvaluator evaluator = new VergenceEvaluator();

    for (int t = 0; t < numTrials; t++) {
      int featureId = randomSpikesGenerator.getRandomSpikeId();
      Set<String> spikes = randomSpikesGenerator.getClonedSpikePatternWithNoise(featureId);
      int labelId = this.clusterer.put(spikes);
      evaluator.update(featureId, labelId);
    }
    return evaluator;
  }
}
