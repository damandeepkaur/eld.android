package com.bsmwireless.screens.common;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

public abstract class BasePermissionActivity extends BaseActivity {
    private static final int REQUEST_PERMISSION = 188;

    abstract protected String[] getDesiredPermissions();

    abstract protected void onPermissionDenied();

    abstract protected void onPermissionGranted();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (hasAllPermissions(getDesiredPermissions())) {
            onPermissionGranted();
        } else {
            ActivityCompat.requestPermissions(this, getDeniedPermissions(getDesiredPermissions()), REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (hasAllPermissions(getDesiredPermissions())) {
                onPermissionGranted();
            } else {
                onPermissionDenied();
            }
        }
    }

    private boolean hasAllPermissions(String[] perms) {
        for (String perm : perms) {
            if (!hasPermission(perm)) {
                return(false);
            }
        }

        return(true);
    }

    protected boolean hasPermission(String perm) {
        return ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED;
    }

    private String[] getDeniedPermissions(String[] desired) {
        ArrayList<String> result = new ArrayList<>();

        for (String permission : desired) {
            if (!hasPermission(permission)) {
                result.add(permission);
            }
        }

        return result.toArray(new String[result.size()]);
    }
}
