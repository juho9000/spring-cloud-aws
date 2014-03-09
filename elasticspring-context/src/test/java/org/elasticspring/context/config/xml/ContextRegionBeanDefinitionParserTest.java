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

package org.elasticspring.context.config.xml;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.elasticspring.core.region.Ec2MetadataRegionProvider;
import org.elasticspring.core.region.RegionPostProcessor;
import org.elasticspring.core.region.RegionProvider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Agim Emruli
 */
public class ContextRegionBeanDefinitionParserTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void parse_staticConfiguredRegion_createsStaticRegionProviderWithSpecifiedRegion() throws Exception {
		//Arrange
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-context.xml", getClass());

		//Act
		RegionProvider myRegionProvider = context.getBean(ContextRegionBeanDefinitionParser.CONTEXT_REGION_PROVIDER_BEAN_NAME,RegionProvider.class);
		RegionPostProcessor regionPostProcessor = context.getBean(RegionPostProcessor.class);

		//Assert
		Assert.assertNotNull(myRegionProvider);
		Assert.assertEquals(Region.getRegion(Regions.SA_EAST_1), myRegionProvider.getRegion());
		Assert.assertNotNull(regionPostProcessor);
	}

	@Test
	public void parse_staticConfiguredRegion_createsStaticRegionProviderWithSpecifiedRegionAsExpression() throws Exception {
		//Arrange
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-testWithExpression.xml", getClass());

		//Act
		RegionProvider myRegionProvider = context.getBean(ContextRegionBeanDefinitionParser.CONTEXT_REGION_PROVIDER_BEAN_NAME, RegionProvider.class);
		RegionPostProcessor regionPostProcessor = context.getBean(RegionPostProcessor.class);

		//Assert
		Assert.assertNotNull(myRegionProvider);
		Assert.assertEquals(Region.getRegion(Regions.SA_EAST_1), myRegionProvider.getRegion());
		Assert.assertNotNull(regionPostProcessor);
	}

	@Test
	public void parse_staticConfiguredRegion_createsStaticRegionProviderWithSpecifiedRegionAsPlaceHolder() throws Exception {
		//Arrange
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-testWithPlaceHolder.xml", getClass());

		//Act
		RegionProvider myRegionProvider = context.getBean(ContextRegionBeanDefinitionParser.CONTEXT_REGION_PROVIDER_BEAN_NAME, RegionProvider.class);
		RegionPostProcessor regionPostProcessor = context.getBean(RegionPostProcessor.class);

		//Assert
		Assert.assertNotNull(myRegionProvider);
		Assert.assertEquals(Region.getRegion(Regions.SA_EAST_1), myRegionProvider.getRegion());
		Assert.assertNotNull(regionPostProcessor);
	}

	@Test
	public void parse_autoDetectRegion_returnEc2MetadataRegionProvider() throws Exception {
		//Arrange
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-testAutoDetection.xml", getClass());

		//Act
		RegionProvider myRegionProvider = context.getBean(ContextRegionBeanDefinitionParser.CONTEXT_REGION_PROVIDER_BEAN_NAME,RegionProvider.class);
		RegionPostProcessor regionPostProcessor = context.getBean(RegionPostProcessor.class);

		//Assert
		Assert.assertNotNull(myRegionProvider);
		Assert.assertTrue(myRegionProvider instanceof Ec2MetadataRegionProvider);
		Assert.assertNotNull(regionPostProcessor);
	}

	@Test
	public void parse_customRegionProvider_returnAliasToCustomRegionProvider() throws Exception {
		//Arrange
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-testCustomRegionProvider.xml", getClass());

		//Act
		RegionProvider defaultRegionProvider = context.getBean(ContextRegionBeanDefinitionParser.CONTEXT_REGION_PROVIDER_BEAN_NAME,RegionProvider.class);
		RegionProvider myRegionProvider = context.getBean("myRegionProvider",RegionProvider.class);
		RegionPostProcessor regionPostProcessor = context.getBean(RegionPostProcessor.class);

		//Assert
		Assert.assertNotNull(defaultRegionProvider);
		Assert.assertNotNull(myRegionProvider);
		Assert.assertSame(defaultRegionProvider, myRegionProvider);
		Assert.assertNotNull(regionPostProcessor);
	}

	@Test
	public void parse_autoDetectionAndStaticRegionProvider_reportsError() throws Exception {
		//Arrange
		this.expectedException.expect(BeanDefinitionParsingException.class);
		this.expectedException.expectMessage("The attribute 'auto-detect' can only be enabled without a region or region-provider specified");

		//Act
		//noinspection ResultOfObjectAllocationIgnored
		new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-testAutoDetectionWithConfiguredRegion.xml", getClass());

		//Assert	
	}

	@Test
	public void parse_noValidRegionProviderConfigured_reportsError() throws Exception {
		//Arrange
		this.expectedException.expect(BeanDefinitionParsingException.class);
		this.expectedException.expectMessage("Either auto-detect must be enabled, or a region or region-provider must be specified");

		//Act
		//noinspection ResultOfObjectAllocationIgnored
		new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-testNoValidRegionProviderConfigurationSpecified.xml", getClass());

		//Assert
	}


	@Test
	public void parse_twoRegionProviderConfigured_reportsError() throws Exception {
		//Arrange
		this.expectedException.expect(BeanDefinitionParsingException.class);
		this.expectedException.expectMessage("Multiple <context-region/> elements detected. The <context-region/> element is only allowed once per application context");

		//Act
		//noinspection ResultOfObjectAllocationIgnored
		new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-testTwoRegionProviderConfigured.xml", getClass());

		//Assert
	}


}
