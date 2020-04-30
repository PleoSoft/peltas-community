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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;

import io.peltas.core.PeltasException;

public class PeltasScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(PeltasScheduler.class);

	private final JobLauncher jobLauncher;
	private final Job job;

	public PeltasScheduler(JobLauncher jobLauncher, Job job) {
		this.jobLauncher = jobLauncher;
		this.job = job;
	}

	@Scheduled(fixedDelayString = "${peltas.scheduler.fixedDelay}")
	public void alfrescoAuditTask() throws Exception {

		Long jobId = System.currentTimeMillis();
		LOGGER.info("------------ starting job with id {} ------------", jobId);
		JobParameters jobParameters = new JobParametersBuilder().addLong("auditId", jobId).toJobParameters();

		JobExecution run = jobLauncher.run(job, jobParameters);
		ExitStatus exitStatus = run.getExitStatus();
		if (!ExitStatus.COMPLETED.equals(exitStatus)) {
			LOGGER.error("------------ job with id {} FAILED ------------", jobId);
			throw new PeltasException("Peltas job is stoping!", new Exception(exitStatus.getExitDescription()));
		}
	}
}
