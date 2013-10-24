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
package com.knowmtech.ahah.classifier;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

import com.mancrd.ahah.classifier.Classifier;
import com.mancrd.ahah.classifier.ClassifierOutput;

/**
 * @author timmolter
 */
@Ignore
public class TestClassifierSerialization {

  public static void main(String[] args) {

    // create a classifier
    Classifier classifier = new Classifier("TestClassifierSerialization");

    // put some data in it
    List<long[]> spikes = new ArrayList<long[]>();
    spikes.add(new long[] { 8765865L, 65434349L });
    ClassifierOutput classifierOutput = classifier.update(new String[] { "hi", "bye" }, spikes);
    System.out.println(classifierOutput.getTotalConfidence(0));

    // save the classifier's state
    long writeTime = classifier.serializeClassifier();
    System.out.println("writeTime= " + writeTime);

    // reopen the classifier
    classifier = new Classifier("TestClassifierSerialization");

    // test that the data is still in it
    classifierOutput = classifier.update(new String[] { "hi", "bye" }, spikes);
    System.out.println(classifierOutput.getTotalConfidence(0));
  }
}
