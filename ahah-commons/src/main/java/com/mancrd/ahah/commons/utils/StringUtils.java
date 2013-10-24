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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class performs various tasks on Strings
 * 
 * @author alexnugent
 */
public final class StringUtils {

  private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);

  private static DecimalFormat priceFormatter = new DecimalFormat("$###,###,##0.00");
  private static DecimalFormat percentFormat = new DecimalFormat("#.##%");
  private static SimpleDateFormat dateFormat_yyyyMMdd = new SimpleDateFormat("yyyy.MM.dd");
  private static SimpleDateFormat dateFormat_MySQL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * Constructor - Private constructor to prevent instantiation
   */
  private StringUtils() {

  }

  public static String formatPrice(double number) {

    return priceFormatter.format(number);
  }

  /**
   * Joins a collection of Objects separated by a specified separator
   * 
   * @param collection
   * @param separator
   * @return the joined String
   */
  public static String join(Collection<? extends Object> collection, String separator) {

    if (collection == null) {
      return null;
    }
    Iterator iterator = collection.iterator();
    // handle null, zero and one elements before building a buffer
    if (iterator == null) {
      return null;
    }
    if (!iterator.hasNext()) {
      return "";
    }
    Object first = iterator.next();
    if (!iterator.hasNext()) {
      return first == null ? "" : first.toString();
    }

    // two or more elements
    StringBuffer buf = new StringBuffer(256); // Java default is 16, probably too small
    if (first != null) {
      buf.append(first);
    }

    while (iterator.hasNext()) {
      if (separator != null) {
        buf.append(separator);
      }
      Object obj = iterator.next();
      if (obj != null) {
        buf.append(obj);
      }
    }
    return buf.toString();

  }

  /**
   * Adds whitespace to the end of given String
   * 
   * @param theString
   * @param finalLength
   * @return
   */
  public static String addWhiteSpaceToEnd(String theString, int finalLength) {

    int difference = finalLength - theString.length();
    StringBuffer buf = new StringBuffer();
    buf.append(theString);
    for (int i = 0; i < difference; i++) {
      buf.append(" ");
    }
    return buf.toString();
  }

  /**
   * Will convert a decimal to a percentage. For example: .755 --> "75.5%"
   * 
   * @param number the number in decimal form.
   * @return
   */
  public static String formatPercentage(double number) {

    return percentFormat.format(number);
  }

  /**
   * @param date
   * @return
   */
  public static String formatDate_yyyyMMdd(Date date) {

    return dateFormat_yyyyMMdd.format(date);
  }

  /**
   * @param date
   * @return
   */
  public static String formatDate_MySQL(Date date) {

    return dateFormat_MySQL.format(date);
  }

  /**
   * @param dateString
   * @return
   */
  public static Date parseDateString_MySQL(String dateString) {

    Date date = null;
    try {
      date = dateFormat_MySQL.parse(dateString);
    } catch (ParseException e) {
      logger.error("COULD NOT PARSE DATE STRING!!!");
    }
    return date;
  }

  /**
   * Get the MD5 hash of a String
   * 
   * @param input the String to hash
   * @return the hash of the given String
   */
  public static String getMD5(String input) {

    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance("MD5");
      messageDigest.reset();
      messageDigest.update(input.getBytes());
      byte[] digest = messageDigest.digest();
      BigInteger bigInt = new BigInteger(1, digest);
      String hashtext = bigInt.toString(16);
      // Now we need to zero pad it if you actually want the full 32 chars.
      while (hashtext.length() < 32) {
        hashtext = "0" + hashtext;
      }
      return hashtext;

    } catch (NoSuchAlgorithmException e) {
      logger.error("ERROR CREATING MD5!!!", e);
      return null;
    }
  }

}
