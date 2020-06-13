/*
 * Copyright 2013-2020 the original author or authors.
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

package org.springframework.cloud.aws.cache;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.AWSIntegration;
import org.springframework.cloud.aws.support.profile.AmazonWebserviceProfileValueSource;
import org.springframework.cloud.aws.support.profile.IfAmazonWebserviceEnvironment;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ProfileValueSourceConfiguration(AmazonWebserviceProfileValueSource.class)
@Category(AWSIntegration.class)
public abstract class ElastiCacheAwsTest {

	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	private CachingService cachingService;

	@Before
	public void resetInvocationCount() throws Exception {
		this.cachingService.resetInvocationCount();
	}

	@Test
	@IfAmazonWebserviceEnvironment
	public void expensiveServiceWithCacheManager() throws Exception {
		this.cachingService.deleteCacheKey("foo");
		this.cachingService.deleteCacheKey("bar");

		assertThat(this.cachingService.getInvocationCount().get()).isEqualTo(0);

		assertThat(this.cachingService.expensiveMethod("foo")).isEqualTo("FOO");
		assertThat(this.cachingService.getInvocationCount().get()).isEqualTo(1);

		assertThat(this.cachingService.expensiveMethod("foo")).isEqualTo("FOO");
		assertThat(this.cachingService.getInvocationCount().get()).isEqualTo(1);

		assertThat(this.cachingService.expensiveMethod("bar")).isEqualTo("BAR");
		assertThat(this.cachingService.getInvocationCount().get()).isEqualTo(2);
	}

	@Test
	@IfAmazonWebserviceEnvironment
	public void expensiveServiceWithRedisCacheManager() throws Exception {
		this.cachingService.deleteRedisCacheKey("foo");
		this.cachingService.deleteRedisCacheKey("bar");

		assertThat(this.cachingService.getInvocationCount().get()).isEqualTo(0);

		assertThat(this.cachingService.expensiveRedisMethod("foo")).isEqualTo("FOO");
		assertThat(this.cachingService.getInvocationCount().get()).isEqualTo(1);

		assertThat(this.cachingService.expensiveRedisMethod("foo")).isEqualTo("FOO");
		assertThat(this.cachingService.getInvocationCount().get()).isEqualTo(1);

		assertThat(this.cachingService.expensiveRedisMethod("bar")).isEqualTo("BAR");
		assertThat(this.cachingService.getInvocationCount().get()).isEqualTo(2);
	}

}
