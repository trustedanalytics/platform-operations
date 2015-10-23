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

import org.trustedanalytics.cloud.cc.api.*;
import org.trustedanalytics.platformoperations.data.OrgUser;

import java.util.ArrayList;
import java.util.List;

public class ControllerSummaryTestResources {

    public static final int MEM_USED_IN_MB = 3;
    public static final int SERVICE_COUNT = 3;
    public static final int APP_COUNT = 3;
    public static final int SPACE_COUNT = 3;
    public static final int ORG_COUNT = 2;
    public static final int USER_COUNT = 3;
    public static final int BUILDPACK_COUNT = 2;

    public static List<CcOrgSummary> getCcOrgSummaries() {
        List<CcOrgSummary> orgSummaries = new ArrayList<>();

        CcOrgSummary orgSummary1 = new CcOrgSummary();
        List<CcOrgSummarySpace> spaces = new ArrayList<>();
        CcOrgSummarySpace space1 = new CcOrgSummarySpace();
        space1.setAppCount(1);
        space1.setMemDevTotal(1);
        space1.setServiceCount(1);

        CcOrgSummarySpace space2 = new CcOrgSummarySpace();
        space2.setAppCount(1);
        space2.setMemDevTotal(1);
        space2.setServiceCount(1);

        spaces.add(space1);
        spaces.add(space2);
        orgSummary1.setSpaces(spaces);

        CcOrgSummary orgSummary2 = new CcOrgSummary();
        List<CcOrgSummarySpace> spaces2 = new ArrayList<>();
        CcOrgSummarySpace space3 = new CcOrgSummarySpace();
        space3.setAppCount(1);
        space3.setMemDevTotal(1);
        space3.setServiceCount(1);

        spaces2.add(space3);
        orgSummary2.setSpaces(spaces2);

        orgSummaries.add(orgSummary1);
        orgSummaries.add(orgSummary2);
        return orgSummaries;
    }

    public static List<CcBuildpack> getCcBuildpacks() {
        List<CcBuildpack> buildpacks = new ArrayList<>();

        CcBuildpack buildpack1 = new CcBuildpack();
        CcBuildpack buildpack2 = new CcBuildpack();

        buildpacks.add(buildpack1);
        buildpacks.add(buildpack2);
        return buildpacks;
    }

    public static List<OrgUser> getOrgUsers() {
        List<OrgUser> orgUsers = new ArrayList<>();
        OrgUser orgUser1 = new OrgUser("userName1");
        OrgUser orgUser2 = new OrgUser("userName2");
        OrgUser orgUser3 = new OrgUser("userName3");
        OrgUser orgUser4 = new OrgUser("userName1");
        orgUsers.add(orgUser1);
        orgUsers.add(orgUser2);
        orgUsers.add(orgUser3);
        orgUsers.add(orgUser4);
        return orgUsers;
    }

}
