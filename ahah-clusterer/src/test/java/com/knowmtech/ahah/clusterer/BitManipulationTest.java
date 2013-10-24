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

import org.junit.Ignore;

/**
 * @author alexnugent
 */
@Ignore
public class BitManipulationTest {

  public static void main(String[] args) {

    int spike = 13348349;
    short id = 5667;

    long longSpike = spike;

    System.out.println("spike: " + Long.toString(longSpike, 2));
    System.out.println("id: " + Long.toString(id, 2));

    long shift = longSpike << 16;
    long composite = shift | id;
    System.out.println("shift: " + Long.toString(shift, 2));
    System.out.println("composite: " + Long.toString(composite, 2));

    long idMask = 65535;// 0b1111111111111111;

    long idReconstructed = composite & idMask;
    long spikeReconstructed = composite >> 16;

    System.out.println("reconstructedSpike: " + Long.toString(spikeReconstructed, 2));
    System.out.println("reconstructedSpike: " + spikeReconstructed);

    System.out.println("idReconstructed: " + Long.toString(idReconstructed, 2));
    System.out.println("idReconstructed: " + idReconstructed);

  }

}
