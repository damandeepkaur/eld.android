package com.bsmwireless.screens.common;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

public abstract class BasePermissionFragment extends BaseFragment {
    private static final int REQUEST_PERMISSION = 172;

    abstract protected String[] getDesiredPermissions();

    abstract protected void onPermissionDenied();

    abstract protected void onPermissionGranted();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (hasAllPermissions(getDesiredPermissions())) {
            onPermissionGranted();
        } else {
            requestPermissions(getDeniedPermissions(getDesiredPermissions()), REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (hasAllPermissions(getDesiredPermissions())) {
                onPermissionGranted();
            } else {
                onPermissionDenied();
            }
        }
    }

    private boolean hasAllPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                return(false);
            }
        }

        return(true);
    }

    protected boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
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
