package io.peltas.alfresco.workspace;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

import io.peltas.core.integration.PeltasIntegrationWriter.PeltasIntegrationMessage;

public class IntegrationServiceSample {

	@ServiceActivator(inputChannel = "peltas_item_execution")
	public void peltas_item_execution(Message<PeltasIntegrationMessage<Object>> message) {
		// use any custom business logic
	}

	@ServiceActivator(inputChannel = "peltas_collection_execution")
	public void peltas_collection_execution(Message<PeltasIntegrationMessage<Object>> message) {
		// use any custom business logic
	}
}
