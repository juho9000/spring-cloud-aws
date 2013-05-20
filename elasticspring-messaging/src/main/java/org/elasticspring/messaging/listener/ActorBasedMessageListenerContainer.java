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

package org.elasticspring.messaging.listener;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.dispatch.Futures;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.typesafe.config.ConfigFactory;
import org.elasticspring.messaging.StringMessage;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.util.Assert;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author Agim Emruli
 * @since 1.0
 */
public class ActorBasedMessageListenerContainer extends AbstractMessageListenerContainer implements BeanClassLoaderAware {

	private ActorSystem actorSystem;
	private Cancellable cancellable;
	private ClassLoader classLoader;
	private String configFile;

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		Assert.notNull(getBeanName(), "beanName must not be null, please use this class inside a " +
				"bean factory or set the bean name manually");

		if (this.configFile != null) {
			Assert.notNull(this.classLoader, "classLoader must not be null, please use this class inside a " +
					"bean factory or set the class loader manually");
			this.actorSystem = ActorSystem.create(getBeanName(),
					ConfigFactory.load(this.classLoader, this.configFile), this.classLoader);
		} else {
			this.actorSystem = ActorSystem.create(getBeanName());
		}
	}

	@Override
	protected void doStart() {
		ActorRef actorRef = this.actorSystem.actorOf(new Props(new UntypedActorFactory() {

			@Override
			public Actor create() throws Exception {
				return new AsynchronousMessageListener(getAmazonSqs(), getReceiveMessageRequest());
			}
		}), "messageListener");

		this.cancellable = this.actorSystem.scheduler().schedule(Duration.Zero(), Duration.apply(0, TimeUnit.SECONDS),
				actorRef, "scheduled", this.actorSystem.dispatcher());
	}

	@Override
	protected void doStop() {
		if (this.cancellable != null) {
			this.cancellable.cancel();
		}
		if (this.actorSystem != null) {
			this.actorSystem.shutdown();
			this.actorSystem.awaitTermination();
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		getAmazonSqs().shutdown();
	}

	private class AsynchronousMessageListener extends UntypedActor {

		private final AmazonSQS amazonSQS;
		private final ReceiveMessageRequest receiveMessageRequest;

		private AsynchronousMessageListener(AmazonSQS amazonSQS, ReceiveMessageRequest receiveMessageRequest) {
			this.amazonSQS = amazonSQS;
			this.receiveMessageRequest = receiveMessageRequest;
		}


		@Override
		public void preStart() {
			getContext().actorOf(new Props(new UntypedActorFactory() {

				@Override
				public Actor create() throws Exception {
					return new MessageExecutor(getMessageListener(), getDestinationResolver().resolveDestinationName(getDestinationName()));
				}
			}), "messageExecutor");
		}

		@Override
		public void onReceive(Object message) throws Exception {
			ReceiveMessageResult receiveMessageResult = this.amazonSQS.receiveMessage(this.receiveMessageRequest);
			if (!receiveMessageResult.getMessages().isEmpty()) {
				getSelf().tell("moreMessages", getSelf());
			}
			for (Message sqsMessage : receiveMessageResult.getMessages()) {
				getContext().actorFor("messageExecutor").tell(sqsMessage, getSelf());
			}
		}
	}

	private class MessageExecutor extends UntypedActor {

		private final MessageListener messageListener;
		private final String queueUrl;
		private final LoggingAdapter logging = Logging.getLogger(getContext().system(), getSelf());

		private MessageExecutor(MessageListener messageListener, String queueUrl) {
			this.messageListener = messageListener;
			this.queueUrl = queueUrl;
		}

		@Override
		public void onReceive(final Object message) throws Exception {
			if (message instanceof Message) {
				Future<Message> future = Futures.future(new Callable<Message>() {

					@Override
					public Message call() throws Exception {
						MessageExecutor.this.messageListener.onMessage(new StringMessage(((Message) message).getBody()));
						return (Message) message;
					}
				}, ActorBasedMessageListenerContainer.this.actorSystem.dispatcher());

				future.onSuccess(new OnSuccess<Message>() {

					@Override
					public void onSuccess(Message result) throws Throwable {
						getAmazonSqs().deleteMessage(new DeleteMessageRequest(MessageExecutor.this.queueUrl, result.getReceiptHandle()));
						MessageExecutor.this.logging.debug("Deleted message with id {} and receipt handle {}", result.getMessageId(), result.getReceiptHandle());
					}
				}, ActorBasedMessageListenerContainer.this.actorSystem.dispatcher());

				future.onFailure(new OnFailure() {

					@Override
					public void onFailure(Throwable failure) throws Throwable {
						handleError(failure);
					}
				}, ActorBasedMessageListenerContainer.this.actorSystem.dispatcher());
			}
		}
	}
}