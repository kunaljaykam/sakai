/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.calendar.impl;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.sakaiproject.calendar.api.RecurrenceRule;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.api.TimeBreakdown;
import org.sakaiproject.time.api.TimeRange;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiproject.util.ResourceLoader;

/**
 * This is a common base for the daily, weekly, monthly, and yearly recurrence rules.
 */
@Slf4j
public abstract class RecurrenceRuleBase implements RecurrenceRule
{
	/** Every this many number of units: 1 would be daily/monthly/annually. */
	private int interval = 1;

	/** For this many occurrences - if 0, does not limit. */
	private int count = 0;

	/** No time ranges past this time are generated - if null, does not limit. */
	private Time until = null;

	/*For i18n of recurrence rule description*/
	protected static final ResourceLoader rb = new ResourceLoader("calendar");

	/**
	* Construct.
	*/
	public RecurrenceRuleBase()
	{
	}	// RecurrenceRuleBase

	/**
	* Construct with no  limits.
	* @param interval Every this many number of days: 1 would be daily.
	*/
	public RecurrenceRuleBase(int interval)
	{
		this.interval = interval;
	}	// RecurrenceRuleBase

	/**
	* Construct with count limit.
	* @param interval Every this many number of days: 1 would be daily.
	* @param count For this many occurrences - if 0, does not limit.
	*/
	public RecurrenceRuleBase(int interval, int count)
	{
		this.interval = interval;
		this.count = count;
	}	// RecurrenceRuleBase

	/**
	* Construct with time limit.
	* @param interval Every this many number of days: 1 would be daily.
	* @param until No time ranges past this time are generated - if null, does not limit.
	*/
	public RecurrenceRuleBase(int interval, Time until)
	{
		this.interval = interval;
		this.until = (Time) until.clone();
	}	// RecurrenceRuleBase
	
	/**
	* Return a List of all RecurrenceInstance objects generated by this rule within the given time range, based on the
	* prototype first range, in time order.
	* @param prototype The prototype first TimeRange.
	* @param range A time range to limit the generated ranges.
	* @param timeZone The time zone to use for displaying times.
	* %%% Note: this is currently not implemented, and always uses the "local" zone.
	* @return a List of max 1000 RecurrenceInstance generated by this rule in this range.
	*/
	public List generateInstances(TimeRange prototype, TimeRange range, TimeZone timeZone)
	{
		// these calendars are used if local time zone and the time zone where the first event was created (timeZone) are different
		GregorianCalendar firstEventCalendarDate = null;
		GregorianCalendar nextFirstEventCalendarDate = null;
		// %%% Note: base the breakdonw on the "timeZone" parameter to support multiple timeZone displays -ggolden
		TimeBreakdown startBreakdown = prototype.firstTime().breakdownLocal();
		
		GregorianCalendar startCalendarDate = TimeService.getCalendar(TimeService.getLocalTimeZone(),0,0,0,0,0,0,0);
		
		startCalendarDate.set(
			startBreakdown.getYear(),
			startBreakdown.getMonth() - 1,
			startBreakdown.getDay(),
			startBreakdown.getHour(),
			startBreakdown.getMin(),
			startBreakdown.getSec());
		
		// if local time zone and first event time zone are different
		// a new calendar is generated to calculate the re-occurring events
		// if not, the local time zone calendar is used
		boolean differentTimeZone = false;
		if (TimeService.getLocalTimeZone().getID().equals(timeZone.getID()))
		{
			differentTimeZone = false;
		}
		else
		{
			differentTimeZone = true;
		}
		
		if (differentTimeZone)
		{
			firstEventCalendarDate = TimeService.getCalendar(timeZone,0,0,0,0,0,0,0);
			firstEventCalendarDate.setTimeInMillis(startCalendarDate.getTimeInMillis());
			nextFirstEventCalendarDate = (GregorianCalendar) firstEventCalendarDate.clone();
		}
		
		List rv = new Vector();
		
		GregorianCalendar nextCalendarDate =
			(GregorianCalendar) startCalendarDate.clone();
			
		int currentCount = 1;
		int maxItems = 1000;
		
		do
		{
			if (differentTimeZone)
			{
				// next time is calculated according to the first event time zone, not the local one
				nextCalendarDate.setTimeInMillis(nextFirstEventCalendarDate.getTimeInMillis());
			}
			
			Time nextTime = TimeService.newTime(nextCalendarDate);
			
			// is this past count?
			if ((getCount() > 0) && (currentCount > getCount()))
				break;
	
			// is this past until?
			if ((getUntil() != null) && isAfter(nextTime, getUntil()) )
				break;
	
			TimeRange nextTimeRange = TimeService.newTimeRange(nextTime.getTime(), prototype.duration());
			
			//
			// Is this out of the range?
			//
			if (isOverlap(range, nextTimeRange))
			{
				TimeRange eventTimeRange = null;
				
				// Single time cases require special handling.
				if ( prototype.isSingleTime() )
				{
					eventTimeRange = TimeService.newTimeRange(nextTimeRange.firstTime());
				}
				else
				{
					eventTimeRange = TimeService.newTimeRange(nextTimeRange.firstTime(), nextTimeRange.lastTime(), true, false);
				}
				
				// use this one
				rv.add(new RecurrenceInstance(eventTimeRange, currentCount));

				// Break if we reach the max limit
				if (rv.size() >= maxItems)
				{
					log.warn("Reached the maximum limit of 1000 items for recurring events.");
					break;
				}
			}
	
			// if next starts after the range, stop generating
			else if (isAfter(nextTime, range.lastTime()))
			{
				break;
			}
	
			// advance interval years.
			
			if (differentTimeZone)
			{
				nextFirstEventCalendarDate = (GregorianCalendar) firstEventCalendarDate.clone();
				nextFirstEventCalendarDate.add(getRecurrenceType(), getInterval()*currentCount);
			}
			else
			{
				nextCalendarDate = (GregorianCalendar) startCalendarDate.clone();
				nextCalendarDate.add(getRecurrenceType(), getInterval()*currentCount);
			}
			
			currentCount++;
		}
		while (true);
		
		return rv;
	}

	protected abstract int getRecurrenceType();

	/**
	 * Checks for overlap.
	 * @param range1
	 * @param range2
	 */
	protected final boolean isOverlap(TimeRange range1, TimeRange range2)
	{
		return range1.overlaps(range2);
	}

	/**
	 * Returns true if time1 is after time2
	 * @param time1
	 * @param time2
	 */
	protected final boolean isAfter(Time time1, Time time2)
	{
		return (time1.getTime() > time2.getTime());
	}

	/**
	 * Gets the number of times (years) that this event should repeat.
	 */
	public final int getCount()
	{
		return count;
	}

	/**
	 * Gets the end time for recurring events.
	 */
	public final Time getUntil()
	{
		return until;
	}

	/**
	 * Gets the interval (in years) for this event.
	 */
	public final int getInterval()
	{
		return interval;
	}

	/**
	 * Sets the number of times that this event will repeat. This object is immutable, but we need
	 * the "set" method for unit testing.
	 * @param i
	 */
	protected final void setCount(int i)
	{
		this.count = i;
		setUntil(null);
	}

	/**
	 * Sets the interval (in months) which this event will repeat. This object is immutable, but we need
	 * the "set" method for unit testing.
	 * @param i
	 */
	protected final void setInterval(int i)
	{
		this.interval = i;
		setCount(0);
	}

	/**
	 * Sets the end time for this recurring event.  This object is immutable, but we need
	 * the "set" method for unit testing.
	 * @param time
	 */
	protected final void setUntil(Time time)
	{
		this.until = time;
	}

	/**
	 * Take values from this xml element
	 * @param el The xml element.
	 */
	public void set(Element el)
	{
		// read the interval
		try
		{
			setInterval(Integer.parseInt(el.getAttribute("interval")));
		}
		catch (Exception any) {}
		
		// read the count
		try
		{
			setCount(Integer.parseInt(el.getAttribute("count")));
		}
		catch (Exception any) {}
	
		// read the until
		try
		{
			setUntil(TimeService.newTimeGmt(el.getAttribute("until")));
		}
		catch (Exception any) {}
	
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.calendar.api.RecurrenceRule#getContentHandler()
	 */
	public ContentHandler getContentHandler(final Map<String,Object> services)
	{
		return new DefaultHandler()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
			 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
			 */
			@Override
			public void startElement(String uri, String localName, String qName,
					Attributes attributes) throws SAXException
			{
				// read the interval
				try
				{
					setInterval(Integer.parseInt(attributes.getValue("interval")));
				}
				catch (Exception any)
				{
				}

				// read the count
				try
				{
					setCount(Integer.parseInt(attributes.getValue("count")));
				}
				catch (Exception any)
				{
				}

				// read the until
				try
				{
					setUntil(((org.sakaiproject.time.api.TimeService)services.get("timeservice")).newTimeGmt(attributes.getValue("until")));
				}
				catch (Exception any)
				{
				}

			}
		};
	}

	/**
	 * Remove from the ranges list any RecurrenceInstance excluded by this rule.
	 * @param ranges The list (RecurrenceInstance) of ranges.
	 */
	public final void excludeInstances(List ranges)
	{
	}

	/**
	 * Set base class attributes in the Element rule during XML serialization.
	 * @param rule
	 */
	protected void setBaseClassXML(Element rule)
	{
		// set the interval
		rule.setAttribute("interval", Integer.toString(getInterval()));

		// set either count or until (or neither), not both
		if (getCount() > 0)
		{
			rule.setAttribute("count", Integer.toString(getCount()));
		}
		else		
		if (getUntil() != null)
		{
			rule.setAttribute("until", getUntil().toString());
		}
	}
}
