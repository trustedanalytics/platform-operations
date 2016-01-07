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
import static org.trustedanalytics.platformoperations.unit.ControllerSummaryTestResources.APP_COUNT;
import static org.trustedanalytics.platformoperations.unit.ControllerSummaryTestResources.BUILDPACK_COUNT;
import static org.trustedanalytics.platformoperations.unit.ControllerSummaryTestResources.MEM_USED_IN_MB;
import static org.trustedanalytics.platformoperations.unit.ControllerSummaryTestResources.ORG_COUNT;
import static org.trustedanalytics.platformoperations.unit.ControllerSummaryTestResources.SPACE_COUNT;
import static org.trustedanalytics.platformoperations.unit.ControllerSummaryTestResources.SERVICE_COUNT;
import static org.trustedanalytics.platformoperations.unit.ControllerSummaryTestResources.USER_COUNT;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import org.trustedanalytics.platformoperations.data.ControllerSummary;

@RunWith(MockitoJUnitRunner.class)
public class ControllerSummaryTest {

    @Test
    public void aggregationCorrectness() {

        ControllerSummary controllerSummary = new ControllerSummary(
            ControllerSummaryTestResources.getCcOrgSummaries(),
            3,
            ControllerSummaryTestResources.getCcBuildpacks());

        assertEquals(APP_COUNT, controllerSummary.getAppCount());
        assertEquals(SPACE_COUNT, controllerSummary.getSpaceCount());
        assertEquals(MEM_USED_IN_MB, controllerSummary.getMemUsedInMb());
        assertEquals(SERVICE_COUNT, controllerSummary.getServiceCount());
        assertEquals(BUILDPACK_COUNT, controllerSummary.getBuildpackCount());
        assertEquals(ORG_COUNT, controllerSummary.getOrgCount());
        assertEquals(USER_COUNT, controllerSummary.getUserCount());
    }
}
