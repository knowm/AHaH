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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mancrd.ahah.clusterer.IClusterer;
import com.mancrd.ahah.clusterer.syndata.RandomSpikePatternGenerator;

/**
 * @author timmolter
 */
public class SweepNumSpikePatternsApp extends Sweeper {

  /**
   * This app takes the following arguments:
   * <ul>
   * <li>isFunctional (true) : functional or circuit AHaH model
   * <li>isSweetSpot (true) : does experiment averaging at sweetspot orthogonal value if true. if false does separate sweeps at all orthogonal values
   * <li>int numSweeps4Averaging (10) : if isSweetSpot is true, how many experiments should be used for averaging
   * <p>
   * NOTE!! Add -Xms512m -Xmx1024m to VM args when running this.
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws Exception {

    boolean isFunctional = true;
    boolean isSweetSpot = true;
    int numSweeps4Averaging = 10;

    try {
      isFunctional = args[0].equals("true");
      isSweetSpot = args[1].equals("true");
      numSweeps4Averaging = Integer.parseInt(args[2]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    if (isSweetSpot) {
      new SweepNumSpikePatternsApp(isFunctional).go(numSweeps4Averaging);
    }
    else {
      new SweepNumSpikePatternsApp(isFunctional).go();
    }
  }

  /**
   * Constructor
   * 
   * @param isFunctional
   */
  public SweepNumSpikePatternsApp(boolean isFunctional) {

    super(isFunctional);
  }

  @Override
  public List<Number> getXAxisValues() {

    List<Number> xAxisValues = new ArrayList<Number>();

    for (int numSpikePatterns = 16; numSpikePatterns <= 32; numSpikePatterns += 2) {
      xAxisValues.add(numSpikePatterns);
    }
    return xAxisValues;
  }

  @Override
  public List<Number> getOrthoganolValues() {

    List<Number> orthogonalValues = new ArrayList<Number>() {

      {
        // num noise bits
        add(4);
        add(8);
        add(16);
        add(32);
      }
    };

    return orthogonalValues;
  }

  @Override
  public List<List<ClustererDriver>> getSweepMatrix(List<Number> orthogonalValues) {

    List<List<ClustererDriver>> sweepMatrix = new ArrayList<List<ClustererDriver>>();

    for (Number orthogonalValue : orthogonalValues) {

      List<ClustererDriver> clustererDrivers = new ArrayList<ClustererDriver>();

      for (Number xAxisValue : getXAxisValues()) {

        int numNoiseBits = (Integer) orthogonalValue;
        int numSpikePatterns = (Integer) xAxisValue;

        IClusterer clusterer;
        if (isFunctional) {
          clusterer = (new com.mancrd.ahah.clusterer.functional.ClustererBuilder()).build();
        }
        else {
          clusterer = (new com.mancrd.ahah.clusterer.circuit.ClustererBuilder()).build();
        }

        RandomSpikePatternGenerator randomSpikesGenerator = new RandomSpikePatternGenerator.Builder().numSpikePatterns(numSpikePatterns).numNoiseBits(numNoiseBits).build();

        clustererDrivers.add(new ClustererDriver(clusterer, randomSpikesGenerator));
      }
      sweepMatrix.add(clustererDrivers);
    }
    return sweepMatrix;
  }

  @Override
  public String getChartTitle() {

    return "Number Spike Patterns";
  }

  @Override
  public String getOrthoganolName() {

    return "Num Noise Bits";
  }

  @Override
  public Number getOrthoganolSweetSpotValue() {

    return 3;
  }

}
