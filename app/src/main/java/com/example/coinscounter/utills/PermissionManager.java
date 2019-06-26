package com.example.coinscounter.utills;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {

    public static Boolean getPermission(@NonNull Activity activity,
                                        @NonNull String permission, @NonNull String toastText, @NonNull int permissionID) {
        if (PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(activity, permission)) {
                return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission)){
            Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show();
            return false;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    permissionID);
            return false;
        }
    }
}
