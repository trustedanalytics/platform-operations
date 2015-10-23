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

import org.trustedanalytics.cloud.cc.api.CcOperations;
import org.trustedanalytics.platformoperations.data.ComponentSummary;
import org.trustedanalytics.platformoperations.data.ControllerSummary;
import org.trustedanalytics.platformoperations.data.PlatformSummary;
import org.trustedanalytics.platformoperations.repository.PlatformSummaryMongoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import nats.client.Nats;

@Service
public class PlatformOperationsScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformOperationsScheduler.class);

    private final AtomicBoolean flag = new AtomicBoolean(false);
    private final PlatformSummaryMongoRepository repository;
    private final ScheduledThreadPoolExecutor executor;
    private final CcOperations client;
    private final Nats nats;

    @Value("${refresh_schedule_h}")
    private long schedule;

    @Autowired
    public PlatformOperationsScheduler(PlatformSummaryMongoRepository repository,
        ScheduledThreadPoolExecutor executor, CcOperations client, Nats nats) {

        this.repository = Objects.requireNonNull(repository);
        this.client = Objects.requireNonNull(client);
        this.executor = Objects.requireNonNull(executor);
        this.nats = Objects.requireNonNull(nats);
    }

    @PostConstruct
    public void defaultSchedule() {
        schedulePlatformSummary(schedule, TimeUnit.HOURS);
    }

    public void schedulePlatformSummary(long delay, TimeUnit timeUnit) {
        LOGGER.info("Schedule Platform Summary: {}h", TimeUnit.HOURS.convert(delay, timeUnit));
        executor.scheduleWithFixedDelay(platformSummary(), 0, delay, timeUnit);
    }

    public void triggerPlatformSummary() {
        executor.submit(platformSummary());
    }

    private Runnable platformSummary() {
        return () -> {

                if (flag.compareAndSet(false, true)) {
                    try {
                        LOGGER.info("Trigger Platform Summary");

                        final CompletableFuture<ComponentSummary> componentSummary =
                            CompletableFuture.supplyAsync(new ComponentDiscoverTask(nats), executor)
                                .thenApply(messages -> new ComponentMetricsTask("DEA", messages).get())
                                .thenApply(ComponentSummary::new);

                        final CompletableFuture<ControllerSummary> controllerSummary =
                            CompletableFuture.supplyAsync(new ControllerMetricsTask(client), executor);

                        CompletableFuture.allOf(componentSummary, controllerSummary).get(10, TimeUnit.MINUTES);

                        repository.save(new PlatformSummary(componentSummary.get(1, TimeUnit.MINUTES), controllerSummary.get(1, TimeUnit.MINUTES)));
                    } catch(TimeoutException | ExecutionException | InterruptedException ex) {
                        LOGGER.error("Exception during fetching metrics: {}" + ex);
                    } finally {
                        flag.set(false);
                    }
                }
                else {
                    LOGGER.info("Request skipped, task already submitted!");
                }
        };
    }
}
