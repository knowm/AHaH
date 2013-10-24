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
package com.mancrd.ahah.samples.classifier.mnist;

import gnu.trove.set.hash.TLongHashSet;

import java.util.HashSet;
import java.util.Set;

import com.mancrd.ahah.commons.spikes.AhahTree;
import com.mancrd.ahah.commons.spikes.SpikeEncoder;

/**
 * @author alexnugent
 */
public class MnistSpikeEncoder extends SpikeEncoder<int[][]> {

  private int poolSize = 8;
  private int patchSize = 8;
  private final AhahTree[] ahahTrees;

  /**
   * Constructor
   * 
   * @param poolSize
   * @param patchSize
   * @param resolution
   * @param numEncoders
   */
  public MnistSpikeEncoder(int poolSize, int patchSize, int resolution, int numEncoders) {

    ahahTrees = new AhahTree[numEncoders];
    for (int i = 0; i < ahahTrees.length; i++) {
      ahahTrees[i] = new AhahTree(resolution);
    }
    this.poolSize = poolSize;
    this.patchSize = patchSize;
  }

  @Override
  public Set<String> getSpikes(int[][] img) {

    Set<String> spikes = new HashSet<String>();

    for (int i = 0; i < img.length - patchSize; i++) {
      for (int j = 0; j < img[0].length - patchSize; j++) {
        TLongHashSet patchFeatures = new TLongHashSet();

        for (int x = 0; x < patchSize; x++) {
          for (int y = 0; y < patchSize; y++) {
            if (img[i + x][j + y] > 10) {
              patchFeatures.add(x * patchSize + y);
            }
          }
        }
        int z0 = i / poolSize;
        int z1 = j / poolSize;
        for (int k = 0; k < ahahTrees.length; k++) {
          StringBuffer b = new StringBuffer();
          b.append(k);
          b.append(".");
          b.append(z0);
          b.append(".");
          b.append(z1);
          b.append(".");
          b.append(ahahTrees[k].encode(patchFeatures.toArray()));
          spikes.add(b.toString());
        }

      }
    }

    spikes.add("BIAS");

    return spikes;
  }

  @Override
  public short getUniquePositiveID() {

    return 0;
  }

}
