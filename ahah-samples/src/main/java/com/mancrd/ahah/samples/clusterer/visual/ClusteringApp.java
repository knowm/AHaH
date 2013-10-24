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
package com.mancrd.ahah.samples.clusterer.visual;

import com.mancrd.ahah.samples.clusterer.visual.oned.Sliding1d;
import com.mancrd.ahah.samples.clusterer.visual.oned.SlidingMany1d;
import com.mancrd.ahah.samples.clusterer.visual.oned.Static1d;
import com.mancrd.ahah.samples.clusterer.visual.twod.ClosePack2d;
import com.mancrd.ahah.samples.clusterer.visual.twod.CurveDot2d;
import com.mancrd.ahah.samples.clusterer.visual.twod.MickyMouse2d;
import com.mancrd.ahah.samples.clusterer.visual.twod.Sliding2d;
import com.mancrd.ahah.samples.clusterer.visual.twod.Static2d;
import com.mancrd.ahah.samples.clusterer.visual.twod.StaticRandom2d;

/**
 * @author timmolter
 */
public class ClusteringApp {

  /**
   * This app takes the following arguments:
   * <ul>
   * <li>int sampleNumber (0): the id of the sample animation to run [0 to 9]
   * <p>
   * 
   * @param args
   */
  public static void main(String[] args) {

    int sampleNumber = 8;
    try {
      sampleNumber = Integer.parseInt(args[0]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    switch (sampleNumber) {
    case 0:
      new ClusteringFrame(new Static1d());
      break;
    case 1:
      new ClusteringFrame(new Sliding1d());
      break;
    case 2:
      new ClusteringFrame(new SlidingMany1d());
      break;
    case 3:
      new ClusteringFrame(new Static2d());
      break;
    case 4:
      new ClusteringFrame(new MickyMouse2d());
      break;
    case 5:
      new ClusteringFrame(new ClosePack2d());
      break;
    case 6:
      new ClusteringFrame(new StaticRandom2d());
      break;
    case 7:
      new ClusteringFrame(new CurveDot2d());
      break;
    case 8:
      new ClusteringFrame(new Sliding2d());
      break;
    case 9:
      break;
    default:
      break;
    }

  }

}
