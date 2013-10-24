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
import com.mancrd.ahah.clusterer.syndata.NoiseInjector.Noise;
import com.mancrd.ahah.clusterer.syndata.RandomSpikePatternGenerator;

/**
 * @author timmolter
 */
public class SweepNoiseTypeApp extends Sweeper {

  /**
   * This app takes the following arguments:
   * <ul>
   * <li>isFunctional (true) : functional or circuit AHaH model
   * <li>isSweetSpot (true) : does experiment averaging at sweetspot orthogonal value if true. if false does separate sweeps at all orthogonal values
   * <li>int numSweeps4Averaging (3) : if isSweetSpot is true, how many experiments should be used for averaging
   * <p>
   * NOTE!! Add -Xms512m -Xmx1024m to VM args when running this.
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws Exception {

    boolean isFunctional = true;
    boolean isSweetSpot = true;
    int numSweeps4Averaging = 3;

    try {
      isFunctional = args[0].equals("true");
      isSweetSpot = args[1].equals("true");
      numSweeps4Averaging = Integer.parseInt(args[2]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }

    if (isSweetSpot) {
      new SweepNoiseTypeApp(isFunctional).go(numSweeps4Averaging);
    }
    else {
      new SweepNoiseTypeApp(isFunctional).go();
    }
  }

  /**
   * Constructor
   * 
   * @param isFunctional
   */
  public SweepNoiseTypeApp(boolean isFunctional) {

    super(isFunctional);
  }

  @Override
  public List<Number> getXAxisValues() {

    List<Number> xAxisValues = new ArrayList<Number>();

    for (int noiseBits = 0; noiseBits <= 64; noiseBits += 4) {
      xAxisValues.add(noiseBits);
    }
    return xAxisValues;
  }

  @Override
  public List<Number> getOrthoganolValues() {

    List<Number> orthogonalValues = new ArrayList<Number>() {

      {
        // noise type
        add(1);
        // add(2);
        // add(3);
        // add(4);
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

        IClusterer clusterer;
        if (isFunctional) {
          clusterer = (new com.mancrd.ahah.clusterer.functional.ClustererBuilder()).build();
        }
        else {
          clusterer = (new com.mancrd.ahah.clusterer.circuit.ClustererBuilder()).build();
        }

        Noise noise = Noise.FLIP;

        if (orthogonalValue.equals(1)) {
          noise = Noise.ADDITIVE;
        }
        else if (orthogonalValue.equals(2)) {
          noise = Noise.SUBTRACTIVE;
        }
        else if (orthogonalValue.equals(3)) {
          noise = Noise.ADD_SUBTRACT;
        }
        else if (orthogonalValue.equals(4)) {
          noise = Noise.FLIP;
        }

        int numNoiseBits = (Integer) xAxisValue;

        RandomSpikePatternGenerator randomSpikesGenerator = new RandomSpikePatternGenerator.Builder().numNoiseBits(numNoiseBits).noise(noise).build();

        clustererDrivers.add(new ClustererDriver(clusterer, randomSpikesGenerator));
      }
      sweepMatrix.add(clustererDrivers);
    }
    return sweepMatrix;
  }

  @Override
  public String getChartTitle() {

    return "Noise Type vs Noise Amount";
  }

  @Override
  public String getOrthoganolName() {

    return "Noise Type";
  }

  @Override
  public Number getOrthoganolSweetSpotValue() {

    return 4;// flip noise. Not really the "sweet spot", just a choice.
  }

}
