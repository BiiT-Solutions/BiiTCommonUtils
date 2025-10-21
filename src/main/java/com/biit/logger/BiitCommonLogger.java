package com.biit.logger;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BiitCommonLogger extends BiitLogger {

    private static Logger logger = LoggerFactory.getLogger(BiitCommonLogger.class);

    public static void severe(String name, Throwable e) {
        severe(logger, BiitLogger.getStackTrace(e));
    }

    public static void info(Class<?> clazz, String message) {
        info(logger, clazz.getName(), message);
    }

    public static void errorMessageNotification(Class<?> clazz, Throwable e) {
        errorMessageNotification(logger, clazz.getName(), BiitLogger.getStackTrace(e));
    }

    public static void warning(Class<?> clazz, String message) {
        warning(logger, clazz.getName(), message);
    }

    public static void errorMessageNotification(Class<?> clazz, String message) {
        errorMessageNotification(logger, clazz.getName(), message);
    }

    public static void severe(Class<?> clazz, String message) {
        severe(logger, clazz.getName(), message);
    }

    public static void debug(Class<?> clazz, String message) {
        debug(logger, clazz.getName(), message);
    }

}
