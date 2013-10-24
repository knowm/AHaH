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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.mancrd.ahah.classifier.ClassificationEvaluator;

/**
 * @author timmolter
 */
public class TestClassificationEvaluator {

  @Test
  public void test0() {

    Set<String> allLabels = new HashSet<String>();
    allLabels.add("t");
    allLabels.add("f");

    ClassificationEvaluator evaluator = new ClassificationEvaluator(allLabels);

    // t, t
    Set<String> trueLabels = new HashSet<String>();
    trueLabels.add("t");
    List<String> labels = new ArrayList<String>();
    labels.add("t");
    evaluator.update(trueLabels, labels);

    assertThat(evaluator.getAccuracyMicroAve(), is(equalTo(1.0)));

    // t, t
    trueLabels = new HashSet<String>();
    trueLabels.add("t");
    labels = new ArrayList<String>();
    labels.add("t");
    evaluator.update(trueLabels, labels);

    assertThat(evaluator.getAccuracyMicroAve(), is(equalTo(1.0)));

    // f, f
    trueLabels = new HashSet<String>();
    trueLabels.add("f");
    labels = new ArrayList<String>();
    labels.add("f");
    evaluator.update(trueLabels, labels);

    assertThat(evaluator.getAccuracyMicroAve(), is(equalTo(1.0)));

    // t, f
    trueLabels = new HashSet<String>();
    trueLabels.add("t");
    labels = new ArrayList<String>();
    labels.add("f");
    evaluator.update(trueLabels, labels);
    assertThat(evaluator.getAccuracyMicroAve(), is(equalTo(.75)));

  }
}
