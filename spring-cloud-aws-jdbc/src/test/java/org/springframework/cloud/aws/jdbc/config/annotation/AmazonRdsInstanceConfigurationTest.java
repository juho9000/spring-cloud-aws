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

package org.springframework.cloud.aws.jdbc.config.annotation;

import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.Endpoint;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cloud.aws.jdbc.rds.AmazonRdsDataSourceFactoryBean;
import org.springframework.cloud.aws.jdbc.rds.AmazonRdsReadReplicaAwareDataSourceFactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class AmazonRdsInstanceConfigurationTest {

	private AnnotationConfigApplicationContext context;

	@After
	public void tearDown() throws Exception {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void configureBean_withDefaultClientSpecifiedAndNoReadReplica_configuresFactoryBeanWithoutReadReplica() throws Exception {
		//Arrange

		//Act
		this.context = new AnnotationConfigApplicationContext(ApplicationConfigurationWithoutReadReplica.class);

		//Assert
		assertNotNull(this.context.getBean(DataSource.class));
		assertNotNull(this.context.getBean(AmazonRdsDataSourceFactoryBean.class));


	}

	@Test
	public void configureBean_withDefaultClientSpecifiedAndReadReplica_configuresFactoryBeanWithReadReplicaEnabled() throws Exception {
		//Arrange

		//Act
		this.context = new AnnotationConfigApplicationContext(ApplicationConfigurationWithReadReplica.class);

		//Assert
		assertNotNull(this.context.getBean(DataSource.class));
		assertNotNull(this.context.getBean(AmazonRdsReadReplicaAwareDataSourceFactoryBean.class));
	}

	@EnableRdsInstance(dbInstanceIdentifier = "test",password = "secret")
	public static class ApplicationConfigurationWithoutReadReplica {

		@Bean
		public AmazonRDS amazonRds() {
			AmazonRDSClient client = Mockito.mock(AmazonRDSClient.class);
			when(client.describeDBInstances(new DescribeDBInstancesRequest().withDBInstanceIdentifier("test"))).thenReturn(
					new DescribeDBInstancesResult().
							withDBInstances(new DBInstance().
											withDBInstanceStatus("available").
											withDBName("test").
											withDBInstanceIdentifier("test").
											withEngine("mysql").
											withMasterUsername("admin").
											withEndpoint(new Endpoint().
															withAddress("localhost").
															withPort(3306)
											).withReadReplicaDBInstanceIdentifiers("read1")
							)
			);
			return client;
		}

	}

	@EnableRdsInstance(dbInstanceIdentifier = "test",password = "secret",readReplicaSupport = true)
	public static class ApplicationConfigurationWithReadReplica {

		@Bean
		public AmazonRDS amazonRds() {
			AmazonRDSClient client = Mockito.mock(AmazonRDSClient.class);
			when(client.describeDBInstances(new DescribeDBInstancesRequest().withDBInstanceIdentifier("test"))).thenReturn(
					new DescribeDBInstancesResult().
							withDBInstances(new DBInstance().
											withDBInstanceStatus("available").
											withDBName("test").
											withDBInstanceIdentifier("test").
											withEngine("mysql").
											withMasterUsername("admin").
											withEndpoint(new Endpoint().
															withAddress("localhost").
															withPort(3306)
											).withReadReplicaDBInstanceIdentifiers("read1")
							)
			);
			when(client.describeDBInstances(new DescribeDBInstancesRequest().withDBInstanceIdentifier("read1"))).thenReturn(
					new DescribeDBInstancesResult().
							withDBInstances(new DBInstance().
											withDBInstanceStatus("available").
											withDBName("read1").
											withDBInstanceIdentifier("read1").
											withEngine("mysql").
											withMasterUsername("admin").
											withEndpoint(new Endpoint().
															withAddress("localhost").
															withPort(3306)
											)
							)
			);
			return client;
		}

	}
}