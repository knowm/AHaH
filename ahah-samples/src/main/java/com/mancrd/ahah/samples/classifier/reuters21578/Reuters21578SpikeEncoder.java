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
package com.mancrd.ahah.samples.classifier.reuters21578;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import com.mancrd.ahah.commons.spikes.SpikeEncoder;
import com.xeiam.datasets.reuters21578.Reuters21578;

/**
 * Typical bag-of-words encoder
 * 
 * @author alexnugent
 */
public class Reuters21578SpikeEncoder extends SpikeEncoder<Reuters21578> {

  private static final String parseRegEx = " |,|\\.|:|'|\\(|\\)";
  private static Calendar cal = Calendar.getInstance();

  public Reuters21578SpikeEncoder() {

  }

  @Override
  public Set<String> getSpikes(Reuters21578 story) {

    Set<String> spikes = new HashSet<String>();

    // date
    cal.setTime(story.getDate());
    spikes.add("month=" + cal.get(Calendar.MONTH));
    spikes.add("day_of_week=" + cal.get(Calendar.DAY_OF_WEEK));
    spikes.add("year=" + cal.get(Calendar.YEAR));
    spikes.add("day_of_month=" + cal.get(Calendar.DAY_OF_MONTH));
    spikes.add("am_pm=" + cal.get(Calendar.AM_PM));
    spikes.add("hour_of_day=" + cal.get(Calendar.HOUR_OF_DAY));
    spikes.add("week_of_month=" + cal.get(Calendar.WEEK_OF_MONTH));
    spikes.add("week_of_year=" + cal.get(Calendar.WEEK_OF_YEAR));

    // places
    addToFeatures(story.getPlaces().split(","), "place=", spikes);
    // people
    addToFeatures(story.getPeople().split(","), "people=", spikes);
    // orgs
    addToFeatures(story.getOrgs().split(","), "orgs=", spikes);
    // exchanges
    addToFeatures(story.getExchanges().split(","), "exchanges=", spikes);
    // title
    addToFeatures(story.getTitle().split(" "), "titleword=", spikes);
    // dateline
    addToFeatures(story.getDateline().split(" "), "dateline=", spikes);
    // company
    addToFeatures(story.getCompanies().split(","), "company=", spikes);

    // story body
    String[] words = story.getBody().toLowerCase().split(parseRegEx);

    addToFeatures(words, "", spikes);

    // features.add(0);// bias is zero

    return spikes;

  }

  private void addToFeatures(String[] features, String concat, Set<String> spikes) {

    if (features.length == 0) {
      return;
    }
    for (int i = 0; i < features.length; i++) {
      spikes.add(concat + features[i].toLowerCase());
    }
  }

  @Override
  public short getUniquePositiveID() {

    // TODO Auto-generated method stub
    return 0;
  }

}
