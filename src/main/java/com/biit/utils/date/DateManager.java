package com.biit.utils.date;

/*-
 * #%L
 * Generic utilities used in all Biit projects.
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateManager {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_SIMPLE = "yyyy-MM-dd";

    private DateManager() {

    }

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
