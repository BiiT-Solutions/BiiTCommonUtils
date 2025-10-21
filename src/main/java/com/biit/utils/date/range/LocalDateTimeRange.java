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

import java.time.LocalDateTime;

public record LocalDateTimeRange(LocalDateTime lowerBound, LocalDateTime upperBound) {
    public LocalDateTimeRange {
        if (lowerBound.isAfter(upperBound) || lowerBound.isEqual(upperBound)) {
            throw new RuntimeException("Invalid range. Lower bound '" + lowerBound + "' is defined after upper bound '" + upperBound + "'.");
        }
    }
}
