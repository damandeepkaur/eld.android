package com.bsmwireless.services.malfunction;

import com.bsmwireless.BaseTest;
import com.bsmwireless.common.utils.malfunction.MalfunctionJob;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class MalfunctionServicePresenterTest extends BaseTest {

    @Mock
    MalfunctionJob firstJob;
    @Mock
    MalfunctionJob secondJob;

    private MalfunctionServicePresenter mMalfunctionServicePresenter;

    @Before
    public void setUp() throws Exception {
        mMalfunctionServicePresenter =
                new MalfunctionServicePresenter(Arrays.asList(firstJob, secondJob));
    }

    @Test
    public void startAndStop() throws Exception {

        mMalfunctionServicePresenter.startMonitoring();
        mMalfunctionServicePresenter.stopMonitoring();

        verify(firstJob).start();
        verify(firstJob).stop();
        verify(secondJob).start();
        verify(secondJob).stop();
    }

    @Test
    public void start() throws Exception {

        mMalfunctionServicePresenter.startMonitoring();
        verify(firstJob).start();
        verify(firstJob, never()).stop();
        verify(secondJob).start();
        verify(secondJob, never()).stop();
    }
}