package com.biit.logger.mail.exceptions;

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


import java.io.Serial;

public class InvalidEmailAddressException extends Exception {
    @Serial
    private static final long serialVersionUID = 3182975812992201906L;

    public InvalidEmailAddressException(String message) {
        super(message);
    }


    public InvalidEmailAddressException(Class<?> clazz, String message) {
        super(clazz + ": " + message);
    }

    public InvalidEmailAddressException(Class<?> clazz, String message, Throwable cause) {
        super(clazz + ": " + message, cause);
    }
}
