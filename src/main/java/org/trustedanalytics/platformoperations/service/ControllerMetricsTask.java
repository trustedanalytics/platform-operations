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

import org.trustedanalytics.cloud.cc.api.CcBuildpack;
import org.trustedanalytics.cloud.cc.api.CcOperations;
import org.trustedanalytics.cloud.cc.api.CcOrg;
import org.trustedanalytics.cloud.cc.api.CcOrgSummary;
import org.trustedanalytics.cloud.cc.api.manageusers.Role;
import org.trustedanalytics.platformoperations.data.ControllerSummary;
import org.trustedanalytics.platformoperations.data.OrgUser;

import com.google.common.base.Stopwatch;

import feign.FeignException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import rx.Observable;

public class ControllerMetricsTask implements Supplier<ControllerSummary> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerMetricsTask.class);
    private final CcOperations client;
    private final String taskName;

    public ControllerMetricsTask(CcOperations client) {
        this.client = client;
        this.taskName = ControllerMetricsTask.class.getSimpleName();
    }

    @Override
    public ControllerSummary get() {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        LOGGER.info("{} started", taskName);

        final Observable<CcOrg> organizations = client.getOrgs().cache();

        final Observable<List<CcBuildpack>> buildpacks = client.getBuildpacks().toList();

        final Observable<List<CcOrgSummary>> summaries = organizations
            .flatMap(org -> client.getOrgSummary(org.getGuid()))
            .onExceptionResumeNext(Observable.empty())
            .toList();

        final Observable<List<OrgUser>> users = organizations
            .flatMap(org -> {
                try {
                    return Observable.from(client.getOrgUsers(org.getGuid(), Role.USERS));
                } catch (FeignException ex) {
                    LOGGER.error("Failed to get organization users", ex);
                    return Observable.empty();
                }
            })
            .map(user -> new OrgUser(user.getUsername()))
            .toList();

        LOGGER.info("{} completed in {}s", taskName, stopwatch.elapsed(TimeUnit.SECONDS));

        return new ControllerSummary(
            summaries.toBlocking().first(),
            users.toBlocking().first(),
            buildpacks.toBlocking().first()
        );
    }
}
