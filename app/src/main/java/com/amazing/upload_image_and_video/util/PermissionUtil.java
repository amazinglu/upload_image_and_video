package com.amazing.upload_image_and_video.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

public class PermissionUtil {
    public static final int REQ_CODE_READ_EXTERNAL_STORAGE = 200;
    public static final int REQ_CODE_WRITE_EXTERNAL_STORAGE = 202;
    public static final int REQ_CODE_CAMERA = 199;

    /**
     * When in AppCompatActivity, you should use ActivityCompat.requestPermissions;
     * When in android.support.v4.app.Fragment, you should use simply requestPermissions
     * (this is an instance method of android.support.v4.app.Fragment)
     * If you call ActivityCompat.requestPermissions in a fragment,
     * the onRequestPermissionsResult callback is called on the activity and not the fragment.
     * */

    public static boolean checkPermission(Context context, String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Fragment fragment, String[] permissions, int reqCode) {
        fragment.requestPermissions(permissions, reqCode);
    }

    public static void requestReadExternalStoragePermission(Fragment fragment) {
        requestPermission(fragment, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE},
                REQ_CODE_READ_EXTERNAL_STORAGE);
    }

    public static void requestWriteExternalStoragePermission(Fragment fragment) {
        requestPermission(fragment, new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQ_CODE_WRITE_EXTERNAL_STORAGE);
    }

    public static void requestCameraPermission(Fragment fragment) {
        requestPermission(fragment, new String[] {Manifest.permission.CAMERA},
                REQ_CODE_CAMERA);
    }
}
