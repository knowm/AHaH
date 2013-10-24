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
package com.knowmtech.ahah.classifier.map;

import java.util.Random;

import org.junit.Ignore;

/**
 * @author timmolter
 */
@Ignore
public abstract class MapTest {

  final int spikeSetSize = 100000;
  final int labelSetSize = 500;
  final int spikePatternSize = 500;
  final int numGetAccumulates = 1000;

  long[] test() {

    long[] results = new long[4];

    Runtime runtime = Runtime.getRuntime();
    Random rand = new Random();
    long startTime = System.nanoTime();
    for (long spikeId = 0; spikeId < spikeSetSize; spikeId++) {
      for (int labelID = 0; labelID < labelSetSize; labelID++) {
        put(spikeId, labelID, rand.nextFloat());
      }
    }
    results[0] = (System.nanoTime() - startTime) / 1000000000;

    startTime = System.nanoTime();
    for (int i = 0; i < numGetAccumulates; i++) {
      accumulate(getRandomSpikes());
    }
    results[1] = (System.nanoTime() - startTime) / 1000000000;
    results[2] = runtime.totalMemory() / 1000000;
    results[3] = runtime.freeMemory() / 1000000;

    return results;

  }

  long[] getRandomSpikes() {

    long[] spikes = new long[spikePatternSize];
    for (int i = 0; i < spikes.length; i++) {
      spikes[i] = (long) (Math.random() * spikeSetSize);
    }
    return spikes;
  }

  abstract void accumulate(long[] spikes);

  abstract void put(long spikeId, int labelId, float w);
}
