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

import lombok.Builder;
import org.trustedanalytics.cloud.cc.api.CcBuildpack;
import org.trustedanalytics.cloud.cc.api.CcOrgSummary;

import java.util.List;

import lombok.Data;

@Builder
@Data
public class ControllerSummary {

    protected List<CcOrgSummary> orgs;
    protected List<CcBuildpack> buildpacks;
    protected long memUsedInMb;
    protected long serviceCount;
    protected long serviceInstancesCount;
    protected long appCount;
    protected long spaceCount;
    protected long orgCount;
    protected long userCount;
    protected long buildpackCount;
}
