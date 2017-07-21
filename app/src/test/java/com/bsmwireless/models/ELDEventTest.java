package com.bsmwireless.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for ELDEvent
 */
@RunWith(MockitoJUnitRunner.class)
public class ELDEventTest {

    /** Test EventType against ELD 7.25 */
    @Test
    public void testEldEventType() {
        // These tests are redundant, but it's important that future developers don't ever
        // accidentally change these values...

        assertEquals(1, ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue());
        assertEquals(2, ELDEvent.EventType.INTERMEDIATE_LOG.getValue());

        // TODO: test for 3 when it exists

        assertEquals(4, ELDEvent.EventType.CERTIFICATION_OF_RECORDS.getValue());
        assertEquals(5, ELDEvent.EventType.LOGIN_LOGOUT.getValue());
        assertEquals(6, ELDEvent.EventType.ENGINE_POWER_CHANGING.getValue());

        // TODO: test for 7 when it exists
    }

    /** Test StatusCode against ELD 7.23 */
    @Test
    public void testEldStatusCode() {
        assertEquals(1, ELDEvent.StatusCode.ACTIVE.getValue());
        assertEquals(2, ELDEvent.StatusCode.INACTIVE_CHANGED.getValue());
        assertEquals(3, ELDEvent.StatusCode.INACTIVE_CHANGE_REQUESTED.getValue());
        assertEquals(4, ELDEvent.StatusCode.INACTIVE_CHANGE_REJECTED.getValue());
    }
}
