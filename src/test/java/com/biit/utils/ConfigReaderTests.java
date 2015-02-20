package com.biit.utils;

import junit.framework.Assert;

import org.testng.annotations.Test;

import com.biit.utils.configuration.ConfigurationReader;
import com.biit.utils.configuration.PropertiesSourceFile;
import com.biit.utils.configuration.SystemVariablePropertiesSourceFile;
import com.biit.utils.configuration.exception.PropertyNotFoundException;

@Test(groups = { "configReader" })
public class ConfigReaderTests {
	
	private static final String PROPERTY_PATH = "sample.path";
	private static final String PROPERTY_INT = "sample.int";
	private static final String PROPERTY_DOUBLE = "sample.double";
	private static final String PROPERTY_BOOLEAN = "sample.boolean";
	private static final String PROPERTY_PATH_DEFAULT_VALUE = "default";
	private static final Integer PROPERTY_INT_DEFAULT_VALUE = new Integer(-1);
	private static final Double PROPERTY_DOUBLE_DEFAULT_VALUE = new Double(-2.0);
	private static final Boolean PROPERTY_BOOLEAN_DEFAULT_VALUE = true;
	
	private static final String PROPERTY_REWRITE_1 = "rewrite1.sample.path";
	private static final String PROPERTY_REWRITE_2 = "rewrite2.sample.int";
	private static final String PROPERTY_REWRITE_12 = "rewrite12.sample.double";
	private static final Object PROPERTY_REWRITE_1_DEFAULT_VALUE = "default/rewrite";
	private static final Object PROPERTY_REWRITE_2_DEFAULT_VALUE = new Integer(-15);
	private static final Object PROPERTY_REWRITE_12_DEFAULT_VALUE = new Double(-7.5);
	
	private static final String PROPERTY_NOT_USED = "not.used";
	
	private static final String FILE_1 = "settings.conf";
	private static final String FILE_2 = "settings2.conf";
	private static final String FILE_3 = "settings3.conf";
	private static final String PROPERTY_PATH_FILE_0 = "../test/test";
	private static final Integer PROPERTY_INT_FILE_0 = new Integer(1);
	private static final Double PROPERTY_DOUBLE_FILE_0 = new Double(2.0);
	private static final Boolean PROPERTY_BOOLEAN_FILE_0 = true;
	private static final String PROPERTY_REWRITE_1_FILE_1 = "../test2/test2";
	private static final Integer PROPERTY_REWRITE_2_FILE_2 = new Integer(3);
	private static final Double PROPERTY_REWRITE_12_FILE_2 = new Double(4.0);
	private static final String BAD_VARIABLE = "BAD_VARIABLE";
	private static final String BAD_FILE = "BAD_FILE";
	
	public class TestConfigurationReader extends ConfigurationReader{

		public TestConfigurationReader(){
			super();
			
			addProperty(PROPERTY_PATH, PROPERTY_PATH_DEFAULT_VALUE);
			addProperty(PROPERTY_INT, PROPERTY_INT_DEFAULT_VALUE);
			addProperty(PROPERTY_DOUBLE, PROPERTY_DOUBLE_DEFAULT_VALUE);
			addProperty(PROPERTY_BOOLEAN, PROPERTY_BOOLEAN_DEFAULT_VALUE);
			
			addProperty(PROPERTY_REWRITE_1, PROPERTY_REWRITE_1_DEFAULT_VALUE);
			addProperty(PROPERTY_REWRITE_2, PROPERTY_REWRITE_2_DEFAULT_VALUE);
			addProperty(PROPERTY_REWRITE_12, PROPERTY_REWRITE_12_DEFAULT_VALUE);
						
			addPropertiesSource(new PropertiesSourceFile(FILE_1));
			addPropertiesSource(new PropertiesSourceFile(FILE_2));
			addPropertiesSource(new PropertiesSourceFile(FILE_3));
			//Test a null bad variable file breaks the application.
			addPropertiesSource(new PropertiesSourceFile(BAD_FILE));
			addPropertiesSource(new SystemVariablePropertiesSourceFile(BAD_VARIABLE, FILE_1));
		}
	}

	@Test
	public void configRead() throws PropertyNotFoundException {
		
		TestConfigurationReader testConfigurationReader = new TestConfigurationReader();
		
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_PATH),PROPERTY_PATH_DEFAULT_VALUE);
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_INT,Integer.class),PROPERTY_INT_DEFAULT_VALUE);
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_DOUBLE,Double.class),PROPERTY_DOUBLE_DEFAULT_VALUE);
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_BOOLEAN,Boolean.class),PROPERTY_BOOLEAN_DEFAULT_VALUE);
		
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_REWRITE_1),PROPERTY_REWRITE_1_DEFAULT_VALUE);
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_REWRITE_2,Integer.class),PROPERTY_REWRITE_2_DEFAULT_VALUE);
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_REWRITE_12,Double.class),PROPERTY_REWRITE_12_DEFAULT_VALUE);
		
		System.out.println("<START of expected debug and info warning messages>");
		testConfigurationReader.readConfigurations();
		System.out.println("<END of expected debug and info warning messages>");
		
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_PATH),PROPERTY_PATH_FILE_0);
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_INT,Integer.class),PROPERTY_INT_FILE_0);
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_DOUBLE,Double.class),PROPERTY_DOUBLE_FILE_0);
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_BOOLEAN,Boolean.class),PROPERTY_BOOLEAN_FILE_0);
		
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_REWRITE_1),PROPERTY_REWRITE_1_FILE_1);
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_REWRITE_2,Integer.class),PROPERTY_REWRITE_2_FILE_2);
		Assert.assertEquals(testConfigurationReader.getProperty(PROPERTY_REWRITE_12,Double.class),PROPERTY_REWRITE_12_FILE_2);
	}
	
	@Test(expectedExceptions = { PropertyNotFoundException.class })
	public void configReadVariableNotDefined() throws PropertyNotFoundException {
		
		TestConfigurationReader testConfigurationReader = new TestConfigurationReader();
		
		System.out.println("<START of expected debug and info warning messages>");
		testConfigurationReader.readConfigurations();
		System.out.println("<END of expected debug and info warning messages>");
		
		testConfigurationReader.getProperty(PROPERTY_NOT_USED,Double.class);
	}
	
}
