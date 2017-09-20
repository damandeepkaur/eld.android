package com.bsmwireless;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

public class BaseTest {

    @Before
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);

    }
}
