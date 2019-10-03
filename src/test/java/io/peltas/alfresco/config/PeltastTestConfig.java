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

package io.peltas.alfresco.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.peltas.core.alfresco.config.PeltasHandlerConfigurationProperties;
import io.peltas.core.alfresco.config.expression.ContainsExpressionEvaluator;
import io.peltas.core.alfresco.config.expression.EqualsExpressionEvaluator;
import io.peltas.core.alfresco.config.expression.EvaluatorExpressionRegistry;

@EnableConfigurationProperties
public class PeltastTestConfig {

	@Bean
	public EvaluatorExpressionRegistry evaluatorExpressionRegistry() {
		EqualsExpressionEvaluator equalsExpressionEvaluator = new EqualsExpressionEvaluator();

		EvaluatorExpressionRegistry registry = new EvaluatorExpressionRegistry(equalsExpressionEvaluator);
		registry.registerEvaluator(new ContainsExpressionEvaluator());
		registry.registerEvaluator(equalsExpressionEvaluator);

		return registry;
	}

	@Bean
	public PeltasHandlerConfigurationProperties alfrescoHandlerProperties() {
		return new PeltasHandlerConfigurationProperties(evaluatorExpressionRegistry());
	}
}
