package com.logger;

/**
 * 
 * @author XuanCui
 *
 */
public class Logger {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(Logger.class);

	public static void info(String tag, Object content) {
		log.info("[" + tag + "]:\t" + content);
	}

	public static void error(Exception e) {
		log.error(e.getMessage(), e);
	}

}