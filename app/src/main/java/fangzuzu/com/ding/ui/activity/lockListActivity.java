package fangzuzu.com.ding.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.util.GsonUtil;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
import fangzuzu.com.ding.adapter.lockListAdapter;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.TTlockListBean;
import fangzuzu.com.ding.bean.UserBean;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.callback.SubcribeCallBackHandler;
import fangzuzu.com.ding.dao.DbService;
import fangzuzu.com.ding.event.MessageEvent;
import fangzuzu.com.ding.impl.OnMqttListener;
import fangzuzu.com.ding.model.Key;
import fangzuzu.com.ding.model.KeyObj;
import fangzuzu.com.ding.presenter.MqttPresenter;
import fangzuzu.com.ding.utils.HandleBackUtil;
import fangzuzu.com.ding.utils.NetWorkTesting;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static fangzuzu.com.ding.net.ResponseService.getLockList;
/**
 * Created by lingyuan on 2018/6/27.
 */

public class lockListActivity extends BaseActivity implements OnMqttListener {
    private static MqttAndroidClient client;
    RecyclerView lock_list_rc;
    String partid;
    String uid;
    lockListAdapter adapter;
    List data3;
    ImageView iv;
    LinearLayout ll;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    SwipeRefreshLayout swipe_refresh;
    public  int REQUEST_ACCESS_COARSE_LOCATION=1;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2;
    TextView tv_lock_list,tv_lock_listone;
    boolean isKitKat = false;

    TextView tv_toolbar;
    List dataPart=new ArrayList();
    private List<Key> keys;
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    iv.setVisibility(View.GONE);
                    lock_list_rc.setVisibility(View.VISIBLE);
                    tv_lock_list.setVisibility(View.GONE);
                    tv_lock_listone.setVisibility(View.GONE);
                    lock_list_rc.setAdapter(adapter);
                    swipe_refresh.setRefreshing(false);
                    break;
                case 2:  //没有数据

                    iv.setVisibility(View.VISIBLE);
                    lock_list_rc.setVisibility(View.GONE);
                    tv_lock_list.setVisibility(View.VISIBLE);
                    tv_lock_listone.setVisibility(View.VISIBLE);
                    swipe_refresh.setRefreshing(false);
                    break;
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  * 沉浸模式
        *
        * api等级>=19
     */

     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
         window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
         isKitKat = true;
        }

        setContentView(R.layout.lock_list_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        tv_toolbar= (TextView) findViewById(R.id.tv_toolbar);
        setStatusBar();

        MainApplication app = (MainApplication )getApplication();
         EventBus.getDefault().register(this);
        /**获取client对象*/
        partid = app.getPartid();
   // uid = app.getUid();
     uid= SharedUtils.getString("uid");
      //  client= mqttService.getMqttAndroidClientInstace();


      //  Log.d("TAG",partid);
  // Log.d("TAG",""uid);
        iv= (ImageView) findViewById(R.id.iv);
        ll= (LinearLayout) findViewById(R.id.ll);
        tv_lock_list= (TextView) findViewById(R.id.tv_lock_list);
        tv_lock_listone= (TextView) findViewById(R.id.tv_lock_listone);
        swipe_refresh= (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);


        initlize();

        if(Build.VERSION.SDK_INT>=23){
            //判断是否有权限
            if (ContextCompat.checkSelfPermission(lockListActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COARSE_LOCATION);
//向用户解释，为什么要申请该权限
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(lockListActivity.this,"打开权限才能用哦", Toast.LENGTH_SHORT).show();
                }
            }
        }
           // initauthor();

        keys = new ArrayList<>();

        getLockListDataFromeService();





    }


    @Override
    protected void onRestart() {
        super.onRestart();
        getLockListDataFromeService();
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









    private void deleteLock() {

        adapter.setOnItemLongClickListener(new lockListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position, final String id, final String lockusid, final String lockNumber, final String secretKey, final String allow, final String keyId) {

                String sb1=new String();

                for (int i = 0; i < allow.length(); i++) {
                    sb1 = allow.replace("", "0");
                }
               String allow2 = sb1.substring(0, sb1.length() - 1);
                Log.d("TAG","拼接allow"+allow2);


                View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                final EditText et= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
               // tv.setText("谨慎操作，导致数据丢失...");
               // tv.setTextColor(Color.RED);
                tv.setVisibility(View.GONE);
                tv.setGravity(Gravity.CENTER);
                TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                final AlertDialog dialog = new AlertDialog.Builder(lockListActivity.this)
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
                tv_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                tv_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         String pasw = SharedUtils.getString("pasw");
                        Log.d("TAG","密码"+pasw);
                        String pas = et.getText().toString().trim();
                        if (!StringUtils.isEmpty(pas)){
                        if (pas.equals(pasw)){
                            Log.d("TAG","删除走了"+pasw);
                            dialog.dismiss();
                        //1 根据当前uid 来判断  和锁里面的uid 是不是蓝牙管理员和普通用户
                        //连接蓝牙 和删除服务器数据
                        if (uid.equals(lockusid)){


                        }else {
                            //普通直接删除钥匙
                            upDataDelet(keyId,position);
                        }

                        }else {
                            Toast.makeText(lockListActivity.this,"你的密码错误", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        }else {
                            Toast.makeText(lockListActivity.this,"请输入密码", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }


    MqttPresenter mqPre;
  ;public void   upDataDelet(String id, final int postion){
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
                Gson gson=new Gson();
                msg m = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                int code = m.getCode();
                if (code==1001){
                    Log.d("TAG","删除成功");
                    Toast.makeText(lockListActivity.this,"删除数据成功", Toast.LENGTH_SHORT).show();
                    data3.remove(postion);
                    adapter.notifyDataSetChanged();
                    mqPre=new MqttPresenter();
                    mqPre.sendMqtt("az"+uid,lockListActivity.this);
                    mqPre.sendMqtt("ios"+uid,lockListActivity.this);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }


    private void initauthor() {
        setLocationService();
    }
    public static final boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (networkProvider || gpsProvider) return true;
        return false;
    }

    private void setLocationService() {
        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (isLocationEnable(this)) {
                //定位已打开的处理


            } else {
                //定位依然没有打开的处理
                Toast.makeText(lockListActivity.this,"请打开权限", Toast.LENGTH_SHORT).show();
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_ACCESS_COARSE_LOCATION) {
            if (permissions[0] .equals(Manifest.permission.ACCESS_COARSE_LOCATION)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意使用该权限
            } else {
                // 用户不同意，向用户展示该权限作用
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //showTipDialog("用来扫描附件蓝牙设备的权限，请手动开启！");
                    return;
                }
            }
        }
    }

    @Override
    protected void onResume() {


        super.onResume();
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
    //   subscibe();
            }
        },2000);
    }


//  fzz.PTP
   //
// public String topic="topicTest";
//public String topic="fzz\\PTP";
public String topic="fzzchat/PTP";
    private void subscibe() {
        NetWorkTesting netWorkTesting=new NetWorkTesting(this);
        if (netWorkTesting.isNetWorkAvailable()){
        if(client!=null){
            /**订阅一个主题，服务的质量默认为0*/
            try {
                client.subscribe(topic,0,null,new SubcribeCallBackHandler(lockListActivity.this));
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(lockListActivity.this,"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    /**
     * 运行在主线程
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String string = event.getString();
        Log.d("TAG","收到消息mqtt走了lockList");
        String code = event.getCode();
        String frome = event.getFrome();

        if (!StringUtils.isEmpty(code)){
            if (code.equals("10086")){
                if (!uid.equals(frome)){

                }

            }
        }



    }


    private void initlize() {
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(lockListActivity.this,addSmartServiceActivityOne.class);
                startActivity(intent);
              //  finish();
            }
        });

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLockListDataFromeService();
            }
        });
        data3=new ArrayList();
        lock_list_rc= (RecyclerView) findViewById(R.id.lock_list_rc);
        LinearLayoutManager lin=new LinearLayoutManager(lockListActivity.this);
        lin.setOrientation(OrientationHelper.VERTICAL);
        lock_list_rc.setLayoutManager(lin);



    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(client!=null) {

            if (client.isConnected()){
              //  client.disconnect();
                               client.close();
                client.registerResources(lockListActivity.this);
            }




        }


    }



    /**
     * 退出程序
     */
    private  static boolean isExit=false;
    Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit=false;
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            exit();

            return  false;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mhandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            removeALLActivity();
            System.exit(0);
        }
    } @Override
    public void onBackPressed() {
        if (!HandleBackUtil.handleBackPress(this)) {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meunlock,menu);
        return  true;
        //  return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch(item.getItemId()){

            case R.id.seting_change_numbler:
                View view = getLayoutInflater().inflate(R.layout.custom_diaglog_layut_exit_app, null);
                final TextView tv = (TextView) view.findViewById(R.id.tv);
                TextView tv_cancle= (TextView) view.findViewById(R.id.add_cancle);
                tv.setText("确定切换账号");
                tv.setTextSize(18);
                tv.setGravity(Gravity.CENTER);
                TextView tv_submit= (TextView) view.findViewById(R.id.add_submit);
                final AlertDialog dialog = new AlertDialog.Builder(lockListActivity.this)
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
                        exitAPP();
                        UserBean user = SharedUtils.getUser();
                        if (user != null) {
                            String name = user.name;
                            name = "";
                            SharedPreferences shared = MainApplication.getInstence().getSharedPreferences("shared", Context.MODE_PRIVATE);
                            shared.edit().clear().commit();
                            MainApplication.getInstence().removeALLActivity_();  //清掉全部Activity
                            Intent intent = new Intent(MainApplication.getInstence(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });





                break;
            case R.id.seting_add_admini:
                Intent intent1=new Intent(lockListActivity.this,addSmartServiceActivityOne.class);
                startActivity(intent1);
                break;
        }
        return true;
        //  return super.onOptionsItemSelected(item);
    }

    public void exitAPP(){
        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .baseUrl(apiManager.baseUrl)
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        Call<String> call = manager.ExitAPP();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.d("TAG","退出APP"+body);
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
    String lockMac;
    String lockAlias;
    int electricQuantity;
    String userType;
    int lockId;
    String noKeyPwd;
    int keyId;
    int lockFlagPos;
    int timezoneRawOffset;
    String aesKeyStr;
    String lockKey;
    String adminPwd;
    int keyRight;
    String keyStatus;
    TTlockListBean.KeyListBean.LockVersionBean lockVersion;
    public void getLockListDataFromeService() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String lockList = getLockList();
                if (!StringUtils.isEmpty(lockList)){


                Log.d("TAG","锁列表"+lockList);
                Gson gson=new Gson();
                TTlockListBean m = gson.fromJson(lockList, new TypeToken<TTlockListBean>() {}.getType());
                List<TTlockListBean.KeyListBean> keyList = m.getKeyList();
                    Message message=new Message();
                    Iterator<TTlockListBean.KeyListBean> iterator = keyList.iterator();
                if (keyList.size()>1){

                    data3.clear();
                    while (iterator.hasNext()){
                        TTlockListBean.KeyListBean next = iterator.next();
                        data3.add(next);

                    }
                    adapter=new lockListAdapter(data3,MainApplication.getInstence());
                    adapter.notifyDataSetChanged();



                    message.what=1;
                    mHandler.sendMessage(message);
                } else if (keyList.size()==1){
                    while (iterator.hasNext()){
                        TTlockListBean.KeyListBean userLockBean = iterator.next();
                        lockMac = userLockBean.getLockMac();
                         lockAlias = userLockBean.getLockAlias();
                       electricQuantity = userLockBean.getElectricQuantity();
                       userType = userLockBean.getUserType();
                        lockId = userLockBean.getLockId();
                        noKeyPwd = userLockBean.getNoKeyPwd();
                        keyId = userLockBean.getKeyId();
                         lockFlagPos = userLockBean.getLockFlagPos();
                         timezoneRawOffset = userLockBean.getTimezoneRawOffset();
                        aesKeyStr = userLockBean.getAesKeyStr();
                        lockVersion = userLockBean.getLockVersion();
                        lockKey = userLockBean.getLockKey();
                     adminPwd = userLockBean.getAdminPwd();
                       keyRight = userLockBean.getKeyRight();
                         keyStatus = userLockBean.getKeyStatus();

                    }
                    Intent intent =new Intent(lockListActivity.this,MainActviity.class);
                    intent.putExtra("lockMac",lockMac);
                    intent.putExtra("lockAlias",lockAlias);
                    intent.putExtra("electricQuantity", electricQuantity+"");
                    intent.putExtra("userType", userType);
                    intent.putExtra("lockId", lockId+"");
                    intent.putExtra("keyId", keyId+"");
                    intent.putExtra("noKeyPwd", noKeyPwd);
                    intent.putExtra("lockVersion", lockVersion);
                    intent.putExtra("lockFlagPos", lockFlagPos+"");
                    intent.putExtra("timezoneRawOffset", timezoneRawOffset+"");
                    intent.putExtra("aesKeyStr", aesKeyStr);
                    intent.putExtra("lockKey", lockKey);
                    intent.putExtra("many", "1");  // 1 表示1条数据
                    intent.putExtra("adminPwd", adminPwd );
                    intent.putExtra("keyRight",keyRight+"");
                    intent.putExtra("keyStatus", keyStatus );
                   startActivity(intent);

                } else {
                    message.what=2;  //没有数据
                    mHandler.sendMessage(message);
                }


                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(lockList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //use lastUpdateDate you can get the newly added key and data after the time
                String keyList1 = null;
                try {
                    keyList1 = jsonObject.getString("keyList");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                keys.clear();
                ArrayList<KeyObj> list = GsonUtil.toObject(keyList1, new TypeToken<ArrayList<KeyObj>>(){});
                keys.addAll(convert2DbModel(list));
                //
                    DbService.deleteAllKey();
                    DbService.saveKeyList(keys);
                }
            }
        }.start();
    }



    private static ArrayList<Key> convert2DbModel(ArrayList<KeyObj> list){
        ArrayList<Key> keyList = new ArrayList<>();
        if(list != null && list.size() > 0){
            for(KeyObj key : list){
                Key DbKey = new Key();
                DbKey.setUserType(key.userType);
                DbKey.setKeyStatus(key.keyStatus);
                DbKey.setLockId(key.lockId);
                DbKey.setKeyId(key.keyId);
                DbKey.setLockVersion(GsonUtil.toJson(key.lockVersion));
                DbKey.setLockName(key.lockName);
                DbKey.setLockAlias(key.lockAlias);
                DbKey.setLockMac(key.lockMac);
                DbKey.setElectricQuantity(key.electricQuantity);
                DbKey.setLockFlagPos(key.lockFlagPos);
                DbKey.setAdminPwd(key.adminPwd);
                DbKey.setLockKey(key.lockKey);
                DbKey.setNoKeyPwd(key.noKeyPwd);
                DbKey.setDeletePwd(key.deletePwd);
                DbKey.setPwdInfo(key.pwdInfo);
                DbKey.setTimestamp(key.timestamp);
                DbKey.setAesKeyStr(key.aesKeyStr);
                DbKey.setStartDate(key.startDate);
                DbKey.setEndDate(key.endDate);
                DbKey.setSpecialValue(key.specialValue);
                DbKey.setTimezoneRawOffset(key.timezoneRawOffset);
                DbKey.setKeyRight(key.keyRight);
                DbKey.setKeyboardPwdVersion(key.keyboardPwdVersion);
                DbKey.setRemoteEnable(key.remoteEnable);
                DbKey.setRemarks(key.remarks);

                keyList.add(DbKey);
            }
        }
        return keyList;
    }
}
