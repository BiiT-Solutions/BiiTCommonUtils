package com.biit.utils.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.ImageIcon;

import com.biit.logger.BiitCommonLogger;

public class FileReader {
	private final static String ICON_FOLDER = "icons";

	public static File getResource(String filename) throws NullPointerException {
		URL url = FileReader.class.getClassLoader().getResource(filename);
		if (url != null) {
			BiitCommonLogger.debug(FileReader.class, "Resource to read '" + filename + "' found at url '" + url.toString() + "'.");
		} else {
			BiitCommonLogger.warning(FileReader.class, "Invalid resource '" + filename + "'.");
		}
		File file = null;
		// Jetty load resource.
		try {
			file = new File(FileReader.convert2OsPath(url));
			// Apache load resource
			if (!file.exists()) {
				try {
					file = new File(url.toURI());
				} catch (URISyntaxException e) {
					BiitCommonLogger.errorMessageNotification(FileReader.class, "File not found: " + FileReader.convert2OsPath(url));
				} catch (IllegalArgumentException e) {
					// Resource inside a jar.
					if (url.toString().contains(".jar!")) {
						BiitCommonLogger.debug(FileReader.class, "Resource inside a jar. Copy to a temporal file.");
						// Copy to a temp file and return it.
						try {
							InputStream inputStream = url.openStream();
							if (inputStream != null) {
								final File tempFile = File.createTempFile(filename, "_jar");
								// tempFile.deleteOnExit();
								OutputStream os = new FileOutputStream(tempFile);
								byte[] buffer = new byte[1024];
								int bytesRead;
								// read from is to buffer
								while ((bytesRead = inputStream.read(buffer)) != -1) {
									os.write(buffer, 0, bytesRead);
								}
								inputStream.close();
								os.close();
								return tempFile;
							}
						} catch (Exception e1) {
							BiitCommonLogger.severe(FileReader.class.getName(), e1);
						}
					}
					BiitCommonLogger.severe(FileReader.class, "File not found: " + FileReader.convert2OsPath(url));
				}
			}
		} catch (NullPointerException npe) {
			throw new NullPointerException("File '" + filename + "' does not exist.");
		}
		return file;
	}

	public static String convert2OsPath(URL string) {
		try {
			if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
				if (string.getPath().startsWith("file:")) {
					return (string.getPath()).replace("file:", "").substring(1);
				} else {
					return (string.getPath()).substring(1);
				}
			} else {
				return (string.getPath());
			}
		} catch (NullPointerException npe) {
			return null;
		}
	}

	public static ImageIcon getIcon(String iconFile) {
		return new ImageIcon(FileReader.class.getClassLoader().getResource(File.separator + ICON_FOLDER + File.separator + iconFile));
	}

	/**
	 * Reads a resource text file and returns a list of strings (one line per
	 * string).
	 * 
	 * @param resourceName
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<String> getResouceAsList(String resourceName) throws FileNotFoundException {
		File file = FileReader.getResource(resourceName);
		return readFileAsList(file);
	}

	/**
	 * Reads a resource text file and returns a list of strings (one line per
	 * string).
	 * 
	 * @param resourceName
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<String> getFileAsList(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		return readFileAsList(file);
	}

	private static List<String> readFileAsList(File file) throws FileNotFoundException {
		Scanner s = new Scanner(file);
		List<String> lines = new ArrayList<String>();
		while (s.hasNext()) {
			lines.add(s.next());
		}
		s.close();
		return lines;
	}

	public static String readFile(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file, "UTF-8");
		// Change delimiter or it will removes all white spaces.
		scanner.useDelimiter("\\A");
		StringBuilder content = new StringBuilder();
		while (scanner.hasNext()) {
			content.append(scanner.next().trim());
		}
		scanner.close();
		return content.toString();
	}

	public static String getResource(String fileName, Charset charset) throws FileNotFoundException {
		StringBuilder result = new StringBuilder("");
		// Get file from resources folder
		try {
			File file = getResource(fileName);
			try {
				for (String line : Files.readAllLines(file.toPath(), charset)) {
					result.append(line).append("\n");
				}
			} catch (IOException e) {
				BiitCommonLogger.errorMessageNotification(FileReader.class, e);
			}
		} catch (NullPointerException npe) {
			throw new FileNotFoundException(npe.getMessage());
		}
		return result.toString();
	}

	public static byte[] downloadUrl(String urlname) throws MalformedURLException {
		URL url = new URL(urlname);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			byte[] chunk = new byte[4096];
			int bytesRead;
			InputStream stream = url.openStream();

			while ((bytesRead = stream.read(chunk)) > 0) {
				outputStream.write(chunk, 0, bytesRead);
			}
			outputStream.close();
		} catch (FileNotFoundException e) {
			throw new MalformedURLException("Invalid url '" + urlname + "'. File not found");
		} catch (IOException e) {
			BiitCommonLogger.errorMessageNotification(FileReader.class, e);
			return null;
		}

		return outputStream.toByteArray();
	}

	public static List<File> getResources(String folderPath) {
		URL url = FileReader.class.getClassLoader().getResource(folderPath);
		try {
			if (url != null) {
				List<File> files = new ArrayList<>();
				BiitCommonLogger.debug(FileReader.class, "Resource to read '" + folderPath + "' found at url '" + url.toString() + "'.");
				URI uri = url.toURI();
				if (uri.getScheme().equals("jar")) {
					// Remove 'file:' and '!' in 'jar!'
					final File jarFile = new File(url.getPath().toString().substring(5, url.toString().indexOf("jar!") - 1));
					try (final JarFile jar = new JarFile(jarFile)) {
						// gives ALL entries in jar
						final Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements()) {
							final JarEntry jarEntry = entries.nextElement();
							// filter according to the path
							if (jarEntry.getName().startsWith(folderPath + "/") && !jarEntry.getName().endsWith(folderPath + "/")) {
								files.add(getResource(jarEntry.getName().substring(jarEntry.getName().indexOf(folderPath))));
							}
						}
						return files;
					}
				} else {
					return Arrays.asList(new File(url.getPath()).listFiles());
				}
			}
		} catch (IOException | URISyntaxException e) {
			BiitCommonLogger.errorMessageNotification(FileReader.class, e);
		}
		BiitCommonLogger.severe(FileReader.class, "Resource folder not found '" + folderPath + "'.");
		return new ArrayList<>();
	}

	public static List<File> getFiles(String folderPath) {
		File folder = new File(folderPath);
		return getFiles(folder);
	}

	public static List<File> getFiles(File folder) {
		return new ArrayList<File>(Arrays.asList(folder.listFiles()));
	}
}
