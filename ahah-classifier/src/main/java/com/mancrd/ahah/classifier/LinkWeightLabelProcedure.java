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
package com.mancrd.ahah.classifier;

import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.procedure.TLongObjectProcedure;

import java.util.LinkedList;
import java.util.List;

import com.mancrd.ahah.commons.LinkWeight;

/**
 * @author alexnugent
 */
public class LinkWeightLabelProcedure implements TLongObjectProcedure<TIntFloatHashMap> {

  private final List<LinkWeight> linkWeights = new LinkedList<LinkWeight>();
  int label;
  String labelString;

  public LinkWeightLabelProcedure(int label, String labelString) {

    this.labelString = labelString;
    this.label = label;
  }

  @Override
  public boolean execute(long spike, TIntFloatHashMap submap) {

    if (submap.contains(label)) {
      LinkWeight linkWeight = new LinkWeight(spike, label, submap.get(label));
      linkWeight.setLabelString(labelString);
      linkWeights.add(linkWeight);
    }

    return true;
  }

  public List<LinkWeight> getLinkWeights() {

    return linkWeights;
  }

}
