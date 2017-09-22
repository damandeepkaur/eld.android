package com.bsmwireless.data.storage.logsheets;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import app.bsmuniversal.com.RxSchedulerRule;

import static junit.framework.Assert.assertEquals;

/**
 * Tests for LogSheetEntity.
 */

@RunWith(MockitoJUnitRunner.class)
public class LogSheetEntityTest {
    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    private LogSheetEntity mLogSheetEntity;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        mLogSheetEntity = new LogSheetEntity();
    }

    @Test
    public void testSyncOrdinal() {

        // SyncType ordinals are stored in local cache, so this helps detect if someone
        // changes them by accident...
        assertEquals(0, LogSheetEntity.SyncType.SYNC.ordinal());
        assertEquals(1, LogSheetEntity.SyncType.UNSYNC.ordinal());
    }
}
