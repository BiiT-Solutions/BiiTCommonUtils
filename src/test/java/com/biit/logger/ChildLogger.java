package com.biit.logger;

import org.apache.log4j.Logger;

public class ChildLogger extends BiitLogger {
	static {
		setLogger(Logger.getLogger(new Object() {
		}.getClass().getEnclosingClass()));
	}

}
