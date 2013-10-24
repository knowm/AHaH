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
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.system.AppSettings;
import com.mancrd.ahah.samples.motorcontroller.roboticarm.data.RoboticArmRaw;
import com.mancrd.ahah.samples.motorcontroller.roboticarm.data.RoboticArmRawDAO;
import com.xeiam.proprioceptron.roboticarm.JointCommand;
import com.xeiam.proprioceptron.roboticarm.RoboticArm;
import com.xeiam.proprioceptron.roboticarm.Score;
import com.xeiam.yank.DBConnectionManager;
import com.xeiam.yank.PropertiesUtils;

/**
 * @author timmolter
 */
public abstract class AbstractRoboticArmApp implements PropertyChangeListener {

  protected RoboticArm roboticArm;
  protected final List<Score> scores = new ArrayList<Score>();

  private int numJoints;

  /**
   * Constructor
   */
  protected void init(int numJoints) {

    this.numJoints = numJoints;

    // disable jme3 logging
    Logger.getLogger("com.jme3").setLevel(Level.SEVERE);

    Properties dbprops = PropertiesUtils.getPropertiesFromClasspath("DB.properties");
    DBConnectionManager.INSTANCE.init(dbprops);

    RoboticArmRawDAO.dropTable();
    RoboticArmRawDAO.createTable();

    roboticArm.setShowSettings(false);
    AppSettings settings = new AppSettings(true);
    settings.setResolution(500, 500);
    settings.setTitle("Robotic Arm App - " + numJoints + " Joints");
    roboticArm.setSettings(settings);
    roboticArm.addChangeListener(this);

  }

  @Override
  public void propertyChange(PropertyChangeEvent pce) {

    if (pce.getPropertyName().equalsIgnoreCase("STATE_CHANGE")) {
      List<JointCommand> jointCommands = getRoboticArmBrain().update(pce);
      roboticArm.moveJoints(jointCommands);
    }
    else if (pce.getPropertyName().equalsIgnoreCase("LEVEL_SCORE")) {
      Score score = (Score) pce.getNewValue();
      printScores(score);
    }
    else if (pce.getPropertyName().equalsIgnoreCase("GAME_OVER")) {

      // RoboticArmResultsPlotter roboticArmResultsPlotter = new RoboticArmResultsPlotter();
      // roboticArmResultsPlotter.plotSimple();
      // roboticArmResultsPlotter.plotLevelSummary();

      DBConnectionManager.INSTANCE.release();
      // System.exit(0);
    }
  }

  /**
   * Prints the scores for each level
   */
  protected void printScores(Score score) {

    System.out.println(score.toString());
    scores.add(score);
    for (int i = 0; i < score.getPillIDs().length; i++) {
      RoboticArmRaw roboticArmRaw = new RoboticArmRaw();
      roboticArmRaw.setAlgo(getRoboticArmBrain().getClass().getSimpleName());
      roboticArmRaw.setNumjoints(numJoints);
      roboticArmRaw.setLevel(score.getLevelId());
      roboticArmRaw.setPillid(i);
      roboticArmRaw.setEnergy(score.getActivationEnergiesRequired()[i]);
      roboticArmRaw.setTime(score.getTimesElapsed()[i]);

      RoboticArmRawDAO.insert(roboticArmRaw);
    }

  }

  protected abstract AbstractRoboticArmBrain getRoboticArmBrain();

}
