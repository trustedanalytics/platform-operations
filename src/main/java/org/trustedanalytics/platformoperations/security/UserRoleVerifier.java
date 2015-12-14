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
package org.trustedanalytics.platformoperations.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.trustedanalytics.platformoperations.client.UserManagementOperations;
import org.trustedanalytics.platformoperations.data.UserRole;

import java.util.function.Function;

public class UserRoleVerifier {

    private final UserManagementOperations userManagementOperations;
    private final Function<Authentication, String> tokenExtractor;

    @Autowired
    public UserRoleVerifier(UserManagementOperations userManagementOperations,
                            Function<Authentication, String> tokenExtractor) {
        this.userManagementOperations = userManagementOperations;
        this.tokenExtractor = tokenExtractor;
    }

    public boolean verifyIsAdmin() {

        if(userManagementOperations
            .getUserModel("bearer " + tokenExtractor.apply(SecurityContextHolder.getContext().getAuthentication()))
            .getRole()
            .equals(UserRole.ADMIN))
            return true;
        throw new AccessDeniedException("You do not have permission to perform this action!");
    }
}
