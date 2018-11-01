package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.enumtype.Operation;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.utils.screenAdapterUtils;

import static fangzuzu.com.ding.MainApplication.mTTLockAPI;

/**
 * Created by lingyuan on 2018/7/20.
 */

public class icLockItemMessageActvity extends BaseActivity implements MainApplication.BleOperDeleteIcTListenner {
    Toolbar toolbar;
    TextView tv_name,tv_addphone,tv_time,tv_ic_add_time;
    Button but_delet;
    ProgressDialog progressDialog;

    boolean isKitKat = false;
    TextView tv_toolbar;
    private String cardNumber;
    private String icCardId;
    private String type;  // 1为指纹  0 ic卡

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
        setContentView(R.layout.ic_item_message_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        getData();//得到数据从上一个界面传过来的
        MainApplication.SetBleOperDeleteIcTListenner(this);


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


    String id;
    String unlockFlag;  //  指纹 ic 身份证  id   16进制的字符串
    String addType;  //  2 指纹    3ic    4身份证
    public void getData() {
        tv_name= (TextView) findViewById(R.id.tv_ic_name);
        tv_addphone= (TextView) findViewById(R.id.tv_ic_phone);
        tv_time= (TextView) findViewById(R.id.tv_ic_time);
        but_delet= (Button) findViewById(R.id.but_ic_delect);
        tv_toolbar=(TextView) findViewById(R.id.tv_toolbar);
        tv_ic_add_time=(TextView) findViewById(R.id.tv_ic_add_time);
        String unlockName = getIntent().getStringExtra("cardName");
        String addPerson = getIntent().getStringExtra("userId");
        final String unlockType = getIntent().getStringExtra("addType");
        String tTcreateTime = getIntent().getStringExtra("TTcreateTime");
        cardNumber = getIntent().getStringExtra("cardNumber");
        icCardId = getIntent().getStringExtra("icCardId");
        type = getIntent().getStringExtra("type");
        tv_ic_add_time.setText(tTcreateTime);
        Log.d("TAG","icCardId"+icCardId);
        tv_name.setText(unlockName);
        tv_addphone.setText(addPerson);
        if (unlockType.equals("0")){
            tv_time.setText("限时");
        }else {
            tv_time.setText("永久");
        }

            tv_toolbar.setText("卡片详情");


        //删除
        but_delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        //1 根据当前uid 来判断  和锁里面的uid 是不是蓝牙管理员和普通用户
                        //连接蓝牙 和删除服务器数据
                String mac = MainApplication.getInstence().getMac();
                long icNumbler = Long.parseLong(cardNumber);
                Log.d("TAG","mac"+mac);
                if (mTTLockAPI.isBLEEnabled(MainApplication.getInstence())){
                    showProgressDialog("连接蓝牙...");

                    mTTLockAPI.connect(mac);
                    Log.d("TAG","type"+type);
                    if (type.equals("1")){ // 1  指纹  0 ic
                        MainApplication.bleSession.setOperation(Operation.DELETE_FINGER);
                    }else {
                        MainApplication.bleSession.setOperation(Operation.DELETE_ICCARD);
                    }

                    MainApplication.bleSession.setLockmac(mac);
                    MainApplication.bleSession.setCardNo(icNumbler);



                }else {
                    //打开蓝牙设备
                    mTTLockAPI.requestBleEnable(icLockItemMessageActvity.this);
                }






            }
        });
    }




    /**
     * 删除服务器数据
     */
    private void delectupData() {


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
    public  void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(icLockItemMessageActvity.this, title, message, true, false);
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
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * 删除IC卡
     * @param error
     * @param l
     */
    @Override
    public void bleOperDeleteIc(Error error, long l) {
        //  icCardId
        cancelProgressDialog();
        if (error==Error.SUCCESS){
            //三处服务器上的数据
            new AsyncTask<Void, Void, String>() {

                @Override
                protected void onPostExecute(String json) {
                    super.onPostExecute(json);
                    Log.d("TAG","json"+json);
                    LogUtil.d("json:" + json, true);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String errcode = jsonObject.getString("errcode");
                        String msg;
                        if(errcode.equals("0")) {
                            msg = "删除成功成功";
                            Toast.makeText(MainApplication.getInstence(), msg, Toast.LENGTH_SHORT).show();
                            finish();
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
                    String json=null;
                    if (type.equals("1")){  // 1指纹
                        json = ResponseService.deleteFinger(id+"", icCardId);
                    }else {
                       json = ResponseService.deleteIcCard(id+"", icCardId);  //1  表示通过蓝牙  2 网管
                    }

                    return json;
                }
            }.execute();
        }else {
            Looper.prepare();
            Toast.makeText(MainApplication.getInstence(), "操作失败", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }
}
