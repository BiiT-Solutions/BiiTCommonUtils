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

import com.biit.utils.file.FileReader;

public class FileWatcher {
	public static final String DIRECTORY_TO_WATCH = FileReader.class.getClassLoader().getResource("/.").toString();
	private Set<FileModifiedListener> fileModifiedListeners;

	public interface FileModifiedListener {
		void changeDetected(Path fileName);
	}

	public FileWatcher() {
		fileModifiedListeners = new HashSet<>();
	}

	public void addFileModifiedListener(FileModifiedListener listener) {
		fileModifiedListeners.add(listener);
	}

	public void setWatcher() throws IOException {
		Path pathToWatch = Paths.get(DIRECTORY_TO_WATCH);
		if (pathToWatch == null) {
			throw new UnsupportedOperationException("Directory not found");
		}
		WatchService watcher = pathToWatch.getFileSystem().newWatchService();

		WatchQueueReader fileWatcher = new WatchQueueReader(watcher, pathToWatch);
		Thread th = new Thread(fileWatcher, "FileWatcher");
		th.start();

		pathToWatch.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// TODO Add files to
	private class WatchQueueReader implements Runnable {
		private WatchService watcher;
		private Path pathToWatch;

		public WatchQueueReader(WatchService watcher, Path pathToWatch) {
			this.watcher = watcher;
			this.pathToWatch = pathToWatch;
		}

		@Override
		public void run() {
			Set<String> filesNames = null;
			try {
				// get the first event before looping
				WatchKey key = watcher.take();
				while (key != null) {
					// we have a polled event, now we traverse it and
					// receive all the states from it
					for (WatchEvent<?> event : key.pollEvents()) {
						if (filesNames != null && filesNames.contains(event.context())) {
							if (event.kind().equals(ENTRY_MODIFY)) {
								for (FileModifiedListener fileModifiedListener : new HashSet<>(fileModifiedListeners)) {
									fileModifiedListener.changeDetected(combine(pathToWatch, (Path) event.context()));
								}
							} else if (event.kind().equals(ENTRY_CREATE)) {

							} else if (event.kind().equals(ENTRY_DELETE)) {

							}
						}

						System.out.printf("Received %s event for file: %s\n", event.kind(), event.context());
					}
					key.reset();
					key = watcher.take();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static Path combine(Path path1, Path path2) {
		return Paths.get(path1.toString(), path2.toString());
	}

}
