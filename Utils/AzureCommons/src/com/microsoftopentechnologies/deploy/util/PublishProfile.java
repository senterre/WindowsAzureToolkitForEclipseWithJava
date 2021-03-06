/**
* Copyright 2015 Microsoft Open Technologies, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*	 http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package com.microsoftopentechnologies.deploy.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.microsoftopentechnologies.model.Subscription;
/* Class needs to be removed after 2.5.1 release.
 * Class is added just to take care of project upgrade scenario.
 */
@XmlType
public class PublishProfile implements Serializable, Cloneable {
	private static final long serialVersionUID = -6569753234382033009L;
	private String publishMethod;
	private String url;
	private String managementCertificate;
	private List<Subscription> subscriptions = new ArrayList<Subscription>();
	private String password = "";
	private String schemaVersion;

	@XmlAttribute(name = "PublishMethod")
	public String getPublishMethod() {
		return publishMethod;
	}

	public void setPublishMethod(String publishMethod) {
		this.publishMethod = publishMethod;
	}

	@XmlAttribute(name = "Url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@XmlAttribute(name = "ManagementCertificate")
	public String getManagementCertificate() {
		return managementCertificate;
	}

	public void setManagementCertificate(String managementCertificate) {
		this.managementCertificate = managementCertificate;
	}

	@XmlAttribute(name = "SchemaVersion")
	public String getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	@XmlElement(name = "Subscription")
	public synchronized List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public synchronized void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	@XmlElement(name = "Password")
	public synchronized String getPassword() {
		return this.password;
	}

	public void setPassword(String pfxPassword) {
		this.password = pfxPassword;
	}
}