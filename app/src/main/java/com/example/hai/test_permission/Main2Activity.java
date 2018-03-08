package com.example.hai.test_permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class Main2Activity extends BasePermissionActivity {
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        findViewById(R.id.tv_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone();
            }
        });

        findViewById(R.id.tv_open2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loc();
            }
        });
    }

    private void loc() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission_loc();
        } else {

        }
    }


    void phone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, new RequestPermissionCallBack() {
                @Override
                public void granted() {
                    callPhone();
                }

                @Override
                public void denied() {
                    finish();
                }
            });
            //permission_phone();
        } else {//6.0以前，直接打电话
            callPhone();
        }
    }

    private void permission_loc() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("TAG", "权限未开启");
            // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, 4);

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //如果第一次拒绝后，可以提示用户需要本权限的原因
                Log.e("TAG", "禁止后提示");
                Toast.makeText(activity, "本产品需要通过定位权限获取位置信息", Toast.LENGTH_SHORT).show();
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            } else {
                Log.e("TAG", "勾选了不再询问");
            }
            //else {
            // No explanation needed, we can request the permission.
            Log.e("TAG", "权限未开启2");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);

            // }
        } else {

        }
    }

    public final static int REQUEST_PERMISSION_CALL_PHONE = 101;
    public final static int REQUEST_PERMISSION_LOCATION = 102;

    private void permission_phone() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.e("TAG", "电话权限被禁止");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                new AlertDialog.Builder(activity).setMessage("应用打电话需要拨打电话权限，请允许").setNegativeButton("好", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL_PHONE);
                        dialog.dismiss();
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL_PHONE);
            }
        } else {
            Log.e("TAG", "电话权限通过");
            callPhone();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "获取到电话权限", Toast.LENGTH_SHORT).show();
                    callPhone();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {//用户选择了不再提示
                        Toast.makeText(activity, "你居然点了不再询问！", Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(this)
                                .setMessage("【用户选择了不在提示按钮，或者系统默认不在提示（如MIUI）。" +
                                        "引导用户到应用设置页去手动授权,注意提示用户具体需要哪些权限】\r\n" +
                                        "获取相关权限失败:xxxxxx,将导致部分功能无法正常使用，需要到设置页面手动授权")
                                .setPositiveButton("去授权", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //引导用户至设置页手动授权
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, 300);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //引导用户手动授权，权限请求失败
                                        finish();
                                    }
                                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                //引导用户手动授权，权限请求失败
                                finish();
                            }
                        }).show();

                    } else {
                        Toast.makeText(activity, "获取电话权限失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case REQUEST_PERMISSION_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "获取到定位权限", Toast.LENGTH_SHORT).show();
                    callPhone();
                } else {
                    Toast.makeText(activity, "获取定位权限失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 拨打电话
     */
    private void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);//直接拨打电话，需要权限
        // Intent intent = new Intent(Intent.ACTION_DIAL);//跳到拨号界面，不需要权限
        Uri data = Uri.parse("tel:" + "10086");
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                callPhone();
            } else {
                finish();
            }
        }
    }
}
