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

package org.elasticspring.messaging;

import org.elasticspring.messaging.config.annotation.TopicListener;
import org.elasticspring.messaging.core.NotificationOperations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

/**
 * @author Agim Emruli
 * @since 1.0
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleNotificationServiceTest {

	@Autowired
	private NotificationOperations notificationOperations;

	@Autowired
	private NotificationReceiver notificationReceiver;

	@Test
	public void testConvertAndSendWithoutSubject() throws Exception {
		String payload = "Hello World";
		this.notificationOperations.convertAndSend(payload);
		this.notificationReceiver.getCountDownLatch().await();
		Assert.assertEquals(payload, this.notificationReceiver.getLastMessage());
	}


	static class NotificationReceiver {

		private final CountDownLatch countDownLatch = new CountDownLatch(1);
		private String lastMessage;

		@TopicListener(topicName = "#{testStackEnvironment.getByLogicalId('MySNSTopic')}",
				protocol = TopicListener.NotificationProtocol.SQS,
				endpoint = "#{testStackEnvironment.getByLogicalId('NotificationQueue')}")
		public void receiveNotification(String message) {
			this.countDownLatch.countDown();
			this.lastMessage = message;
		}

		CountDownLatch getCountDownLatch() {
			return this.countDownLatch;
		}

		String getLastMessage() {
			return this.lastMessage;
		}
	}
}