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
