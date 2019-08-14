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
