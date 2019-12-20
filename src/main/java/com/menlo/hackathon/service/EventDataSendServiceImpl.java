package com.menlo.hackathon.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.menlo.hackathon.domain.TeleData;
import com.menlo.hackathon.util.Util;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;

@Service
public class EventDataSendServiceImpl implements EventDataSendService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventDataSendServiceImpl.class);

	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	public void sendEventData() throws EventHubException, ExecutionException, InterruptedException, IOException {
		final String methodName = this.getClass().getSimpleName() + ": customerBalance()";
		final long startTimeInMS = System.currentTimeMillis();
		StringBuilder methodInfo = new StringBuilder();
		methodInfo.append("Send Data to EventHub");
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(Util.CONTENT_TYPE, Util.CONTENT_VALUE);
		try {
			LOGGER.info("{} {} Send Data to Azure EventHub ia started", methodName, methodInfo);

			final ConnectionStringBuilder connStr = new ConnectionStringBuilder().setNamespaceName("credit-card-eh")
					.setEventHubName("fraud-detection").setSasKeyName("fraud-detection")
					.setSasKey("UAOnYEeTSYFk2inorGejrDoARbnyzvYJdFV4+JO19F4=");
			final Gson gson = new GsonBuilder().create();
			final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
			// Each EventHubClient instance spins up a new TCP/SSL connection, which is
			// expensive.
			// It is always a best practice to reuse these instances. The following sample
			// shows this.
			final EventHubClient ehClient = EventHubClient.createFromConnectionStringSync(connStr.toString(),
					executorService);
			try {
				for (int i = 0; i < 10; i++) {
					ObjectMapper mapper = new ObjectMapper();
					Resource resource = resourceLoader.getResource("classpath:teleco.txt");
					InputStream inputStream = resource.getInputStream();
					byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
					String data = new String(bdata, StandardCharsets.UTF_8);
					List<TeleData> teleDataList = Arrays.asList(mapper.readValue(data, TeleData[].class));
					teleDataList.forEach(teleData -> {
						byte[] payloadBytes = gson.toJson(teleData).getBytes(Charset.defaultCharset());
						EventData sendEvent = EventData.create(payloadBytes);
						// Send - not tied to any partition
						// Event Hubs service will round-robin the events across all Event Hubs
						// partitions.
						// This is the recommended & most reliable way to send to Event Hubs.
						LOGGER.info("{} ==> {}", teleData, sendEvent);
						try {
							ehClient.sendSync(sendEvent);
						} catch (EventHubException e) {
							e.printStackTrace();
						}
					});
				}
			} finally {
				ehClient.closeSync();
				executorService.shutdown();
			}
			LOGGER.info("{} {} Send Data to Azure EventHub ia completed", methodName, methodInfo);
		} catch (Exception e) {
			LOGGER.error("{} {} Exception occured while send data to Azure EventHub.", methodName, methodInfo);
			throw e;
		} finally {
			Util.timeSpent(methodName, methodInfo.toString(), startTimeInMS, Level.INFO);
		}
	}
}