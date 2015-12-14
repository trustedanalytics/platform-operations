/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.platformoperations;

import feign.Logger;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

import org.springframework.security.core.Authentication;
import org.trustedanalytics.cloud.auth.AuthTokenRetriever;
import org.trustedanalytics.cloud.auth.OAuth2TokenRetriever;
import org.trustedanalytics.cloud.cc.FeignClient;
import org.trustedanalytics.cloud.cc.api.CcOperations;
import org.trustedanalytics.cloud.cc.api.loggers.ScramblingSlf4jLogger;
import org.trustedanalytics.platformoperations.client.UserManagementOperations;
import org.trustedanalytics.platformoperations.security.OAuth2TokenExtractor;
import org.trustedanalytics.platformoperations.security.UserRoleVerifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Function;

@Configuration
@EnableAsync
@EnableScheduling
public class ApplicationConfiguration {

    @Value("${cf.resource:/}")
    private String apiBaseUrl;

    @Value("${services.user-management.url}")
    private String userManagementUrl;

    @Bean
    public AuthTokenRetriever authTokenRetriever() {
        return new OAuth2TokenRetriever();
    }

    @Bean
    public OAuth2ClientContext oauth2ClientContext() {
        return new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest());
    }

    @Bean
    @ConfigurationProperties("spring.oauth2.client")
    public OAuth2ProtectedResourceDetails clientCredentials() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    public OAuth2RestTemplate clientRestTemplate() {
        OAuth2RestTemplate template = new OAuth2RestTemplate(clientCredentials());
        ClientCredentialsAccessTokenProvider provider = new ClientCredentialsAccessTokenProvider();
        template.setAccessTokenProvider(provider);
        return template;
    }

    @Bean
    protected CcOperations ccPrivilegedClient() {
        final String token = clientRestTemplate().getAccessToken().toString();
        return new FeignClient(apiBaseUrl, builder -> builder
            .requestInterceptor(template -> template.header("Authorization", "bearer " + token))
            .logLevel(Logger.Level.BASIC));
    }

    @Bean
    public ScheduledThreadPoolExecutor scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(4);
    }

    @Bean
    public Function<Authentication, String> tokenExtractor() {
        return new OAuth2TokenExtractor();
    }

    @Bean
    public UserManagementOperations userManagementOperations() {
        return getClient(UserManagementOperations.class, userManagementUrl);
    }

    @Bean
    public UserRoleVerifier userRoleVerifier() {
        return new UserRoleVerifier(userManagementOperations(), tokenExtractor());
    }

    private <T> T getClient(Class<T> clientType, String url) {
        return Feign.builder()
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .logger(new ScramblingSlf4jLogger(clientType.getClass()))
            .logLevel(Logger.Level.FULL)
            .target(clientType, url);
    }
}
