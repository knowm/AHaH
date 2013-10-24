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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mancrd.ahah.motorcontroller.Actuator;
import com.xeiam.proprioceptron.roboticarm.JointCommand;
import com.xeiam.proprioceptron.roboticarm.RoboticArmGameState;

/**
 * @author timmolter
 */
public class RoboticArmAhahBrain extends AbstractRoboticArmBrain {

  /** converts the robotic arm's game state to a sparse encoded feature */
  private final RoboticArmSpikeEncoder roboticArmSpikeEncoder;

  private final Actuator actuator;

  private double e = 0;

  private double numUpdates = 0;
  private double numCollisions = 0;
  private double totalActuation = 0;

  private double performance = 0;
  private final double pk = .05;

  // private final int maxActuationSteps = 5;// for 9+ or more joints

  private final int maxActuationSteps = 50;// for 2-9 or less joints

  /**
   * Constructor
   * 
   * @param numJoints
   */
  public RoboticArmAhahBrain(int numJoints, int bufferLength, int numFibersPerMuscle) {

    roboticArmSpikeEncoder = new RoboticArmSpikeEncoder();

    // actuator
    actuator = new Actuator(numJoints, numFibersPerMuscle, bufferLength);
  }

  @Override
  public List<JointCommand> update(PropertyChangeEvent pce) {

    numUpdates++;

    // 1. convert the robotic arms game state (pce) to a sparse encoded feature
    long[] longspikes = roboticArmSpikeEncoder.encode(pce); // not really needed but to get the spike space it is. TODO refactor actuator to be a "classifier" using longs for spikes.
    Set<String> spikes = roboticArmSpikeEncoder.getSpikes(pce);
    System.out.println("Spike Pattern Length = " + spikes.size());

    // 2. get the raw RoboticArmGameState
    RoboticArmGameState RoboticArmGameState = (RoboticArmGameState) pce.getNewValue();

    // 3. update score if there was a collision
    if (RoboticArmGameState.getEnvState().wasCollision()) {
      e += 1;
      numCollisions++;
      if (numCollisions == 1) {
        performance = totalActuation;
      }
      else {
        performance = (1 - pk) * performance + pk * totalActuation;
      }

      // reset total Activation
      totalActuation = 0;
      // System.out.println(performance);
    }

    // 4. get the actuations from the motor classifier
    double value = (e) + 1 / (1 + RoboticArmGameState.getEnvState().getDistHead());
    int[] actuations = actuator.update(spikes, value);

    // 5. create the Joint Commands
    List<JointCommand> jointCommands = new ArrayList<JointCommand>();
    for (int i = 0; i < actuations.length; i++) {
      JointCommand jointCommand = new JointCommand(i, (int) Math.signum(actuations[i]), limitActuation(Math.abs(actuations[i])));
      jointCommands.add(jointCommand);
      this.totalActuation += jointCommand.getSteps();
      // e -= .001 * jointCommand.getSteps();
    }
    System.out.println("Spike Pattern Space = " + roboticArmSpikeEncoder.getSpikePatternSpace());
    System.out.println("Num AHaH Nodes = " + actuator.getNumUniqueLabels());

    return jointCommands;

  }

  private int limitActuation(int steps) {

    if (steps > maxActuationSteps) {
      return maxActuationSteps;
    }
    else {
      return steps;
    }
  }

}
