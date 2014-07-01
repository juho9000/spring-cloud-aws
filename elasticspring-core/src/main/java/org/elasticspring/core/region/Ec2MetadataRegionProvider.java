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

package org.elasticspring.core.region;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.util.EC2MetadataUtils;

public class Ec2MetadataRegionProvider implements RegionProvider {

	@Override
	public Region getRegion() {
		String availabilityZone = getAvailabilityZone();
		if (availabilityZone == null) {
			throw new IllegalStateException("There is not EC2 meta data available, because the application is not running " +
					"in the EC2 environment. Region detection is only possible if the application is running on a EC2 instance");
		}

		for (Regions candidate : Regions.values()) {
			if (availabilityZone.startsWith(candidate.getName())) {
				return Region.getRegion(candidate);
			}
		}

		throw new IllegalStateException("There could be no region detected for the availability zone '" + availabilityZone + "'");
	}

	protected String getAvailabilityZone() {
		return EC2MetadataUtils.getAvailabilityZone();
	}
}