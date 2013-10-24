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
package com.mancrd.ahah.clusterer.syndata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.mancrd.ahah.clusterer.syndata.NoiseInjector.Noise;

/**
 * @author alexnugent
 */
public class RandomSpikePatternGenerator {

  /** the number of spikePatterns to create */
  private final int numSpikePatterns;

  /** the total number of spikes over all patterns */
  private final int maxInputSpikes;

  /** the length of the input spike pattern. Note that for each actual trial, a smaller or larger number inputs may fire due to noise */
  private final int spikePatternLength;

  /** the number of bits to randomly flip in each spikes */
  private final int numNoiseBits;

  /** Noise type */
  private final Noise noise;

  /** A 2D Matrix of Integers */
  private final List<Set<String>> spikePatterns;

  /** A Random object used for creating random numbers */
  private final Random random = new Random();

  /** A NoiseInjector for injecting noise into a feature */
  private final NoiseInjector noiseInjector = new NoiseInjector();

  /**
   * Constructor
   * 
   * @param builder
   */
  private RandomSpikePatternGenerator(Builder builder) {

    this.numSpikePatterns = builder.numSpikePatterns;
    this.maxInputSpikes = builder.maxInputSpikes;
    if (builder.spikePatternLength > builder.maxInputSpikes) {
      throw new IllegalArgumentException("spikesLength cannot be greater than maxInputSpikes !");
    }
    this.spikePatternLength = builder.spikePatternLength;
    this.numNoiseBits = builder.numNoiseBits;
    this.noise = builder.noise;
    this.spikePatterns = generateSpikePatterns();

  }

  /**
   * create a set of random spikes
   * 
   * @return
   */
  private List<Set<String>> generateSpikePatterns() {

    List<Set<String>> spikePatternList = new ArrayList<Set<String>>();
    for (int i = 0; i < numSpikePatterns; i++) {
      spikePatternList.add(generateRandomSpikePattern());
    }
    return spikePatternList;
  }

  /**
   * create a random pattern defined by the co-activation of input lines. Each input line has an ID. A pattern is a collection of sensors that are
   * active for a pattern.
   * 
   * @return
   */
  private Set<String> generateRandomSpikePattern() {

    Set<String> spikes = new HashSet<String>();
    do {
      spikes.add(new Integer(random.nextInt(this.maxInputSpikes)).toString());
    } while (spikes.size() < this.spikePatternLength);

    return spikes;
  }

  /**
   * @return
   */
  public List<Set<String>> getSpikePatterns() {

    return spikePatterns;
  }

  /**
   * @return a random spikes index between 0 and spikes.size() - 1
   */
  public int getRandomSpikeId() {

    return this.random.nextInt(spikePatterns.size());
  }

  /**
   * @param spikeId
   * @return a spikePattern given in spikeId
   */
  public Set<String> getSpikePattern(int spikeId) {

    return spikePatterns.get(spikeId);
  }

  public Set<String> getRandomClonedSpikePatternWithNoise() {

    return getClonedSpikePatternWithNoise(getRandomSpikeId());
  }

  /**
   * @param spikeId
   * @return cloned spikes patterns with noise added to it
   */
  public Set<String> getClonedSpikePatternWithNoise(int spikeId) {

    Set<String> noisySpikes = spikePatterns.get(spikeId);
    Set<String> clonedSpikes = new HashSet<String>();

    for (String integer : noisySpikes) {
      clonedSpikes.add(new String(integer));
    }

    noiseInjector.injectNoise(clonedSpikes, this.maxInputSpikes, this.noise, this.numNoiseBits);

    return clonedSpikes;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < spikePatterns.size(); i++) {
      sb.append(i + ": " + spikePatterns.get(i).toString() + "\n");
    }
    return sb.toString();
  }

  // BUILDER /////////////////////////////////////////////////////////

  public static class Builder {

    private Noise noise = RandomSpikeGeneratorDefaultValues.NOISE;
    private int numNoiseBits = RandomSpikeGeneratorDefaultValues.NUM_NOISE_BITS;

    private int maxInputSpikes = RandomSpikeGeneratorDefaultValues.MAX_INPUT_SPIKES;
    private int spikePatternLength = RandomSpikeGeneratorDefaultValues.SPIKE_PATTERN_LENGTH;
    private int numSpikePatterns = RandomSpikeGeneratorDefaultValues.NUM_SPIKE_PATTERNS;

    public Builder numSpikePatterns(int numSpikePatterns) {

      this.numSpikePatterns = numSpikePatterns;
      return this;
    }

    public Builder maxInputSpikes(int maxInputSpikes) {

      this.maxInputSpikes = maxInputSpikes;
      return this;
    }

    public Builder spikePatternLength(int spikePatternLength) {

      this.spikePatternLength = spikePatternLength;
      return this;
    }

    public Builder numNoiseBits(int numNoiseBits) {

      this.numNoiseBits = numNoiseBits;
      return this;
    }

    public Builder noise(Noise noise) {

      this.noise = noise;
      return this;
    }

    /**
     * return fully built object
     * 
     * @return a RandomFeatureGenerator
     */
    public RandomSpikePatternGenerator build() {

      return new RandomSpikePatternGenerator(this);
    }
  }

}
