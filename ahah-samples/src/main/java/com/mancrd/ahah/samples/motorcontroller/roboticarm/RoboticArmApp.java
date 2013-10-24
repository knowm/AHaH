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

import com.xeiam.proprioceptron.roboticarm.RoboticArm;

/**
 * Run the Robotic Arm game with only the Classifier
 * 
 * @author alexnugent
 */
public class RoboticArmApp extends AbstractRoboticArmApp {

  private final AbstractRoboticArmBrain roboticArmBrain;

  /**
   * This app takes the following arguments:
   * <ul>
   * <li>int numJoints (9): the number of joints in the robotic arm
   * <li>int startLevel (0): the level that that game should start at, zero-indexed
   * <li>int numTargetsPerLevel (100): the number of pills the arm should catch before switching to the next level
   * <li>int numFibersPerMuscle (20): the number of muscle fibers each muscle has
   * <li>int bufferLength (1): the buffer length - controls the delay time in which the proprioceptive data feeds back into the motor controller
   * 
   * @param args
   */
  public static void main(String[] args) {

    new RoboticArmApp(args);
  }

  /**
   * Constructor
   * 
   * @param args
   */
  public RoboticArmApp(String[] args) {

    // read in params from args
    int numJoints = 9;
    int startLevel = 0;
    int numTargetsPerLevel = 100;
    int numFibersPerMuscle = 20;
    int bufferLength = 1;// if you increase this, may also want to decrease the learning rate in Actuator.
    try {
      numJoints = Integer.parseInt(args[0]);
      startLevel = Integer.parseInt(args[1]);
      numTargetsPerLevel = Integer.parseInt(args[2]);
      numFibersPerMuscle = Integer.parseInt(args[3]);
      bufferLength = Integer.parseInt(args[4]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      // just ignore
    }
    roboticArmBrain = new RoboticArmAhahBrain(numJoints, bufferLength, numFibersPerMuscle);
    // roboticArmBrain = new RoboticArmBrainRandom(numJoints, bufferLength, numFibersPerMuscle);

    roboticArm = new RoboticArm(numJoints, startLevel, numTargetsPerLevel);

    init(numJoints);

    roboticArm.start();
  }

  @Override
  protected AbstractRoboticArmBrain getRoboticArmBrain() {

    return roboticArmBrain;
  }
}