/*
 * Copyright 2013-2014 the original author or authors.
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

package org.springframework.cloud.aws.context.config.annotation;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.cloud.aws.context.annotation.ConditionalOnAwsCloudEnvironment;
import org.springframework.cloud.aws.context.config.AmazonEc2InstanceDataPropertySourcePostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Agim Emruli
 */
@SuppressWarnings("NonFinalUtilityClass")
@Configuration
@ConditionalOnAwsCloudEnvironment
public class ContextInstanceDataConfiguration {

	@Bean
	public static BeanFactoryPostProcessor instanceDataPostProcessor() {
		return new AmazonEc2InstanceDataPropertySourcePostProcessor();
	}
}