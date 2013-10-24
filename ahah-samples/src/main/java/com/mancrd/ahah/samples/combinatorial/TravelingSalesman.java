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
package com.mancrd.ahah.samples.combinatorial;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.mancrd.ahah.combinatorial.BinarySetConverter;
import com.mancrd.ahah.combinatorial.Valuator;

/**
 * @author alexnugent
 */
public class TravelingSalesman implements Valuator {

  private List<City> cities;

  /**
   * Constructor
   * 
   * @param numCities
   */
  public TravelingSalesman(int numCities) {

    this.cities = new LinkedList<City>();
    Random rand = new Random();
    for (int i = 0; i < numCities; i++) {
      cities.add(new City(rand.nextGaussian(), rand.nextGaussian(), "" + i));
    }
  }

  @Override
  public int getMaxBitLength() {

    return BinarySetConverter.getNumEncodingBits(cities.size());
  }

  @Override
  public float getConfigValue(boolean[] path) {

    int[] cityPath = BinarySetConverter.getSet(cities.size(), path);
    float d = 0;
    for (int i = 0; i < cityPath.length - 1; i++) {
      City a = cities.get(cityPath[i]);
      City b = cities.get(cityPath[i + 1]);
      d += a.distanceTo(b);
    }

    return d;
  }

}

class City {

  private final double xPosition;
  private final double yPosition;
  private final String id;

  /**
   * Constructor
   * 
   * @param xPosition
   * @param yPosition
   * @param id
   */
  public City(double xPosition, double yPosition, String id) {

    this.xPosition = xPosition;
    this.yPosition = yPosition;
    this.id = id;
  }

  public double getXPosition() {

    return xPosition;
  }

  public double getYPosition() {

    return yPosition;
  }

  public double distanceTo(City otherCity) {

    return Math.sqrt(Math.pow(xPosition - otherCity.getXPosition(), 2) + Math.pow(yPosition - otherCity.yPosition, 2));
  }

  @Override
  public String toString() {

    return id;
  }
}
