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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;

/**
 * @author alexnugent
 */
@Ignore
public class JavaMapTest extends MapTest {

  private final Map<Long, Map<Integer, Float>> map = new HashMap<Long, Map<Integer, Float>>();

  public static void main(String[] args) {

    JavaMapTest javaMapTest = new JavaMapTest();
    System.out.println(Arrays.toString(javaMapTest.test()));
  }

  @Override
  void accumulate(long[] spikes) {

    Map<Integer, Float> activations = new HashMap<Integer, Float>();
    for (int i = 0; i < spikes.length; i++) {
      Map<Integer, Float> labelMap = map.get(spikes[i]);
      for (Map.Entry<Integer, Float> entry : labelMap.entrySet()) {
        Float y = activations.get(entry.getKey());
        if (y == null) {
          y = 0f;
        }
        y += entry.getValue();
        activations.put(entry.getKey(), y);
      }
    }
  }

  @Override
  void put(long spikeId, int labelId, float w) {

    Map<Integer, Float> subMap = map.get(spikeId);
    if (subMap == null) {
      subMap = new HashMap<Integer, Float>();
      map.put(spikeId, subMap);
    }
    subMap.put(labelId, w);
  }
}
