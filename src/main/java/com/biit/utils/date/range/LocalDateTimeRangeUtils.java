package com.biit.utils.date.range;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class LocalDateTimeRangeUtils {

    private LocalDateTimeRangeUtils() {

    }

    public static List<LocalDateTimeRange> removeRange(Collection<LocalDateTimeRange> baseTime, LocalDateTimeRange timeToExclude) {
        final ArrayList<LocalDateTimeRange> ranges = new ArrayList<>();
        baseTime.forEach(range -> ranges.addAll(removeRange(range, timeToExclude)));
        return ranges;
    }

    public static List<LocalDateTimeRange> removeRange(LocalDateTimeRange baseTime, LocalDateTimeRange timeToExclude) {
        final ArrayList<LocalDateTimeRange> ranges = new ArrayList<>();
        // Ranges do not overlap.
        if (timeToExclude.upperBound().isBefore(baseTime.lowerBound())
                || timeToExclude.lowerBound().isAfter(baseTime.upperBound())) {
            ranges.add(baseTime);
            return ranges;
        }
        //TimeToExclude starts before baseTime.
        if (timeToExclude.lowerBound().isBefore(baseTime.lowerBound())
                || timeToExclude.lowerBound().isEqual(baseTime.lowerBound())) {
            if (timeToExclude.upperBound().isBefore(baseTime.upperBound())) {
                ranges.add(new LocalDateTimeRange(timeToExclude.upperBound(), baseTime.upperBound()));
                return ranges;
            } else {
                //We remove all baseTime. Return empty list.
                return ranges;
            }
            //TimeToExclude starts after baseTime
        } else {
            if (timeToExclude.upperBound().isAfter(baseTime.upperBound())) {
                ranges.add(new LocalDateTimeRange(baseTime.lowerBound(), timeToExclude.lowerBound()));
                return ranges;
            }
            //Excluded time is in the middle that base time.
            ranges.add(new LocalDateTimeRange(baseTime.lowerBound(), timeToExclude.lowerBound()));
            ranges.add(new LocalDateTimeRange(timeToExclude.upperBound(), baseTime.upperBound()));
            return ranges;
        }
    }
}
