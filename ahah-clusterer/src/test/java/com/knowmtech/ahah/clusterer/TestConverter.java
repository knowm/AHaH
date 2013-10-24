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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.mancrd.ahah.clusterer.Converter;

/**
 * @author timmolter
 */
public class TestConverter {

  @Test
  public void testGetSignature() {

    Converter converter = new Converter();
    double[] nodeActivations = new double[] { 1, 1, 1, 1 };
    int signature = converter.getSignature(nodeActivations);
    System.out.println("signature: " + signature);
    assertThat(signature, is(15));

    nodeActivations = new double[] { 0, 0, 0, 0, 0 };
    signature = converter.getSignature(nodeActivations);
    System.out.println("signature: " + signature);
    assertThat(signature, is(0));

    nodeActivations = new double[] { 0, -.99, -.08, .44, .88 };
    signature = converter.getSignature(nodeActivations);
    System.out.println("signature: " + signature);
    assertThat(signature, is(3));

  }

  // @Test
  // public void testGetAhahNodeActivations() {
  //
  // Converter converter = new Converter();
  // double[] ahahActivations = converter.getAhahNodeActivations(15, 4);
  // System.out.println("ahahActivations: " + Arrays.toString(ahahActivations));
  // assertTrue(Arrays.equals(ahahActivations, new double[] { 1, 1, 1, 1 }));
  //
  // ahahActivations = converter.getAhahNodeActivations(0, 5);
  // System.out.println("ahahActivations: " + Arrays.toString(ahahActivations));
  // assertTrue(Arrays.equals(ahahActivations, new double[] { -1.0, -1.0, -1.0, -1.0, -1.0 }));
  //
  // ahahActivations = converter.getAhahNodeActivations(3, 5);
  // System.out.println("ahahActivations: " + Arrays.toString(ahahActivations));
  // assertTrue(Arrays.equals(ahahActivations, new double[] { -1.0, -1.0, -1.0, 1.0, 1.0 }));
  // }
}
