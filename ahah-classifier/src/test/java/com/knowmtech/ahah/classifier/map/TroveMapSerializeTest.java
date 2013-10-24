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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.junit.Ignore;

/**
 * @author alexnugent
 */
@Ignore
public class TroveMapSerializeTest extends MapTest {

  private final TLongObjectHashMap<TIntFloatHashMap> map = new TLongObjectHashMap<TIntFloatHashMap>();

  public static void main(String[] args) throws IOException, ClassNotFoundException {

    TroveMapSerializeTest troveMapTest = new TroveMapSerializeTest();
    System.out.println(Arrays.toString(troveMapTest.test()));

    troveMapTest.write();

  }

  private void write() throws IOException, ClassNotFoundException {

    long startTime = System.nanoTime();

    FileOutputStream f = new FileOutputStream("serialize1.ser");
    ObjectOutputStream out = new ObjectOutputStream(f);
    map.writeExternal(out);
    long writeTime = (System.nanoTime() - startTime) / 1000000000;
    System.out.println("writeTime= " + writeTime);

    TLongObjectHashMap<TIntFloatHashMap> map2 = new TLongObjectHashMap<TIntFloatHashMap>();
    FileInputStream f2 = new FileInputStream("serialize1.ser");
    ObjectInputStream in = new ObjectInputStream(f2);
    map2.readExternal(in);

    long readTime = (System.nanoTime() - writeTime) / 1000000000;
    System.out.println("readTime= " + writeTime);

    // for (int i = 0; i < 100; i++) {
    // TIntFloatHashMap innerMap = map2.get(i);
    // System.out.println(innerMap.toString());
    // }
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
