package com.hualala.grantdemo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.unistrong.yang.zb_permission.Permission;
import com.unistrong.yang.zb_permission.ZbPermission;
import com.unistrong.yang.zb_permission.ZbPermissionFail;
import com.unistrong.yang.zb_permission.ZbPermissionSuccess;

public class MainActivity2 extends AppCompatActivity {

    private final int REQUEST_CONTACT = 50;
    private final int REQUEST_STORAGE = 100;
    private final int REQUEST_CAMERA = 200;
    private Button bt_request_storage;
    private Button bt_request_camera;
    private Button bt_request_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
    }

    private void initView() {
        bt_request_contact = (Button) findViewById(R.id.bt_request_contact);
        bt_request_camera = (Button) findViewById(R.id.bt_request_camera);
        bt_request_storage = (Button) findViewById(R.id.bt_request_storage);
    }

    private void setListener() {
        bt_request_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //request()有参数ZbPermissionCallback且不为空的时候，权限回调的时候会调用响应的接口的方法。否则调用相应的注解方法
                ZbPermission.with(MainActivity2.this)
                        .addRequestCode(REQUEST_CONTACT)
                        .permissions(Manifest.permission_group.CALENDAR, Manifest.permission_group.LOCATION, Manifest.permission_group.PHONE)
                        .request(/*new ZbPermission.ZbPermissionCallback() {
                            @Override
                            public void permissionSuccess(int requestCode) {
                                Toast.makeText(MainActivity.this, "成功授予Contact权限: " + requestCode, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void permissionFail(int requestCode) {
                                Toast.makeText(MainActivity.this, "成功授予Contact拍照权限: " + requestCode, Toast.LENGTH_SHORT).show();
                            }
                        }*/);
            }
        });

        bt_request_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //needPermission()方法没有ZbPermissionCallback作为参数的时候，回去调用相应的注解方法
                ZbPermission.needPermission(MainActivity2.this, REQUEST_STORAGE, Permission.STORAGE);
            }
        });

        bt_request_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //needPermission()方法有ZbPermissionCallback对象作为参数且不为空的时候，权限回调的时候会调用相应的接口的方法。否则调用相应的注解方法
                ZbPermission.needPermission(MainActivity2.this, REQUEST_CAMERA, Permission.CAMERA, new ZbPermission.ZbPermissionCallback() {
                    @Override
                    public void permissionSuccess(int requestCode) {
                        toast("成功授予拍照权限: " + requestCode);
                    }

                    @Override
                    public void permissionFail(int requestCode) {
                        toast("授予拍照权限失败: " + requestCode);
                    }
                });
            }
        });
    }

    @ZbPermissionSuccess(requestCode = REQUEST_STORAGE)
    public void permissionSuccess() {
        toast("成功授予读写权限注解");
    }

    @ZbPermissionFail(requestCode = REQUEST_STORAGE)
    public void permissionFail() {
        toast("授予读写权限失败注解");
    }

    @ZbPermissionSuccess(requestCode = REQUEST_CONTACT)
    public void permissionSuccessContact() {
        toast("成功授予Contact权限注解");
    }

    @ZbPermissionFail(requestCode = REQUEST_CONTACT)
    public void permissionFailContact() {
        toast("授予Contact权限失败注解");
    }

    private void toast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ZbPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
