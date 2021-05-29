package com.biit.utils.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;

import com.biit.logger.BiitCommonLogger;

public class FileReader {
	private static final String ICON_FOLDER = "icons";

	public static File getResource(String fileName) throws NullPointerException {
		return getResource(FileReader.class, fileName);
	}

	public static File getResource(Class<?> classWithResources, String fileName) throws NullPointerException {
		final URL url = classWithResources.getClassLoader().getResource(fileName);
		if (url != null) {
			BiitCommonLogger.info(FileReader.class,
					"Resource to read '" + fileName + "' found at url '" + url.toString() + "'.");
		} else {
			BiitCommonLogger.warning(FileReader.class, "Invalid resource '" + fileName + "'.");
		}
		File file = null;
		// Jetty load resource.
		try {
			// We use path to remove URI special codification that is not
			// allowed for File.
			final String path = URLDecoder.decode(url.getPath(), "UTF-8");
			file = new File(FileReader.convert2OsPath(url));
			// Apache load resource
			if (!file.exists()) {
				file = new File(path);
				// Resource inside a jar.
				if (path.contains(".jar!")) {
					BiitCommonLogger.info(FileReader.class, "Resource inside a jar. Copy to a temporal file.");
					// Copy to a temp file and return it.
					try {
						// Url has the absolute path with the correct
						// codification for an InputStream.
						final InputStream inputStream = url.openStream();
						try {
							if (inputStream != null) {
								final File tempFile = File.createTempFile(fileName, "_jar");
								// tempFile.deleteOnExit();
								final OutputStream os = new FileOutputStream(tempFile);
								final byte[] buffer = new byte[1024];
								int bytesRead;
								// read from is to buffer
								while ((bytesRead = inputStream.read(buffer)) != -1) {
									os.write(buffer, 0, bytesRead);
								}
								os.close();
								return tempFile;
							}
						} finally {
							try {
								inputStream.close();
							} catch (Exception e) {
								// Do nothing.
							}
						}
					} catch (IOException e1) {
						BiitCommonLogger.severe(FileReader.class.getName(), e1);
					}
				}
				if (!file.exists()) {
					BiitCommonLogger.severe(FileReader.class, "File not found '" + path + "'.");
				}
			}
		} catch (NullPointerException npe) {
			throw new NullPointerException("File '" + fileName + "' does not exist.");
		} catch (UnsupportedEncodingException ue) {
			BiitCommonLogger.errorMessageNotification(FileReader.class, ue);
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
		return new ImageIcon(FileReader.class.getClassLoader().getResource(
				File.separator + ICON_FOLDER + File.separator + iconFile));
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
		final File file = FileReader.getResource(resourceName);
		return readFileAsList(file);
	}

	/**
	 * Reads a resource text file and returns a list of strings (one line per
	 * string).
	 * 
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<String> getFileAsList(String filePath) throws FileNotFoundException {
		final File file = new File(filePath);
		return readFileAsList(file);
	}

	private static List<String> readFileAsList(File file) throws FileNotFoundException {
		final Scanner scanner = new Scanner(file, StandardCharsets.UTF_8.name());
		scanner.useDelimiter("\\A");
		final List<String> lines = new ArrayList<String>();
		while (scanner.hasNext()) {
			lines.add(scanner.next());
		}
		scanner.close();
		return lines;
	}

	public static String readFile(File file) throws FileNotFoundException {
		final Scanner scanner = new Scanner(file, "UTF-8");
		// Change delimiter or it will removes all white spaces.
		scanner.useDelimiter("\\A");
		final StringBuilder content = new StringBuilder();
		while (scanner.hasNext()) {
			content.append(scanner.next().trim());
		}
		scanner.close();
		return content.toString();
	}

	public static String getResource(String fileName, Charset charset) throws FileNotFoundException {
		final StringBuilder result = new StringBuilder();
		// Get file from resources folder
		try {
			final File file = getResource(fileName);
			try {
				for (final String line : Files.readAllLines(file.toPath(), charset)) {
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
		final URL url = new URL(urlname);
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			final byte[] chunk = new byte[4096];
			int bytesRead;
			final InputStream stream = url.openStream();

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
		final URL url = FileReader.class.getClassLoader().getResource(folderPath);
		try {
			if (url != null) {
				final List<File> files = new ArrayList<>();
				BiitCommonLogger.debug(FileReader.class,
						"Resource to read '" + folderPath + "' found at url '" + url.toString() + "'.");
				final URI uri = url.toURI();
				if (uri.getScheme().equals("jar")) {
					// Remove 'file:' and '!' in 'jar!'
					final File jarFile = new File(url.getPath()
							.substring(5, url.toString().indexOf("jar!") - 1));
					try (final JarFile jar = new JarFile(jarFile)) {
						// gives ALL entries in jar
						final Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements()) {
							final JarEntry jarEntry = entries.nextElement();
							// filter according to the path
							if (jarEntry.getName().startsWith(folderPath + "/")
									&& !jarEntry.getName().endsWith(folderPath + "/")) {
								files.add(getResource(jarEntry.getName().substring(
										jarEntry.getName().indexOf(folderPath))));
							}
						}
						return files;
					}
				} else {
					final File[] fileList = new File(url.getPath()).listFiles();
					if (fileList != null) {
						return Arrays.asList(fileList);
					}
				}
			}
		} catch (IOException | URISyntaxException e) {
			BiitCommonLogger.errorMessageNotification(FileReader.class, e);
		}
		BiitCommonLogger.severe(FileReader.class, "Resource folder not found '" + folderPath + "'.");
		return new ArrayList<>();
	}

	public static List<File> getFiles(String folderPath) {
		final File folder = new File(folderPath);
		return getFiles(folder);
	}

	public static List<File> getFiles(File folder) {
		final File[] fileList = folder.listFiles();
		if (fileList != null) {
			return new ArrayList<>(Arrays.asList(fileList));
		}
		return new ArrayList<>();
	}

	/**
	 * Determine whether a file is a JAR File.
	 */
	public static boolean isJarFile(String path) throws IOException {
		return isJarFile(new File(path));
	}

	/**
	 * Determine whether a file is a JAR File.
	 */
	public static boolean isJarFile(File file) throws IOException {
		if (!isZipFile(file)) {
			return false;
		}
		final ZipFile zip = new ZipFile(file);
		final boolean manifest = zip.getEntry("META-INF/MANIFEST.MF") != null;
		zip.close();
		return manifest;
	}

	/**
	 * Determine whether a file is a ZIP File.
	 */
	public static boolean isZipFile(File file) throws IOException {
		if (file.isDirectory()) {
			return false;
		}
		if (!file.canRead()) {
			throw new IOException("Cannot read file " + file.getAbsolutePath());
		}
		if (file.length() < 4) {
			return false;
		}
		final DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		final int test = in.readInt();
		in.close();
		return test == 0x504b0304;
	}
}
