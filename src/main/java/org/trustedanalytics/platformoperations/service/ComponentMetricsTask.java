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

package org.trustedanalytics.platformoperations.service;

import org.trustedanalytics.platformoperations.client.Component;
import org.trustedanalytics.platformoperations.data.ComponentDiscoverMessage;
import org.trustedanalytics.platformoperations.data.ComponentMetrics;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentMetricsTask implements Supplier<List<ComponentMetrics>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentMetricsTask.class);
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .setPropertyNamingStrategy(new LowerCaseWithUnderscoresStrategy());

    private final String type;
    private final Collection<ComponentDiscoverMessage> messages;

    public ComponentMetricsTask(String type, Collection<ComponentDiscoverMessage> messages) {
        this.type = type;
        this.messages = messages;
    }

    @Override
    public List<ComponentMetrics> get() {
        LOGGER.info("{} started", ComponentMetricsTask.class.getSimpleName());

        return messages.stream()
            .filter(message -> type.equalsIgnoreCase(message.getType()))
            .map(this::toClient)
            .flatMap(client -> {
                try {
                    return Stream.of(client.getMetrics());
                } catch (Exception ex) {
                    LOGGER.error("Failed to get metrics", ex);
                    return Stream.empty();
                }
            })
            .collect(Collectors.toList());
    }

    private Component toClient(ComponentDiscoverMessage message) {
        final String username = message.getCredentials().get(0);
        final String password = message.getCredentials().get(1);

        return Feign.builder()
            .requestInterceptor(new BasicAuthRequestInterceptor(username, password))
            .encoder(new JacksonEncoder(MAPPER))
            .decoder(new JacksonDecoder(MAPPER))
            .logger(new Slf4jLogger(ComponentMetricsTask.class))
            .logLevel(feign.Logger.Level.BASIC)
            .target(Component.class, "http://" + message.getHost());
    }
}
