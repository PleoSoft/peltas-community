/**
 * Copyright 2019 Pleo Soft d.o.o. (pleosoft.com)

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.peltas.alfresco.workspace;

import java.util.List;
import java.util.Map;

public class AlfrescoNodeMetadata {
	Long id;
	String tenantDomain;
	String nodeRef;
	String type;
	Long aclId;
	Long txnId;
	Map<String, Object> properties;
	List<Object> paths;
	List<String> aspects;
	List<String> childAssocs;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTenantDomain() {
		return tenantDomain;
	}

	public void setTenantDomain(String tenantDomain) {
		this.tenantDomain = tenantDomain;
	}

	public String getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getAclId() {
		return aclId;
	}

	public void setAclId(Long aclId) {
		this.aclId = aclId;
	}

	public Long getTxnId() {
		return txnId;
	}

	public void setTxnId(Long txnId) {
		this.txnId = txnId;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public List<Object> getPaths() {
		return paths;
	}

	public void setPaths(List<Object> paths) {
		this.paths = paths;
	}

	public List<String> getChildAssocs() {
		return childAssocs;
	}

	public void setChildAssocs(List<String> childAssocs) {
		this.childAssocs = childAssocs;
	}

	public List<String> getAspects() {
		return aspects;
	}

	public void setAspects(List<String> aspects) {
		this.aspects = aspects;
	}
}
