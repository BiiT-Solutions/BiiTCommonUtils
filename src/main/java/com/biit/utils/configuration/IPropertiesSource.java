package com.biit.utils.configuration;

import java.util.Properties;

public interface IPropertiesSource {

    Properties loadFile();

    String getFilePath();

    String getFileName();

}
