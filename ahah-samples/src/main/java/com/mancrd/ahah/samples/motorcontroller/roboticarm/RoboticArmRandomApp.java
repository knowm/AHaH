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
 * Run the Robotic Arm game with only the Classifier.
 * 
 * @author alexnugent
 */
public class RoboticArmRandomApp extends AbstractRoboticArmApp {

  private static final int NUM_JOINTS = 4;
  private static final int START_LEVEL_ID = 0;
  private static final int NUM_TARGETS_PER_LEVEL = 50;

  private static final int NUM_FIBERS_PER_MUSCLE = 100;
  private static final int BUFFER_LENGTH = 1;

  private final RoboticArmBrainRandom roboticArmBrainRandom;

  /**
   * Constructor
   */
  public RoboticArmRandomApp() {

    roboticArmBrainRandom = new RoboticArmBrainRandom(NUM_JOINTS, BUFFER_LENGTH, NUM_FIBERS_PER_MUSCLE);

    roboticArm = new RoboticArm(NUM_JOINTS, START_LEVEL_ID, NUM_TARGETS_PER_LEVEL);

    init(NUM_JOINTS);

    roboticArm.start();
  }

  public static void main(String[] args) {

    new RoboticArmRandomApp();
  }

  @Override
  protected AbstractRoboticArmBrain getRoboticArmBrain() {

    return roboticArmBrainRandom;
  }
}