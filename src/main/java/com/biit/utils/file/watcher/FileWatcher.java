package com.biit.utils.file.watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.file.FileReader;

public class FileWatcher {
	private WatchQueueReader fileWatcher = null;
	public String directoryToWatch;
	private Set<FileModifiedListener> fileModifiedListeners;
	private Set<FileCreationListener> fileCreationListeners;
	private Set<FileDeletionListener> fileDeletionListeners;

	public interface FileModifiedListener {
		void changeDetected(Path pathToFile);
	}

	public interface FileCreationListener {
		void fileCreated(Path pathToFile);
	}

	public interface FileDeletionListener {
		void fileDeleted(Path pathToFile);
	}

	public FileWatcher(Set<String> filesNames) throws IOException {
		directoryToWatch = FileReader.class.getClassLoader().getResource(".").toString();
		fileModifiedListeners = new HashSet<>();
		fileCreationListeners = new HashSet<>();
		fileDeletionListeners = new HashSet<>();
		startWatcher(filesNames);
	}

	public FileWatcher(String directoryToWatch, Set<String> filesNames) throws IOException {
		this.directoryToWatch = directoryToWatch;
		fileModifiedListeners = new HashSet<>();
		fileCreationListeners = new HashSet<>();
		fileDeletionListeners = new HashSet<>();
		startWatcher(filesNames);
	}

	public void addFileModifiedListener(FileModifiedListener listener) {
		fileModifiedListeners.add(listener);
	}

	public void addFileCreationListener(FileCreationListener listener) {
		fileCreationListeners.add(listener);
	}

	public void addFileDeletionListener(FileDeletionListener listener) {
		fileDeletionListeners.add(listener);
	}

	private void startWatcher(Set<String> filesNames) throws IOException {
		Path pathToWatch = Paths.get(directoryToWatch);
		if (pathToWatch == null) {
			throw new UnsupportedOperationException("Directory not found");
		}
		WatchService watcher = pathToWatch.getFileSystem().newWatchService();

		fileWatcher = new WatchQueueReader(watcher, pathToWatch);
		fileWatcher.setFilesNames(filesNames);
		Thread th = new Thread(fileWatcher, "FileWatcher");
		th.start();

		pathToWatch.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
	}

	private class WatchQueueReader implements Runnable {
		private WatchService watcher;
		private Path pathToWatch;
		private Set<String> filesNames;

		public WatchQueueReader(WatchService watcher, Path pathToWatch) {
			this.watcher = watcher;
			this.pathToWatch = pathToWatch;
		}

		@Override
		public void run() {
			try {
				// Get the first event before looping
				WatchKey key = watcher.take();
				while (key != null) {
					// We have a polled event, now we traverse it and receive
					// all the states from it
					for (WatchEvent<?> event : key.pollEvents()) {
						if (filesNames != null && filesNames.contains(event.context().toString())) {
							if (event.kind().equals(ENTRY_MODIFY)) {
								for (FileModifiedListener fileModifiedListener : new HashSet<>(fileModifiedListeners)) {
									fileModifiedListener.changeDetected(combine(pathToWatch, (Path) event.context()));
								}
							} else if (event.kind().equals(ENTRY_CREATE)) {
								for (FileCreationListener fileCreationListener : new HashSet<>(fileCreationListeners)) {
									fileCreationListener.fileCreated(combine(pathToWatch, (Path) event.context()));
								}
							} else if (event.kind().equals(ENTRY_DELETE)) {
								for (FileDeletionListener fileDeletionListener : new HashSet<>(fileDeletionListeners)) {
									fileDeletionListener.fileDeleted(combine(pathToWatch, (Path) event.context()));
								}
							}
						}
					}
					key.reset();
					key = watcher.take();
				}
			} catch (InterruptedException e) {
				BiitCommonLogger.severe(this.getClass().getName(), e);
			}
		}

		public void setFilesNames(Set<String> filesNames) {
			this.filesNames = filesNames;
		}
	}

	private static Path combine(Path path1, Path path2) {
		return Paths.get(path1.toString(), path2.toString());
	}

}
