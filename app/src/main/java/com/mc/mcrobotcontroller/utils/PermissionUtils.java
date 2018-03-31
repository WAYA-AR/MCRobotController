package com.mc.mcrobotcontroller.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.mc.mcrobotcontroller.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mcharfi on 31/03/2018.
 */

public class PermissionUtils {


    public static int PERMISSION_CODE = 123;

    private Activity activity;
    private List<String> permission_list = new ArrayList<>();
    private OnPermissionGrantedHandler handler;
    private List<String> listPermissionsNeeded = new ArrayList<>();

    public PermissionUtils(Activity activity, List<String> permission_list, OnPermissionGrantedHandler handler) {
        this.activity = activity;
        this.permission_list = permission_list;
        this.handler = handler;
    }

    public void askPermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            handler.onPermissionsGranted();
            return;
        }

        if (permission_list.size() > 0) {
            listPermissionsNeeded = new ArrayList<>();

            for (int i = 0; i < permission_list.size(); i++) {
                int hasPermission = ContextCompat.checkSelfPermission(activity, permission_list.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permission_list.get(i));
                }

            }

            //All permissions are already granted
            if (listPermissionsNeeded.isEmpty()) {
                handler.onPermissionsGranted();
                return;
            }

            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_CODE);
        }

    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode != PERMISSION_CODE) {
            return;
        }

        if (grantResults.length > 0) {
            Map<String, Integer> perms = new HashMap<>();

            for (int i = 0; i < permissions.length; i++) {
                perms.put(permissions[i], grantResults[i]);
            }

            final ArrayList<String> pending_permissions = new ArrayList<>();
            final ArrayList<String> denied_permissions = new ArrayList<>();

            for (int i = 0; i < listPermissionsNeeded.size(); i++) {
                if (perms.get(listPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, listPermissionsNeeded.get(i)))
                        pending_permissions.add(listPermissionsNeeded.get(i));
                    else {
                        denied_permissions.add(listPermissionsNeeded.get(i));
                    }
                }

            }

            if (pending_permissions.size() > 0) {
                askPermissions();
                return;
            }

            if (denied_permissions.size() > 0) {

                String title = activity.getString(R.string.permission_needed_title);
                String message = "";
                if (denied_permissions.size() == 1) {
                    message = String.format(activity.getString(R.string.permission_needed_message), denied_permissions.get(0));
                } else {
                    String arg1 = "";
                    for (int i = 0; i < denied_permissions.size() - 1; i++) {
                        arg1 += denied_permissions.get(i) + ", ";
                    }
                    String arg2 = denied_permissions.get(denied_permissions.size() - 1);
                    message = String.format(activity.getString(R.string.permission_needed_plural_message), arg1, arg2);
                }

                AlertDialog dialog = new AlertDialog.Builder(activity)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                intent.setData(uri);
                                activity.startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    activity.finishAffinity();
                                }else{
                                    activity.finish();
                                }
                            }
                        })
                        .create();
                dialog.show();
                return;
            }
            handler.onPermissionsGranted();
        }
    }
/*
    public static void _askPermissions(final Activity activity, final OnPermissionGrantedHandler handler) {
        Dexter.withActivity(activity).withPermissions(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    handler.onPermissionsGranted();
                                }
                            });
                            return;
                        }
                        List<PermissionDeniedResponse> deniedPermissions = report.getDeniedPermissionResponses();
                        if (deniedPermissions == null || deniedPermissions.isEmpty()) {
                            return;
                        }
                        String title = activity.getString(R.string.permission_needed_title);
                        String message = "";
                        if (deniedPermissions.size() == 1) {
                            message = String.format(activity.getString(R.string.permission_needed_message), deniedPermissions.get(0).getPermissionName());
                        } else {
                            String arg1 = "";
                            for (int i = 0; i < deniedPermissions.size() - 1; i++) {
                                arg1 += deniedPermissions.get(i).getPermissionName() + ", ";
                            }
                            String arg2 = deniedPermissions.get(deniedPermissions.size() - 1).getPermissionName();
                            message = String.format(activity.getString(R.string.permission_needed_plural_message), arg1, arg2);
                        }

                        AlertDialog dialog = new AlertDialog.Builder(activity)
                                .setTitle(title)
                                .setMessage(message)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PermissionUtils.askPermissions(activity, handler);
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                        intent.setData(uri);
                                        activity.startActivity(intent);
                                    }
                                })
                                .create();
                        dialog.show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }*/

    public interface OnPermissionGrantedHandler {
        void onPermissionsGranted();
    }
}
