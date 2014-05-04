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

package org.elasticspring.context.support.io;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class ResourceLoaderTest {


	@Test
	public void testInjectionForFields() throws Exception {

		StaticApplicationContext staticApplicationContext = new StaticApplicationContext();
		AnnotationConfigUtils.registerAnnotationConfigProcessors(staticApplicationContext);

		ResourceLoader resourceLoader = mock(ResourceLoader.class);
		Resource resource = mock(Resource.class);
		when(resourceLoader.getResource("s3://bucket/object")).thenReturn(resource);

		BeanDefinitionBuilder beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ResourceLoaderBeanPostProcessor.class);
		beanDefinition.addConstructorArgValue(resourceLoader);

		staticApplicationContext.registerBeanDefinition("beanPostProcessor", beanDefinition.getBeanDefinition());
		staticApplicationContext.registerSingleton("client", FieldInjectionTarget.class);
		staticApplicationContext.refresh();

		FieldInjectionTarget fieldInjectionTarget = staticApplicationContext.getBean(FieldInjectionTarget.class);
		assertNotNull(fieldInjectionTarget.getApplicationContext());
		Resource applicationContextResource = fieldInjectionTarget.getApplicationContext().getResource("s3://bucket/object");
		assertSame(resource, applicationContextResource);

		assertNotNull(fieldInjectionTarget.getResourceLoader());
		Resource resourceLoaderResource = fieldInjectionTarget.getResourceLoader().getResource("s3://bucket/object");
		assertSame(resource, resourceLoaderResource);
	}

	@Test
	public void testInjectionForMethods() throws Exception {

		StaticApplicationContext staticApplicationContext = new StaticApplicationContext();
		AnnotationConfigUtils.registerAnnotationConfigProcessors(staticApplicationContext);

		ResourceLoader resourceLoader = mock(ResourceLoader.class);
		Resource resource = mock(Resource.class);
		when(resourceLoader.getResource("s3://bucket/object")).thenReturn(resource);

		BeanDefinitionBuilder beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ResourceLoaderBeanPostProcessor.class);
		beanDefinition.addConstructorArgValue(resourceLoader);

		staticApplicationContext.registerBeanDefinition("beanPostProcessor", beanDefinition.getBeanDefinition());
		staticApplicationContext.registerSingleton("client", MethodInjectionTarget.class);
		staticApplicationContext.refresh();

		MethodInjectionTarget methodInjectionTarget = staticApplicationContext.getBean(MethodInjectionTarget.class);
		assertNotNull(methodInjectionTarget.getApplicationContext());
		Resource applicationContextResource = methodInjectionTarget.getApplicationContext().getResource("s3://bucket/object");
		assertSame(resource, applicationContextResource);

		assertNotNull(methodInjectionTarget.getResourceLoader());
		Resource resourceLoaderResource = methodInjectionTarget.getResourceLoader().getResource("s3://bucket/object");
		assertSame(resource, resourceLoaderResource);
	}

	@Test
	public void testInjectionForConstructor() throws Exception {

		StaticApplicationContext staticApplicationContext = new StaticApplicationContext();
		AnnotationConfigUtils.registerAnnotationConfigProcessors(staticApplicationContext);

		ResourceLoader resourceLoader = mock(ResourceLoader.class);
		Resource resource = mock(Resource.class);
		when(resourceLoader.getResource("s3://bucket/object")).thenReturn(resource);

		BeanDefinitionBuilder beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ResourceLoaderBeanPostProcessor.class);
		beanDefinition.addConstructorArgValue(resourceLoader);

		staticApplicationContext.registerBeanDefinition("beanPostProcessor", beanDefinition.getBeanDefinition());
		staticApplicationContext.registerSingleton("client", ConstructorInjectionTarget.class);
		staticApplicationContext.refresh();

		ConstructorInjectionTarget constructorInjectionTarget = staticApplicationContext.getBean(ConstructorInjectionTarget.class);
		assertNotNull(constructorInjectionTarget.getApplicationContext());
		Resource applicationContextResource = constructorInjectionTarget.getApplicationContext().getResource("s3://bucket/object");
		assertSame(resource, applicationContextResource);

		assertNotNull(constructorInjectionTarget.getResourceLoader());
		Resource resourceLoaderResource = constructorInjectionTarget.getResourceLoader().getResource("s3://bucket/object");
		assertSame(resource, resourceLoaderResource);
	}

	@Test
	public void testResourceLoaderAwareBean() throws Exception {
		StaticApplicationContext staticApplicationContext = new StaticApplicationContext();

		ResourceLoader resourceLoader = mock(ResourceLoader.class);
		Resource resource = mock(Resource.class);
		when(resourceLoader.getResource("s3://bucket/object")).thenReturn(resource);

		BeanDefinitionBuilder beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ResourceLoaderBeanPostProcessor.class);
		beanDefinition.addConstructorArgValue(resourceLoader);

		staticApplicationContext.registerBeanDefinition("beanPostProcessor", beanDefinition.getBeanDefinition());
		staticApplicationContext.registerSingleton("client", ResourceLoaderAwareBean.class);
		staticApplicationContext.refresh();

		ResourceLoaderAwareBean resourceLoaderAwareBean = staticApplicationContext.getBean(ResourceLoaderAwareBean.class);
		assertNotNull(resourceLoaderAwareBean.getResourceLoader());
		Resource resourceLoaderResource = resourceLoaderAwareBean.getResourceLoader().getResource("s3://bucket/object");
		assertSame(resource, resourceLoaderResource);
	}

	@Test
	public void testApplicationContextAwareBean() throws Exception {
		StaticApplicationContext staticApplicationContext = new StaticApplicationContext();

		ResourceLoader resourceLoader = mock(ResourceLoader.class);
		Resource resource = mock(Resource.class);
		when(resourceLoader.getResource("s3://bucket/object")).thenReturn(resource);

		BeanDefinitionBuilder beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ResourceLoaderBeanPostProcessor.class);
		beanDefinition.addConstructorArgValue(resourceLoader);

		staticApplicationContext.registerBeanDefinition("beanPostProcessor", beanDefinition.getBeanDefinition());
		staticApplicationContext.registerSingleton("client", ApplicationContextAwareBean.class);
		staticApplicationContext.refresh();

		ApplicationContextAwareBean applicationContextAware = staticApplicationContext.getBean(ApplicationContextAwareBean.class);
		assertNotNull(applicationContextAware.getApplicationContext());
		Resource resourceLoaderResource = applicationContextAware.getApplicationContext().getResource("s3://bucket/object");
		assertSame(resource, resourceLoaderResource);
	}


	@Test
	public void testWithCustomResourceLoaderForApplicationContext() throws Exception {
		StaticApplicationContext staticApplicationContext = new StaticApplicationContext();
		ResourceLoader resourceLoader = mock(ResourceLoader.class);

		Resource resource = mock(Resource.class);
		when(resourceLoader.getResource("s3://bucket/object")).thenReturn(resource);

		staticApplicationContext.setResourceLoader(resourceLoader);
		staticApplicationContext.registerSingleton("client", ApplicationContextAwareBean.class);

		staticApplicationContext.refresh();

		ApplicationContextAwareBean applicationContextAwareBean = staticApplicationContext.getBean(ApplicationContextAwareBean.class);
		Resource resourceLoaderResource = applicationContextAwareBean.getApplicationContext().getResource("s3://bucket/object");
		assertNotNull(resourceLoaderResource);
		assertSame(resource, resourceLoaderResource);
	}

	private static final class FieldInjectionTarget {

		@Autowired
		private ApplicationContext applicationContext;

		@SuppressWarnings("SpringJavaAutowiringInspection")
		@Autowired
		private ResourceLoader resourceLoader;

		public ApplicationContext getApplicationContext() {
			return this.applicationContext;
		}

		public ResourceLoader getResourceLoader() {
			return this.resourceLoader;
		}
	}

	private static final class MethodInjectionTarget {

		private ApplicationContext applicationContext;

		private ResourceLoader resourceLoader;

		public ApplicationContext getApplicationContext() {
			return this.applicationContext;
		}

		@Autowired
		public void setApplicationContext(ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}

		public ResourceLoader getResourceLoader() {
			return this.resourceLoader;
		}

		@Autowired
		public void setResourceLoader(@SuppressWarnings("SpringJavaAutowiringInspection") ResourceLoader resourceLoader) {
			this.resourceLoader = resourceLoader;
		}
	}

	private static final class ConstructorInjectionTarget {

		private final ResourceLoader resourceLoader;
		private final ApplicationContext applicationContext;

		@Autowired
		private ConstructorInjectionTarget(@SuppressWarnings("SpringJavaAutowiringInspection") ResourceLoader resourceLoader, ApplicationContext applicationContext) {
			this.resourceLoader = resourceLoader;
			this.applicationContext = applicationContext;
		}

		public ApplicationContext getApplicationContext() {
			return this.applicationContext;
		}

		public ResourceLoader getResourceLoader() {
			return this.resourceLoader;
		}
	}


	private static final class ResourceLoaderAwareBean implements ResourceLoaderAware {

		private ResourceLoader resourceLoader;

		public ResourceLoader getResourceLoader() {
			return this.resourceLoader;
		}

		@Override
		public void setResourceLoader(ResourceLoader resourceLoader) {
			this.resourceLoader = resourceLoader;
		}
	}

	private static final class ApplicationContextAwareBean implements ApplicationContextAware {

		private ApplicationContext applicationContext;

		public ApplicationContext getApplicationContext() {
			return this.applicationContext;
		}

		@Override
		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			this.applicationContext = applicationContext;
		}
	}
}
