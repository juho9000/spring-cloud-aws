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

package org.elasticspring.jdbc.datasource;

public final class DataSourceInformation {

	private final DatabaseType dataSourceClass;
	private final String hostName;
	private final Integer port;
	private final String databaseName;
	private final String userName;
	private final String password;

	public DataSourceInformation(DatabaseType dataSourceClass, String hostName, Integer port, String databaseName, String userName, String password) {
		this.dataSourceClass = dataSourceClass;
		this.hostName = hostName;
		this.port = port;
		this.databaseName = databaseName;
		this.userName = userName;
		this.password = password;
	}

	public DatabaseType getDataSourceClass() {
		return this.dataSourceClass;
	}

	public String getHostName() {
		return this.hostName;
	}

	public Integer getPort() {
		return this.port;
	}

	public String getDatabaseName() {
		return this.databaseName;
	}

	public String getUserName() {
		return this.userName;
	}

	public String getPassword() {
		return this.password;
	}

	@Override
	public int hashCode() {
		int result = this.dataSourceClass.hashCode();
		result = 31 * result + this.hostName.hashCode();
		result = 31 * result + this.port.hashCode();
		result = 31 * result + this.databaseName.hashCode();
		result = 31 * result + this.userName.hashCode();
		result = 31 * result + this.password.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DataSourceInformation)) {
			return false;
		}

		DataSourceInformation that = (DataSourceInformation) obj;

		if (this.dataSourceClass != that.getDataSourceClass()) {
			return false;
		}
		if (!this.databaseName.equals(that.getDatabaseName())) {
			return false;
		}
		if (!this.hostName.equals(that.getHostName())) {
			return false;
		}
		if (!this.password.equals(that.getPassword())) {
			return false;
		}
		if (!this.port.equals(that.getPort())) {
			return false;
		}
		return this.userName.equals(that.getUserName());

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DataSourceInformation");
		sb.append("{dataSourceClass=").append(this.dataSourceClass);
		sb.append(", hostName='").append(this.hostName).append("'");
		sb.append(", port=").append(this.port);
		sb.append(", databaseName='").append(this.databaseName).append("'");
		sb.append(", userName='").append(this.userName).append("'");
		sb.append(", password='").append(this.password).append("'");
		sb.append("}");
		return sb.toString();
	}

	public enum DatabaseType {
		MYSQL,
		ORACLE,
		MSSQL
	}
}
