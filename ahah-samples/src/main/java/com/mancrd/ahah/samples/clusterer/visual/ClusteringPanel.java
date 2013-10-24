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
package com.mancrd.ahah.samples.clusterer.visual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.mancrd.ahah.clusterer.IClusterer;
import com.mancrd.ahah.commons.spikes.KNearestNeighbors;

/**
 * @author timmolter
 */
public class ClusteringPanel extends JPanel implements Runnable {

  protected boolean isRunning = false;

  private final int DELAY = 5;
  private Thread animator;

  protected BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
  protected Random random = new Random();

  protected int cycleCount = 0;
  protected int direction = 1;

  protected IClusterer clusterer;

  protected KNearestNeighbors kNearestNeighbors;

  protected HashMap<Integer, Color> colorMap = new HashMap<Integer, Color>();

  /**
   * Constructor
   */
  public ClusteringPanel() {

    setBackground(Color.WHITE);
    setDoubleBuffered(true);

    // Space bar for pausing
    KeyStroke spaceBar = KeyStroke.getKeyStroke(' ');
    this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(spaceBar, "pause");
    this.getActionMap().put("pause", new PauseAction());
  }

  @Override
  public void addNotify() {

    super.addNotify();
    animator = new Thread(this);
    animator.start();
  }

  @Override
  public void run() {

    long beforeTime, timeDiff, sleep;

    beforeTime = System.currentTimeMillis();

    while (true) {

      repaint();

      timeDiff = System.currentTimeMillis() - beforeTime;
      sleep = DELAY - timeDiff;

      if (sleep < 0)
        sleep = 2;
      try {
        Thread.sleep(sleep);
      } catch (InterruptedException e) {
        System.out.println("interrupted");
      }

      beforeTime = System.currentTimeMillis();
    }
  }

  private class PauseAction extends AbstractAction {

    public PauseAction() {

      super("pause");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

      isRunning = !isRunning;
    }
  }
}
