package com.biit.utils.date.range;

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
