/**
 * Copyright (c) 2003-2026 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.event.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.entity.impl.EntityManagerComponent;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.event.api.UsageSession;
import org.sakaiproject.thread_local.impl.ThreadLocalComponent;
import org.sakaiproject.tool.impl.ActiveToolComponent;
import org.sakaiproject.tool.impl.SessionComponent;

public class EventTrackingServiceTest {

    private final ClusterEventTracking service = new ClusterEventTracking();
    private final List<String> calls = new ArrayList<>();
    private Event event;

    @Before
    public void setUp() {
        ThreadLocalComponent threadLocalManager = new ThreadLocalComponent();
        SessionComponent sessionManager = new SessionComponent();
        sessionManager.setThreadLocalManager(threadLocalManager);
        ActiveToolComponent toolManager = new ActiveToolComponent();
        toolManager.setThreadLocalManager(threadLocalManager);

        service.setEntityManager(new EntityManagerComponent());
        service.setSessionManager(sessionManager);
        service.setToolManager(toolManager);
        event = service.newEvent("realm.upd", "/realm/site/test", "test", true, NotificationService.NOTI_NONE, true);
    }

    @Test
    public void localPriorityObserverRunsBeforeRegularAndLocalObservers() {
        service.addLocalPriorityObserver((source, value) -> calls.add("local priority"));
        service.addObserver((source, value) -> calls.add("regular"));
        service.addLocalObserver((source, value) -> calls.add("local"));

        service.post(event, (UsageSession) null);

        assertEquals(List.of("local priority", "regular", "local"), calls);
    }

    @Test
    public void remoteEventsSkipLocalPriorityObservers() {
        service.addLocalPriorityObserver((source, value) -> calls.add("local priority"));
        service.addPriorityObserver((source, value) -> calls.add("priority"));
        service.addObserver((source, value) -> calls.add("regular"));

        service.notifyObservers(event, false);

        assertEquals(List.of("priority", "regular"), calls);
    }

    @Test
    public void changingObserverRegistrationMovesItToTheNewTier() {
        Observer observer = (source, value) -> calls.add("observer");
        service.addLocalPriorityObserver(observer);
        service.addLocalObserver(observer);

        service.post(event, (UsageSession) null);

        assertEquals(List.of("observer"), calls);
    }
}
