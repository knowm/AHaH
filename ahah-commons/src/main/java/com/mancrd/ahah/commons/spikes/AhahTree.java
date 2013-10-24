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
package com.mancrd.ahah.commons.spikes;

/**
 * This class takes a spike code in an arbitrary dimension and converts it to a spike code in the given dimension R=2^depth.
 * 
 * @author alexnugent
 */
public class AhahTree {

  private AhahTree zeroBranch;
  private AhahTree oneBranch;
  private final TreeNode node;

  private static int depth = 8;

  /**
   * Constructor
   * 
   * @param depth
   */
  public AhahTree(int depth) {

    AhahTree.depth = depth;
    node = new TreeNode();

  }

  private AhahTree() {

    node = new TreeNode();

  }

  public long encode(long[] spikes) {

    return update(spikes, new StringBuffer());
  }

  public long update(long[] spikes, StringBuffer path) {

    int z = node.update(spikes);

    if (z == 1) { // take one path
      path.append("1");
      if (path.length() == depth) {
        return path.toString().hashCode();
      }
      else {

        if (oneBranch == null) {
          oneBranch = new AhahTree();
        }

        return oneBranch.update(spikes, path);
      }
    }
    else {// take zero path
      path.append("0");
      if (path.length() == depth) {
        return path.toString().hashCode();
      }
      else {
        if (zeroBranch == null) {
          zeroBranch = new AhahTree();
        }
        return zeroBranch.update(spikes, path);
      }
    }

  }

}
