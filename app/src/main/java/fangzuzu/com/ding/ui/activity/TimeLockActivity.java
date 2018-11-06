package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.enumtype.Operation;
import fangzuzu.com.ding.utils.screenAdapterUtils;

import static fangzuzu.com.ding.MainApplication.bleSession;
import static fangzuzu.com.ding.MainApplication.mTTLockAPI;

/**
 * 同步时间
 * Created by lingyuan on 2018/6/22.
 */

public class TimeLockActivity extends BaseActivity implements MainApplication.BleOperSynchronizationkTimeListenner {
    Toolbar toolbar;
    TextView tv_time;
    ProgressDialog progressDialog;

    boolean isKitKat = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        setContentView(R.layout.time_lock_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();

        initlize();
       //初始化蓝牙

MainApplication.BleOperSynchronizationkTimeListenne(this);


    }

    protected void setStatusBar() {
        if (isKitKat){

            int statusH = screenAdapterUtils.getStatusHeight(this);
            //获取ToolBar的布局属性，设置ToolBar的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)toolbar.getLayoutParams();
            params.height = params.height + statusH;
            toolbar.setLayoutParams(params);
            //设置ToolBar的PaddingTop属性
            toolbar.setPadding(0, statusH, 0, 0);
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Log.d("TAG","普通");
        }
    }



    private void initlize() {
        tv_time= (TextView) findViewById(R.id.time_lock);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());

        String format = simpleDateFormat.format(date);

        long time=System.currentTimeMillis();//获取系统时间的10位的时间戳
        Log.d("TAG","当前时间撮"+time);
        tv_time.setText(format);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
//同步时间按钮
    public void butClick(View view) {
        showProgressDialog("","连接蓝牙");
        String tTmac = getIntent().getStringExtra("TTmac");
        if (mTTLockAPI.isBLEEnabled(MainApplication.getInstence())){
            mTTLockAPI.connect(tTmac);
            bleSession.setOperation(Operation.SET_LOCK_TIME);
            bleSession.setLockmac(tTmac);
        }else {
            //打开蓝牙设备
            mTTLockAPI.requestBleEnable(TimeLockActivity.this);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

     //   mBleController.closeBleConn();

    }

    public  void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(TimeLockActivity.this, title, message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void bleOperSynchronizationkTime() {
        // 同步时间成功回调
        hideProgressDialog();
        show_Toast("同步时间成功");
        finish();
    }
}

