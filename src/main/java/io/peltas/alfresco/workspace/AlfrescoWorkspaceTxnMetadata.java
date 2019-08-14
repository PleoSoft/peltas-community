package io.peltas.alfresco.workspace;

import java.util.HashMap;
import java.util.List;

public class AlfrescoWorkspaceTxnMetadata {
	private List<HashMap<String, Object>> transactions;
	private Long maxTxnCommitTime;
	private Long maxTxnId;

	public List<HashMap<String, Object>> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<HashMap<String, Object>> transactions) {
		this.transactions = transactions;
	}

	public Long getMaxTxnCommitTime() {
		return maxTxnCommitTime;
	}

	public void setMaxTxnCommitTime(Long maxTxnCommitTime) {
		this.maxTxnCommitTime = maxTxnCommitTime;
	}

	public Long getMaxTxnId() {
		return maxTxnId;
	}

	public void setMaxTxnId(Long maxTxnId) {
		this.maxTxnId = maxTxnId;
	}
}
