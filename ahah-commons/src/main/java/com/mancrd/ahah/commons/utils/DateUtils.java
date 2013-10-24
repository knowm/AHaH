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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author timmolter
 */
public final class DateUtils {

  /**
   * Constructor - Private constructor to prevent instantiation
   */
  private DateUtils() {

  }

  /**
   * Given a Date String and a format String, returns a Date Object
   * 
   * @param dateString
   * @param formatString
   * @return
   * @throws ParseException
   */
  public static Date getDateFromString(String dateString, String formatString) throws ParseException {

    DateFormat sdf = new SimpleDateFormat(formatString);
    return sdf.parse(dateString);
  }

  /**
   * Add years to a date. Use - sign to subtract years
   * 
   * @param date
   * @param years
   * @return
   */
  public static Date addYears(Date date, int years) {

    Calendar lCalendar = Calendar.getInstance();
    lCalendar.setTime(date);
    lCalendar.add(Calendar.YEAR, years);
    return lCalendar.getTime();
  }

  /**
   * Add months to a date. Use - sign to subtract months
   * 
   * @param date
   * @param months
   * @return
   */
  public static Date addMonths(Date date, int months) {

    Calendar lCalendar = Calendar.getInstance();
    lCalendar.setTime(date);
    lCalendar.add(Calendar.MONTH, months);
    return lCalendar.getTime();
  }

  /**
   * Add days to a date. Use - sign to subtract days
   * 
   * @param date
   * @param days
   * @return
   */
  public static Date addDays(Date date, int days) {

    Calendar lCalendar = Calendar.getInstance();
    lCalendar.setTime(date);
    lCalendar.add(Calendar.DAY_OF_YEAR, days);
    return lCalendar.getTime();
  }

  /**
   * Add hours to a date. Use - sign to subtract hours
   * 
   * @param date
   * @param hours
   * @return
   */
  public static Date addHours(Date date, int hours) {

    Calendar lCalendar = Calendar.getInstance();
    lCalendar.setTime(date);
    lCalendar.add(Calendar.HOUR_OF_DAY, hours);
    return lCalendar.getTime();
  }

  /**
   * Add minutes to a date. Use - sign to subtract minutes
   * 
   * @param date
   * @param minutes
   * @return
   */
  public static Date addMinutes(Date date, int minutes) {

    Calendar lCalendar = Calendar.getInstance();
    lCalendar.setTime(date);
    lCalendar.add(Calendar.MINUTE, minutes);
    return lCalendar.getTime();
  }

  /**
   * Add seconds to a date. Use - sign to subtract seconds
   * 
   * @param date
   * @param seconds
   * @return
   */
  public static Date addSeconds(Date date, int seconds) {

    Calendar lCalendar = Calendar.getInstance();
    lCalendar.setTime(date);
    lCalendar.add(Calendar.SECOND, seconds);
    return lCalendar.getTime();
  }

  /**
   * Checks if the first date is near to the second date within the number of days specified
   * 
   * @param date1
   * @param date2
   * @param days
   * @return
   */
  public static boolean isNear(Date date1, Date date2, int days) {

    if (date1 == null || date2 == null) {
      return false;
    }
    Date vLowerDate = addDays(date2, -days);
    Date vUpperDate = addDays(date2, days);

    return date1.after(vLowerDate) && date1.before(vUpperDate);
  }

  /**
   * Removes the timestamp portion of a date
   * 
   * @param date
   * @return
   */
  public static Date removeTimestamp(Date date) {

    try {
      DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);
      String dateString = dateFormat.format(date);
      return dateFormat.parse(dateString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String getDateSQLString(Date date) {

    SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    return SQL_DATE_FORMAT.format(date);
  }

  public static String getDateTimeSQLString(Date date) {

    SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    return SQL_DATE_FORMAT.format(date);
  }

  /**
   * Check if the given date is today
   * 
   * @param date
   * @return
   */
  public static boolean isToday(Date date) {

    Date today = removeTimestamp(new Date());
    return today.equals(removeTimestamp(date));
  }

  /**
   * Check if two Date objects are the same date
   * 
   * @param date1
   * @param date2
   * @return
   */
  public static boolean isSameDate(Date date1, Date date2) {

    date1 = removeTimestamp(date1);
    date2 = removeTimestamp(date2);
    return date1.equals(date2);
  }

  /**
   * Check if given date is the first of the month
   * 
   * @param date
   * @return
   */
  public static boolean isFirstDayOfMonth(Date date) {

    Calendar lCalendar = Calendar.getInstance();
    lCalendar.setTime(date);

    if (lCalendar.get(Calendar.DAY_OF_MONTH) == 1) {
      return true;
    }
    else {
      return false;
    }
  }

  public static Date getLastDayOfPreviousMonth(Date date) {

    Calendar lCalendar = Calendar.getInstance();
    lCalendar.setTime(date);

    lCalendar.set(Calendar.DAY_OF_MONTH, 1);
    Date lTmpDate = lCalendar.getTime();

    return addDays(lTmpDate, -1);
  }

  public static Date getLastDayOfMonth(Date date) {

    date = addMonths(date, 1);
    return getLastDayOfPreviousMonth(date);
  }

  /**
   * Creates a current Date without timestamp
   * 
   * @return
   */
  public static Date createTruncatedDate() {

    return removeTimestamp(new Date());
  }

  public static long getDifferenceInDays(Date from, Date to) {

    double lDifference = to.getTime() - from.getTime();
    return Math.round(lDifference / (1000 * 60 * 60 * 24));
  }

  public static Date getNextBusinessDay(Date date) {

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

    if (dayOfWeek == Calendar.FRIDAY) {
      calendar.add(Calendar.DATE, 3);
    }
    else if (dayOfWeek == Calendar.SATURDAY) {
      calendar.add(Calendar.DATE, 2);
    }
    else {
      calendar.add(Calendar.DATE, 1);
    }

    Date nextBusinessDay = calendar.getTime();

    return nextBusinessDay;

  }

  public static boolean isSameDateTime(Date date1, Date date2) {

    int results = date1.compareTo(date2);

    if (results > 0) {
      return false;
    }
    else if (results < 0) {
      return false;
    }
    else {
      return true;
    }
  }

  public static int getTimezoneDifference(String timezone1, String timezone2) {

    // Timezone1
    Calendar lCalendar = new GregorianCalendar(TimeZone.getTimeZone(timezone1));
    lCalendar.setTimeInMillis(new Date().getTime());
    int Timezone1HourOfDay = lCalendar.get(Calendar.HOUR_OF_DAY);
    int Timezone1DayOfMonth = lCalendar.get(Calendar.DAY_OF_MONTH);

    // Timezone2
    lCalendar = new GregorianCalendar(TimeZone.getTimeZone(timezone2));
    lCalendar.setTimeInMillis(new Date().getTime());
    int TimezoneHourOfDay = lCalendar.get(Calendar.HOUR_OF_DAY);
    int TimezoneDayOfMonth = lCalendar.get(Calendar.DAY_OF_MONTH);

    int hourDifference = Timezone1HourOfDay - TimezoneHourOfDay;
    int dayDifference = Timezone1DayOfMonth - TimezoneDayOfMonth;
    if (dayDifference != 0) {
      hourDifference = hourDifference + 24;
    }
    return hourDifference;
  }

}
