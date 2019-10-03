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

import javax.sql.DataSource;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.peltas.core.alfresco.PeltasEntry;
import io.peltas.core.alfresco.config.AbstractAlfrescoPeltasConfiguration;
import io.peltas.core.alfresco.config.PeltasProperties;
import io.peltas.core.alfresco.config.PeltasProperties.Authentication.BasicAuth;
import io.peltas.core.batch.AbstractPeltasRestReader;
import io.peltas.core.batch.PeltasDataHolder;
import io.peltas.core.batch.PeltasItemWriter;
import io.peltas.core.config.EnablePeltasInMemory;
import io.peltas.core.repository.TxDataRepository;
import io.peltas.core.repository.database.CustomDatasourceInitializer;
import io.peltas.core.repository.database.CustomDatasourceProperties;
import io.peltas.core.repository.database.JpaTxDataWriter;
import io.peltas.core.repository.database.PeltasDatasourceInitializer;
import io.peltas.core.repository.database.PeltasDatasourceProperties;
import io.peltas.core.repository.database.PeltasJdbcBatchWriter;
import io.peltas.core.repository.database.PeltasTimestamp;
import io.peltas.core.repository.database.PeltasTimestampRepository;

@Configuration
@EnablePeltasInMemory
public class AlfrescoWorkspaceConfiguration extends AbstractAlfrescoPeltasConfiguration {

	@Autowired
	private TxDataRepository dataRepository;

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();

		BasicAuth basicAuth = alfrescoAuditProperties().getAuth().getBasic();
		restTemplate.getInterceptors()
				.add(new BasicAuthorizationInterceptor(basicAuth.getUsername(), basicAuth.getPassword()));
		restTemplate.getMessageConverters().add(0, mappingJacksonHttpMessageConverter());
		return restTemplate;
	}

	@Override
	public AbstractPeltasRestReader<PeltasEntry, AlfrescoWorkspaceNodes> reader() {
		RestTemplate restTemplate = restTemplate();

		PeltasProperties properties = alfrescoAuditProperties();
		return new AlfrescoWorkspaceRestReader(restTemplate, properties, dataRepository);
	}

	private MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper());
		return converter;
	}

	private ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}

	@Configuration
	@ConditionalOnProperty(name = "peltas.repository", havingValue = "database")
	@EnableJpaRepositories(basePackageClasses = PeltasTimestampRepository.class)
	@EntityScan(basePackageClasses = PeltasTimestamp.class)
	public class DatabaseConfiguration {

		@Bean
		public JpaTxDataWriter txDataWriter(PeltasTimestampRepository repository) {
			return new JpaTxDataWriter(repository);
		}

		@Bean
		public PeltasJdbcBatchWriter peltasBatchWriter(JdbcTemplate jdbcTemplate,
				@Value("classpath:io/peltas/db/executions/*.sql") Resource[] resources) {
			return new PeltasJdbcBatchWriter(new NamedParameterJdbcTemplate(jdbcTemplate), resources);
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


		@Bean
		public CustomDatasourceProperties peltasCustomDatasourceProperties() {
			return new CustomDatasourceProperties();
		}

		@Bean
		@ConditionalOnProperty(name = "peltas.custom.datasource.enabled", havingValue = "true")
		public CustomDatasourceInitializer peltasCustomDatasourceInitializer(DataSource dataSource,
				ResourceLoader resourceLoader) {
			return new CustomDatasourceInitializer(dataSource, resourceLoader, peltasCustomDatasourceProperties());
		}
	}

	@Configuration
	@ConditionalOnProperty(name = "peltas.repository", havingValue = "sample")
	public class HazelcastConfiguration {

		@Bean
		public TxDataRepository txDataWriter() {
			return new TxDataRepository() {

				PeltasTimestamp lastTs = null;

				@Override
				public PeltasTimestamp readTx(String applicationName) {
					return lastTs;
				}

				@Override
				public PeltasTimestamp writeTx(PeltasTimestamp ts) {
					lastTs = ts;
					return ts;
				}
			};
		}

		@Bean
		public ItemWriter<PeltasDataHolder> peltasBatchWriter() {
			return new PeltasItemWriter<Object, Object>() {

				@Override
				public Object createItemInputParameters(PeltasDataHolder item) {
					return null;
				}

				@Override
				public void itemExecution(String executionKey, Object parameters) {
				}

				@Override
				public Object createCollectionItemInputParameters(Object itemParams, String collectionKey,
						Object collectionValue) {
					return null;
				}

				@Override
				public void collectionExecution(String executionKey, Object params) {
				}

			};
		}
	}
}
