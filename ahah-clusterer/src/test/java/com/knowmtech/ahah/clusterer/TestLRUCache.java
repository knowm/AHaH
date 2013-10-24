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
package com.knowmtech.ahah.clusterer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import com.mancrd.ahah.commons.LRUCache;

/**
 * Test class for LRUCache
 * 
 * @author timmolter
 */
public class TestLRUCache {

  private String[] getSampleKeys() {

    return new String[] { "A", "B", "C", "D", "E" };
  }

  /**
   * tests basic functionality
   */
  @Test
  public void testBasicFunction() {

    String[] keys = getSampleKeys();
    assertThat(keys.length, is(equalTo(5)));
    Iterator<String> keyItr = null;
    Iterator<Integer> labelItr = null;

    // empty map
    LRUCache lruCache = new LRUCache(2);
    assertThat(lruCache.size(), is(equalTo(0)));
    assertFalse(lruCache.isFull());
    assertFalse(lruCache.isReassign());
    assertThat(lruCache.maxSize(), is(equalTo(2)));
    System.out.println("initial: " + lruCache.getAll().toString());

    // map with one value
    Integer label = lruCache.put(keys[0]);
    System.out.println(keys[0] + " added: " + lruCache.getAll().toString());
    assertThat(label, is(equalTo(new Integer(0))));
    assertThat(lruCache.size(), is(equalTo(new Integer(1))));
    assertFalse(lruCache.isFull());
    assertFalse(lruCache.isReassign());
    assertThat(lruCache.maxSize(), is(equalTo(new Integer(2))));

    // map with two values, full
    label = lruCache.put(keys[1]);
    System.out.println(keys[1] + " added: " + lruCache.getAll().toString());
    assertThat(label, is(equalTo(new Integer(1))));
    assertThat(lruCache.size(), is(equalTo(new Integer(2))));
    assertTrue(lruCache.isFull());
    assertFalse(lruCache.isReassign());
    assertThat(lruCache.maxSize(), is(equalTo(new Integer(2))));

    keyItr = lruCache.getMap().keySet().iterator();
    assertThat(keyItr.next(), is(equalTo(keys[0])));
    assertThat(keyItr.next(), is(equalTo(keys[1])));

    labelItr = lruCache.getMap().values().iterator();
    assertThat(labelItr.next(), is(equalTo(new Integer(0))));
    assertThat(labelItr.next(), is(equalTo(new Integer(1))));

    // map with new key, overflow occurs
    label = lruCache.put(keys[2]);
    System.out.println(keys[2] + " added: " + lruCache.getAll().toString());

    assertThat(label, is(equalTo(new Integer(0))));
    assertThat(lruCache.size(), is(equalTo(new Integer(2))));
    assertTrue(lruCache.isFull());
    assertTrue(lruCache.isReassign());
    assertThat(lruCache.maxSize(), is(equalTo(new Integer(2))));

    keyItr = lruCache.getMap().keySet().iterator();
    assertThat(keyItr.next(), is(equalTo(keys[1])));
    assertThat(keyItr.next(), is(equalTo(keys[2])));

    labelItr = lruCache.getMap().values().iterator();
    assertThat(labelItr.next(), is(equalTo(new Integer(1))));
    assertThat(labelItr.next(), is(equalTo(new Integer(0))));

    // map with same key, overflow occurs
    label = lruCache.put(keys[1]);
    System.out.println(keys[1] + " added: " + lruCache.getAll().toString());

    assertThat(label, is(equalTo(new Integer(1))));
    assertThat(lruCache.size(), is(equalTo(new Integer(2))));
    assertTrue(lruCache.isFull());
    assertTrue(lruCache.isReassign());
    assertThat(lruCache.maxSize(), is(equalTo(new Integer(2))));

    keyItr = lruCache.getMap().keySet().iterator();
    assertThat(keyItr.next(), is(equalTo(keys[2])));
    assertThat(keyItr.next(), is(equalTo(keys[1])));

    labelItr = lruCache.getMap().values().iterator();
    assertThat(labelItr.next(), is(equalTo(new Integer(0))));
    assertThat(labelItr.next(), is(equalTo(new Integer(1))));

    // map with new key, overflow occurs
    label = lruCache.put(keys[3]);
    System.out.println(keys[3] + " added: " + lruCache.getAll().toString());

    assertThat(label, is(equalTo(new Integer(0))));
    assertThat(lruCache.size(), is(equalTo(new Integer(2))));
    assertTrue(lruCache.isFull());
    assertThat(lruCache.maxSize(), is(equalTo(new Integer(2))));

    keyItr = lruCache.getMap().keySet().iterator();
    assertThat(keyItr.next(), is(equalTo(keys[1])));
    assertThat(keyItr.next(), is(equalTo(keys[3])));

    labelItr = lruCache.getMap().values().iterator();
    assertThat(labelItr.next(), is(equalTo(new Integer(1))));
    assertThat(labelItr.next(), is(equalTo(new Integer(0))));
  }

  @Test
  public void testReset() {

    String[] keys = getSampleKeys();

    // empty map
    LRUCache lruCache = new LRUCache(2);
    assertThat(lruCache.size(), is(equalTo(0)));
    assertFalse(lruCache.isFull());
    assertThat(lruCache.maxSize(), is(equalTo(2)));

    // add one value
    lruCache.put(keys[0]);
    assertThat(lruCache.size(), is(equalTo(1)));
    assertFalse(lruCache.isFull());

    // add another value
    lruCache.put(keys[1]);
    assertThat(lruCache.size(), is(equalTo(2)));
    assertTrue(lruCache.isFull());

    // add one value, overflowing
    lruCache.put(keys[2]);
    assertThat(lruCache.size(), is(equalTo(2)));
    assertTrue(lruCache.isFull());

    lruCache.reset();
    assertThat(lruCache.size(), is(equalTo(0)));
    assertFalse(lruCache.isFull());
    assertThat(lruCache.maxSize(), is(equalTo(2)));
  }

  @Test
  public void testReverseLookup() {

    String[] keys = getSampleKeys();

    // empty map
    LRUCache lruCache = new LRUCache(2);
    assertThat(lruCache.size(), is(equalTo(0)));
    assertFalse(lruCache.isFull());
    assertThat(lruCache.maxSize(), is(equalTo(2)));

    // add one value
    Integer label = lruCache.put(keys[0]);
    assertThat(label, is(equalTo(new Integer(0))));
    assertThat(lruCache.reverseLookup(label), is(equalTo(keys[0])));

    // add another value, cache full
    label = lruCache.put(keys[1]);
    assertThat(label, is(equalTo(new Integer(1))));
    assertThat(lruCache.reverseLookup(label), is(equalTo(keys[1])));

    // add another value, cache overflow
    label = lruCache.put(keys[2]);
    assertThat(label, is(equalTo(new Integer(0))));
    assertThat(lruCache.reverseLookup(label), is(equalTo(keys[2])));

  }

}
