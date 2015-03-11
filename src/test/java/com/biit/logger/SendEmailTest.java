package com.biit.logger;

import org.testng.annotations.Test;

@Test(groups = { "sendEmail" })
public class SendEmailTest {

	@Test
	public void checkLoggerName() {
		ChildLogger.errorMessage(SendEmailTest.class.getName(), BiitLogger.getStackTrace(new Exception(
				"Catastrophic Error: Pray Anything you can before the end of the world.")));
	}
}
