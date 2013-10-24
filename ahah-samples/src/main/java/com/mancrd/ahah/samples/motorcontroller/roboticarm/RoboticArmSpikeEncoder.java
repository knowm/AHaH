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
package com.mancrd.ahah.samples.motorcontroller.roboticarm;

import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Set;

import com.jme3.math.Vector3f;
import com.mancrd.ahah.commons.spikes.AHaHA2D;
import com.mancrd.ahah.commons.spikes.SpikeEncoder;
import com.xeiam.proprioceptron.roboticarm.RoboticArmGameState;

/**
 * @author timmolter
 */
public class RoboticArmSpikeEncoder extends SpikeEncoder<PropertyChangeEvent> {

  private static final int TREE_DEPTH = 3;

  AHaHA2D difTreeBinner = new AHaHA2D(TREE_DEPTH);
  AHaHA2D distanceTreeBinner = new AHaHA2D(TREE_DEPTH);
  AHaHA2D jointTreeBinner = new AHaHA2D(TREE_DEPTH);
  AHaHA2D jointCommandBinner = new AHaHA2D(TREE_DEPTH);

  @Override
  public Set<String> getSpikes(PropertyChangeEvent pce) {

    Set<String> feature = new HashSet<String>();

    // EnvState oldEnvState = (EnvState) pce.getOldValue();
    RoboticArmGameState newEnvState = (RoboticArmGameState) pce.getNewValue();

    // Here's the info for the robotic arm that we have to work with:
    // private final float distLeftEye;
    // private final float distRightEye;
    // private final float distHead;
    // private final Vector3f[] relativePositions;
    // private final boolean wasCollision;

    appendToFeature("DISTANCE_RIGHT_EYE=", distanceTreeBinner.putAndParse(newEnvState.getEnvState().getDistRightEye()), feature);
    appendToFeature("DISTANCE_LEFT_EYE=", distanceTreeBinner.putAndParse(newEnvState.getEnvState().getDistLeftEye()), feature);
    appendToFeature("DISTANCE_DIF_H2RH=", difTreeBinner.putAndParse(newEnvState.getEnvState().getDistHead() - newEnvState.getEnvState().getDistRightEye()), feature);
    appendToFeature("DISTANCE_DIF_H2LH=", difTreeBinner.putAndParse(newEnvState.getEnvState().getDistHead() - newEnvState.getEnvState().getDistLeftEye()), feature);
    appendToFeature("DISTANCE_DIF_EYES=", difTreeBinner.putAndParse(newEnvState.getEnvState().getDistRightEye() - newEnvState.getEnvState().getDistLeftEye()), feature);

    for (int i = 0; i < newEnvState.getEnvState().getRelativePositions().length; i++) {
      Vector3f jointVector = newEnvState.getEnvState().getRelativePositions()[i];
      appendToFeature("JOINT_" + i + "_X=", jointTreeBinner.putAndParse(jointVector.getX()), feature);
      appendToFeature("JOINT_" + i + "_Z=", jointTreeBinner.putAndParse(jointVector.getZ()), feature);
    }

    return feature;
  }

  /**
   * @param header
   * @param binnerStates
   * @param spikes - the spike set we're adding spikes to
   */
  private void appendToFeature(String header, int[] binnerStates, Set<String> spikes) {

    for (int i = 0; i < binnerStates.length; i++) {
      spikes.add(header + binnerStates[i]);
    }
  }

  /**
   * Given the number of joints, how many possible unique features are there
   * 
   * @param numJoints
   * @return
   */
  public int getTotalNumberOfPossibleFeatures(int numJoints) {

    int numberDistanceFeatures = (int) (2 * 2 * Math.pow(2, TREE_DEPTH));
    int numberDifFeatures = (int) (3 * 2 * Math.pow(2, TREE_DEPTH));
    int numberJointFeatures = (int) (2 * 2 * Math.pow(2, TREE_DEPTH)) * numJoints;
    return numberDistanceFeatures + numberDifFeatures + numberJointFeatures;
  }

  @Override
  public short getUniquePositiveID() {

    // TODO Auto-generated method stub
    return 0;
  }

}
