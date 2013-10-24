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
package com.mancrd.ahah.samples.clusterer.visual.twod;

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
public abstract class ClusteringPanel2d extends ClusteringPanel {

  static final int DOT_RADIUS = 8;

  // All Coordinates
  Queue<Coordinate2d> fifo;

  /**
   * Constructor
   */
  public ClusteringPanel2d() {

    super();

    setPreferredSize(new Dimension(getPanelSize(), getPanelSize()));

    // Spike Encoder
    kNearestNeighbors = new KNearestNeighbors(512, 2, .01);
    clusterer = new ClustererBuilder().numInputs(60000).ahahNodes(24).learningRate(.003).maxInitWeight(.003).build();
    fifo = new LinkedList<Coordinate2d>();
  }

  @Override
  public void paint(Graphics g) {

    super.paintComponent(g);

    if (isRunning) {

      cycle();

      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setStroke(stroke);

      // 1. draw blob center cross marks
      g2d.setColor(Color.BLACK);
      for (int i = 0; i < getBlobs().size(); i++) {

        int xCoord = getBlobs().get(i).getxCoord();
        int yCoord = getBlobs().get(i).getyCoord();
        g2d.drawLine(xCoord * getPanelSize() / 100, yCoord * getPanelSize() / 100 - 15, xCoord * getPanelSize() / 100, yCoord * getPanelSize() / 100 + 15);
        g2d.drawLine(xCoord * getPanelSize() / 100 - 15, yCoord * getPanelSize() / 100, xCoord * getPanelSize() / 100 + 15, yCoord * getPanelSize() / 100);
      }

      // 2. draw blobs
      for (Coordinate2d coordinate : fifo) {
        // System.out.println(coordinate.getColorIdx());
        Color color = colorMap.get(coordinate.getColorIdx());
        if (color == null) {
          colorMap.put(coordinate.getColorIdx(), new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
        }

        g2d.setColor(color);
        g2d.fillOval(coordinate.getxCoord() - DOT_RADIUS, coordinate.getyCoord() - DOT_RADIUS, DOT_RADIUS, DOT_RADIUS);
      }

    }
    else {
      Toolkit.getDefaultToolkit().sync();
      g.drawString("Press Spacebar to Continue.", 15, 15);
    }
    g.dispose();
  }

  /**
   * Update entire Surface state
   */
  public void cycle() {

    cycleCount++;

    if (this instanceof Slideable) {

      for (int i = 0; i < getBlobs().size() - 1; i++) {
        if (cycleCount % (1000 / ((Slideable) this).getSlideRate()) == 0) {
          getBlobs().get(i).setxCoord(1 * direction + getBlobs().get(i).getxCoord());
        }
        if (getBlobs().get(i).getxCoord() * getPanelSize() / 100 <= 0) {
          direction = 1; // switch direction
        }
        else if (getBlobs().get(i).getxCoord() * getPanelSize() / 100 >= getPanelSize() - 1) {
          direction = -1; // switch direction
        }
      }

    }

    // pick a random blob center
    Blob2d blob2d = getBlobs().get(random.nextInt(getBlobs().size()));
    fifo.add(getCoordinate(blob2d));
    if (fifo.size() > 75 * getBlobs().size()) {
      fifo.remove();
    }
  }

  /**
   * Given a center of blob, return random points next to it picked from a Gaussian distribution
   * 
   * @param blob
   * @return Coordinate - the featureset and label
   */
  private Coordinate2d getCoordinate(Blob2d blob) {

    int xCoord = -1;
    int yCoord = -1;

    do { // pick a random point offset from the blob center
      xCoord = (blob.getxCoord() * getPanelSize() / 100 + (int) (random.nextGaussian() * blob.getRadius()));
      yCoord = (blob.getyCoord() * getPanelSize() / 100 + (int) (random.nextGaussian() * blob.getRadius()));
    } while (xCoord < 0 && xCoord > getPanelSize() && yCoord < 0 && yCoord > getPanelSize());

    double[] d = { xCoord, yCoord };
    Set<String> spikes = kNearestNeighbors.encode(d, getNumSpikes());
    // Set<String> spikes = treeBinnerVector.encode(d);

    // System.out.println(spikes);

    return new Coordinate2d(xCoord, yCoord, clusterer.put(spikes));
  }

  /** as a percentage of the width */
  public abstract List<Blob2d> getBlobs();

  public abstract int getPanelSize();

  public abstract int getNumSpikes();

}