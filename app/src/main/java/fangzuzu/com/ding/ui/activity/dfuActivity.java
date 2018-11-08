package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ttlock.bl.sdk.api.DeviceFirmwareUpdateApi;
import com.ttlock.bl.sdk.callback.DeviceFirmwareUpdateCallback;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.util.GsonUtil;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.bean.TTlockListBean;
import fangzuzu.com.ding.constant.Config;
import fangzuzu.com.ding.model.FirmwareInfo;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.utils.screenAdapterUtils;

import static fangzuzu.com.ding.MainApplication.mTTLockAPI;

/**
 * Created by lingyuan on 2018/8/21.
 */

public class dfuActivity extends BaseActivity {
    Toolbar toolbar;
    boolean isKitKat = false;
    private FirmwareInfo firmwareInfo;
    private boolean checkAgain;
    private String tTlockId;
        TextView tv_state,tv_version;


    ProgressDialog progressDialog;

    private DeviceFirmwareUpdateApi deviceFirmwareUpdateApi;

    private DeviceFirmwareUpdateCallback deviceFirmwareUpdateCallback = new DeviceFirmwareUpdateCallback() {
        @Override
        public void onGetLockFirmware(int specialValue, String module, String hardware, String firmware) {
         //   LogUtil.d("firmwareInfo:" + firmwareInfo, DBG);
            if(firmwareInfo != null) {
                firmwareInfo.specialValue = specialValue;
                firmwareInfo.modelNum = module;
                firmwareInfo.hardwareRevision = hardware;
                firmwareInfo.firmwareRevision = firmware;
                checkAgain();
            }
        }

        @Override
        public void onStatusChanged(final int status) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (status) {
                        case DeviceFirmwareUpdateApi.UpgradeOprationPreparing:
                            tv_state.setText(getString(R.string.words_preparing));
                            break;
                        case DeviceFirmwareUpdateApi.UpgradeOprationUpgrading:
                            tv_state.setText(getString(R.string.words_upgrading));
                            progressDialog = new ProgressDialog(dfuActivity.this);
                            break;
                        case DeviceFirmwareUpdateApi.UpgradeOprationRecovering:
                            tv_state.setText(getString(R.string.words_recovering));
                            break;
                        case DeviceFirmwareUpdateApi.UpgradeOprationSuccess:
                            deviceFirmwareUpdateApi.upgradeComplete();
                            cancelProgressDialog();
                            tv_state.setText(getString(R.string.words_upgrade_successed));
                         //   toast(getString(R.string.words_upgrade_successed));
                            break;
                    }
                }
            });
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
          //  LogUtil.d(deviceAddress, DBG);
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
          //  LogUtil.e("percent:" + percent, DBG);
            cancelProgressDialog();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(percent);
            progressDialog.show();
        }



        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
         //   LogUtil.d("deviceAddress:" + deviceAddress, DBG);
        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
           // LogUtil.d("deviceAddress:" + deviceAddress, DBG);
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
          //  LogUtil.d("deviceAddress:" + deviceAddress, DBG);
            progressDialog.cancel();
            showProgressDialog(getString(R.string.words_recovering));
        }

        @Override
        public void onError(int errorCode, Error error, String errorContent) {
          //  LogUtil.w("errorCode:" + errorCode, DBG);
          //  LogUtil.w("error:" + error, DBG);
         //   LogUtil.w("errorContent:" + errorContent, DBG);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cancelProgressDialog();
                    tv_state.setText(getString(R.string.words_upgrade_failed));
                 //   showRetryDialog();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        setContentView(R.layout.dfu_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        initlize();
        //初始化蓝牙
      mTTLockAPI.stopBTDeviceScan();
        deviceFirmwareUpdateApi = new DeviceFirmwareUpdateApi(this,mTTLockAPI, deviceFirmwareUpdateCallback);
        tv_state=(TextView) findViewById(R.id.tv_state);
        tv_version=(TextView) findViewById(R.id.tv_version);
        tTlockId = MainApplication.getInstence().getTTlockId();

        if(checkAgain){
            getLockFirmware();
        }else {
            checkUpdate();
        }



    }
    TTlockListBean.KeyListBean.LockVersionBean lockVersion;
    String lockFlagPos;
    String timezoneRawOffset;
    String aesKeyStr;
    String lockKey;
    String adminPwd;
  private void initlize() {
      //获取密钥
      lockVersion = (TTlockListBean.KeyListBean.LockVersionBean)getIntent().getSerializableExtra("lockVersion");
      lockFlagPos =getIntent().getStringExtra("lockFlagPos");
      timezoneRawOffset =getIntent().getStringExtra("timezoneRawOffset");
      aesKeyStr = getIntent().getStringExtra("aesKeyStr");
      lockKey = getIntent().getStringExtra("lockKey");
      adminPwd = getIntent().getStringExtra("adminPwd");

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









    /**
     * 点击连接蓝牙检查版本号  固件版本小于服务器版本就升级  ，否则提示用户已是最新版本
     *
     * 连接蓝牙进去dfu模式
     * @param view
     */



    private StringBuilder mStringBuilder;
 TextView tv;
    AlertDialog dialog;
    public void butClick(View view) {



        Log.d("TAG","点击升级");
        // 弹出对话框  提示用户 正在升级


      /*  View viewname = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
       tv = (TextView) viewname.findViewById(R.id.dialog_editname);
        TextView tv_cancle= (TextView) viewname.findViewById(R.id.add_cancle);
        EditText et_yanzhenpasw= (EditText) viewname.findViewById(R.id.et_yanzhenpasw);
        et_yanzhenpasw.setVisibility(View.INVISIBLE);
        TextView tv1= (TextView) viewname.findViewById(R.id.tv);
        tv1.setVisibility(View.INVISIBLE);
      //  mStringBuilder.append("给锁升级,需要一分钟左右，请勿离开锁");
        tv.setText("给锁升级,需要一分钟左右，请勿离开锁");
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER);
        TextView tv_submit= (TextView) viewname.findViewById(R.id.add_submit);
        dialog = new AlertDialog.Builder(dfuActivity.this)
                .setView(viewname)
                .create();
        Window window=dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        WindowManager manager=getWindowManager();
        Display defaultDisplay = manager.getDefaultDisplay();
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.width= (int) (defaultDisplay.getWidth()*0.85);
        dialog.getWindow().setAttributes(p);     //设置生效

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });*/










    }


    private void getLockFirmware() {
        showProgressDialog();
      //  deviceFirmwareUpdateApi.getLockFirmware(mKey.getLockMac(), mKey.getLockVersion(), mKey.getAdminPwd(), mKey.getLockKey(), mKey.getLockFlagPos(), mKey.getAesKeyStr());
//        MyApplication.bleSession.setOperation(Operation.GET_LOCK_VERSION_INFO);
//        MyApplication.bleSession.setLockmac(mKey.getLockMac());
//        mTTLockAPI.connect(mKey.getLockMac());
    }


    private void checkAgain() {
        new AsyncTask<Void, String, FirmwareInfo>() {


            @Override
            protected FirmwareInfo doInBackground(Void... params) {

                String json = ResponseService.isNeedUpdateAgain(Integer.parseInt(tTlockId), firmwareInfo);
                firmwareInfo = GsonUtil.toObject(json, FirmwareInfo.class);
                return firmwareInfo;
            }
            @Override
            protected void onPostExecute(FirmwareInfo firmwareInfo) {
                super.onPostExecute(firmwareInfo);
                cancelProgressDialog();
                if(firmwareInfo.errcode == 0) {
                    switch (firmwareInfo.needUpgrade) {
                        case 0://no need to upgrade
                           tv_state.setText("最新版本");
                            tv_version.setText(firmwareInfo.version);
                            break;
                        case 1://need upgrade
                            tv_state.setText(getString(R.string.new_version_found));
                            tv_version.setText(firmwareInfo.version);
                            showUpgradeDialog();
                            break;
                        case 2://unknown version
                            tv_state.setText(getString(R.string.unknown_lock_version));
                            tv_version.setText(getString(R.string.unknown_lock_version));
                            showGetLockFirmwareDialog();
                            break;
                    }
                } else {
                   // toast(firmwareInfo.errmsg);
                }
            }
        }.execute();
    }
    public void showGetLockFirmwareDialog() {

        getLockFirmware();
    }

    public void showUpgradeDialog() {
        String tTlockId = MainApplication.getInstence().getTTlockId();
        Log.d("TAG","点击升级"+"tTlockId:"+tTlockId+"lockFlagPos:"+lockFlagPos+lockVersion);
        Gson gson=new Gson();
        String s = gson.toJson(lockVersion);
        String mac = MainApplication.getInstence().getMac();
        deviceFirmwareUpdateApi.upgradeFirmware(Config.CLIENT_ID, SharedUtils.getString("access_token"),
                Integer.parseInt(tTlockId), firmwareInfo.modelNum, firmwareInfo.hardwareRevision, firmwareInfo.firmwareRevision, mac,s, adminPwd, lockKey, Integer.parseInt(lockFlagPos),aesKeyStr, Long.parseLong(timezoneRawOffset));

    }
    private void checkUpdate() {
        new AsyncTask<Void, String, FirmwareInfo>() {
            @Override
            protected FirmwareInfo doInBackground(Void... params) {
                String json = ResponseService.isNeedUpdate(Integer.parseInt(tTlockId));
                firmwareInfo = GsonUtil.toObject(json, FirmwareInfo.class);
                return firmwareInfo;
            }
            @Override
            protected void onPostExecute(FirmwareInfo firmwareInfo) {
                super.onPostExecute(firmwareInfo);
                cancelProgressDialog();
                if(firmwareInfo.errcode == 0) {
                    switch (firmwareInfo.needUpgrade) {
                        case 0://no need to upgrade
                            tv_state.setText("已经是最新版本");
                            tv_version.setText(firmwareInfo.version);
                            break;
                        case 1://need upgrade
                            tv_state.setText(getString(R.string.new_version_found));
                            tv_version.setText(firmwareInfo.version);
                            showUpgradeDialog();
                            break;
                        case 2://unknown version
                            tv_state.setText(getString(R.string.unknown_lock_version));
                            tv_version.setText(getString(R.string.unknown_lock_version));
                            showGetLockFirmwareDialog();
//                            checkAgain = true;
                            break;
                    }
                } else {
                  //  toast(firmwareInfo.errmsg);
                }
            }
        }.execute();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    public  void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(dfuActivity.this, title, message, true, false);
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



}
