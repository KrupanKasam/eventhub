package com.menlo.hackathon.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.menlo.hackathon.service.EventDataSendService;
import com.menlo.hackathon.util.Util;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class EventDataSendController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventDataSendController.class);

	@Autowired
	private EventDataSendService eventDataSendService;

	@ApiOperation(value = "Send Data To Azure EventHub", notes = "This API is used send data to azure eventhub")
	@ApiResponses({ // swagger - describe return status code
			@ApiResponse(code = 200, message = "Success. Request completed."),
			@ApiResponse(code = 400, message = "BAD Request"),
			@ApiResponse(code = 404, message = "Not Found - resource doesn't exist."),
			@ApiResponse(code = 500, message = "Internal Server error.") })
	@PostMapping("/send")
	public ResponseEntity<?> sendData() throws Exception {
		final String methodName = this.getClass().getSimpleName() + ": customerBalance()";
		final long startTimeInMS = System.currentTimeMillis();
		StringBuilder methodInfo = new StringBuilder();
		methodInfo.append("Send Data to EventHub");
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(Util.CONTENT_TYPE, Util.CONTENT_VALUE);
		try {
			LOGGER.info("{} {} Send Data to Azure EventHub ia started", methodName, methodInfo);
			eventDataSendService.sendEventData();
			LOGGER.info("{} {} Send Data to Azure EventHub ia completed", methodName, methodInfo);
			return new ResponseEntity<Void>(responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("{} {} Exception occured while send data to Azure EventHub.", methodName, methodInfo);
			throw e;
		} finally {
			Util.timeSpent(methodName, methodInfo.toString(), startTimeInMS, Level.INFO);
		}
	}
}
