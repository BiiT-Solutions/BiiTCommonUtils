package com.biit.utils.file.watcher;

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

import com.biit.utils.file.watcher.FileWatcher.FileAddedListener;
import com.biit.utils.file.watcher.FileWatcher.FileModifiedListener;
import com.biit.utils.file.watcher.FileWatcher.FileRemovedListener;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Test(groups = {"fileWatcherTests"})
public class FileWatcherTests {
    private boolean fileModificationDetected = false;
    private boolean fileCreationDetected = false;
    private boolean fileDeletionDetected = false;

    private class FileModified extends Exception {
        private static final long serialVersionUID = 4567824418370310105L;
    }

    private class FileCreated extends Exception {
        private static final long serialVersionUID = 4567824418370310105L;
    }

    private class FileDeleted extends Exception {
        private static final long serialVersionUID = 4567824418370310105L;
    }

    @Test(expectedExceptions = {FileModified.class})
    public void checkFileModificationListener() throws IOException, FileModified, InterruptedException {
        fileModificationDetected = false;

        // Create a file.
        final File file = File.createTempFile("watcherTest", ".tmp");
        file.deleteOnExit();
        final Set<String> checkedFiles = new HashSet<>(Arrays.asList(new String[]{file.getName()}));
        final FileWatcher fileWatcher = new FileWatcher(file.getParent(), checkedFiles);
        fileWatcher.addFileModifiedListener(new FileModifiedListener() {

            @Override
            public void changeDetected(Path pathToFile) {
                fileModificationDetected = true;
            }
        });

        // Add content to file.
        Files.write(Paths.get(file.getPath()), "NewText".getBytes(), StandardOpenOption.APPEND);

        // Check change alert!
        for (int i = 0; i < 10; i++) {
            if (fileModificationDetected) {
                throw new FileModified();
            }
            Thread.sleep(100);
        }
    }

    @Test(expectedExceptions = {FileCreated.class})
    public void checkFileCreationListener() throws IOException, InterruptedException, FileCreated {
        fileCreationDetected = true;

        final String folder = System.getProperty("java.io.tmpdir");
        final String fileName = "fileCreated.txt";

        // Create a file.
        final Set<String> checkedFiles = new HashSet<>(Arrays.asList(new String[]{fileName}));
        final FileWatcher fileWatcher = new FileWatcher(folder, checkedFiles);
        fileWatcher.addFileAddedListener(new FileAddedListener() {

            @Override
            public void fileCreated(Path pathToFile) {
                fileCreationDetected = true;
            }
        });

        final File file = new File(Files.write(Paths.get(folder + File.separator + fileName), "NewFile".getBytes(), StandardOpenOption.CREATE_NEW).toString());
        file.deleteOnExit();

        // Check change alert!
        for (int i = 0; i < 10; i++) {
            if (fileCreationDetected) {
                throw new FileCreated();
            }
            Thread.sleep(100);
        }
    }

    @Test(expectedExceptions = {FileDeleted.class})
    public void checkFileDeletionListener() throws IOException, InterruptedException, FileCreated, FileDeleted {
        fileDeletionDetected = false;

        final String folder = System.getProperty("java.io.tmpdir");
        final String fileName = "fileDeleted.txt";

        // Create a file.
        final Set<String> checkedFiles = new HashSet<>(Arrays.asList(new String[]{fileName}));
        final FileWatcher fileWatcher = new FileWatcher(folder, checkedFiles);
        fileWatcher.addFileRemovedListener(new FileRemovedListener() {

            @Override
            public void fileDeleted(Path pathToFile) {
                fileDeletionDetected = true;
            }
        });

        final File file = new File(Files.write(Paths.get(folder + File.separator + fileName), "NewFile".getBytes(), StandardOpenOption.CREATE_NEW).toString());
        file.delete();

        // Check change alert!
        for (int i = 0; i < 10; i++) {
            if (fileDeletionDetected) {
                throw new FileDeleted();
            }
            Thread.sleep(100);
        }
    }
}
