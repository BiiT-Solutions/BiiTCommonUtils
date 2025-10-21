package com.biit.utils.os;

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

import com.biit.utils.os.exceptions.ExecutableCanNotBeExecuted;
import com.biit.utils.os.exceptions.PathToExecutableNotFound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Helper class for several Operative system file related issues.
 */
public final class OsUtils {

    private static final String OPERATIVE_SYSTEM = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OPERATIVE_SYSTEM.contains("win"));
    }

    public static boolean isMac() {
        return (OPERATIVE_SYSTEM.indexOf("mac") >= 0);
    }

    private OsUtils() {

    }

    public static boolean isUnix() {
        return (OPERATIVE_SYSTEM.contains("nix") || OPERATIVE_SYSTEM.contains("nux") || OPERATIVE_SYSTEM
                .indexOf("aix") > 0);
    }

    public static boolean isSolaris() {
        return (OPERATIVE_SYSTEM.contains("sunos"));
    }

    public static String getProgramsFolder() {
        if (isWindows()) {
            return System.getenv("ProgramFiles(x86)") != null ? System.getenv("ProgramFiles(x86)")
                    : System.getenv("ProgramFiles");
        } else if (isMac()) {
            return File.separator + "usr" + File.separator + "bin";
        } else if (isUnix()) {
            return File.separator + "usr" + File.separator + "bin";
        } else if (isSolaris()) {
            return File.separator + "usr" + File.separator + "bin";
        } else {
            return "";
        }
    }

    public static String readPropertiesValue(String fileName, String property) throws IOException {
        final Properties prop = new Properties();
        String propFileName = fileName;
        if (!propFileName.endsWith(".properties")) {
            propFileName += ".properties";
        }

        final InputStream inputStream = OsUtils.class.getClassLoader().getResourceAsStream(propFileName);
        if (inputStream == null) {
            throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath");
        }
        prop.load(inputStream);

        return prop.getProperty(property);
    }

    public static String findExecutablePropertiesFile(String fileName, String property) throws
            IOException, ExecutableCanNotBeExecuted, PathToExecutableNotFound {

        final String executablePath = readPropertiesValue(fileName, property);
        checkExecutable(executablePath);
        return executablePath;
    }

    public static String findExecutableEnvironmentVariable(String environmentVariable) throws PathToExecutableNotFound,
            ExecutableCanNotBeExecuted {
        final String path = System.getenv(environmentVariable);

        if (path != null && !path.isEmpty()) {
            checkExecutable(path);
            return path;
        }
        throw new PathToExecutableNotFound();
    }

    public static String findExecutablePath(String executableName) throws ExecutableCanNotBeExecuted,
            PathToExecutableNotFound {
        final String basicPath = getProgramsFolder();
        String filePath = "";

        if (isWindows()) {
            filePath = basicPath + File.separator + executableName + ".exe";
        }
        if (isUnix() || isMac() || isSolaris()) {
            filePath = basicPath + File.separator + executableName;
        }

        checkExecutable(filePath);
        return filePath;
    }

    public static void checkExecutable(String executablePath) throws ExecutableCanNotBeExecuted,
            PathToExecutableNotFound {
        final File executable = new File(executablePath);
        if (executable.exists()) {
            if (!executable.canExecute()) {
                throw new ExecutableCanNotBeExecuted();
            }
        } else {
            throw new PathToExecutableNotFound();
        }
    }

    public static void execSynchronic(String... stringArgs) throws IOException, InterruptedException {
        final Runtime rt = Runtime.getRuntime();
        final Process p = rt.exec(stringArgs);
        p.waitFor();
    }

}
