package com.biit.utils.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.ImageIcon;

import com.biit.utils.logger.CommonUtilsLogger;

public class FileReader {
	private final static String ICON_FOLDER = "icons";

	public static File getResource(String filename) throws NullPointerException {
		URL url = FileReader.class.getClassLoader().getResource(filename);
		File file = null;
		// Jetty load resource.
		try {
			file = new File(FileReader.convert2OsPath(url));
			// Apache load resource
			if (!file.exists()) {
				try {
					file = new File(url.toURI());
				} catch (URISyntaxException e) {
					CommonUtilsLogger.errorMessageNotification(FileReader.class.getName(), "File not found: "
							+ FileReader.convert2OsPath(url));
				} catch (IllegalArgumentException e) {
					CommonUtilsLogger.severe(FileReader.class.getName(),
							"File not found: " + FileReader.convert2OsPath(url));
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
				return (string.getPath()).substring(1);
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
		File file = FileReader.getResource(resourceName);
		return readFile(file);
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
		return readFile(file);
	}

	private static List<String> readFile(File file) throws FileNotFoundException {
		Scanner s = new Scanner(file);
		List<String> lines = new ArrayList<String>();
		while (s.hasNext()) {
			lines.add(s.next());
		}
		s.close();
		return lines;
	}

	/**
	 * Reads a resource text file and returns String
	 * 
	 * @param resourceName
	 * @return
	 * @throws FileNotFoundException
	 */
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

}
