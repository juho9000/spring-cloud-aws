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

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.converter.MessageConversionException;
import org.springframework.messaging.support.converter.MessageConverter;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 */
public class MarshallingMessageConverter implements MessageConverter {

	private final Marshaller marshaller;
	private final Unmarshaller unmarshaller;
	private static final String DEFAULT_ENCODING = "UTF-8";

	private final String encoding;

	public MarshallingMessageConverter(Marshaller marshaller, Unmarshaller unmarshaller) {
		this(marshaller, unmarshaller, DEFAULT_ENCODING);
	}

	public MarshallingMessageConverter(Marshaller marshaller, Unmarshaller unmarshaller, String encoding) {
		this.marshaller = marshaller;
		this.unmarshaller = unmarshaller;
		this.encoding = Charset.forName(encoding).name();
	}

	@Override
	public Message<String> toMessage(Object payload, MessageHeaders header) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		StreamResult streamResult = new StreamResult(byteArrayOutputStream);
		try {
			this.marshaller.marshal(payload, streamResult);
			String messagePayload = byteArrayOutputStream.toString(this.encoding);
			return MessageBuilder.withPayload(messagePayload).build();
		} catch (IOException e) {
			throw new MessageConversionException(String.format("Error converting payload %s to Message", payload), e);
		}
	}

	@Override
	public Object fromMessage(Message<?> message, Class<?> targetClass) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(message.getPayload().toString().getBytes(this.encoding));
			return this.unmarshaller.unmarshal(new StreamSource(inputStream));
		} catch (IOException e) {
			throw new MessageConversionException(String.format("Error converting message %s to object", message), e);
		}
	}
}