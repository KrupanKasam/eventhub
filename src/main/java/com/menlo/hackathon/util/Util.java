package com.menlo.hackathon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class Util {

	private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_VALUE = "application/json; charset=utf-8";

	private Util() {
	}

	/**
	 * Utility for checking empty string
	 * 
	 * @param String
	 * @return
	 */
	public static boolean isNull(String object) {
		if (null == object || object.trim().isEmpty())
			return true;
		return false;
	}

	/**
	 * Utility for checking empty object
	 * 
	 * @param object
	 * @return
	 */

	public static boolean isNull(Object object) {
		if (null == object) {
			return true;
		}
		return false;
	}

	/**
	 * Log timeSpent for methodName and component from startTimeInMS.
	 * 
	 * @param methodName
	 * @param component
	 * @param startTimeInMS
	 */
	public static void timeSpent(String methodName, String component, final long startTimeInMS, Level loggerLevel) {
		if (methodName == null) {
			throw new IllegalArgumentException("Invlid methodName");
		}
		if (component == null) {
			throw new IllegalArgumentException("Invlid component");
		}
		// Measuring time spent
		StringBuilder timeSpentInfo = new StringBuilder(methodName).append(" Took total ")
				.append((System.currentTimeMillis() - startTimeInMS) / 1000D).append(" seconds ");
		if (!component.trim().isEmpty()) {
			timeSpentInfo.append(" ").append(component);
		}
		if (loggerLevel.equals(Level.INFO)) {
			LOGGER.info(timeSpentInfo.toString());
		} else {
			LOGGER.debug(timeSpentInfo.toString());
		}
	}

}
