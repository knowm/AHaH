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
package com.mancrd.ahah.samples.clusterer.visual.oned;

import java.util.ArrayList;
import java.util.List;

import com.mancrd.ahah.samples.clusterer.visual.ClusteringFrame;

/**
 * Random points along a line picked from a Gaussian distribution centered at N blobs are eventually clustered and labeled in an unsupervised manner
 * by the clusterer
 * 
 * @author timmolter
 */
public class Static1d extends ClusteringPanel1d {

  public static void main(String[] args) {

    new ClusteringFrame(new Static1d());
  }

  private final List<Blob1d> blobCenters = new ArrayList<Blob1d>();

  /**
   * Constructor
   */
  public Static1d() {

    blobCenters.add(new Blob1d(20, 40));
    blobCenters.add(new Blob1d(90, 10));
    blobCenters.add(new Blob1d(50, 30));
    blobCenters.add(new Blob1d(70, 10));
    blobCenters.add(new Blob1d(40, 10));
  }

  @Override
  public List<Blob1d> getBlobs() {

    return blobCenters;
  }

  @Override
  public int getPanelWidth() {

    return 1024;
  }

  @Override
  public int getNumSpikes() {

    return 64;
  }
}
