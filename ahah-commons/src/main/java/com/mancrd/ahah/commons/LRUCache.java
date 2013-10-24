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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A specialized LRUCache where only keys are put into it. The values associated with the keys are managed internally.
 * 
 * @author timmolter
 */
public class LRUCache {

  private static final float HASH_TABLE_LOAD_FACTOR = 0.75f;

  /** the internal Map containing the key, value pairs */
  private LinkedHashMap<String, Integer> map;

  private int cacheSize;
  private boolean capacityReached = false;
  private String lastKey = null;
  private int nextLabel = 0;
  private boolean reassign = false;

  /**
   * Constructor
   * 
   * @param cacheSize the maximum number of entries that will be kept in this cache.
   */
  public LRUCache(int cacheSize) {

    if (cacheSize <= 1) {
      throw new IllegalArgumentException("It makes no sense to have a cache of size less than two!");
    }

    this.cacheSize = cacheSize;
    int hashTableCapacity = (int) Math.ceil(cacheSize / HASH_TABLE_LOAD_FACTOR) + 1;
    map = new LinkedHashMap<String, Integer>(hashTableCapacity, HASH_TABLE_LOAD_FACTOR, true) {

      /**
       * This method is invoked by put and putAll AFTER inserting a new entry into the map.
       * 
       * @param eldest - The least recently inserted entry in the map, or if this is an access-ordered map, the least recently accessed entry. This is
       *          the entry that will be removed it this method returns true. If the map was empty prior to the put or putAll invocation resulting in
       *          this invocation, this will be the entry that was just inserted; in other words, if the map contains a single entry, the eldest entry
       *          is also the newest.
       * @returns true if the eldest entry should be removed from the map; false if it should be retained.
       */
      @Override
      protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {

        if (size() > LRUCache.this.cacheSize) {
          LRUCache.this.capacityReached = true;
          Integer eldestLabel = eldest.getValue();
          map.put(LRUCache.this.lastKey, eldestLabel); // override last put with correct label
          LRUCache.this.reassign = true;
          return true; // tell it to remove the eldest
        }
        else {
          LRUCache.this.reassign = false;
          if (!LRUCache.this.capacityReached) {// if the max size has not been reached then increment the label
            LRUCache.this.nextLabel++;
          }
          return false;
        }
      }
    };
  }

  /**
   * Adds an entry to this cache. The new entry becomes the MRU (most recently used) entry. If an entry with the specified key already exists in the
   * cache, it is replaced by the new entry. If the cache is full, the LRU (least recently used) entry is removed from the cache.
   * 
   * @param signature - the key with which the specified value is to be associated. This is usually a String or an Integer
   */
  public Integer put(String signature) {

    this.lastKey = signature;

    Integer label = map.get(signature);
    if (label == null) { // no match, add. When added the LRUCache will overwrite the least recently used row, assigning the new id to the row id.
      addEntry(signature);
      label = map.get(signature);
    }
    return label;
  }

  private void addEntry(String key) {

    map.put(key, nextLabel);
  }

  /**
   * Returns whether or not the last insert was an overwrite or not.
   * 
   * @return
   */
  public boolean isReassign() {

    return reassign;
  }

  /**
   * resets everything to new state
   */
  public void reset() {

    map.clear();
    nextLabel = 0;
    reassign = false;
  }

  /**
   * Returns the number of used entries in the cache.
   * 
   * @return the number of entries currently in the cache.
   */
  public int size() {

    return map.size();
  }

  /**
   * check if the CAM is full
   */
  public boolean isFull() {

    return map.size() == this.cacheSize;
  }

  /**
   * check the max size of the CAM
   */
  public int maxSize() {

    return this.cacheSize;
  }

  /**
   * get the internal key/value (Signature/Lables) map of the CAM
   */
  public LinkedHashMap<String, Integer> getMap() {

    return this.map;
  }

  /**
   * Returns a <code>Collection</code> that contains a copy of all cache entries.
   * 
   * @return a <code>Collection</code> with a copy of the cache content.
   */
  public Collection<Map.Entry<String, Integer>> getAll() {

    return new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
  }

  /**
   * given a label, return the signature
   */
  public String reverseLookup(Integer label) {

    for (Entry<String, Integer> entry : map.entrySet()) {
      if (label.equals(entry.getValue())) {
        return entry.getKey();
      }
    }
    return null;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Integer> entry : map.entrySet()) {

      sb.append(entry.getKey());
      sb.append(" | ");
      sb.append(entry.getValue());
      sb.append(System.getProperty("line.separator"));
    }
    return sb.toString();
  }

}
