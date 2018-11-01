package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ttlock.bl.sdk.entity.Error;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.event.createtimeMessage;
import fangzuzu.com.ding.event.losetimeMessage;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import fangzuzu.com.ding.widget.DatePicier;

/**
 * Created by lingyuan on 2018/6/4.
 */

public class addfingerOneStepActivity extends BaseActivity implements MainApplication.BleOperAddFingerListenner,MainApplication.BleOperAddFingerCollectionListenner{




    Toolbar toolbar;
    LinearLayout  create_time;
    String username,usernumber;
    ToggleButton tglSound;
    LinearLayout lose_time;
    EditText electfrg_key_name;

    private TextView currentDate, currentTime;
    Button but_next;
    String lockNumber;
    boolean isKitKat = false;
    String  adminUserId;
    String uid;
    RelativeLayout re_type;
    String fingerName;
    long startTime1;
    long endTime1;
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
        setContentView(R.layout.add_finger_one_activity_layout);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initlize();
        setStatusBar();
        EventBus.getDefault().register(this);
        MainApplication.SetBleOperAddFingerListenner(this);
        MainApplication.SetBleOperAddFingerCollectionListenner(this);
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
        electfrg_key_name= (EditText) findViewById(R.id.electfrg_key_name);
        but_next= (Button) findViewById(R.id.but_next);
        currentTime= (TextView)findViewById(R.id.electfrg_effect_time);
        currentDate= (TextView) findViewById(R.id.electfrg_lose_time);
        create_time= (LinearLayout)findViewById(R.id.create_time);
        lose_time=(LinearLayout)findViewById(R.id.lose_time);
        DatePicier.initDatePicker(currentDate, currentTime, addfingerOneStepActivity.this);
        create_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicier.getCustomDatePicker2().show(currentTime.getText().toString());
            }
        });
        lose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicier.getCustomDatePicker1().show(currentTime.getText().toString());
            }
        });
        tglSound= (ToggleButton) findViewById(R.id.tglSound);



        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             fingerName = electfrg_key_name.getText().toString().trim();


                String time = currentTime.getText().toString().trim();
                String s = unixTime.dateToStampone(time);
                Log.d("TAG","开始时间戳"+s);
                String substring1 = s.substring(0, s.length() - 3);
                int startTime = Integer.parseInt(substring1);
                Log.d("TAG","开始时间"+ startTime);

                String endtime = currentDate.getText().toString().trim();
                String send = unixTime.dateToStampone(endtime);
                Log.d("TAG","结束时间戳"+send);
                String substring1end = send.substring(0, send.length() - 3);
                int endTime = Integer.parseInt(substring1end);
                Log.d("TAG","结束时间"+ endTime);
                if (!StringUtils.isEmpty(fingerName)){
                    if ( tglSound.isChecked()){
                        Log.d("TAG","永久走了");
                    //    addFinger(0,0,true); // 永久位true;
                        Intent intent=new Intent(addfingerOneStepActivity.this,addfingerTwoStepActivity.class);
                        intent.putExtra("statTime","0");
                        intent.putExtra("endTime","0");
                        intent.putExtra("flag","0");  //0 永久
                        startActivity(intent);

                    }
                else {


                    if (StringUtils.isEmpty(endtime)) {
                        Toast.makeText(MainApplication.getInstence(), "请输入失效时间", Toast.LENGTH_SHORT).show();
                    }else if (startTime<endTime&&startTime!=endTime){

                      startTime1 = Long.parseLong(s);
                        Log.d("TAG","限时走了startTime"+startTime1);
                       endTime1 = Long.parseLong( send);
                        Log.d("TAG","限时走了endTime"+endTime1);
                       // addFinger(startTime1, endTime1,false);   //限时

                        Intent intent=new Intent(addfingerOneStepActivity.this,addfingerTwoStepActivity.class);
                        intent.putExtra("statTime",startTime1+"");
                        intent.putExtra("endTime",endTime1+"");
                        intent.putExtra("flag","1");  // 1限时
                        intent.putExtra("fingerName",fingerName);
                        startActivity(intent);
                        finish();

                    }else {
                        Toast.makeText(MainApplication.getInstence(),"失效时间不能小于生效时间，并且2个时间不能相同",Toast.LENGTH_LONG).show();
                    }
                    }
                }else {
                    Toast.makeText(MainApplication.getInstence(),"请给指纹名称",Toast.LENGTH_LONG).show();
                }





            }
        });










        tglSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    //做开启的业务
                    create_time.setVisibility(View.INVISIBLE);
                    lose_time.setVisibility(View.INVISIBLE);

                }else{
                    //做没开启的业务  对事显示
                    Log.d("TAG","显示走了");
                    create_time.setVisibility(View.VISIBLE);
                    lose_time.setVisibility(View.VISIBLE);

                }

            }
        });
    }















    String s2;
    String s4;
    String s1;
    String s3;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        finish();

    }
    String endtime;

    //失效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(losetimeMessage event){
        endtime = event.getTime().toString().trim();
        Log.d("TAG","event"+endtime);
    }

    String createtime;
    //生效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBuscreate(createtimeMessage event){
        createtime = event.getTime();
        Log.d("TAG","event"+currentTime);


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
ProgressDialog progressDialog;
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
     * 添加指纹
     * @param error
     * @param l
     */
    @Override
    public void bleOperAddFinger(final Error error, final long l) {
            cancelProgressDialog();



    }

    /**
     * 指纹采集回调
     * @param error
     * @param l
     */
    @Override
    public void bleOperAddFingerCollection(Error error, int l) {


    }




}
