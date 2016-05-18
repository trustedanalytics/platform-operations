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

import org.trustedanalytics.platformoperations.data.ComponentDiscoverMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import nats.client.Nats;
import nats.client.Request;

public class ComponentDiscoverTask implements Supplier<Collection<ComponentDiscoverMessage>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentDiscoverTask.class);
    private static final String SUBJECT = "vcap.component.discover";
    private static final TimeUnit unit = TimeUnit.SECONDS;
    private static final long timeout = 10;
    private final ObjectMapper mapper;
    private final Nats nats;

    public ComponentDiscoverTask(Nats nats) {
        this.mapper = new ObjectMapper();
        this.nats = nats;
    }

    @Override
    public Collection<ComponentDiscoverMessage> get() {
        final String taskName = ComponentDiscoverTask.class.getSimpleName();
        LOGGER.info("{} : started", taskName);

        final List<ComponentDiscoverMessage> messages = Collections.synchronizedList(new ArrayList<>());
        final Request request = nats.request(SUBJECT, timeout, unit, message -> {
            try {
                messages.add(mapper.readValue(message.getBody(), ComponentDiscoverMessage.class));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });

        try {
            unit.sleep(timeout);
        } catch (InterruptedException ex) {
            LOGGER.warn("Interrupted", ex);
        }

        LOGGER.info("{} : completed, received: {} replies", taskName, request.getReceivedReplies());
        return messages;
    }
}
