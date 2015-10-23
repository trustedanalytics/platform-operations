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
package org.trustedanalytics.platformoperations.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.trustedanalytics.platformoperations.data.PlatformSummary;
import org.trustedanalytics.platformoperations.repository.PlatformSummaryMongoRepository;
import org.trustedanalytics.platformoperations.service.PlatformOperationsScheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlatformController {

    private static final String PLATFORM_SUMMARY = "/rest/platform/summary";
    private static final String PLATFORM_SUMMARY_CACHE = "/rest/platform/summary/refresh_cache";

    private final PlatformOperationsScheduler scheduler;
    private final PlatformSummaryMongoRepository repository;

    @Autowired
    public PlatformController(PlatformOperationsScheduler scheduler,
        PlatformSummaryMongoRepository repository) {
        this.scheduler = scheduler;
        this.repository = repository;
    }

    @RequestMapping(value = PLATFORM_SUMMARY, method = GET, produces = APPLICATION_JSON_VALUE)
    public PlatformSummary getPlatformSummary() {
        return repository.findTopByOrderByTimestampDesc();
    }

    @RequestMapping(value = PLATFORM_SUMMARY_CACHE, method = POST)
    public void refreshPlatformSummary() {
        scheduler.triggerPlatformSummary();
    }

}
