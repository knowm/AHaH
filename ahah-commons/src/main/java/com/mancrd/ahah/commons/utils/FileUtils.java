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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author timmolter
 */
public final class FileUtils {

  private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

  /**
   * Constructor - Private constructor to prevent instantiation
   */
  private FileUtils() {

  }

  /**
   * Given a path to a File, return the content of the file as a String
   * 
   * @param file
   * @return
   */
  public static String readFileToString(String filePath) {

    String result = null;
    File file = new File(filePath);
    if (!file.exists()) {
      logger.error("SOURCE FILE (" + filePath + ") NOT FOUND!!!");
      return null;
    }
    BufferedReader reader = null;

    try {
      reader = new BufferedReader(new FileReader(file));
      result = readerToString(reader);
    } catch (FileNotFoundException e) {
      logger.error("ERROR IN READFILETOSTRING!!!", e);
    }

    // show file contents here
    return result;
  }

  /**
   * Given a File from the classpath, return the content of the file as a String
   * 
   * @param fileName
   * @return
   */
  public static String readFileFromClasspathToString(String fileName) {

    BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtils.class.getClassLoader().getResourceAsStream(fileName)));
    String result = readerToString(reader);

    // show file contents here
    return result;
  }

  private static String readerToString(BufferedReader reader) {

    StringBuffer sb = new StringBuffer();

    try {
      String text = null;

      // repeat until all lines are read
      while ((text = reader.readLine()) != null) {
        sb.append(text).append(System.getProperty("line.separator"));
      }
    } catch (FileNotFoundException e) {
      logger.error("ERROR IN READFILETOSTRING!!!", e);
    } catch (IOException e) {
      logger.error("ERROR IN READFILETOSTRING!!!", e);
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        logger.error("ERROR IN READFILETOSTRING!!!", e);
      }
    }

    // show file contents here
    return sb.toString();

  }

  public static Object deserialize(File file) {

    // Read from disk using FileInputStream
    Object obj = null;
    try {
      FileInputStream f_in = new FileInputStream(file.getAbsoluteFile());

      // Read object using ObjectInputStream
      ObjectInputStream obj_in = new ObjectInputStream(f_in);

      // Read an object
      obj = obj_in.readObject();

      return obj;
    } catch (Exception e) {
      logger.error("Error deserializing " + file.getAbsolutePath(), e);
    }

    return null;
  }

  public static void serialize(Object object, String path) {

    // Write to disk with FileOutputStream
    try {
      FileOutputStream f_out = new FileOutputStream(path);

      // Write object with ObjectOutputStream
      ObjectOutputStream obj_out = new ObjectOutputStream(f_out);

      // Write object out to disk
      obj_out.writeObject(object);
    } catch (Exception e) {
      logger.error("Error serializing object.", e);
    }
  }

  /**
   * This method returns the names of all the files found in the given directory
   * 
   * @param dirName - ex. "./images/colors/original/" *make sure you have the '/' on the end
   * @return String[] - an array of file names
   */
  public static String[] getAllFileNames(String dirName) {

    File dir = new File(dirName);

    File[] files = dir.listFiles(); // returns files and folders

    if (files != null) {
      List<String> fileNames = new ArrayList<String>();

      for (int i = 0; i < files.length; i++) {
        if (files[i].isFile()) {
          fileNames.add(files[i].getName());
        }
      }

      return fileNames.toArray(new String[fileNames.size()]);
    }
    else {
      logger.debug(dirName + " does not denote a valid directory!");
      return new String[0];
    }
  }

  /**
   * This method returns the Files found in the given directory
   * 
   * @param dirName - ex. "./images/colors/original/" *make sure you have the '/' on the end
   * @return File[] - an array of files
   */
  public static File[] getAllFiles(String dirName) {

    File dir = new File(dirName);

    File[] files = dir.listFiles(); // returns files and folders

    if (files != null) {
      List<File> filteredFiles = new ArrayList<File>();
      for (int i = 0; i < files.length; i++) {

        if (files[i].isFile()) {
          filteredFiles.add(files[i]);
        }
      }
      return filteredFiles.toArray(new File[filteredFiles.size()]);
    }
    else {
      logger.debug(dirName + " does not denote a valid directory!");
      return new File[0];
    }
  }

  /**
   * This method returns the names of all the files found in the given directory matching the given regular expression.
   * 
   * @param dirName - ex. "./images/colors/original/" *make sure you have the '/' on the end
   * @param regex - ex. ".*.png"
   * @return String[] - an array of file names
   */
  public static String[] getAllFileNames(String dirName, String regex) {

    String[] allFileNames = getAllFileNames(dirName);

    List<String> matchingFileNames = new ArrayList<String>();

    for (int i = 0; i < allFileNames.length; i++) {

      if (allFileNames[i].matches(regex)) {
        matchingFileNames.add(allFileNames[i]);
      }
    }

    return matchingFileNames.toArray(new String[matchingFileNames.size()]);

  }

  /**
   * This method returns the files found in the given directory matching the given regular expression.
   * 
   * @param dirName - ex. "./images/colors/original/" *make sure you have the '/' on the end
   * @param regex - ex. ".*.png"
   * @return File[] - an array of files
   */
  public static File[] getAllFiles(String dirName, String regex) {

    File[] allFiles = getAllFiles(dirName);

    List<File> matchingFiles = new ArrayList<File>();

    for (int i = 0; i < allFiles.length; i++) {

      if (allFiles[i].getName().matches(regex)) {
        matchingFiles.add(allFiles[i]);
      }
    }

    return matchingFiles.toArray(new File[matchingFiles.size()]);

  }

  /**
   * Copies src file to dst file. If the dst file does not exist, it is created.
   * 
   * @param srcPath
   * @param destPath
   * @throws IOException
   */
  public static boolean copy(String srcPath, String destPath) {

    boolean success = true;

    InputStream in = null;
    OutputStream out = null;

    try {

      File srcFile = new File(srcPath);

      if (!srcFile.exists()) {
        logger.error("SOURCE FILE NOT FOUND!!!");
        return false;
      }

      mkDirIfNotExists(destPath.substring(0, destPath.lastIndexOf(File.separatorChar)));

      File destFile = new File(destPath);

      in = new FileInputStream(srcFile);

      out = new FileOutputStream(destFile); // Transfer bytes from in to out

      byte[] buf = new byte[1024];

      int len;

      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }

    } catch (Exception e) {

      logger.error("ERROR COPYING FILE!!!", e);
      success = false;

    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          // eat it
        }
      }
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          // eat it
        }
      }
    }

    return success;

  }

  /**
   * @param fullyQualifiedFileName
   * @return
   */
  public static boolean deleteFile(String fullyQualifiedFileName) {

    File file = new File(fullyQualifiedFileName);

    boolean deleteSuccessful = false;
    if (file.exists() && file.canWrite() && !file.isDirectory()) {

      try {

        deleteSuccessful = file.delete();

      } catch (Exception e) {
        logger.error("ERROR DELETING FILE!!!", e);
      }
    }
    return deleteSuccessful;
  }

  /**
   * Checks if a file exists
   * 
   * @param pFilePath
   * @return
   */
  public static boolean fileExists(String pFilePath) {

    File file = new File(pFilePath);
    return file.exists();
  }

  /**
   * Makes a dir, if it doesn't already exist
   * 
   * @param pFilePath
   */
  public static void mkDirIfNotExists(String pFilePath) {

    File f = new File(pFilePath);
    if (!f.exists()) {
      f.mkdir();
    }
  }

}