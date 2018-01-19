package com.biit.utils.file.watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;

import com.biit.logger.BiitCommonLogger;

public class FolderWatcher {
	private WatchQueueReader fileWatcher = null;
	private Set<FolderModifiedListener> folderModifiedListener;
	private Set<FileAddedListener> fileAddedListener;
	private Set<FileRemovedListener> fileRemovedListener;
	public String directoryToWatch = null;
	private Path pathToWatch = null;
	private WatchService watcher = null;
	private Thread thread;

	public interface FolderModifiedListener {
		void folderModified(Path pathToFile);
	}

	public interface FileAddedListener {
		void fileAdded(Path pathToFile);
	}

	public interface FileRemovedListener {
		void fileRemoved(Path pathToFile);
	}

	/**
	 * Only check for a directory. Watch if any file is added, updated or
	 * deleted.
	 * 
	 * @param directoryToWatch
	 * @throws IOException
	 */
	public FolderWatcher(String directoryToWatch) throws IOException {
		setDirectoryToWatch(directoryToWatch);
		folderModifiedListener = new HashSet<>();
		fileAddedListener = new HashSet<>();
		fileRemovedListener = new HashSet<>();
		startWatcher();
	}

	public void addFolderModifiedListener(FolderModifiedListener listener) {
		folderModifiedListener.add(listener);
	}

	public void addFileAddedListener(FileAddedListener listener) {
		fileAddedListener.add(listener);
	}

	public void addFileRemovedListener(FileRemovedListener listener) {
		fileRemovedListener.add(listener);
	}

	private WatchService getWatchService() throws IOException {
		if (watcher == null) {
			if (getDirectoryToWatch() == null) {
				throw new UnsupportedOperationException("Directory not found");
			}
			watcher = getDirectoryToWatch().getFileSystem().newWatchService();
		}
		return watcher;
	}

	private void startWatcher() {
		try {
			fileWatcher = new WatchQueueReader(getWatchService(), getDirectoryToWatch());
			stopThread();
			thread = new Thread(fileWatcher, "FileWatcher");
			thread.start();
			pathToWatch.register(getWatchService(), StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);
		} catch (IOException e) {
			BiitCommonLogger.severe(this.getClass().getName(), e);
		}
	}

	private class WatchQueueReader implements Runnable {
		private WatchService watcher;
		private Path pathToWatch;

		public WatchQueueReader(WatchService watcher, Path pathToWatch) {
			this.watcher = watcher;
			this.pathToWatch = pathToWatch;
		}

		@Override
		public void run() {
			try {
				WatchKey key;
				do {
					key = watcher.take();

					// We have a polled event, now we traverse it and receive
					// all the states from it
					for (WatchEvent<?> event : key.pollEvents()) {
						if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
							for (FolderModifiedListener folderModifiedListener : new HashSet<>(folderModifiedListener)) {
								folderModifiedListener.folderModified(combine(pathToWatch, (Path) event.context()));
							}
						} else if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
							for (FileAddedListener fileAddedListener : new HashSet<>(fileAddedListener)) {
								fileAddedListener.fileAdded(combine(pathToWatch, (Path) event.context()));
							}
						} else if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
							for (FileRemovedListener fileRemovedListener : new HashSet<>(fileRemovedListener)) {
								fileRemovedListener.fileRemoved(combine(pathToWatch, (Path) event.context()));
							}
						} else if (event.kind().equals(StandardWatchEventKinds.OVERFLOW)) {
							BiitCommonLogger.errorMessageNotification(this.getClass(), "File Watcher events vents may have been lost or discarded.");
						}
					}
				} while (key.reset());
			} catch (InterruptedException e) {
				BiitCommonLogger.severe(this.getClass().getName(), e);
			}
		}
	}

	private static Path combine(Path path1, Path path2) {
		return Paths.get(path1.toString(), path2.toString());
	}

	public void setDirectoryToWatch(String directoryToWatch) {
		if (!directoryToWatch.equals(this.directoryToWatch)) {
			this.directoryToWatch = directoryToWatch;
			closeFolderWatcher();
		}
	}

	private Path getDirectoryToWatch() {
		if (pathToWatch == null) {
			pathToWatch = Paths.get(directoryToWatch);
		}
		return pathToWatch;
	}

	public void closeFolderWatcher() {
		pathToWatch = null;
		if (watcher != null) {
			try {
				watcher.close();
				// stopThread();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		watcher = null;
	}

	public void stopThread() {
		if (thread != null) {
			thread.interrupt();
		}
	}
}
