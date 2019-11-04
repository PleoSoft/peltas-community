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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.messaging.core.GenericMessagingTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.peltas.core.alfresco.integration.PeltasIntegrationWriter;
import io.peltas.core.alfresco.workspace.DefaultAlfrescoPeltasConfiguration;
import io.peltas.core.batch.PeltasDataHolder;
import io.peltas.core.batch.PeltasExecutionItemWriter;
import io.peltas.core.config.EnablePeltasInMemory;
import io.peltas.core.repository.TxDataRepository;
import io.peltas.core.repository.database.JpaTxDataWriter;
import io.peltas.core.repository.database.PeltasDatasourceInitializer;
import io.peltas.core.repository.database.PeltasDatasourceProperties;
import io.peltas.core.repository.database.PeltasJdbcWriter;
import io.peltas.core.repository.database.PeltasTimestamp;
import io.peltas.core.repository.database.PeltasTimestampRepository;

@Configuration
@EnableScheduling
@EnableIntegration
@EnableTransactionManagement
@PropertySource(ignoreResourceNotFound = true, value = { "classpath:io/peltas/peltas-community.properties" })
@Import(DefaultAlfrescoPeltasConfiguration.class)
@EnablePeltasInMemory
public class AlfrescoWorkspaceConfiguration {

	@Configuration
	@ConditionalOnProperty(name = "peltas.writer", havingValue = "inmemory")
	public class InMemoryWriterConfiguration {

		@Bean
		public TxDataRepository txDataWriter() {
			return new TxDataRepository() {

				@Override
				public PeltasTimestamp writeTx(PeltasTimestamp ts) {
					return ts;
				}

				@Override
				public PeltasTimestamp readTx(String applicationName) {
					return null;
				}
			};
		}

		@Bean
		public ResourcelessTransactionManager transactionManager() {
			return new ResourcelessTransactionManager();
		}

		@Bean
		public ItemWriter<PeltasDataHolder> peltasBatchWriter() {
			return new PeltasExecutionItemWriter<Object, Object>();
		}
	}

	@Configuration
	@ConditionalOnProperty(name = "peltas.writer", havingValue = "integration")
	public class IntegrationWriterConfiguration {

		@Bean
		public GenericMessagingTemplate messagingTemplate(BeanFactory beanFactory) {
			GenericMessagingTemplate template = new GenericMessagingTemplate();
			template.setBeanFactory(beanFactory);
			return template;
		}

		@Bean
		public TxDataRepository txDataWriter() {
			return new TxDataRepository() {

				@Override
				public PeltasTimestamp writeTx(PeltasTimestamp ts) {
					return ts;
				}

				@Override
				public PeltasTimestamp readTx(String applicationName) {
					return null;
				}
			};
		}

		@Bean
		public ResourcelessTransactionManager transactionManager() {
			return new ResourcelessTransactionManager();
		}

		@Bean
		public ItemWriter<PeltasDataHolder> peltasBatchWriter(BeanFactory beanFactory) {
			return new PeltasIntegrationWriter<Object, Object>(messagingTemplate(beanFactory));
		}

		@Bean
		public IntegrationServiceSample integrationServiceSample() {
			return new IntegrationServiceSample();
		}


	}

	@Configuration
	@ConditionalOnProperty(name = "peltas.writer", havingValue = "database")
	@EnableJpaRepositories(basePackageClasses = PeltasTimestampRepository.class)
	@EntityScan(basePackageClasses = PeltasTimestamp.class)
	public class WriterConfiguration {

		@Bean
		public PeltasJdbcWriter peltasBatchWriter(JdbcTemplate jdbcTemplate,
				@Value("classpath:io/peltas/db/executions/*.sql") Resource[] resources) {
			return new PeltasJdbcWriter(new NamedParameterJdbcTemplate(jdbcTemplate), resources);
		}

		@Bean
		public PeltasDatasourceProperties peltasDatasourceProperties() {
			return new PeltasDatasourceProperties();
		}

		@Bean
		public PeltasDatasourceInitializer peltasDatasourceInitializer(DataSource dataSource,
				ResourceLoader resourceLoader) {
			return new PeltasDatasourceInitializer(dataSource, resourceLoader, peltasDatasourceProperties());
		}

		@Bean
		public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
			return new JpaTransactionManager(emf);
		}

		@Bean
		public JpaTxDataWriter txDataWriter(PeltasTimestampRepository repository) {
			return new JpaTxDataWriter(repository);
		}

		@Bean
		public PeltasDatasourceProperties peltasTimestampDatasourceProperties() {
			PeltasDatasourceProperties datasourceProperties = new PeltasDatasourceProperties();
			datasourceProperties.setSchema("classpath:io/peltas/db/peltas-create-timestamp-@@platform@@.sql");

			return datasourceProperties;
		}

		@Bean
		public PeltasDatasourceInitializer peltasTimestampDatasourceInitializer(DataSource dataSource,
				ResourceLoader resourceLoader) {
			return new PeltasDatasourceInitializer(dataSource, resourceLoader, peltasTimestampDatasourceProperties());
		}
	}
}
