package com.biit.utils.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileWriter {

	/**
	 * Writes a temporal file with a string content
	 * 
	 * @param prefix
	 *            prefix of the file
	 * @param sufix
	 *            sufix of the file
	 * @param string
	 *            string to write
	 * @return a file
	 * @throws IOException
	 */
	public static File writeInTempFile(String prefix, String sufix, String string) throws IOException {
		final File temp;
		temp = File.createTempFile(prefix, sufix);

		final FileOutputStream fileStream = new FileOutputStream(temp);
		final OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");

		writer.write(string);
		writer.close();
		return temp;
	}
}
