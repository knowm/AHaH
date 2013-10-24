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
package com.mancrd.ahah.samples.classifier.breastcancer;

import java.util.HashSet;
import java.util.Set;

import com.mancrd.ahah.commons.spikes.AHaHA2D;
import com.mancrd.ahah.commons.spikes.SpikeEncoder;
import com.xeiam.datasets.breastcancerwisconsinorginal.BreastCancer;

/**
 * Converts the data set into a spike code for use in the AHaH Classifier (or any other optimal linear classifier).
 * 
 * @author alexnugent
 */
public class BreastCancerSpikeEncoder extends SpikeEncoder<BreastCancer> {

  private final int treeBinnerDepth = 2;

  private final AHaHA2D[] encoders = new AHaHA2D[9];

  /**
   * Constructor
   */
  public BreastCancerSpikeEncoder() {

    for (int i = 0; i < encoders.length; i++) {
      encoders[i] = new AHaHA2D(treeBinnerDepth);
    }
  }

  @Override
  public Set<String> getSpikes(BreastCancer breastCancer) {

    Set<String> spikes = new HashSet<String>();

    /**
     * 1. Sample code number id number
     * <p>
     * 2. Clump Thickness 1 - 10
     * <p>
     * 3. Uniformity of Cell Size 1 - 10
     * <p>
     * 4. Uniformity of Cell Shape 1 - 10
     * <p>
     * 5. Marginal Adhesion 1 - 10
     * <p>
     * 6. Single Epithelial Cell Size 1 - 10
     * <p>
     * 7. Bare Nuclei 1 - 10
     * <p>
     * 8. Bland Chromatin 1 - 10
     * <p>
     * 9. Normal Nucleoli 1 - 10
     * <p>
     * 10. Mitoses 1 - 10
     * <p>
     * 11. Class: (2 for benign, 4 for malignant)
     */

    addToSpikes("ClumpThickness:", encoders[0].putAndParse(breastCancer.getClumpThickness()), spikes);
    addToSpikes("UniformityOfCellSize:", encoders[1].putAndParse(breastCancer.getUniformityOfCellSize()), spikes);
    addToSpikes("UniformityOfCellShape:", encoders[2].putAndParse(breastCancer.getUniformityOfCellShape()), spikes);
    addToSpikes("MarginalAdhesion:", encoders[3].putAndParse(breastCancer.getMarginalAdhesion()), spikes);
    addToSpikes("EpithelialCellSize:", encoders[4].putAndParse(breastCancer.getSingleEpithelialCellSize()), spikes);
    addToSpikes("BareNuclei:", encoders[5].putAndParse(breastCancer.getBareNuclei()), spikes);
    addToSpikes("BlandChromatin:", encoders[6].putAndParse(breastCancer.getBlandChromatin()), spikes);
    addToSpikes("NormalNucleoli:", encoders[7].putAndParse(breastCancer.getNormalNucleoli()), spikes);
    addToSpikes("Mitoses:", encoders[8].putAndParse(breastCancer.getMitoses()), spikes);
    spikes.add("BIAS");

    return spikes;

  }

  private void addToSpikes(String concat, int[] bins, Set<String> spikes) {

    for (int i = 0; i < bins.length; i++) {
      spikes.add(concat + bins[i]);
    }
  }

  @Override
  public short getUniquePositiveID() { // don't worry about this.

    return 1;
  }

}
