/*
 * Copyright 2013-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.aws.cloudmap;

import java.util.Set;

import com.amazonaws.services.servicediscovery.AWSServiceDiscovery;
import com.amazonaws.util.StringUtils;
import org.reflections8.Reflections;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.aws.cloudmap.annotations.CloudMapRegistry;
import org.springframework.core.annotation.AnnotationUtils;

public class CloudMapRegistryAnnotationScanner {

	private static final Logger log = LoggerFactory.getLogger(CloudMapRegistryAnnotationScanner.class);

	private final AWSServiceDiscovery serviceDiscovery;

	private final String annotationBasePackage;

	public CloudMapRegistryAnnotationScanner(AWSServiceDiscovery serviceDiscovery, String annotationBasePackage) {
		this.serviceDiscovery = serviceDiscovery;
		this.annotationBasePackage = annotationBasePackage;
	}

	public void scanAndRegister() {
		Reflections reflections = new Reflections(this.annotationBasePackage, new TypeAnnotationsScanner());
		Set<Class<?>> annotatedTypes = reflections.getTypesAnnotatedWith(CloudMapRegistry.class, true);
		annotatedTypes.forEach(x -> {
			CloudMapRegistry cloudMapRegistry = AnnotationUtils.findAnnotation(x, CloudMapRegistry.class);
			CloudMapRegistryProperties cloudMapRegistryProperties = getRegistryProperties(cloudMapRegistry);
			if (cloudMapRegistryProperties != null) {
				new CloudMapRegistryService(this.serviceDiscovery, cloudMapRegistryProperties).registerInstances();
			}
		});
	}

	private CloudMapRegistryProperties getRegistryProperties(CloudMapRegistry cloudMapRegistry) {
		try {
			if (!StringUtils.isNullOrEmpty(cloudMapRegistry.serviceNameSpace())
					&& !StringUtils.isNullOrEmpty(cloudMapRegistry.service())) {
				CloudMapRegistryProperties properties = new CloudMapRegistryProperties();
				properties.setServiceNameSpace(cloudMapRegistry.serviceNameSpace());
				properties.setService(cloudMapRegistry.service());

				if (cloudMapRegistry.port() != 0) {
					properties.setPort(cloudMapRegistry.port());
				}
				if (!StringUtils.isNullOrEmpty(cloudMapRegistry.description())) {
					properties.setDescription(cloudMapRegistry.description());
				}
				if (!StringUtils.isNullOrEmpty(cloudMapRegistry.healthCheckProtocol())) {
					properties.setHealthCheckProtocol(cloudMapRegistry.healthCheckProtocol());
				}
				if (!StringUtils.isNullOrEmpty(cloudMapRegistry.healthCheckResourcePath())) {
					properties.setHealthCheckResourcePath(cloudMapRegistry.healthCheckResourcePath());
				}
				if (cloudMapRegistry.healthCheckThreshold() != 0) {
					properties.setHealthCheckThreshold(cloudMapRegistry.healthCheckThreshold());
				}
				return properties;
			}
		}
		catch (Exception e) {
			log.error("Error while fetching registry details {}", e.getMessage(), e);
		}

		return null;
	}

}
