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

import org.trustedanalytics.platformoperations.data.ComponentMetrics;

import java.util.ArrayList;
import java.util.List;

public class ComponentSummaryTestResources {

    public static final int RESERVABLE_STAGERS = 2;
    public static final int MEM_FREE_BYTES = 2;
    public static final int MEM_USED_BYTES = 2;

    public static List<ComponentMetrics> getComponentMetrics() {
        List<ComponentMetrics> componentMetrics = new ArrayList<>();

        ComponentMetrics componentMetrics1 = new ComponentMetrics();
        componentMetrics1.setMemFreeBytes(1000000);
        componentMetrics1.setMemUsedBytes(1000000);
        componentMetrics1.setReservableStagers(1);

        ComponentMetrics componentMetrics2 = new ComponentMetrics();
        componentMetrics2.setMemFreeBytes(1000000);
        componentMetrics2.setMemUsedBytes(1000000);
        componentMetrics2.setReservableStagers(1);

        componentMetrics.add(componentMetrics1);
        componentMetrics.add(componentMetrics2);

        return componentMetrics;
    }
}
