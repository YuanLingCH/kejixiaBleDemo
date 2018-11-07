package fangzuzu.com.ding.ui.activity;

import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.util.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.enumtype.Operation;
import fangzuzu.com.ding.event.createtimeMessage;
import fangzuzu.com.ding.event.losetimeMessage;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import fangzuzu.com.ding.widget.DatePicier;

import static fangzuzu.com.ding.MainApplication.mTTLockAPI;

/**
 * Created by lingyuan on 2018/9/19.
 */

public class modificationKeyLockMessageActivity extends BaseActivity implements MainApplication.BleOperaddmodificationIcTListenner,MainApplication.BleOpermodificationkFingerTimeListenner{
    TextView tv_start_time,tv_end_time;
    LinearLayout ll_start_time,ll_end_time;
    Button but_time;
    Toolbar toolbar;
    boolean isKitKat = false;
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
        setContentView(R.layout.modification_key_activity_layout);

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EventBus.getDefault().register(this);

        getIntentData();
        initViews();
        DatePicier.initDatePicker(tv_start_time, tv_end_time, modificationKeyLockMessageActivity.this);
        setStatusBar();
        initEvent();
        MainApplication.SetBleOperBleOperaddmodificationIcTListenner(this);
        MainApplication.BleOpermodificationkFingerTimeListenne(this);



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
    /**
     * 点击事件
     */
    private void initEvent() {
        tv_start_time.setText(getstartTime);
        tv_end_time.setText(getendTime);
        Log.d("TAG","getstartTime"+getstartTime);
        Log.d("TAG","getendTime"+getendTime);

        // 点击开始时间
        ll_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePicier.getCustomDatePicker1().show(tv_start_time.getText().toString());
            }
        });
        // 点击结束时间
        ll_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicier.getCustomDatePicker2().show(tv_end_time.getText().toString());
            }
        });
        /**
         * 点击改时间
         */


        but_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  kaishi=null;
                if (!StringUtils.isEmpty(createtime)){  //  endtime  kaishi 时间

                if (StringUtils.isEmpty(endtime)){
                    kaishi=getstartTime;
                }else {
                    kaishi=endtime;
                }
                    final String  s = unixTime.dateToStampone( kaishi);
                    Log.d("TAG","开始时间戳"+s);
                    String substring1 = s.substring(0, s.length() - 3);
                    int startTime = Integer.parseInt(substring1);
                    Log.d("TAG","开始时间"+ startTime);

//  currentDate
                    final String send = unixTime.dateToStampone(createtime);
                    Log.d("TAG","结束时间戳"+send);
                    String substring1end = send.substring(0, send.length() - 3);
                    int endTime = Integer.parseInt(substring1end);
                    Log.d("TAG","结束时间"+ endTime);
                    if (startTime<endTime){

                        Log.d("TAG","cardNumber"+ cardNumber);

                            if (!StringUtils.isEmpty(cardNumber)){   //修改ic卡s时间
                                if (mTTLockAPI.isBLEEnabled(MainApplication.getInstence())){
                                    String mac = MainApplication.getInstence().getMac();
                                    showProgressDialog("连接蓝牙...");
                                    mTTLockAPI.startBTDeviceScan();
                                    if (type.equals("0")){
                                        MainApplication.bleSession.setOperation(Operation.MODIFY_ICPEROID);
                                    }else {
                                        //  指纹   MODIFY_FINGER_PRINT_PEROID
                                        MainApplication.bleSession.setOperation(Operation.MODIFY_FINGER_PRINT_PEROID);
                                    }

                                    MainApplication.bleSession.setLockmac(mac);
                                    MainApplication.bleSession.setCardNo(Long.parseLong(cardNumber));
                                    MainApplication.bleSession.setEndDate(Long.parseLong(send));
                                    MainApplication.bleSession.setStartDate(Long.parseLong(s));
                                 //   MainApplication.bleSession.setFlag(true);  //永久


                                }else {
                                    //打开蓝牙设备
                                    mTTLockAPI.requestBleEnable(modificationKeyLockMessageActivity.this);
                                }
                            }else {


                                final int keyID = Integer.parseInt(ttKeyId);





                        new AsyncTask<Void, Integer, String>() {
                            @Override
                            protected String doInBackground(Void... params) {


                                String TTjson = ResponseService.modificationKeyeffectivityTimeData(keyID, Long.parseLong(s), Long.parseLong(send));
                                Log.d("TAG","接收json"+TTjson );
                                String msg = "";
                                try {
                                    JSONObject jsonObject = new JSONObject(TTjson);
                                    if(jsonObject.getInt("errcode") == 0) {
                                        msg = getString(R.string.modification_ekey_successed);
                                        //delete local key
                                        //  DbService.deleteKey(key);
                                    } else msg = jsonObject.getString("description");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return msg;
                            }

                            @Override
                            protected void onPostExecute(String msg) {
                                super.onPostExecute(msg);
                              Toast.makeText(modificationKeyLockMessageActivity.this,msg, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }.execute();

                            }

                    }else {
                        Toast.makeText(MainApplication.getInstence(),"失效时间必须大于生效时间",Toast.LENGTH_LONG).show();
                    }
            }else {
                    Toast.makeText(MainApplication.getInstence(),"请选择时间",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void initViews() {
        tv_start_time=(TextView) findViewById(R.id.tv_start_time);
        tv_end_time=(TextView)findViewById(R.id.tv_end_time);
        ll_start_time=(LinearLayout) findViewById(R.id.ll_start_time);
        ll_end_time=(LinearLayout) findViewById(R.id.ll_end_time);

        but_time=(Button) findViewById(R.id.but_time);

    }
    String getstartTime;
    String getendTime;
    String id;
    String keyName;
    String ttKeyId;
    String cardNumber; //ic卡号
    String unlockName;  //卡名称
    String type;  //  1为指纹  0 ic卡
    public void getIntentData() {
        getstartTime = getIntent().getStringExtra("startTime");
        getendTime = getIntent().getStringExtra("endTime");
        keyName = getIntent().getStringExtra("keyName");
        id = getIntent().getStringExtra("id");  // TTKeyId
         ttKeyId = getIntent().getStringExtra("TTKeyId");
       cardNumber = getIntent().getStringExtra("cardNumber");
      unlockName = getIntent().getStringExtra("unlockName");
         type = getIntent().getStringExtra("type");
        Log.d("TAG"," unlockName"+ unlockName);
    }
    String endtime;

    //失效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(losetimeMessage event){
        endtime = event.getTime();
      //  tv_end_time.setText(endtime);

        Log.d("TAG","event1"+endtime);
    }

    String createtime;
    //生效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBuscreate(createtimeMessage event){
        createtime = event.getTime().toString().trim();
      //  tv_start_time.setText( createtime);
        Log.d("TAG","event2"+createtime);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    @Override
    public void bleOperaddmodificationIcTime(Error error, long l) {
        Log.d("TAG","修改Ic卡回调成功error"+error+"l"+l);
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPostExecute(String json) {
                super.onPostExecute(json);
                Log.d("TAG","json"+json);
                LogUtil.d("json:" + json, true);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String cardId = jsonObject.getString("cardId");
                    String msg;
                    if(!StringUtils.isEmpty(cardId)) {
                        msg = "修改时间成功";
                        Toast.makeText(MainApplication.getInstence(), msg, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        msg = "delete passcode successed by server";
                          /*  keyboardPwds.remove(position);
                            notifyDataSetChanged();*/



                    }
                    cancelProgressDialog();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                String tTlockId = MainApplication.getInstence().getTTlockId();
                int id = Integer.parseInt(tTlockId);

                String  kaishi=null;
                if (!StringUtils.isEmpty(createtime)) {  //  endtime  kaishi 时间

                    if (StringUtils.isEmpty(endtime)) {
                        kaishi = getstartTime;
                    } else {
                        kaishi = endtime;
                    }
                    final String  s = unixTime.dateToStampone( kaishi);
                    final String send = unixTime.dateToStampone(createtime);
                    String json = ResponseService.AddIcCard(id, unlockName, cardNumber,Long.parseLong(s),Long.parseLong(send));  //1  表示通过蓝牙  2 网管
                    return json;
                }

            return null;
            }
        }.execute();
    }

    /**
     * 修改指纹成功回调
     */
    @Override
    public void bleOpermodificationkFingerTTime() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPostExecute(String json) {
                super.onPostExecute(json);
                Log.d("TAG","json"+json);
                LogUtil.d("json:" + json, true);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String cardId = jsonObject.getString("cardId");
                    String msg;
                    if(!StringUtils.isEmpty(cardId)) {
                        msg = "修改时间成功";
                        Toast.makeText(MainApplication.getInstence(), msg, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        msg = "delete passcode successed by server";
                          /*  keyboardPwds.remove(position);
                            notifyDataSetChanged();*/



                    }
                    cancelProgressDialog();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                String tTlockId = MainApplication.getInstence().getTTlockId();
                int id = Integer.parseInt(tTlockId);

                String  kaishi=null;
                if (!StringUtils.isEmpty(createtime)) {  //  endtime  kaishi 时间

                    if (StringUtils.isEmpty(endtime)) {
                        kaishi = getstartTime;
                    } else {
                        kaishi = endtime;
                    }
                    final String  s = unixTime.dateToStampone( kaishi);
                    final String send = unixTime.dateToStampone(createtime);
                    String json = ResponseService.AddFinger(id, unlockName, cardNumber,Long.parseLong(s),Long.parseLong(send));  //1  表示通过蓝牙  2 网管
                    return json;
                }

                return null;
            }
        }.execute();
    }
}
