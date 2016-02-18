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
import static org.trustedanalytics.platformoperations.unit.ComponentSummaryTestFixtures.MEM_FREE_BYTES;
import static org.trustedanalytics.platformoperations.unit.ComponentSummaryTestFixtures.MEM_USED_BYTES;
import static org.trustedanalytics.platformoperations.unit.ComponentSummaryTestFixtures.RESERVABLE_STAGERS;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.trustedanalytics.platformoperations.data.ComponentSummary;

@RunWith(MockitoJUnitRunner.class)
public class ComponentSummaryTest {

    @Test
    public void aggregationCorrectness() {

        ComponentSummary componentSummary = new ComponentSummary(
            ComponentSummaryTestFixtures.getComponentMetrics());

        assertEquals(RESERVABLE_STAGERS, componentSummary.getReservableStagers());
        assertEquals(MEM_FREE_BYTES, componentSummary.getMemFreeInMb());
        assertEquals(MEM_USED_BYTES, componentSummary.getMemUsedInMb());
    }

}
