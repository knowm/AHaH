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
package com.mancrd.ahah.samples.clusterer.visual.oned;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.mancrd.ahah.clusterer.functional.ClustererBuilder;
import com.mancrd.ahah.commons.spikes.KNearestNeighbors;
import com.mancrd.ahah.samples.clusterer.visual.ClusteringPanel;
import com.mancrd.ahah.samples.clusterer.visual.Slideable;

/**
 * @author timmolter
 */
public abstract class ClusteringPanel1d extends ClusteringPanel {

  static final int PANEL_HEIGHT = 260;

  protected Queue<Coordinate1d> fifo;

  /**
   * Constructor
   */
  public ClusteringPanel1d() {

    super();

    setPreferredSize(new Dimension(getPanelWidth(), PANEL_HEIGHT));

    kNearestNeighbors = new KNearestNeighbors(512, 1, .1);
    clusterer = new ClustererBuilder().numInputs(60000).ahahNodes(24).learningRate(.003).maxInitWeight(.003).build();

    fifo = new LinkedList<Coordinate1d>();
  }

  @Override
  public void paint(Graphics g) {

    super.paintComponent(g);

    if (isRunning) {

      cycle();

      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setStroke(stroke);

      // 1. draw blob centers
      g2d.setColor(Color.BLACK);
      for (int i = 0; i < getBlobs().size(); i++) {
        int xCoord = getBlobs().get(i).getxCoord();
        g2d.drawLine(xCoord * getPanelWidth() / 100, 0, xCoord * getPanelWidth() / 100, PANEL_HEIGHT);
      }

      // 2. draw blobs
      for (Coordinate1d coordinate : fifo) {
        // System.out.println(coordinate.getColorIdx());
        Color color = colorMap.get(coordinate.getColorIdx());
        if (color == null) {
          color = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
          colorMap.put(coordinate.getColorIdx(), color);
        }
        g2d.setColor(color);
        g2d.fillRect(coordinate.getxCoord(), (int) (PANEL_HEIGHT * .25), 2, (int) (PANEL_HEIGHT * .5));
      }
    }
    else {
      g.drawString("Press Spacebar to Continue.", 15, 15);
    }
    Toolkit.getDefaultToolkit().sync();
    g.dispose();
  }

  /**
   * Update entire Surface state
   */
  public void cycle() {

    cycleCount++;

    if (this instanceof Slideable) {

      if (cycleCount % (1000 / ((Slideable) this).getSlideRate()) == 0) {
        getBlobs().get(0).setxCoord(1 * direction + getBlobs().get(0).getxCoord());
      }
      if (getBlobs().get(0).getxCoord() * getPanelWidth() / 100 <= 0) {
        direction = 1; // switch direction
      }
      else if (getBlobs().get(0).getxCoord() * getPanelWidth() / 100 >= getPanelWidth() - 1) {
        direction = -1; // switch direction
      }

    }

    // pick a random blob center
    Blob1d blob1d = getBlobs().get(random.nextInt(getBlobs().size()));
    fifo.add(getCoordinate(blob1d));
    if (fifo.size() > 20 * getBlobs().size()) {
      fifo.remove();
    }
  }

  /**
   * @param centerOfBlob
   * @return
   */
  private Coordinate1d getCoordinate(Blob1d blob) {

    int xCoord = -1;

    do { // pick a random point offset from the blob center
      xCoord = (blob.getxCoord() * getPanelWidth() / 100 + (int) (random.nextGaussian() * blob.getRadius()));
    } while (xCoord < 0 && xCoord > getPanelWidth());

    double[] d = { xCoord };
    Set<String> spikes = kNearestNeighbors.encode(d, getNumSpikes());

    return new Coordinate1d(xCoord, clusterer.put(spikes));
  }

  /** as a percentage of the width */
  public abstract List<Blob1d> getBlobs();

  public abstract int getPanelWidth();

  public abstract int getNumSpikes();

}