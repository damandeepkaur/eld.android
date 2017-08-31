package com.bsmwireless.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for ELDEvent
 *
 * Some of these tests are redundant, but it's important that future developers don't ever
 * accidentally change these values.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ELDEventTest {

    /**
     * Tests LoginLogoutCode against ELD 7.20
     *
     * This event code is only valid for event type = 5
     */
    @Test
    public void testEldLoginLogoutCode() {
        assertEquals(1, ELDEvent.LoginLogoutCode.LOGIN.getValue());
        assertEquals(2, ELDEvent.LoginLogoutCode.LOGOUT.getValue());
    }

    /** Tests EventOrigin against ELD 7.22 */
    @Test
    public void testEldEventOrigin() {
        assertEquals(1, ELDEvent.EventOrigin.AUTOMATIC_RECORD.getValue());
        assertEquals(2, ELDEvent.EventOrigin.DRIVER.getValue());
        assertEquals(3, ELDEvent.EventOrigin.NON_DRIVER.getValue());
        assertEquals(4, ELDEvent.EventOrigin.UNIDENTIFIED_DRIVER.getValue());
    }

    /** Tests StatusCode against ELD 7.23 */
    @Test
    public void testEldStatusCode() {
        assertEquals(1, ELDEvent.StatusCode.ACTIVE.getValue());
        assertEquals(2, ELDEvent.StatusCode.INACTIVE_CHANGED.getValue());
        assertEquals(3, ELDEvent.StatusCode.INACTIVE_CHANGE_REQUESTED.getValue());
        assertEquals(4, ELDEvent.StatusCode.INACTIVE_CHANGE_REJECTED.getValue());
    }

    /** Tests EventType against ELD 7.25 */
    @Test
    public void testEldEventType() {
        assertEquals(1, ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue());
        assertEquals(2, ELDEvent.EventType.INTERMEDIATE_LOG.getValue());
        assertEquals(3, ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue());
        assertEquals(4, ELDEvent.EventType.CERTIFICATION_OF_RECORDS.getValue());
        assertEquals(5, ELDEvent.EventType.LOGIN_LOGOUT.getValue());
        assertEquals(6, ELDEvent.EventType.ENGINE_POWER_CHANGING.getValue());
        assertEquals(7, ELDEvent.EventType.DATA_DIAGNOSTIC.getValue());
    }


}
