package com.menlo.hackathon.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.microsoft.azure.eventhubs.EventHubException;

public interface EventDataSendService {

	public void sendEventData() throws EventHubException, ExecutionException, InterruptedException, IOException;

}
