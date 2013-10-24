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
package com.knowmtech.ahah.classifier.map;

import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.procedure.TIntFloatProcedure;

import java.util.Arrays;

import org.junit.Ignore;

/**
 * @author alexnugent
 */
@Ignore
public class TroveMapTest extends MapTest {

  private final TLongObjectHashMap<TIntFloatHashMap> map = new TLongObjectHashMap<TIntFloatHashMap>();

  public static void main(String[] args) {

    TroveMapTest troveMapTest = new TroveMapTest();
    System.out.println(Arrays.toString(troveMapTest.test()));

  }

  @Override
  void accumulate(long[] spikes) {

    TIntFloatHashMap activations = new TIntFloatHashMap();

    TIntFloatProcedure procedure = new TroveProcedure(activations);

    for (int i = 0; i < spikes.length; i++) {
      TIntFloatHashMap labelMap = map.get(spikes[i]);
      labelMap.forEachEntry(procedure);
    }
  }

  @Override
  void put(long spikeId, int labelId, float w) {

    TIntFloatHashMap subMap = map.get(spikeId);
    if (subMap == null) {
      subMap = new TIntFloatHashMap();
      map.put(spikeId, subMap);
    }
    subMap.put(labelId, w);
  }

  class TroveProcedure implements TIntFloatProcedure {

    TIntFloatHashMap activations;

    public TroveProcedure(TIntFloatHashMap activations) {

      this.activations = activations;
    }

    @Override
    public boolean execute(int key, float value) {

      float y = activations.get(key);
      y += value;
      activations.put(key, y);
      return true;
    }

  }

}
