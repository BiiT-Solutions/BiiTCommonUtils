package com.biit.utils.date.range;

import java.time.LocalDateTime;

public record LocalDateTimeRange(LocalDateTime lowerBound, LocalDateTime upperBound) {
    public LocalDateTimeRange {
        if (lowerBound.isAfter(upperBound) || lowerBound.isEqual(upperBound)) {
            throw new RuntimeException("Invalid range. Lower bound '" + lowerBound + "' is defined after upper bound '" + upperBound + "'.");
        }
    }
}
