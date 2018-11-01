package fangzuzu.com.ding.ui.activity;

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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ttlock.bl.sdk.entity.Error;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.enumtype.Operation;
import fangzuzu.com.ding.utils.screenAdapterUtils;

import static fangzuzu.com.ding.MainApplication.mTTLockAPI;

/**
 * Created by lingyuan on 2018/6/4.
 */

public class addfingerTwoStepActivity extends BaseActivity implements MainApplication.BleOperAddFingerListenner,MainApplication.BleOperAddFingerCollectionListenner {
    Toolbar toolbar;
    ImageView iv_0;
    private String statTime;
    private String endTime;
    private String flag;
    private String fingerName;
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
        setContentView(R.layout.add_finger_two_step_aactivity);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initlize();
        setStatusBar();
        getIntentData();
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

        }
    }


    private void initlize() {
        iv_0= (ImageView) findViewById(R.id.iv_0);
        Animation translateAnimation = new TranslateAnimation(0,-210,0,0);//平移动画  从0,0,平移到100,100
        translateAnimation.setDuration(1500);//动画持续的时间为1.5s
        translateAnimation.setRepeatCount(Animation.INFINITE);
        translateAnimation.setRepeatMode(Animation.REVERSE);
                iv_0.setAnimation(translateAnimation);//给imageView添加的动画效果
       // translateAnimation.setFillEnabled(true);//使其可以填充效果从而不回到原地
       // translateAnimation.setFillAfter(true);//不回到起始位置
        //如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
        translateAnimation.startNow();//动画开始执行 放在最后即可
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
     * 点击添加指纹  连接蓝牙
     * @param view
     */
    public void butClick(View view) {
        long TTstartTime = Long.parseLong(statTime);
        long TTendTime = Long.parseLong(endTime);

        if (flag.equals("0")){  //0  永久
            addFinger( TTstartTime ,TTendTime,true);  //永久位true;
        }else {
            addFinger( TTstartTime ,TTendTime,false);
        }



    }

    private void addFinger(long startTime,long endTime,boolean flag) {

        String mac = MainApplication.getInstence().getMac();
        if (mTTLockAPI.isBLEEnabled(MainApplication.getInstence())){
            showProgressDialog("连接蓝牙...");
            mTTLockAPI.connect(mac);
            MainApplication.bleSession.setOperation(Operation.ADD_FINGER);
            MainApplication.bleSession.setLockmac(mac);
            MainApplication.bleSession.setFlag(flag);  //永久
            MainApplication.bleSession.setStartDate(startTime);
            MainApplication.bleSession.setEndDate(endTime);


        }else {
            //打开蓝牙设备
            mTTLockAPI.requestBleEnable(addfingerTwoStepActivity.this);
        }
    }


    /**
     * 获取上一个界面的数据
     */
    public void getIntentData() {
        statTime = getIntent().getStringExtra("statTime");
        endTime = getIntent().getStringExtra("endTime");
        flag = getIntent().getStringExtra("flag");
        fingerName = getIntent().getStringExtra("fingerName");
        Log.d("TAG","statTime"+statTime+"endTime"+endTime+"flag"+flag);
    }

    @Override
    public void bleOperAddFinger(Error error, long l) {
        if (error==Error.SUCCESS) {

            if (l == 0) {
                cancelProgressDialog();
                Intent intent=new Intent(this,addfingerThreeStepActivity.class);
                intent.putExtra("statTime",statTime);
                intent.putExtra("endTime",endTime);
                intent.putExtra("fingerName",fingerName);
                startActivity(intent);

            }
        }
    }

    @Override
    public void bleOperAddFingerCollection(Error error, int l) {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
