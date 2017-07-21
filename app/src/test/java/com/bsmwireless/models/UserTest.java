package com.bsmwireless.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for User
 */

@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    /** Verify compliance with RESTful API values */
    @Test
    public void testDriverTypeMatchesAPI() {
        assertEquals(0, User.DriverType.DRIVER.ordinal());
        assertEquals(1, User.DriverType.CO_DRIVER.ordinal());
        assertEquals(2, User.DriverType.EXEMPT.ordinal());
        assertEquals(3, User.DriverType.CARRIER.ordinal());
    }
}
