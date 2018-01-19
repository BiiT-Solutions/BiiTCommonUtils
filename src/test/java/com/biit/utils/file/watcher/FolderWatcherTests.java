package com.biit.utils.file.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.testng.annotations.Test;

import com.biit.utils.file.watcher.FileWatcher.FileAddedListener;
import com.biit.utils.file.watcher.FileWatcher.FileRemovedListener;

@Test(groups = { "folderWatcherTests" })
public class FolderWatcherTests {
	private final static String FOLDER = "WatchedFolder";

	private boolean fileAddedDetected = false;
	private boolean fileRemovedDetected = false;

	private class FileAdded extends Exception {
		private static final long serialVersionUID = 5797137086506193964L;
	}

	private class FileRemoved extends Exception {
		private static final long serialVersionUID = 992196416293719673L;
	}

	@Test(expectedExceptions = { FileAdded.class })
	public void checkFolderFileAdded() throws IOException, InterruptedException, FileAdded, FileRemoved {
		fileAddedDetected = false;

		String folderPath = Paths.get(System.getProperty("java.io.tmpdir"), FOLDER).toString();
		File folder = new File(folderPath);
		folder.mkdirs();
		folder.deleteOnExit();

		// Register the folder.
		FileWatcher folderWatcher = new FileWatcher(folderPath);
		folderWatcher.addFileAddedListener(new FileAddedListener() {

			@Override
			public void fileCreated(Path pathToFile) {
				fileAddedDetected = true;
			}
		});

		String fileName = "fileAddedToFolder.txt";
		File file = new File(Files.write(Paths.get(folderPath + File.separator + fileName), "NewFile".getBytes(), StandardOpenOption.CREATE_NEW).toString());
		file.deleteOnExit();

		// Check change alert!
		for (int i = 0; i < 10; i++) {
			if (fileAddedDetected) {
				throw new FileAdded();
			}
			Thread.sleep(100);
		}
	}

	@Test(expectedExceptions = { FileRemoved.class })
	public void checkFolderFileDeleted() throws IOException, InterruptedException, FileAdded, FileRemoved {
		fileRemovedDetected = false;

		String folderPath = Paths.get(System.getProperty("java.io.tmpdir"), FOLDER).toString();
		File folder = new File(folderPath);
		folder.mkdirs();
		folder.deleteOnExit();

		// File already on the folder.
		String fileName = "fileRemovedFromFolder.txt";
		File file = new File(Files.write(Paths.get(folderPath + File.separator + fileName), "NewFile".getBytes(), StandardOpenOption.CREATE_NEW).toString());
		file.deleteOnExit();

		// Register the folder.
		FileWatcher folderWatcher = new FileWatcher(folderPath);
		folderWatcher.addFileRemovedListener(new FileRemovedListener() {

			@Override
			public void fileDeleted(Path pathToFile) {
				fileRemovedDetected = true;

			}
		});

		// Remove file.
		file.delete();

		// Check change alert!
		for (int i = 0; i < 10; i++) {
			if (fileRemovedDetected) {
				throw new FileRemoved();
			}
			Thread.sleep(100);
		}
	}
}
