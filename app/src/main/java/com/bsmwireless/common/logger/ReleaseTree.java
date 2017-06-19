package com.bsmwireless.common.logger;

import timber.log.Timber;

public class ReleaseTree extends Timber.DebugTree {

    public ReleaseTree() {

    }

    @Override public void v(String message, Object... args) {}
    @Override public void v(Throwable t, String message, Object... args) {}
    @Override public void d(String message, Object... args) {}
    @Override public void d(Throwable t, String message, Object... args) {}
    @Override public void i(String message, Object... args) {}
    @Override public void i(Throwable t, String message, Object... args) {}
    @Override public void e(String message, Object... args) {}
    @Override public void e(Throwable t, String message, Object... args) {}
}
