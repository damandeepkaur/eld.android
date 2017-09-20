package com.bsmwireless.common.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import javax.inject.Inject;

public class StorageUtil {

    private final StatsFsInternal mStatsFsInternal;

    @Inject
    public StorageUtil() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mStatsFsInternal = new StatFsApi18();
        } else {
            mStatsFsInternal = new StatFsApi14();
        }
    }

    public long getAvailableSpace() {
        return mStatsFsInternal.freeSpace();
    }

    public long getTotalSpace() {
        return mStatsFsInternal.totalSpace();
    }

    private interface StatsFsInternal {
        long totalSpace();

        long freeSpace();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static class StatFsApi18 implements StatsFsInternal {

        private final StatFs mStatFsInternal;
        private final StatFs mStatFsExternal;

        private StatFsApi18() {
            mStatFsExternal = new StatFs(Environment.getExternalStorageDirectory().getPath());
            mStatFsInternal = new StatFs(Environment.getRootDirectory().getPath());
        }

        @Override
        public long totalSpace() {
            return getTotal(mStatFsExternal) + getTotal(mStatFsInternal);
        }

        @Override
        public long freeSpace() {
            return getFree(mStatFsInternal) + getFree(mStatFsExternal);
        }

        private long getTotal(StatFs statFs) {
            return statFs.getBlockCountLong() * statFs.getBlockSizeLong();
        }

        private long getFree(StatFs statFs) {
            return statFs.getAvailableBytes();
        }
    }

    private static class StatFsApi14 implements StatsFsInternal {

        private final StatFs mStatFsInternal;
        private final StatFs mStatFsExternal;

        private StatFsApi14() {
            mStatFsExternal = new StatFs(Environment.getExternalStorageDirectory().getPath());
            mStatFsInternal = new StatFs(Environment.getRootDirectory().getPath());
        }

        @Override
        public long totalSpace() {
            return getTotal(mStatFsExternal) + getTotal(mStatFsInternal);
        }

        @Override
        public long freeSpace() {
            return getFree(mStatFsInternal) + getFree(mStatFsExternal);
        }

        @SuppressWarnings("deprecation")
        private long getTotal(StatFs statFs) {
            return (long) statFs.getBlockCount() * (long) statFs.getBlockSize();
        }

        @SuppressWarnings("deprecation")
        private long getFree(StatFs statFs) {
            return (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
        }
    }

}
