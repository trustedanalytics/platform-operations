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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.trustedanalytics.utils.errorhandling.ErrorFormatter;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@ControllerAdvice
public class PlatformExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public void accessForbidden(HttpServletResponse response) throws IOException {

        long errorId = generateErrorId();
        LOGGER.warn(ErrorFormatter.formatErrorMessage("Access forbidden.", errorId));
        response.sendError(FORBIDDEN.value(), ErrorFormatter.formatErrorMessage("You do not have permission to perform this action!", errorId));
    }

    private static long generateErrorId() {
        return new Date().getTime();
    }
}
