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
package com.mancrd.ahah.commons;

/**
 * @author alexnugent
 */
public class LinkWeight implements Comparable<LinkWeight> {

  private final long spike;
  private final int label;
  private final double weight;

  private String labelString;
  private String spikeString;

  /**
   * Constructor
   * 
   * @param feature
   * @param label
   * @param weight
   */
  public LinkWeight(long spike, int label, double weight) {

    this.spike = spike;
    this.label = label;
    this.weight = weight;
  }

  public long getSpike() {

    return spike;
  }

  public int getLabel() {

    return label;
  }

  public double getWeight() {

    return weight;
  }

  @Override
  public int compareTo(LinkWeight other) {

    if (other.getWeight() > getWeight()) {
      return 1;
    }
    else if (other.getWeight() < getWeight()) {
      return -1;
    }
    return 0;
  }

  @Override
  public String toString() {

    return spikeString + "/" + spike + "-->(" + weight + ")-->" + labelString + "/" + label;
  }

  public String getLabelString() {

    return labelString;
  }

  public void setLabelString(String labelString) {

    this.labelString = labelString;
  }

  public String getSpikeString() {

    return spikeString;
  }

  public void setSpikeString(String spikeString) {

    this.spikeString = spikeString;
  }

}
