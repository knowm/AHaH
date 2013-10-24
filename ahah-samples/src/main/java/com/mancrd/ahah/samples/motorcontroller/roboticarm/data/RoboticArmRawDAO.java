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
package com.mancrd.ahah.samples.motorcontroller.roboticarm.data;

import java.util.List;

import com.xeiam.yank.DBProxy;

/**
 * @author timmolter
 */
public class RoboticArmRawDAO {

  public static int dropTable() {

    return DBProxy.executeSQL("roboticarm", "DROP TABLE IF EXISTS ROBOTIC_ARM_RAW", null);
  }

  public static int createTable() {

    String BREAST_CANCER_CREATE =
        "CREATE CACHED TABLE ROBOTIC_ARM_RAW (algo VARCHAR(256) NOT NULL, numjoints INTEGER NOT NULL, level INTEGER NOT NULL, pillid INTEGER NOT NULL, energy INTEGER NOT NULL, time DOUBLE NOT NULL)";
    return DBProxy.executeSQL("roboticarm", BREAST_CANCER_CREATE, null);
  }

  public static int insert(RoboticArmRaw roboticArmRaw) {

    Object[] params = new Object[] {

    // @formatter:off
        roboticArmRaw.getAlgo(),
        roboticArmRaw.getNumjoints(),
        roboticArmRaw.getLevel(),
        roboticArmRaw.getPillid(),
        roboticArmRaw.getEnergy(),
        roboticArmRaw.getTime()
    // @formatter:on

        };
    String ROBOTIC_ARM_RAW_INSERT = "INSERT INTO ROBOTIC_ARM_RAW (ALGO, NUMJOINTS, LEVEL, PILLID, ENERGY, TIME) VALUES (?, ?, ?, ?, ?, ?)";
    return DBProxy.executeSQL("roboticarm", ROBOTIC_ARM_RAW_INSERT, params);
  }

  public static List<RoboticArmRaw> selectAll() {

    String ROBOTIC_ARM_SELECT = "SELECT * FROM ROBOTIC_ARM_RAW";
    return DBProxy.queryObjectListSQL("roboticarm", ROBOTIC_ARM_SELECT, RoboticArmRaw.class, null);
  }

  public static List<RoboticArmRaw> selectNumJoints(String algo, int numJoints) {

    Object[] params = new Object[] { algo, numJoints };

    String ROBOTIC_ARM_SELECT = "SELECT * FROM ROBOTIC_ARM_RAW WHERE ALGO = ? AND NUMJOINTS = ?";
    return DBProxy.queryObjectListSQL("roboticarm", ROBOTIC_ARM_SELECT, RoboticArmRaw.class, params);
  }

  public static List<RoboticArmRaw> selectLevel(String algo, int level) {

    Object[] params = new Object[] { algo, level };

    String ROBOTIC_ARM_SELECT = "SELECT * FROM ROBOTIC_ARM_RAW WHERE ALGO = ? AND LEVEL = ?";
    return DBProxy.queryObjectListSQL("roboticarm", ROBOTIC_ARM_SELECT, RoboticArmRaw.class, params);
  }

  public static List<RoboticArmRaw> selectRunId(String algo) {

    Object[] params = new Object[] { algo };

    String ROBOTIC_ARM_SELECT = "SELECT * FROM ROBOTIC_ARM_RAW WHERE ALGO = ?";
    return DBProxy.queryObjectListSQL("roboticarm", ROBOTIC_ARM_SELECT, RoboticArmRaw.class, params);
  }

}
