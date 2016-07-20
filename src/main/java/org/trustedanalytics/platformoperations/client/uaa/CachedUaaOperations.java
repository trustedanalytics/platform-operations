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

package org.trustedanalytics.platformoperations.client.uaa;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.Exception;import java.lang.Override;import java.lang.String;import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CachedUaaOperations implements UaaOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedUaaOperations.class);
    public static final String CACHE_KEY = "CACHE_KEY";
    private final UaaOperations uaaOperations;
    LoadingCache<String, AccessTokenResponse> tokenCache;
    private final ObjectMapper objectMapper;

    public CachedUaaOperations(String uaaUri, String clientId, String clientSecret) {
        objectMapper = new ObjectMapper()
                .setPropertyNamingStrategy(new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        uaaOperations = Feign.builder().encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .logger(new Slf4jLogger(UaaOperations.class))
                .requestInterceptor(new BasicAuthRequestInterceptor(clientId, clientSecret))
                .logLevel(feign.Logger.Level.BASIC)
                .target(UaaOperations.class, uaaUri);

        tokenCache =
                CacheBuilder.newBuilder()
                        .maximumSize(1)
                        .expireAfterAccess(30, TimeUnit.SECONDS)
                        .build(new CacheLoader<String, AccessTokenResponse>() {

                            @Override
                            public AccessTokenResponse load(String key) throws Exception {
                                LOGGER.info("Refreshing token at: {}",  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
                                //make the expensive call
                                return uaaOperations.authenticate();
                            }
                        });
    }

    @Override
    public synchronized AccessTokenResponse authenticate() {
        return tokenCache.getUnchecked(CACHE_KEY);
    }
}
