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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author alexnugent
 */
public class MnistImagePatchStats {

  private final HashMap<Long, PatchStat> features = new HashMap<Long, PatchStat>();

  private final int patchSize;
  private int numUpdates = 0;

  public MnistImagePatchStats(int patchSize) {

    this.patchSize = patchSize;
  }

  public double[] getCounts() {

    double[] counts = new double[features.size()];

    int i = 0;
    for (Long s : features.keySet()) {
      counts[i] = features.get(s).getCount();
      i++;
    }

    Arrays.sort(counts);

    return counts;
  }

  public void update(Set<Long> spikes, int[][] patch) {

    for (Long spike : spikes) {
      update(spike, patch);
    }

  }

  public void update(long spike, int[][] patch) {

    PatchStat patchStat = features.get(spike);
    if (patchStat == null) {
      patchStat = new PatchStat(patchSize);
      features.put(spike, patchStat);
    }
    patchStat.update(patch);
    numUpdates++;
  }

  public int[][] getAllPatchAverages(double minFrequency) {

    List<int[][]> patches = new ArrayList<int[][]>();
    for (Long reg : features.keySet()) {
      double freq = (double) features.get(reg).getCount() / numUpdates;

      if (freq > minFrequency) {
        patches.add(features.get(reg).getNormPatch());
      }
    }

    int length = (int) (patchSize * Math.ceil((Math.sqrt(patches.size()))));
    int[][] bigImage = new int[length][length];

    int x = 0;
    int y = 0;
    for (int[][] img : patches) {

      for (int i = 0; i < img.length; i++) {
        for (int j = 0; j < img.length; j++) {
          bigImage[x + i][y + j] = img[i][j];
        }
      }

      if (x < length - patchSize) {
        x += patchSize;
      }
      else if (y < length - patchSize) {
        x = 0;
        y += patchSize;
      }
      else {
        break;
      }

    }

    return bigImage;
  }
}

class PatchStat {

  private final int[][] patchTotals;
  private int n = 0;

  public PatchStat(int patchSize) {

    patchTotals = new int[patchSize][patchSize];
  }

  public void update(int[][] patch) {

    for (int i = 0; i < patch.length; i++) {
      for (int j = 0; j < patch[0].length; j++) {
        patchTotals[i][j] += patch[i][j];
      }
    }
    n++;
  }

  public int[][] getAvePatch() {

    int[][] avePatch = new int[patchTotals.length][patchTotals[0].length];
    for (int i = 0; i < avePatch.length; i++) {
      for (int j = 0; j < avePatch[0].length; j++) {
        avePatch[i][j] = (int) ((double) patchTotals[i][j] / n);
      }
    }
    return avePatch;
  }

  public int[][] getNormPatch() {

    int[][] avePatch = new int[patchTotals.length][patchTotals[0].length];

    int max = patchTotals[0][0];
    int min = max;
    for (int i = 0; i < avePatch.length; i++) {
      for (int j = 0; j < avePatch[0].length; j++) {
        if (patchTotals[i][j] > max) {
          max = patchTotals[i][j];
        }
        if (patchTotals[i][j] < min) {
          min = patchTotals[i][j];
        }
      }
    }

    for (int i = 0; i < avePatch.length; i++) {
      for (int j = 0; j < avePatch[0].length; j++) {
        avePatch[i][j] = (int) (255 * (double) (patchTotals[i][j] - min) / (max - min));
      }
    }
    return avePatch;
  }

  public int getCount() {

    return n;
  }

}
