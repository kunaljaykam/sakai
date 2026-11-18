/*
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
package org.sakaiproject.scorm.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.model.api.SessionBean;
import org.sakaiproject.scorm.service.api.launch.ScormLaunchState;
import org.sakaiproject.scorm.service.api.launch.ScormRuntimeInvocation;
import org.sakaiproject.scorm.service.api.launch.ScormRuntimeResult;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ScormLaunchServiceRuntimeBatchTest.LaunchTestConfiguration.class)
public class ScormLaunchServiceRuntimeBatchTest
{
    private static final String USER_ID = "learner-1";

    @Autowired private ScormLaunchServiceImpl launchService;
    @Autowired private ScormLaunchSessionRegistry sessionRegistry;
    @Autowired private SessionManager sessionManager;

    private String sessionId;

    @Configuration
    static class LaunchTestConfiguration
    {
        @Bean
        public SessionManager sessionManager()
        {
            return mock(SessionManager.class);
        }

        @Bean
        public ScormLaunchSessionRegistry sessionRegistry()
        {
            return new ScormLaunchSessionRegistry();
        }

        @Bean
        public ScormLaunchServiceImpl launchService(ScormLaunchSessionRegistry sessionRegistry, SessionManager sessionManager)
        {
            ScormLaunchServiceImpl service = new ScormLaunchServiceImpl();
            service.setSessionRegistry(sessionRegistry);
            service.setSessionManager(sessionManager);
            return service;
        }
    }

    @Before
    public void setUp()
    {
        reset(sessionManager);
        Session session = mock(Session.class);
        when(session.getUserId()).thenReturn(USER_ID);
        when(sessionManager.getCurrentSession()).thenReturn(session);

        sessionId = sessionRegistry.register(new SessionBean(), new ContentPackage("Golf", 1L), USER_ID, ScormLaunchState.READY, null);
    }

    @Test
    public void runtimeBatchRunsEveryCallInOrderUnderOneLock()
    {
        ScormLaunchServiceImpl service = spy(launchService);
        List<String> seen = new ArrayList<>();
        List<Boolean> lockHeld = new ArrayList<>();

        doAnswer(invocation -> {
            ScormRuntimeInvocation call = invocation.getArgument(1);
            seen.add(call.getMethod() + " " + call.getArguments());
            lockHeld.add(sessionRegistry.getLock(sessionId).isHeldByCurrentThread());
            return ScormRuntimeResult.builder().value("true").errorCode("0").sessionEnded("Terminate".equals(call.getMethod())).build();
        }).when(service).runtime(eq(sessionId), any(ScormRuntimeInvocation.class));

        List<ScormRuntimeResult> results = service.runtimeBatch(sessionId, List.of(
            invocation("SetValue", "cmi.session_time", "PT4.5S"),
            invocation("SetValue", "cmi.exit", "suspend"),
            invocation("Terminate")));

        assertEquals(List.of("SetValue [cmi.session_time, PT4.5S]", "SetValue [cmi.exit, suspend]", "Terminate []"), seen);
        assertEquals("Each call must run while the batch holds the session lock", List.of(true, true, true), lockHeld);
        assertEquals(3, results.size());
        assertFalse(results.get(1).isSessionEnded());
        assertTrue(results.get(2).isSessionEnded());
        assertFalse("The batch must release the session lock", sessionRegistry.getLock(sessionId).isLocked());
    }

    @Test
    public void runtimeBatchReleasesLockWhenACallFails()
    {
        ScormLaunchServiceImpl service = spy(launchService);
        doAnswer(invocation -> {
            throw new IllegalStateException("boom");
        }).when(service).runtime(eq(sessionId), any(ScormRuntimeInvocation.class));

        try
        {
            service.runtimeBatch(sessionId, List.of(invocation("Commit", "")));
            fail("The failing call must propagate");
        }
        catch (IllegalStateException expected)
        {
            assertEquals("boom", expected.getMessage());
        }

        assertFalse(sessionRegistry.getLock(sessionId).isLocked());
    }

    @Test(expected = IllegalArgumentException.class)
    public void runtimeBatchRejectsUnknownSession()
    {
        launchService.runtimeBatch("no-such-session", List.of(invocation("Commit", "")));
    }

    private static ScormRuntimeInvocation invocation(String method, String... args)
    {
        return new ScormRuntimeInvocation(method, List.of(args), "sco-1", null, null, null);
    }
}
