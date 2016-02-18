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
package org.trustedanalytics.platformoperations.unit;

import static org.junit.Assert.assertEquals;

import static org.trustedanalytics.platformoperations.unit.ControllerSummaryTestFixtures.MEM_USED_IN_MB;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import org.trustedanalytics.platformoperations.data.ControllerSummary;

@RunWith(MockitoJUnitRunner.class)
public class ControllerSummaryTest {

    @Test
    public void aggregationCorrectness() {

        ControllerSummary controllerSummary = ControllerSummary.builder()
                .buildpacks(ControllerSummaryTestFixtures.getCcBuildpacks())
                .orgs(ControllerSummaryTestFixtures.getCcOrgSummaries())
                .userCount(0)
                .appCount(0)
                .buildpackCount(0)
                .orgCount(0)
                .serviceCount(0)
                .serviceInstancesCount(0)
                .spaceCount(0)
                .memUsedInMb(3)
                .build();

        assertEquals(MEM_USED_IN_MB, controllerSummary.getMemUsedInMb());
    }
}
