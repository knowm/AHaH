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
package com.mancrd.ahah.commons.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

/**
 * @author timmolter
 */
public class CSVUtils {

  /**
   * @param listOfLists
   * @param path2Dir
   * @param fileName
   */
  public static void writeCSVRows(List<List<? extends Object>> listOfLists, String path2Dir, String fileName) {

    File newFile = new File(path2Dir + fileName + ".csv");
    Writer out = null;
    try {

      out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), "UTF8"));
      for (int i = 0; i < listOfLists.size(); i++) {
        List<? extends Object> list = listOfLists.get(i);
        String csv = StringUtils.join(list, ",") + System.getProperty("line.separator");
        out.write(csv);
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException e) {
          // NOP
        }
      }
    }

  }

  public static double[][] read(String path) {

    List<double[]> lines = new LinkedList<double[]>();
    try {
      BufferedReader br = new BufferedReader(new FileReader(new File(path)));
      String line;
      while ((line = br.readLine()) != null) {

        if (!line.contains("#")) {
          String[] a = line.split(",");
          try {
            double[] n = new double[a.length];
            for (int i = 0; i < n.length; i++) {
              n[i] = Double.parseDouble(a[i]);
            }
            lines.add(n);
          } catch (Exception e) {
            System.out.println(e.toString());
          }

        }
      }
      br.close();

      double[][] output = new double[lines.size()][lines.get(0).length];
      for (int i = 0; i < output.length; i++) {
        output[i] = lines.get(i);
      }
      return output;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }
}
