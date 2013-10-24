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
package com.mancrd.ahah.samples.classifier.mnist;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * @author alexnugent
 */
public class MnistImagePanel extends JPanel {

  private BufferedImage image;

  public MnistImagePanel(int[][] img, int scale) {

    image = new BufferedImage(img.length * scale, img[0].length * scale, BufferedImage.TYPE_INT_RGB);

    for (int y = 0; y < img.length; y++) {
      for (int x = 0; x < img[0].length; x++) {
        int value = img[y][x] << 16 | img[y][x] << 8 | img[y][x];
        image.setRGB(x, y, value);
      }
    }
    scale(scale);

    setPreferredSize(new Dimension(img.length * scale, img[0].length * scale));
  }

  private void scale(int scale) {

    int w = image.getWidth();
    int h = image.getHeight();
    BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    AffineTransform at = new AffineTransform();
    at.scale(scale, scale);
    AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    after = scaleOp.filter(image, after);

    this.image = after;

  }

  @Override
  public void paintComponent(Graphics g) {

    super.paintComponent(g);
    g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters
  }

}
