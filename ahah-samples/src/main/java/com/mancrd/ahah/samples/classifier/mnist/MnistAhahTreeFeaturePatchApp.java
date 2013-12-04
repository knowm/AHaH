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

import java.io.File;

import javax.swing.JPanel;

import com.mancrd.ahah.commons.spikes.AhahTree;
import com.xeiam.datasets.mnist.Mnist;
import com.xeiam.datasets.mnist.MnistDAO;

/**
 * @author alexnugent
 */
public class MnistAhahTreeFeaturePatchApp {

  /**
   * This app takes the following arguments:
   * <ul>
   * <li>int numSamples (500): number of MNIST training samples to use
   * <li>int patchSize (10): size of patch edge in pixels to feed into the AHaH tree
   * <li>int resolution (8): number of features generate=2^resolution. Resolution of 8 will result in 256 features
   * 
   * @param args
   */
  public static void main(String[] args) {

    File tempDBFile = MnistDAO.init(); // setup data
    MnistAhahTreeFeaturePatchApp ahahTreeMnist = new MnistAhahTreeFeaturePatchApp();
    ahahTreeMnist.go(args);
    MnistDAO.release(tempDBFile); // release data resources

  }

  public void go(String[] args) {

    int numSamples = 500;
    int patchSize = 10;
    int resolution = 8;

    try {
      numSamples = Integer.parseInt(args[0]);
      patchSize = Integer.parseInt(args[1]);
      resolution = Integer.parseInt(args[2]);

    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    AhahTree tree = new AhahTree(resolution);
    MnistImagePatchStats imagePatchStats = new MnistImagePatchStats(patchSize);

    for (int t = 0; t < numSamples; t++) {
      Mnist mnistData = MnistDAO.selectSingle(t);
      int[][] img = mnistData.getImageMatrix();
      for (int i = 0; i < img.length - patchSize; i++) {
        for (int j = 0; j < img[0].length - patchSize; j++) {

          TLongHashSet patchSpikes = new TLongHashSet();
          int[][] patch = new int[patchSize][patchSize];
          for (int x = 0; x < patchSize; x++) {
            for (int y = 0; y < patchSize; y++) {
              if (img[i + x][j + y] > 10) {
                patchSpikes.add(x * patchSize + y);
              }
              patch[x][y] = img[i + x][j + y];
            }
          }
          long spike = tree.encode(patchSpikes.toArray());
          imagePatchStats.update(spike, patch);
        }
      }

      if (t % 10 == 0) {
        System.out.println("t=" + t + " of " + numSamples);
      }

    }

    // paint the patches
    JPanel mnistImagePanel = new MnistImagePanel(imagePatchStats.getAllPatchAverages(.001), 5);
    new MnistDigitViewer(mnistImagePanel, "AhahTree MNIST Patch Features");

    // // plot patch counts
    // double[] counts = imagePatchStats.getCounts();
    // Chart chart = new Chart(800, 600);
    // chart.setChartTitle("Feature Counts");
    // chart.setXAxisTitle("Feature");
    // chart.setYAxisTitle("Count");
    //
    // Series countSeries = chart.addSeries("Counts", null, counts);
    // countSeries.setMarker(SeriesMarker.NONE);
    //
    // new SwingWrapper(chart).displayChart();

  }
}
