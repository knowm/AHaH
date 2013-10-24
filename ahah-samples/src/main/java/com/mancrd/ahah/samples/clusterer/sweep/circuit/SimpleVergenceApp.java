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
package com.mancrd.ahah.samples.clusterer.sweep.circuit;

import com.mancrd.ahah.clusterer.IClusterer;
import com.mancrd.ahah.clusterer.circuit.ClustererBuilder;
import com.mancrd.ahah.clusterer.eval.VergenceEvaluator;
import com.mancrd.ahah.clusterer.syndata.RandomSpikePatternGenerator;
import com.mancrd.ahah.samples.clusterer.sweep.ClustererDriver;

/**
 * @author timmolter
 */
public class SimpleVergenceApp {

  /**
   * @param args
   */
  public static void main(String[] args) {

    long startTime = System.currentTimeMillis();

    SimpleVergenceApp simpleVergenceApp = new SimpleVergenceApp();
    simpleVergenceApp.go();

    System.out.println((System.currentTimeMillis() - startTime) + " ms");
  }

  private void go() {

    int maxInputSpikes = 256;
    int numAhahNodes = 16;
    int numBias = 1;
    int spikePatternLength = 16;
    int numSpikePatterns = 16;
    int numNoiseBits = 10;
    int numLearnSteps = 100;
    int numTestSteps = 100;

    IClusterer clusterer = new ClustererBuilder().ahahNodes(numAhahNodes).numInputs(maxInputSpikes).numBias(numBias).build();

    RandomSpikePatternGenerator randomSpikesGenerator =
        new RandomSpikePatternGenerator.Builder().maxInputSpikes(maxInputSpikes).spikePatternLength(spikePatternLength).numSpikePatterns(numSpikePatterns).numNoiseBits(numNoiseBits).build();

    ClustererDriver rdd = new ClustererDriver(clusterer, randomSpikesGenerator);

    System.out.println("Learning");
    rdd.learnAll(numLearnSteps);
    System.out.println("Testing");
    VergenceEvaluator ve = rdd.testAllSpikes(numTestSteps);
    System.out.println(ve.toVergenceString());

  }
}
