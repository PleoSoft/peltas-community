package io.peltas.alfresco.workspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.peltas.core.alfresco.PeltasEntry;
import io.peltas.core.alfresco.config.AbstractAlfrescoPeltasConfiguration;
import io.peltas.core.alfresco.config.PeltasProperties;
import io.peltas.core.alfresco.config.PeltasProperties.Authentication.BasicAuth;
import io.peltas.core.batch.AbstractPeltasRestReader;
import io.peltas.core.config.EnablePeltasInMemory;
import io.peltas.core.repository.TxDataRepository;

@Configuration
@EnablePeltasInMemory
public class AlfrescoWorkspaceConfiguration extends AbstractAlfrescoPeltasConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoWorkspaceConfiguration.class);

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
}
