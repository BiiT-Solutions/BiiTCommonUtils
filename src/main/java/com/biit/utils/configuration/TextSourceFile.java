package com.biit.utils.configuration;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.file.FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class TextSourceFile extends SourceFile<String> {

    public TextSourceFile(String fileName) {
        super(fileName);
    }

    public TextSourceFile(String filePath, String fileName) {
        super(filePath, fileName);
    }

    @Override
    public String loadFile() throws FileNotFoundException {
        try {
            if (getFilePath() == null) {
                // return PropertiesFile.load(getFileName());
                FileReader.getResource(getFileName(), StandardCharsets.UTF_8);
            } else {
                // return PropertiesFile.load(getFilePath(), getFileName());
                return readFile(getFilePath() + File.separator + getFileName(), StandardCharsets.UTF_8);
            }
        } catch (NoSuchFileException nsfe) {
            BiitCommonLogger.warning(this.getClass(), "File not found '" + getFilePath() + File.separator
                    + getFileName() + "'.");
        } catch (IOException e) {
            BiitCommonLogger.errorMessageNotification(this.getClass(), e);
        } catch (NullPointerException e) {
            BiitCommonLogger.info(this.getClass(), e.getMessage());
        }
        return null;
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        final byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
