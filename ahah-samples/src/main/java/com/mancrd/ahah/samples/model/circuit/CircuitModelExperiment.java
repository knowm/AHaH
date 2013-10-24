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
package com.mancrd.ahah.samples.model.circuit;

import java.util.HashSet;
import java.util.Set;

import com.mancrd.ahah.model.circuit.AHaH21Circuit;
import com.mancrd.ahah.samples.model.ModelExperiment;

/**
 * @author timmolter
 */
public class CircuitModelExperiment extends ModelExperiment {

  public int testSpikeLogicState(AHaH21Circuit ahahNode) {

    Set<Integer> spikes = new HashSet<Integer>();

    // binary logic--->spike logic
    // 00--->1010

    spikes.add(0);
    spikes.add(2);
    double y00 = ahahNode.update(spikes, 0);
    spikes.clear();

    // 01-->1001
    spikes.add(0);
    spikes.add(3);
    double y01 = ahahNode.update(spikes, 0);
    spikes.clear();

    // 10-->0110
    spikes.add(1);
    spikes.add(2);
    double y10 = ahahNode.update(spikes, 0);
    spikes.clear();

    // 11-->0101
    spikes.add(1);
    spikes.add(3);
    double y11 = ahahNode.update(spikes, 0);

    StringBuffer sb = new StringBuffer();
    sb.append(H(y00));
    sb.append(H(y01));
    sb.append(H(y10));
    sb.append(H(y11));

    int logicFunction = Integer.parseInt(sb.toString(), 2);

    return logicFunction;

  }

}
