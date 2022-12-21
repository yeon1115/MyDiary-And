package com.google.firebase.quickstart.database.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.quickstart.database.R;

public class CommonPermissionsUtil {
    public static final String TAG = CommonPermissionsUtil.class.getSimpleName();
    public static int PERMISSION_REQUEST_CODE = 451;
    //permissions we need
    public static final String[] permissions = new String[]{
//            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
            , Manifest.permission.RECORD_AUDIO};


    public static final String[] videoCallPermissions = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    public static final String[] permissions_read_storage = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE};


    //check if user granted all permissions
    public static boolean permissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public static boolean hasVideoCallPermissions(Context context) {
        if (context != null && permissions != null) {
            for (String permission : videoCallPermissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasVoiceCallPermissions(Context context) {
        if (context != null && permissions != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                return false;

            }
        }
        return true;
    }

    //check if the permissions granted or not (without request permissions from user)
    public static boolean hasPermissions(Context context) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasLocationPermissions(Context context) {
        if (context != null && permissions != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasStoragePermissions(Context context) {
        if (context != null && permissions != null) {
            for (String permission : permissions_read_storage) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasWindowOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                return false;
            }
        }
        return true;
    }

    public static void showPermissionNotice(Activity activity, final @NonNull String[] permissions) {
        try{
            Log.d(TAG, "showPermissionNotice S===>");
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
            final ScrollView scrollView = (ScrollView) View.inflate(activity, R.layout.permission_notice_storage, null);

            //builder.setTitle(arrayListPermission != null ? R.string.permission_notice_title : R.string.privacy_policy_update_title);
            builder.setView(scrollView);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(
                            activity,
                            permissions,
                            PERMISSION_REQUEST_CODE
                    );
                }
            });

            // ===================================================================
            // For Google Featured
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    //AppState.appIsRun = false;
                    //finish();
                }
            });
            // ===================================================================
            builder.show();
            Log.d(TAG, "showPermissionNotice <===E");
        }catch (Throwable t){
            Log.d(TAG, "showPermissionNotice e::\n"+t.getMessage());
        }

    }

    public static void showPermissionDenyAlertDialog(Activity activity, final @NonNull String content, int requestCode) {

        if (content != null) {
            String message = content;

            new AlertDialog.Builder(activity)
                    .setMessage(Html.fromHtml(message))
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    // global_action_settings,
                    // media_route_chooser_extended_settings
                    // notification_app_name_settings
                    .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            goAppSettings(activity, requestCode);
                        }
                    })
                    .show();
        }
    }

    public static void goAppSettings(Activity activity, int requestCode){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, requestCode);
    }
}
