/*
 * Copyright 2010-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elasticspring.context.config.xml;

import com.amazonaws.auth.AWSCredentialsProvider;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * {@link org.springframework.beans.factory.xml.NamespaceHandler} implementation for the ElasticSpring context
 * namespace.
 *
 * @author Agim Emruli
 * @since 1.0
 */
public class ContextNamespaceHandler extends NamespaceHandlerSupport {

	public static final String DEFAULT_CREDENTIALS_PROVIDER_BEAN_NAME = AWSCredentialsProvider.class.getName();
	static final String RESOURCE_LOADER_BEAN_NAME = "resourceLoader";

	@Override
	public void init() {
		registerBeanDefinitionParser("context-credentials", new CredentialsBeanDefinitionParser());
		registerBeanDefinitionParser("context-region", new RegionProviderBeanDefinitionParser());
		registerBeanDefinitionParser("context-resource-loader", new SimpleStorageLoaderBeanDefinitionParser());
	}
}