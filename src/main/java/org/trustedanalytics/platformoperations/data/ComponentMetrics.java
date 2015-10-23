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

package org.trustedanalytics.platformoperations.data;

import lombok.Data;

@Data
public class ComponentMetrics {

    protected String uuid;
    protected String host;
    protected String start;
    protected long numCores;
    protected String uptime;
    protected double cpu;
    protected double cpuLoadAvg;
    protected long memBytes;
    protected long memUsedBytes;
    protected long  memFreeBytes;
    protected long totalWardenResponseTimeInMs;
    protected long wardenRequestCount;
    protected long reservableStagers;
    protected long canStage;
    protected double availableMemoryRatio;
    protected double availableDiskRatio;

}
