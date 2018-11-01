package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hansion.h_ble.BleController;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.TTlockListBean;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.bean.userLockBean;
import fangzuzu.com.ding.enumtype.Operation;
import fangzuzu.com.ding.impl.OnMqttListener;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.presenter.MqttPresenter;
import fangzuzu.com.ding.utils.NetWorkTesting;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static fangzuzu.com.ding.MainApplication.bleSession;
import static fangzuzu.com.ding.MainApplication.mTTLockAPI;

/**
 * 打开蓝牙检查
 * Created by lingyuan on 2018/6/20.
 */

public class keySetActivity extends BaseActivity implements OnMqttListener,MainApplication.BleOperDeleteLockListenner{
    List dataPart=new ArrayList();
    List data3;

    Toolbar toolbar;
    TextView tv_time;
    ProgressDialog progressDialog;
    private BleController mBleController;
    LinearLayout ll_set_managerPasw;
    TextView set_keymanager,tv_factory_reset,name_lock,lock_elect,mac;
    RelativeLayout rl;
    String uid; //蓝牙管理员
    String adminUserId;//锁的

    boolean isKitKat = false;
    LinearLayout  ll_dfu;
    View v_1,v_2;
    TextView youxiaoqi;
    private String tTelectricQuantity;
    private String tTlockAlias;
    String noKeyPwd;
    String TTmac;
    private String TTuserType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        setContentView(R.layout.key_set_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        mBleController = BleController.getInstance().init(keySetActivity.this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        initgetInintentData();
        initlize();
        MainApplication.setBleOperDeleteLockListenner(this);

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


    TTlockListBean.KeyListBean.LockVersionBean lockVersion;
    String lockFlagPos;
    String timezoneRawOffset;
    String aesKeyStr;
    String lockKey;
    String adminPwd;

    private void initgetInintentData() {
         uid = SharedUtils.getString("uid");
      adminUserId = SharedUtils.getString("adminUserId");   //  TTelectricQuantity
        Log.d("TAG","uid"+uid);
        Log.d("TAG","adminUserId"+adminUserId);
        tTelectricQuantity = getIntent().getStringExtra("TTelectricQuantity");
        tTlockAlias = getIntent().getStringExtra("TTlockAlias");
        noKeyPwd = getIntent().getStringExtra("noKeyPwd");
        TTuserType = getIntent().getStringExtra("TTuserType");
        lockVersion = (TTlockListBean.KeyListBean.LockVersionBean)getIntent().getSerializableExtra("lockVersion");
        lockFlagPos =getIntent().getStringExtra("lockFlagPos");
        timezoneRawOffset =getIntent().getStringExtra("timezoneRawOffset");
        aesKeyStr = getIntent().getStringExtra("aesKeyStr");
        lockKey = getIntent().getStringExtra("lockKey");
        adminPwd = getIntent().getStringExtra("adminPwd");
        Log.d("TAG"," lockFlagPos"+ lockFlagPos);
    }

    String id;
    private void initlize() {
       id = getIntent().getStringExtra("id");
        v_1=(View) findViewById(R.id.v_1);
        v_2=(View) findViewById(R.id.v_2);
        set_keymanager= (TextView) findViewById(R.id.set_keymanager);
        tv_time= (TextView) findViewById(R.id.tv_time_clock);
        youxiaoqi=(TextView) findViewById(R.id.youxiaoqi);
        String startTime = MainApplication.getInstence().getStartTime();
        String endTime = MainApplication.getInstence().getEndTime();
        if (!startTime.equals("0")&&!endTime.equals("0")){
            youxiaoqi.setText("限时");
        }else {

            youxiaoqi.setText("永久");
        }
        ll_set_managerPasw= (LinearLayout) findViewById(R.id.ll_set_managerPasw);
        //同步时钟
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(keySetActivity.this,TimeLockActivity.class);
                intent.putExtra("TTmac",TTmac);
                startActivity(intent);
            }
        });
        ll_set_managerPasw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (uid.equals(adminUserId)){
                        View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                        final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                        TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                        final EditText et= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
                        final TextView tv_tishi = (TextView) viewDialog.findViewById(R.id.tv);
                        tv_tishi.setText("密码验证");

                        // tv.setText("谨慎操作，导致数据丢失...");
                        // tv.setTextColor(Color.RED);
                        tv.setVisibility(View.GONE);
                        tv.setGravity(Gravity.CENTER);
                        TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                        final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                                .setView(viewDialog)
                                .create();
                        Window window=dialog.getWindow();
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        final String pasw = SharedUtils.getString("pasw");
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
                                dialog.dismiss();
                                String pas = et.getText().toString().trim();
                                if (pas.equals(pasw)){

                                    // tv_pasw.setText(content);
                                    //跳到修改管理员密码界面
                                    Intent intent =new Intent(keySetActivity.this,upDataManagerPaswActivity.class);
                                    startActivity(intent);

                                }else {
                                    Toast.makeText(keySetActivity.this,"你的密码错误", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }else {

                        View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                        final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                        TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                        TextView tv1= (TextView) viewDialog.findViewById(R.id.tv);
                        final EditText et= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
                        tv1.setVisibility(View.INVISIBLE);
                        tv.setText("你不是锁管理员，没有权限操作");
                        et.setVisibility(View.INVISIBLE);
                        tv.setTextColor(Color.RED);
                        tv.setGravity(Gravity.CENTER);
                        TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                        final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                                .setView(viewDialog)
                                .create();
                        dialog.show();
                        tv_cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                            }
                        });
                        tv_submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();


                            }
                        });
                    }

            }
        });
        //恢复出厂设置
        tv_factory_reset= (TextView) findViewById(R.id.tv_factory_reset);
        tv_factory_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uid.equals(adminUserId)){

                    View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                    final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                    TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                    final EditText et= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
                    final TextView tv_tishi = (TextView) viewDialog.findViewById(R.id.tv);
                    tv_tishi.setText("密码验证");

                    // tv.setText("谨慎操作，导致数据丢失...");
                    // tv.setTextColor(Color.RED);
                    tv.setVisibility(View.GONE);
                    tv.setGravity(Gravity.CENTER);
                    TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                    final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                            .setView(viewDialog)
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
                    final String pasw = SharedUtils.getString("pasw");
                    tv_cancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });
                    tv_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            String pas = et.getText().toString().trim();
                            if (pas.equals(pasw)){

                                Intent intent =new Intent(keySetActivity.this,factoryResetActivity.class);
                                intent.putExtra("id",id);
                                startActivity(intent);

                            }else {
                                Toast.makeText(keySetActivity.this,"你的密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }else {
                    View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                    final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                    TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                    tv.setText("你不是锁管理员，没有权限操作");
                    TextView tv1= (TextView) viewDialog.findViewById(R.id.tv);
                    tv1.setVisibility(View.INVISIBLE);
                    tv.setTextColor(Color.RED);
                    tv.setGravity(Gravity.CENTER);
                    TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                    final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                            .setView(viewDialog)
                            .create();
                    dialog.show();
                    tv_cancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });
                    tv_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();


                        }
                    });
                }


            }
        });
        rl= (RelativeLayout) findViewById(R.id.rl);
        name_lock= (TextView) findViewById(R.id.name_lock);
        String lockName = MainApplication.getInstence().getLockName();
        name_lock.setText(tTlockAlias);
        lock_elect= (TextView) findViewById(R.id.lock_elect);
        String elect = MainApplication.getInstence().getElect();
        lock_elect.setText(tTelectricQuantity+"%");
            set_keymanager.setText(noKeyPwd);


        TTmac = MainApplication.getInstence().getMac();
        this.mac = (TextView) findViewById(R.id.mac);
        this.mac.setText(TTmac);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框 修改锁名字
        //  if (uid.equals(adminUserId)){
                LayoutInflater inflater = LayoutInflater.from(keySetActivity.this);
                View view = inflater.inflate(R.layout.updata_lock_name, null);
                TextView add_cancle= (TextView) view.findViewById(R.id.add_cancle);
                // add_submit
                TextView add_submit= (TextView) view.findViewById(R.id.add_submit);


                final EditText editText = (EditText) view.findViewById(R.id.dialog_edit_name);
                final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                        .setView(view)
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
                add_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                add_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String trim = editText.getText().toString().trim();
                        if (!StringUtils.isEmpty(trim)){
                            name_lock.setText(trim);
                            dialog.dismiss();
                            upDataLockName(trim);
                        }else {

                            Toast.makeText(keySetActivity.this,"内容不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



/*            }else {
                View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                final EditText et= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
                tv.setText("你不是锁管理员，没有权限操作");
                TextView tv1= (TextView) viewDialog.findViewById(R.id.tv);
                tv1.setVisibility(View.INVISIBLE);
                tv.setTextColor(Color.RED);
                tv.setGravity(Gravity.CENTER);
                et.setVisibility(View.INVISIBLE);
                TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                        .setView(viewDialog)
                        .create();
                dialog.show();
                tv_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                tv_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();



                    }
                });
            }*/

            }
        });



        //ble dfus升级
        ll_dfu=(LinearLayout)findViewById(R.id.ll_dfu);
        if (TTuserType.equals("110301")){
            ll_dfu.setVisibility(View.VISIBLE);
            v_1.setVisibility(View.VISIBLE);
            v_2.setVisibility(View.VISIBLE);
        }

        ll_dfu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                    final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                    TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                    final EditText et= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
                    final TextView tv_tishi = (TextView) viewDialog.findViewById(R.id.tv);
                    tv_tishi.setText("密码验证");

                    // tv.setText("谨慎操作，导致数据丢失...");
                    // tv.setTextColor(Color.RED);
                    tv.setVisibility(View.GONE);
                    tv.setGravity(Gravity.CENTER);
                    TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                    final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                            .setView(viewDialog)
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
                    final String pasw = SharedUtils.getString("pasw");
                    tv_cancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });
                    tv_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            String pas = et.getText().toString().trim();
                            if (pas.equals(pasw)){

                                Intent intent=new Intent(MainApplication.getInstence(),dfuActivity.class);
                                intent.putExtra("lockVersion", lockVersion);
                                intent.putExtra("lockFlagPos", lockFlagPos);
                                intent.putExtra("timezoneRawOffset", timezoneRawOffset);
                                intent.putExtra("aesKeyStr", aesKeyStr);
                                intent.putExtra("lockKey", lockKey);
                                intent.putExtra("adminPwd", adminPwd );
                                startActivity(intent);

                            }else {
                                Toast.makeText(keySetActivity.this,"你的密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });




            }
        });

    }

    /**
     * 修改锁名
     */
    private void upDataLockName(final String lockAlias) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPostExecute(String json) {
                super.onPostExecute(json);
                Log.d("TAG","json"+json);
                LogUtil.d("json:" + json, true);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String cardId = jsonObject.getString("errcode");
                    String msg;
                    if(!StringUtils.isEmpty(cardId)) {
                        msg = "修改成功";
                        Toast.makeText(MainApplication.getInstence(), msg, Toast.LENGTH_SHORT).show();
                    } else {
                        msg = "delete passcode successed by server";
                          /*  keyboardPwds.remove(position);
                            notifyDataSetChanged();*/



                    }
                    cancelProgressDialog();
                    hideProgressDialog();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                String tTlockId = MainApplication.getInstence().getTTlockId();
                int id = Integer.parseInt(tTlockId);
                String json = ResponseService.modificationLockName(id,lockAlias);  //1  表示通过蓝牙  2 网管
                return json;
            }
        }.execute();




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


    public void butClick(View view) {

        final String adminUserId = SharedUtils.getString("adminUserId");
        final String keyId = SharedUtils.getString("keyId");
        Log.d("TAG","修改+点击了我"+adminUserId+":"+ keyId);
        final String lockid = MainApplication.getInstence().getLockid();
        final String mac = MainApplication.getInstence().getMac();
        View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
        final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
        TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
        final EditText et= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
        // tv.setText("谨慎操作，导致数据丢失...");
        // tv.setTextColor(Color.RED);
        tv.setVisibility(View.GONE);
        tv.setGravity(Gravity.CENTER);
        TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
        final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                .setView(viewDialog)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        Window window=dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        WindowManager manager=this.getWindowManager();
        Display defaultDisplay = manager.getDefaultDisplay();
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.width= (int) (defaultDisplay.getWidth()*0.85);
        dialog.getWindow().setAttributes(p);     //设置生效
        final String pasw = SharedUtils.getString("pasw");
        Log.d("TAG","密码"+pasw);

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pas = et.getText().toString().trim();
                if (!StringUtils.isEmpty(pas)){
                    if (pas.equals(pasw)){  //钥匙用户类型：110301-管理员钥匙，110302-普通用户钥匙。
                        if (TTuserType.equals("110301")){


                        Log.d("TAG","删除走了"+pasw);
                        dialog.dismiss();
                        //1 根据当前uid 来判断  和锁里面的uid 是不是蓝牙管理员和普通用户
                        //连接蓝牙 和删除服务器数据
                        if (mTTLockAPI.isBLEEnabled(MainApplication.getInstence())){
                            showProgressDialog("","正在连接蓝牙...");
                        bleSession.setOperation(Operation.RESET_LOCK);
                        bleSession.setLockmac(TTmac);
                            MainApplication.mTTLockAPI.startBTDeviceScan();
                        }else {
                            //打开蓝牙设备
                            mTTLockAPI.requestBleEnable(keySetActivity.this);

                        }
                        }else {
                            // 不是蓝牙管理理员 就直接删除
                            showProgressDialog("","正在连shuju...");
                            Log.d("TAG","不是蓝牙管理员");
                            dialog.dismiss();
                            deleteLockService();
                        }
                    }else {
                        Toast.makeText(keySetActivity.this,"你的密码错误", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }else {
                    Toast.makeText(keySetActivity.this,"请输入密码", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    MqttPresenter mqPre;
    ;public void   upDataDelet(String id){
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.delectKey(id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.d("TAG","删除钥匙"+body);
                Gson gson=new Gson();
                msg m = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                int code = m.getCode();
                if (code==1001){
                    Log.d("TAG","删除成功");
                    Toast.makeText(keySetActivity.this,"删除数据成功", Toast.LENGTH_SHORT).show();

                    getUserLockList();

                    mqPre=new MqttPresenter();
                    mqPre.sendMqtt("az"+uid,keySetActivity.this);
                    mqPre.sendMqtt("ios"+uid,keySetActivity.this);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    @Override
    public void mqttSuccess() {

    }

    @Override
    public void mqttFaild() {

    }




    public  void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, title, message, true, false);
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


    /**
     * 请求网络数据
     */
    String lockName;
    String secretKey;
    String adminPsw;
    String adminUser;
    String electricity;
    String allow;
    String id1;
    String lockNumber;
    public void getUserLockList() {
        data3=new ArrayList();
        String partid = SharedUtils.getString("partid");
        uid= SharedUtils.getString("uid");
        Log.d("TAG","partid"+partid);

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        final NetWorkTesting net=new NetWorkTesting(this);
        if (net.isNetWorkAvailable()){
            Call<String> call = manager.getLockUserList("aa123456", uid);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String body = response.body();
                    if (StringUtils.isEmpty(body)){
                        Log.d("TAG","网络错误"+body);

                    }else {
                        Log.d("TAG","测试一把手锁"+body );
                        Gson gson=new Gson();
                        userLockBean bean = gson.fromJson(body, new TypeToken<userLockBean>() {}.getType());
                        int code = bean.getCode();
                        data3.clear();// 防止加载重复数据
                        if (code==1001){
                            userLockBean.DataBean data = bean.getData();
                            List<?> parentLock = data.getParentLock();
                            Iterator<?> iterator = parentLock.iterator();
                            while (iterator.hasNext()){
                                Object next = iterator.next();
                                dataPart.add(next);
                            }
                            List<userLockBean.DataBean.UserLockBean> userLock = data.getUserLock();
                            Iterator<userLockBean.DataBean.UserLockBean> iterator1 = userLock.iterator();
                            while (iterator1.hasNext()){
                                userLockBean.DataBean.UserLockBean next = iterator1.next();
                                lockName = next.getLockName();
                                secretKey = next.getSecretKey();
                                adminPsw = next.getAdminPsw();
                                adminUserId = next.getAdminUserId();
                                electricity = next.getElectricity();
                                allow = next.getAllow();
                                String keyId = next.getKeyId();
                                SharedUtils.putString("keyId",keyId);
                                id1 = next.getId();
                                lockNumber = next.getLockNumber();
                                Log.d("TAG","锁命"+lockName);
                                data3.add(next);
                            }
                            data3.addAll(dataPart);
                            Log.d("TAG","集合大小"+data3.size());
                            Log.d("TAG",body);
/*
                            if (data3.size()==1){
                                Timer timer=new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(MainApplication.getInstence(),MainActviity.class);
                                        intent.putExtra("id",id1);
                                        intent.putExtra("secretKey",secretKey);
                                        intent.putExtra("allow",allow);
                                        intent.putExtra("electricity",electricity);
                                        intent.putExtra("lockNumber",lockNumber);
                                        intent.putExtra("adminPsw",adminPsw);
                                        intent.putExtra("lockName",lockName);
                                        intent.putExtra("jihe","1");
                                        intent.putExtra("adminUserId",adminUser);
                                        startActivity(intent);
                                        finish();
                                    }
                                },1000);


                            }else {*/
                                Timer timer=new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(MainApplication.getInstence(),lockListActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                },1000);
                           /* }*/


                        }else if(code==1002){
                            Log.d("TAG","网络错误");

                        }

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }else {

            Toast.makeText(keySetActivity.this,"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();



        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * 删除锁的回调
     * @param error
     */
    @Override
    public void bleOperDeleteLock(Error error) {


        if (error==Error.SUCCESS){
            deleteLockService();
        }
    }

    public void deleteLockService(){
        final String keyId = getIntent().getStringExtra("keyId");
        //删除服务器数据
        new Thread(){
            @Override
            public void run() {
                super.run();
                String s = ResponseService.deleteKey(Integer.parseInt(keyId));
                try {
                    JSONObject object=new JSONObject(s);
                   String errcode = object.getString("errcode");
                    if (errcode.equals("0")){
                        hideProgressDialog();
                        show_Toast("删除成功");
                        Intent intent=new Intent(keySetActivity.this,lockListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("TAG",s);
            }
        }.start();
    }

}
