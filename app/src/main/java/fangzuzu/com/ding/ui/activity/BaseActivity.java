package fangzuzu.com.ding.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;

/**
 * Created by lingyuan on 2018/6/20.
 */

public class BaseActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_REQ_CODE = 1;
    private MainApplication application;
    private BaseActivity oContext;
    public ProgressDialog progressDialog;

    private Handler handler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (application == null) {
            // 得到Application对象
            application = (MainApplication) getApplication();
        }
        oContext = this;// 把当前的上下文对象赋值给BaseActivity
        addActivity();// 调用添加方法
        handler = new Handler();
    }

    // 添加Activity方法
    public void addActivity() {
        application.addActivity_(oContext);// 调用myApplication的添加Activity方法
    }
    //销毁当个Activity方法
    public void removeActivity() {
        application.removeActivity_(oContext);// 调用myApplication的销毁单个Activity方法
    }
    //销毁所有Activity方法
    public  void removeALLActivity() {
        application.removeALLActivity_();// 调用myApplication的销毁所有Activity方法
    }

    /* 把Toast定义成一个方法  可以重复使用，使用时只需要传入需要提示的内容即可*/
    public void show_Toast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(oContext, text, Toast.LENGTH_SHORT).show();
            }
        });

    }



    /**
     * (permission request)
     * @param permission
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//                LogUtil.w("not grant", true);
                return false;
            }
            //(permission request)
            requestPermissions(new String[]{permission}, REQUEST_PERMISSION_REQ_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION_REQ_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[0]))
                 //   LogUtil.d("(Location permission granted)", DBG);
                MainApplication.mTTLockAPI.startBTDeviceScan();
            } else {
              //  LogUtil.w("Permission denied.", DBG);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelProgressDialog();

    }

    public void start_activity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
    public void showProgressDialog() {
        showProgressDialog(getString(R.string.words_wait));
    }

    public void showProgressDialog(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(progressDialog == null) {
                    progressDialog = new ProgressDialog(BaseActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                }
                progressDialog.setMessage(msg);
                progressDialog.show();
            }
        });
    }

    public  void cancelProgressDialog() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(progressDialog != null)
                    progressDialog.cancel();
            }
        });
    }
}
