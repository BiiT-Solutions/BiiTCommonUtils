package com.biit.logger;

import junit.framework.Assert;

import org.testng.annotations.Test;

@Test(groups = { "inheritance" })
public class InheritanceTest {

	@Test
	public void checkLoggerName() {
		BiitLogger childLogger = new ChildLogger();
		Assert.assertEquals(ChildLogger.class.getName(), childLogger.getLogger().getName());
	}
}
