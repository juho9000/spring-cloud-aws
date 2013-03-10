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

import org.elasticspring.messaging.Message;
import org.elasticspring.messaging.StringMessage;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class StringMessageConverterTest {

	@Test
	public void testToMessage() throws Exception {
		StringMessageConverter stringMessageConverter = new StringMessageConverter();
		Message<String> message = stringMessageConverter.toMessage("content");
		Assert.assertEquals("content", message.getPayload());
	}


	@Test
	public void testFromMessage() throws Exception {
		StringMessageConverter stringMessageConverter = new StringMessageConverter();
		String content = stringMessageConverter.fromMessage(new StringMessage("content"));
		Assert.assertEquals("content", content);
	}

}
