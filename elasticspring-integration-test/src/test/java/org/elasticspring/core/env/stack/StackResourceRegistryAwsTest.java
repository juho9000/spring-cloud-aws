package org.elasticspring.core.env.stack;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

// TODO discuss where which types of tests should live (e.g. tests requiring amazon environment)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("StackResourceRegistryAwsTest-context.xml")
public class StackResourceRegistryAwsTest {

	@Autowired
	@Qualifier("staticStackNameProviderBasedStackResourceRegistry")
	private StackResourceRegistry staticStackNameProviderBasedStackResourceRegistry;

	@Autowired
	@Qualifier("autoDetectingStackNameProviderBasedStackResourceRegistry")
	private StackResourceRegistry autoDetectingStackNameProviderBasedStackResourceRegistry;

	@Test
	public void stackResourceRegistry_staticStackNameProvider_stackResourceRegistryBeanExposed() {
		// Assert
		assertThat(this.staticStackNameProviderBasedStackResourceRegistry, is(not(nullValue())));
	}

	@Test
	public void stackResourceRegistry_autoDetectingStackNameProvider_stackResourceRegistryBeanExposed() {
		// Assert
		assertThat(this.autoDetectingStackNameProviderBasedStackResourceRegistry, is(not(nullValue())));
	}

	@Test
	public void lookupPhysicalResourceId_staticStackNameProviderAndLogicalResourceIdOfExistingResourceProvided_returnsPhysicalResourceId() {
		// Act
		String physicalResourceId = this.staticStackNameProviderBasedStackResourceRegistry.lookupPhysicalResourceId("RdsSingleMicroInstance");

		// Assert
		assertThat(physicalResourceId, is(not(nullValue())));
	}

	@Test
	public void lookupPhysicalResourceId_autoDetectingStackNameProviderAndLogicalResourceIdOfExistingResourceProvided_returnsPhysicalResourceId() {
		// Act
		String physicalResourceId = this.autoDetectingStackNameProviderBasedStackResourceRegistry.lookupPhysicalResourceId("RdsSingleMicroInstance");

		// Assert
		assertThat(physicalResourceId, is(not(nullValue())));
	}

	@Test
	public void lookupPhysicalResourceId_logicalResourceIdOfNonExistingResourceProvided_throwsException() {
		// Act
		String physicalResourceId = this.staticStackNameProviderBasedStackResourceRegistry.lookupPhysicalResourceId("nonExistingLogicalResourceId");

		// Assert
		assertThat(physicalResourceId, is(nullValue()));
	}

}
