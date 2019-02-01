/*
 * Copyright 2013-2019 the original author or authors.
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

package org.springframework.cloud.aws.cache;

import org.springframework.cloud.aws.IntegrationTestConfig;
import org.springframework.cloud.aws.cache.config.annotation.CacheClusterConfig;
import org.springframework.cloud.aws.cache.config.annotation.EnableElastiCache;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Agim Emruli
 */
@ContextConfiguration(classes = JavaElastiCacheAwsTest.JavaElastiCacheAwsTestConfig.class)
public class JavaElastiCacheAwsTest extends ElastiCacheAwsTest {

	@Configuration
	@EnableElastiCache(@CacheClusterConfig(name = "CacheCluster"))
	@Import(IntegrationTestConfig.class)
	@ComponentScan
	static class JavaElastiCacheAwsTestConfig {

	}

}
