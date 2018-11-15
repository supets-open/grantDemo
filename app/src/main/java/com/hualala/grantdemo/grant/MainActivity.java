package com.hualala.grantdemo.grant;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.hualala.grantdemo.R;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity implements PermissionInterface {

    private PermissionHelper mPermissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        VideoView videoView = findViewById(R.id.player);
//        videoView.setVideoURI(Uri.parse("https://caidanmao-recruit-test-1256963883.cos.ap-beijing.myqcloud.com/videos/clti9uhsaw"));
//        videoView.start();
//        // ((ImageView) findViewById(R.id.image)).setImageBitmap(createVideoThumbnail("https://caidanmao-recruit-test-1256963883.cos.ap-beijing.myqcloud.com/videos/qv5y9ipsgg", MediaStore.Images.Thumbnails.MINI_KIND));
//        ((ImageView) findViewById(R.id.image)).setImageBitmap(createVideoThumbnail("https://caidanmao-recruit-test-1256963883.cos.ap-beijing.myqcloud.com/videos/clti9uhsaw", MediaStore.Images.Thumbnails.MINI_KIND));

        //初始化并发起权限申请
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getPermissionsRequestCode() {
        //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可。
        return 15;
    }

    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
    }

    @Override
    public void requestPermissionsSuccess() {
        //权限请求用户已经全部允许
        initViews();
        Toast.makeText(this, "权限请求用户已经全部允许", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
        Toast.makeText(this, "requestPermissionsFail", Toast.LENGTH_SHORT).show();
        //用户拒绝，勾选了不再询问，只能跳转或者用户退出
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
            Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
        } else {
            //没有勾选，可以提示退出或者提示权限的用途并重新发起权限申请
            //finish();
            Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
        }


    }

    private void initViews() {
        //已经拥有所需权限，可以放心操作任何东西了

    }


    public static final int REQUEST_PERMISSION_SETTING = 0x1011;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (PermissionUtil.getDeniedPermissions(this, getPermissions()) == null) {
                //权限请求用户已经全部允许
                //mPermissionHelper.requestPermissions();
                Toast.makeText(this, "权限请求用户已经全部允许2", Toast.LENGTH_SHORT).show();
            } else {
                // finish();
                Toast.makeText(this, "fail2", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Bitmap createVideoThumbnail(String filePath, int kind) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (filePath.startsWith("http://")
                    || filePath.startsWith("https://")
                    || filePath.startsWith("widevine://")) {
                retriever.setDataSource(filePath, new Hashtable<String, String>());
            } else {
                retriever.setDataSource(filePath);
            }
            bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC); //retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
                ex.printStackTrace();
            }
        }

        if (bitmap == null) {
            return null;
        }

        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {//压缩图片 开始处
            // Scale down the bitmap if it's too large.
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);
            if (max > 512) {
                float scale = 512f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }//压缩图片 结束处
        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                    96,
                    96,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

}