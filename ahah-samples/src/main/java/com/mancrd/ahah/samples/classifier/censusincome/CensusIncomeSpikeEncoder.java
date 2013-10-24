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
package com.mancrd.ahah.samples.classifier.censusincome;

import java.util.HashSet;
import java.util.Set;

import com.mancrd.ahah.commons.spikes.AHaHA2D;
import com.mancrd.ahah.commons.spikes.SpikeEncoder;
import com.xeiam.datasets.censusincome.CensusIncome;

/**
 * @author alexnugent
 */
public class CensusIncomeSpikeEncoder extends SpikeEncoder<CensusIncome> {

  AHaHA2D ageBinner = new AHaHA2D(8);
  AHaHA2D fnlwgtBinner = new AHaHA2D(8);
  AHaHA2D educationNumBinner = new AHaHA2D(8);
  AHaHA2D capitalGainBinner = new AHaHA2D(8);
  AHaHA2D capitalLossBinner = new AHaHA2D(8);
  AHaHA2D hoursPerWeekBinner = new AHaHA2D(8);

  @Override
  public Set<String> getSpikes(CensusIncome censusIncome) {

    Set<String> spikes = new HashSet<String>();

    // Age
    int[] ageFeatures = ageBinner.putAndParse(censusIncome.getAge());
    for (int i = 0; i < ageFeatures.length; i++) {
      spikes.add(("0:" + ageFeatures[i]));
    }
    // Workclass
    spikes.add(("1:" + censusIncome.getWorkclass()));
    // FnlWght
    int[] fnlwgtFeatures = fnlwgtBinner.putAndParse(censusIncome.getFnlwgt());
    for (int i = 0; i < fnlwgtFeatures.length; i++) {
      spikes.add(("2:" + fnlwgtFeatures[i]));
    }
    // Education
    spikes.add(("3:" + censusIncome.getEducation()));
    // educationNum
    int[] educationNumFeatures = educationNumBinner.putAndParse(censusIncome.getEducationNum());
    for (int i = 0; i < educationNumFeatures.length; i++) {
      spikes.add(("4: " + educationNumFeatures[i]));
    }
    // maritalStatus
    spikes.add(("5:" + censusIncome.getMaritalStatus()));
    // occupation
    spikes.add(("6:" + censusIncome.getOccupation()));
    // relationship
    spikes.add(("7:" + censusIncome.getRelationship()));
    // race
    spikes.add(("8:" + censusIncome.getRace()));
    // sex
    spikes.add(("9:" + censusIncome.getSex()));
    // capitalGain
    int[] capitalGainFeatures = capitalGainBinner.putAndParse(censusIncome.getCapitalGain());
    for (int i = 0; i < capitalGainFeatures.length; i++) {
      spikes.add(("10:" + capitalGainFeatures[i]));
    }
    // capitalLoss
    int[] capitalLossFeatures = capitalLossBinner.putAndParse(censusIncome.getCapitalLoss());
    for (int i = 0; i < capitalLossFeatures.length; i++) {
      spikes.add(("11:" + capitalLossFeatures[i]));
    }
    // hoursPerWeek
    int[] hoursPerWeekFeatures = hoursPerWeekBinner.putAndParse(censusIncome.getHoursPerWeek());
    for (int i = 0; i < hoursPerWeekFeatures.length; i++) {
      spikes.add(("12:" + hoursPerWeekFeatures[i]));
    }
    // native country
    spikes.add(("13:" + censusIncome.getNativeCountry()));

    // income less than 50k <--this is what we are trying to predict. DO NOT ADD!

    spikes.add("BIAS");

    return spikes;

  }

  @Override
  public short getUniquePositiveID() {

    return 0;
  }

  @Override
  public String getSpikeLabel(int spike) {

    return null;
  }

}
