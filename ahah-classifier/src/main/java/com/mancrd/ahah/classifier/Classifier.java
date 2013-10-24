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
package com.mancrd.ahah.classifier;

import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.mancrd.ahah.commons.LinkWeight;
import com.mancrd.ahah.commons.utils.FileUtils;

/**
 * This class learns to associate input spikes with input labels. It is capable of supervised and semi-supervised operation.
 * 
 * @author alexnugent
 */
public class Classifier {

  private final static float STARTING_WEIGHT_MAGNITUDE = 1E-9f; // so small as to have no effect.

  private final TLongObjectHashMap<TIntFloatHashMap> weightMap; // input line --> (Label --> Weight)
  private final TIntObjectHashMap<String> labelReverseMap;
  private final TObjectIntHashMap<String> labelForwardMap;

  private final String classifierDBDir;
  public final static String LINK_SER_NAME = "link.ser";
  public final static String FORWARD_SER_NAME = "forward.ser";
  public final static String REVERSE_SER_NAME = "reverse.ser";
  public final static String COUNTS_SER_NAME = "counts.ser";

  private float learningRate = .1f;
  private boolean unsupervisedEnabled = false;
  private double unsupervisedConfidenceThreshold = 1;

  private final double bias = 0;

  Random random = new Random();

  int numLabelsProcessed = 0;
  int numSpikesProcessed = 0;
  int numUpdates = 0;
  long totalClassificationTimeInNanoSeconds = 0L;

  /**
   * In-memory-only Classifier
   * <p>
   * Constructor
   */
  public Classifier() {

    weightMap = new TLongObjectHashMap<TIntFloatHashMap>();
    labelReverseMap = new TIntObjectHashMap<String>();
    labelForwardMap = new TObjectIntHashMap<String>();
    // spikeCounts = new TLongIntHashMap();

    classifierDBDir = null;
  }

  /**
   * Constructor
   * 
   * @param classifierDBDir
   * @param classifierName
   */
  public Classifier(String classifierDBDir) {

    weightMap = new TLongObjectHashMap<TIntFloatHashMap>();
    labelReverseMap = new TIntObjectHashMap<String>();
    labelForwardMap = new TObjectIntHashMap<String>();

    this.classifierDBDir = classifierDBDir;

    // load maps from File DB
    boolean dirExists = FileUtils.fileExists(classifierDBDir);
    if (dirExists) {
      deserializeClassifier();
    }

  }

  /**
   * Convenience method. Calls update(String[] labels, List<long[]> spikes), after wrapping long[].
   * 
   * @param labels
   * @param spikes
   * @return
   */
  public ClassifierOutput update(String[] labels, long[] spikes) {

    List<long[]> spikeList = new LinkedList<long[]>();
    spikeList.add(spikes);
    return update(labels, spikeList);
  }

  /**
   * Given a set of trueLabels and an input pattern (spikes), it returns the labels based on a classification of the spikes before learning. The
   * classifier then learns.
   * 
   * @param labels - The known true labels associated with the input Spikes.
   * @param spikes - a List of long[]s identifying each spike in the spike pattern.
   * @return - labels as Strings.
   */
  public ClassifierOutput update(String[] labels, List<long[]> spikes) {

    numUpdates++;
    if (labels != null) {
      numLabelsProcessed += labels.length;
    }
    long classifiyStartTime = System.nanoTime();

    int[] trueLabels = getLabelsAsInts(labels);
    ClassifierOutput classifierOutput = new ClassifierOutput();
    int totalSpikes = 0;
    for (int i = 0; i < spikes.size(); i++) {
      numSpikesProcessed += spikes.get(i).length;
      for (int j = 0; j < spikes.get(i).length; j++) {
        addToActivation(spikes.get(i)[j], trueLabels, classifierOutput);
      }
      totalSpikes += spikes.get(i).length;
    }

    if (trueLabels != null && trueLabels.length > 0) { // learn labels

      // use trueLabels as true-positives
      TIntSet positiveLabels = new TIntHashSet();
      positiveLabels.addAll(trueLabels);

      // use mistakes as true-negatives
      List<LabelOutput> labelOutputs = classifierOutput.getSortedLabelOutputs(0.0);
      TIntSet negativeLabels = new TIntHashSet();

      for (LabelOutput labelOutput : labelOutputs) {
        if (!positiveLabels.contains(labelOutput.getLabel())) {
          negativeLabels.add(labelOutput.getLabel());
        }
      }

      // learn
      learn(classifierOutput, positiveLabels, negativeLabels, spikes, totalSpikes);
    }
    else if (unsupervisedEnabled) { // unsupervised adaptation
      learn(classifierOutput, null, null, spikes, totalSpikes);
    }

    classifierOutput.translateLabels(labelReverseMap);

    totalClassificationTimeInNanoSeconds += System.nanoTime() - classifiyStartTime;

    return classifierOutput;
  }

  private int[] getLabelsAsInts(String[] labels) {

    if (labels == null) {
      return null;
    }

    int[] l = new int[labels.length];
    for (int i = 0; i < l.length; i++) {
      l[i] = getLabelInt(labels[i]);
    }
    return l;
  }

  private int getLabelInt(String label) {

    if (labelForwardMap.contains(label)) {
      return labelForwardMap.get(label);
    }
    else {
      int i = labelForwardMap.size();
      labelForwardMap.put(label, i);
      labelReverseMap.put(i, label);
      return i;
    }
  }

  private void addToActivation(long spike, int[] trueLabels, ClassifierOutput classifierOutput) {

    // 1. get the activations (Map<String, Double>) that have been activated in the past when this inputLine was active.

    TIntFloatHashMap subMap = weightMap.get(spike);

    if (subMap == null) {// create sub map if
      subMap = new TIntFloatHashMap();
      weightMap.put(spike, subMap);
    }
    // add new links to true labels if they do already not exist.
    if (trueLabels != null) {
      for (int label : trueLabels) {
        if (!subMap.containsKey(label)) {
          getWeight(spike, label); // a call to this method will generate the link if it does not exist.
        }
      }
    }
    // update activations for all elements of submap
    subMap.forEachEntry(classifierOutput);

  }

  private void learn(ClassifierOutput classifierOutput, TIntSet positiveLabels, TIntSet negativeLabels, List<long[]> spikes, int totalSpikes) {

    // AHaH rule
    float lRate = learningRate / totalSpikes; // dynamic learning rate avoids having to parameter tweak. TotalSpikes should be constant anyway, so this is really a constant.
    for (LabelOutput labelOutput : classifierOutput.getLabelOutputs()) {

      float dW = 0.0f;

      if (positiveLabels != null && positiveLabels.contains(labelOutput.getLabel())) { // state (y) should be positive even if its not
        dW = lRate * (1 - labelOutput.getConfidence());
      }
      else if (negativeLabels != null && negativeLabels.contains(labelOutput.getLabel())) { // state (y) should be negative even if its not
        dW = lRate * (-1 - labelOutput.getConfidence());
      }
      else if (Math.abs(labelOutput.getConfidence()) > unsupervisedConfidenceThreshold) {
        dW = lRate * (Math.signum(labelOutput.getConfidence()) - labelOutput.getConfidence());

      }

      if (dW != 0.0f) {
        if (dW > lRate) {
          dW = lRate;
        }
        else if (dW < -lRate) {
          dW = -lRate;
        }
        updateWeights(spikes, labelOutput.getLabel(), dW);
      }

    }

  }

  private void updateWeights(List<long[]> spikes, int label, float dW) {

    for (int i = 0; i < spikes.size(); i++) {
      for (int j = 0; j < spikes.get(i).length; j++) {
        weightMap.get(spikes.get(i)[j]).put(label, getWeight(spikes.get(i)[j], label) + dW);
      }
    }
  }

  /**
   * gets the weight and creates a new random weight if it does not exist.
   * 
   * @param spike
   * @param label
   * @return
   */
  private float getWeight(long spike, int label) {

    if (!weightMap.contains(spike)) {
      TIntFloatHashMap z = new TIntFloatHashMap();
      float w = 2f * STARTING_WEIGHT_MAGNITUDE * (random.nextFloat() - .5f);
      z.put(label, w);
      weightMap.put(spike, z);
      return w;
    }
    else {
      TIntFloatHashMap z = weightMap.get(spike);
      if (!z.contains(label)) {
        float w = 2f * STARTING_WEIGHT_MAGNITUDE * (random.nextFloat() - .5f);
        z.put(label, w);
        return w;
      }
      else {
        return z.get(label);
      }
    }
  }

  public long deserializeClassifier() {

    long startTime = System.nanoTime();
    FileInputStream fileInputStream = null;
    ObjectInputStream objectInputStream = null;

    // load maps from file
    try {

      fileInputStream = new FileInputStream(classifierDBDir + File.separatorChar + LINK_SER_NAME);
      objectInputStream = new ObjectInputStream(fileInputStream);
      weightMap.readExternal(objectInputStream);
      objectInputStream.close();
      fileInputStream.close();

      fileInputStream = new FileInputStream(classifierDBDir + File.separatorChar + FORWARD_SER_NAME);
      objectInputStream = new ObjectInputStream(fileInputStream);
      labelForwardMap.readExternal(objectInputStream);
      objectInputStream.close();
      fileInputStream.close();

      fileInputStream = new FileInputStream(classifierDBDir + File.separatorChar + REVERSE_SER_NAME);
      objectInputStream = new ObjectInputStream(fileInputStream);
      labelReverseMap.readExternal(objectInputStream);
      objectInputStream.close();
      fileInputStream.close();

    } catch (IOException e) {
      try {
        if (fileInputStream != null) {
          fileInputStream.close();
        }
      } catch (IOException ex) {
        e.printStackTrace();
      }
      try {
        if (objectInputStream != null) {
          objectInputStream.close();
        }
      } catch (IOException ex) {
        e.printStackTrace();
      }
      throw new ClassifierException("Problem deserializing classifier maps with given classifierDBDir: " + classifierDBDir, e);
    } catch (ClassNotFoundException e) {
      try {
        if (fileInputStream != null) {
          fileInputStream.close();
        }
      } catch (IOException ex) {
        e.printStackTrace();
      }
      try {
        if (objectInputStream != null) {
          objectInputStream.close();
        }
      } catch (IOException ex) {
        e.printStackTrace();
      }
      throw new ClassifierException("Problem deserializing classifier maps with given classifierDBDir: " + classifierDBDir, e);
    }
    long writeTime = (System.nanoTime() - startTime) / 1000000000;

    return writeTime;
  }

  public long serializeClassifier() {

    if (classifierDBDir == null) {
      throw new ClassifierException("This classifier instant is not meant to be serializable.");
    }

    long startTime = System.nanoTime();
    FileOutputStream fileOutputStream = null;
    ObjectOutputStream objectOutputStream = null;
    try {

      File file = new File(classifierDBDir);

      if (!file.exists()) {
        file.mkdirs();
      }

      // Path dir = Paths.get(classifierDBDir);
      // boolean dirExists = Files.exists(dir, LinkOption.NOFOLLOW_LINKS);
      //
      // if (!dirExists) {
      // // Files.createDirectory(dir);
      // Files.createDirectories(dir);
      // }

      // fileOutputStream = new FileOutputStream(dir.resolve(LINK_SER_NAME).toString());
      fileOutputStream = new FileOutputStream(new File(classifierDBDir + File.separatorChar + LINK_SER_NAME));
      objectOutputStream = new ObjectOutputStream(fileOutputStream);
      weightMap.writeExternal(objectOutputStream);
      objectOutputStream.close();
      fileOutputStream.close();

      // fileOutputStream = new FileOutputStream(dir.resolve(FORWARD_SER_NAME).toString());
      fileOutputStream = new FileOutputStream(new File(classifierDBDir + File.separatorChar + FORWARD_SER_NAME));
      objectOutputStream = new ObjectOutputStream(fileOutputStream);
      labelForwardMap.writeExternal(objectOutputStream);
      objectOutputStream.close();
      fileOutputStream.close();

      // fileOutputStream = new FileOutputStream(dir.resolve(REVERSE_SER_NAME).toString());
      fileOutputStream = new FileOutputStream(new File(classifierDBDir + File.separatorChar + REVERSE_SER_NAME));
      objectOutputStream = new ObjectOutputStream(fileOutputStream);
      labelReverseMap.writeExternal(objectOutputStream);
      objectOutputStream.close();
      fileOutputStream.close();

    } catch (IOException e) {
      e.printStackTrace();
      try {
        if (fileOutputStream != null) {
          fileOutputStream.close();
        }
      } catch (IOException ex) {
        e.printStackTrace();
      }
      try {
        if (objectOutputStream != null) {
          objectOutputStream.close();
        }
      } catch (IOException ex) {
        e.printStackTrace();
      }

      throw new ClassifierException("Problem serializing classifier");
    }

    long writeTime = (System.nanoTime() - startTime) / 1000000000;

    return writeTime;

  }

  public float getLearningRate() {

    return learningRate;
  }

  public void setLearningRate(float learningRate) {

    this.learningRate = learningRate;
  }

  public void setUnsupervisedEnabled(boolean unsupervisedEnabled) {

    this.unsupervisedEnabled = unsupervisedEnabled;
  }

  public int getNumLinks() {

    LinkMapCountProcedure linkMapCountProcedure = new LinkMapCountProcedure();
    weightMap.forEachValue(linkMapCountProcedure);
    return linkMapCountProcedure.getNumLinks();

  }

  public void setUnsupervisedConfidenceThreshold(double unsupervisedConfidenceThreshold) {

    this.unsupervisedConfidenceThreshold = unsupervisedConfidenceThreshold;
  }

  public long getTotalClassificationTimeInNanoSeconds() {

    return totalClassificationTimeInNanoSeconds;
  }

  public int getNumSpikesProcessed() {

    return numSpikesProcessed;
  }

  public int getNumUpdates() {

    return numUpdates;
  }

  public int getNumLabelsProcessed() {

    return numLabelsProcessed;
  }

  public void deleteLabel(String label) {

    DeleteLabelProcedure deleteLabelProcedure = new DeleteLabelProcedure(labelForwardMap.get(label));
    weightMap.forEachEntry(deleteLabelProcedure);
  }

  public List<LinkWeight> getSortedLinkWeights() {

    LinkWeightProcedure linkWeightProcedure = new LinkWeightProcedure();
    weightMap.forEachEntry(linkWeightProcedure);
    return linkWeightProcedure.getSortedLinkWeights();
  }

  public List<LinkWeight> getLinkWeightsForLabel(String label) {

    if (!labelForwardMap.contains(label)) {
      return null;
    }

    LinkWeightLabelProcedure linkWeightLabelProcedure = new LinkWeightLabelProcedure(labelForwardMap.get(label), label);
    weightMap.forEachEntry(linkWeightLabelProcedure);
    return linkWeightLabelProcedure.getLinkWeights();
  }

  public void setLinkWeightLabelString(List<LinkWeight> linkWeights) {

    for (LinkWeight linkWeight : linkWeights) {
      linkWeight.setLabelString(labelReverseMap.get(linkWeight.getLabel()));
    }
  }

  public int getNumUniqueLabels() {

    return labelForwardMap.size();
  }

}
