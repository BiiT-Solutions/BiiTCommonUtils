package com.biit.utils.date.range;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Test(groups = {"localDateTimeRangeTests"})
public class LocalDateTimeRangeTests {

    public void invalidRangeToRemoveLowerSide() {
        final LocalDateTimeRange localDateTimeRange = new LocalDateTimeRange(
                LocalDateTime.of(2024, 2, 13, 13, 0, 0),
                LocalDateTime.of(2024, 2, 13, 18, 0, 0)
        );
        final LocalDateTimeRange invalidLowerRange = new LocalDateTimeRange(
                LocalDateTime.of(2024, 2, 13, 10, 0, 0),
                LocalDateTime.of(2024, 2, 13, 12, 0, 0)
        );

        Assert.assertEquals(localDateTimeRange, LocalDateTimeRangeUtils.removeRange(localDateTimeRange, invalidLowerRange).get(0));
    }

    public void invalidRangeToRemoveUpperSide() {
        final LocalDateTimeRange localDateTimeRange = new LocalDateTimeRange(
                LocalDateTime.of(2024, 2, 13, 13, 0, 0),
                LocalDateTime.of(2024, 2, 13, 18, 0, 0)
        );
        final LocalDateTimeRange invalidLowerRange = new LocalDateTimeRange(
                LocalDateTime.of(2024, 2, 13, 18, 0, 0),
                LocalDateTime.of(2024, 2, 13, 21, 0, 0)
        );

        Assert.assertEquals(localDateTimeRange, LocalDateTimeRangeUtils.removeRange(localDateTimeRange, invalidLowerRange).get(0));
    }

    public void validRangeToRemoveUpperSide() {
        final LocalDateTimeRange localDateTimeRange = new LocalDateTimeRange(
                LocalDateTime.of(2024, 2, 13, 13, 0, 0),
                LocalDateTime.of(2024, 2, 13, 18, 0, 0)
        );
        final LocalDateTimeRange rangeToExclude = new LocalDateTimeRange(
                LocalDateTime.of(2024, 2, 13, 15, 0, 0),
                LocalDateTime.of(2024, 2, 13, 21, 0, 0)
        );

        Assert.assertEquals(new LocalDateTimeRange(
                        LocalDateTime.of(2024, 2, 13, 13, 0, 0),
                        LocalDateTime.of(2024, 2, 13, 15, 0, 0)
                ),
                LocalDateTimeRangeUtils.removeRange(localDateTimeRange, rangeToExclude).get(0));
    }

    public void validRangeToRemoveLowerSide() {
        final LocalDateTimeRange localDateTimeRange = new LocalDateTimeRange(
                LocalDateTime.of(2024, 2, 13, 13, 0, 0),
                LocalDateTime.of(2024, 2, 13, 18, 0, 0)
        );
        final LocalDateTimeRange rangeToExclude = new LocalDateTimeRange(
                LocalDateTime.of(2024, 2, 13, 10, 0, 0),
                LocalDateTime.of(2024, 2, 13, 16, 0, 0)
        );

        Assert.assertEquals(new LocalDateTimeRange(
                        LocalDateTime.of(2024, 2, 13, 16, 0, 0),
                        LocalDateTime.of(2024, 2, 13, 18, 0, 0)
                ),
                LocalDateTimeRangeUtils.removeRange(localDateTimeRange, rangeToExclude).get(0));
    }

    public void validRangeToRemoveIntermediateSide() {
        final LocalDateTimeRange localDateTimeRange = new LocalDateTimeRange(
                LocalDateTime.of(2024, 2, 13, 13, 0, 0),
                LocalDateTime.of(2024, 2, 13, 18, 0, 0)
        );
        final LocalDateTimeRange rangeToExclude = new LocalDateTimeRange(
                LocalDateTime.of(2024, 2, 13, 15, 0, 0),
                LocalDateTime.of(2024, 2, 13, 16, 0, 0)
        );

        Assert.assertEquals(new LocalDateTimeRange(
                        LocalDateTime.of(2024, 2, 13, 13, 0, 0),
                        LocalDateTime.of(2024, 2, 13, 15, 0, 0)
                ),
                LocalDateTimeRangeUtils.removeRange(localDateTimeRange, rangeToExclude).get(0));
        Assert.assertEquals(new LocalDateTimeRange(
                        LocalDateTime.of(2024, 2, 13, 16, 0, 0),
                        LocalDateTime.of(2024, 2, 13, 18, 0, 0)
                ),
                LocalDateTimeRangeUtils.removeRange(localDateTimeRange, rangeToExclude).get(1));
    }

    public void validMultipleRangeToRemoveIntermediateSide() {
        final ArrayList<LocalDateTimeRange> ranges = new ArrayList<>();

        ranges.add(new LocalDateTimeRange(
                LocalDateTime.of(2024, 2, 13, 10, 0, 0),
                LocalDateTime.of(2024, 2, 13, 15, 0, 0)
        ));
        ranges.add(new LocalDateTimeRange(
                LocalDateTime.of(2024, 2, 13, 16, 0, 0),
                LocalDateTime.of(2024, 2, 13, 18, 0, 0)
        ));

        final LocalDateTimeRange rangeToExclude = new LocalDateTimeRange(
                LocalDateTime.of(2024, 2, 13, 12, 0, 0),
                LocalDateTime.of(2024, 2, 13, 17, 0, 0)
        );

        Assert.assertEquals(new LocalDateTimeRange(
                        LocalDateTime.of(2024, 2, 13, 10, 0, 0),
                        LocalDateTime.of(2024, 2, 13, 12, 0, 0)
                ),
                LocalDateTimeRangeUtils.removeRange(ranges, rangeToExclude).get(0));
        Assert.assertEquals(new LocalDateTimeRange(
                        LocalDateTime.of(2024, 2, 13, 17, 0, 0),
                        LocalDateTime.of(2024, 2, 13, 18, 0, 0)
                ),
                LocalDateTimeRangeUtils.removeRange(ranges, rangeToExclude).get(1));
    }
}
