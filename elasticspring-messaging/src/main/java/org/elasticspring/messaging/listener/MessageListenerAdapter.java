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

import org.elasticspring.messaging.Message;
import org.elasticspring.messaging.support.converter.MessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;

public class MessageListenerAdapter implements MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageListenerAdapter.class);
	private final MessageConverter messageConverter;
	private final Object delegate;
	private final String listenerMethod;

	public MessageListenerAdapter(MessageConverter messageConverter, Object delegate, String listenerMethod) {
		Assert.notNull(messageConverter, "messageConverter must not be null");
		Assert.notNull(delegate, "delegate must not be null");
		Assert.notNull(listenerMethod, "listenerMethod must not be null");
		this.messageConverter = messageConverter;
		this.delegate = delegate;
		this.listenerMethod = listenerMethod;
	}

	protected Object getDelegate() {
		return this.delegate;
	}

	protected String getListenerMethod() {
		return this.listenerMethod;
	}

	@Override
	public void onMessage(Message<String> message) {
		MethodInvoker methodInvoker = new MethodInvoker();
		methodInvoker.setTargetObject(this.delegate);
		methodInvoker.setTargetMethod(this.listenerMethod);
		Object param = this.messageConverter.fromMessage(message);

		prepareArguments(methodInvoker, param);

		try {
			LOGGER.debug("Preparing method invoker for object {} and method {} with argument(s) {}", this.delegate, this.listenerMethod, methodInvoker.getArguments());
			methodInvoker.prepare();
		} catch (ClassNotFoundException e) {
			throw new ListenerExecutionFailedException(e);
		} catch (NoSuchMethodException e) {
			throw new ListenerExecutionFailedException(e);
		}

		try {
			methodInvoker.invoke();
		} catch (InvocationTargetException e) {
			throw new ListenerExecutionFailedException(e.getTargetException());
		} catch (IllegalAccessException e) {
			throw new ListenerExecutionFailedException(e.getCause());
		}
	}

	protected void prepareArguments(MethodInvoker methodInvoker, Object payload) {
		methodInvoker.setArguments(new Object[]{payload});
	}
}