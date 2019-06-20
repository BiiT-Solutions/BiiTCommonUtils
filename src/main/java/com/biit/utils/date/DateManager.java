package com.biit.utils.date;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateManager {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_SIMPLE = "yyyy-MM-dd";

	public static String convertDateToString(Date date, String dateFormat) {
		if (date == null) {
			return new SimpleDateFormat(dateFormat).format(new Timestamp(new java.util.Date(0).getTime()));
		}
		return new SimpleDateFormat(dateFormat).format(date);
	}

	public static String convertDateToString(Timestamp timestamp, String dateFormat) {
		if (timestamp == null) {
			return new SimpleDateFormat(dateFormat).format(new Timestamp(0));
		}
		return new SimpleDateFormat(dateFormat).format(timestamp);
	}

	public static String convertDateToStringWithHours(Timestamp time) {
		if (time == null) {
			return "";
		}
		final Date date = new Date(time.getTime());
		return convertDateToString(date, DATE_FORMAT);
	}

	public static String convertDateToString(Timestamp time) {
		final Date date = new Date(time.getTime());
		return convertDateToString(date, DATE_FORMAT_SIMPLE);
	}

	public static Date incrementDateOneDay(Date date) {
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1); // number of days to add
		return c.getTime();
	}

	public static Timestamp convertToTimestamp(Date date) {
		return new Timestamp(date.getTime());
	}

	public static Date getDate(String date, String format) throws ParseException {
		final SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.parse(date);
	}

	public static String convertDateToString(Date date, String dateFormat, Locale locale) {
		if (locale == null) {
			locale = Locale.ENGLISH;
		}
		if (date == null) {
			return new SimpleDateFormat(dateFormat, locale).format(new Timestamp(new java.util.Date(0).getTime()));
		}
		return new SimpleDateFormat(dateFormat, locale).format(date);
	}

	public static String convertDateToString(Timestamp timestamp, String dateFormat, Locale locale) {
		if (locale == null) {
			locale = Locale.ENGLISH;
		}
		if (timestamp == null) {
			return new SimpleDateFormat(dateFormat, locale).format(new Timestamp(0));
		}
		return new SimpleDateFormat(dateFormat, locale).format(timestamp);
	}

	public static String convertDateToStringWithHours(Date date) {
		return convertDateToString(date, DATE_FORMAT, null);
	}

	public static String convertDateToString(Date date) {
		return convertDateToString(date, DATE_FORMAT_SIMPLE, null);
	}

	public static String convertDateToString(Timestamp time, Locale locale) {
		final Date date = new Date(time.getTime());
		return convertDateToString(date, DATE_FORMAT_SIMPLE, locale);
	}

	public static String formatStringDate(String date, String originFormat, String destinyFormat) throws ParseException {
		return convertDateToString(new Date(new SimpleDateFormat(originFormat).parse(date).getTime()), destinyFormat,
				null);
	}

	public static String formatStringDate(Timestamp date, String destinyFormat) throws ParseException {
		return convertDateToString(new Date(date.getTime()), destinyFormat, null);
	}

}
