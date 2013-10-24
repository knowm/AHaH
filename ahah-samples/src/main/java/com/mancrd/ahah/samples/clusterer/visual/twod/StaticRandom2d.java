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
package com.mancrd.ahah.samples.clusterer.visual.twod;

import java.util.ArrayList;
import java.util.List;

import com.mancrd.ahah.samples.clusterer.visual.ClusteringFrame;

/**
 * @author timmolter
 */
public class StaticRandom2d extends ClusteringPanel2d {

  private List<Blob2d> blobCenters = new ArrayList<Blob2d>();

  public static void main(String[] args) {

    new ClusteringFrame(new StaticRandom2d());
  }

  /**
   * Constructor
   */
  public StaticRandom2d() {

    this.blobCenters = new ArrayList<Blob2d>();
    int numCenters = 12;
    int blobSize = 10;

    for (int i = 0; i < numCenters; i++) {
      blobCenters.add(new Blob2d((int) ((100 * (Math.random() - .5)) + 50), (int) (100 * (Math.random() - .5) + 50), (int) (Math.random() * blobSize)));
    }

    int bigBlobX = 50;
    int bigBlobY = 50;
    int bigBlobSize = 20;
    for (int i = 0; i < 1; i++) {// until I change how new centers are added in the kNN spike encoder, the total frequency of a cluster needs to be
                                 // equal to the others. This is just an artificat of the kNN algo.
      blobCenters.add(new Blob2d(bigBlobX, bigBlobY, bigBlobSize));
      blobCenters.add(new Blob2d(bigBlobX, bigBlobY, bigBlobSize));
    }
  }

  @Override
  public List<Blob2d> getBlobs() {

    return blobCenters;

  }

  @Override
  public int getPanelSize() {

    return 300;
  }

  @Override
  public int getNumSpikes() {

    return 24;
  }

}
