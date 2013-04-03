/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elasticspring.messaging.support.converter;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticspring.messaging.Message;

import java.io.IOException;

/**
 * @author Agim Emruli
 * @since 1.0
 */
public class NotificationMessageConverter implements MessageConverter {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Message<String> toMessage(Object payload) {
		throw new UnsupportedOperationException("This converter only supports reading a SNS notification and not writing them");
	}

	@Override
	public Object fromMessage(Message<String> message) {
		try {
			JsonNode jsonNode = this.objectMapper.readValue(message.getPayload(), JsonNode.class);
			if (!jsonNode.has("Type")) {
				throw new MessageConversionException("Payload: '" + message.getPayload() + "' does not contain a Type attribute");
			}

			if (!"Notification".equals(jsonNode.get("Type").getTextValue())) {
				throw new MessageConversionException("Payload: '" + message.getPayload() + "' is not a valid notification");
			}

			if (!jsonNode.has("Message")) {
				throw new MessageConversionException("Payload: '" + message.getPayload() + "' does not contain a message");
			}

			return jsonNode.get("Message").getTextValue();
		} catch (IOException e) {
			throw new MessageConversionException("Error reading payload :'" + message.getPayload() + "' from message", e);
		}
	}
}
