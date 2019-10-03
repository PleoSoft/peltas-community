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
