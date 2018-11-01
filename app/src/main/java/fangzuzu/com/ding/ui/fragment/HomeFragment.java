package fangzuzu.com.ding.ui.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ttlock.bl.sdk.entity.Error;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.adapter.PermissionLockhomeAdapter;
import fangzuzu.com.ding.bean.TTlockListBean;
import fangzuzu.com.ding.enumtype.Operation;
import fangzuzu.com.ding.event.BleOperMessage;
import fangzuzu.com.ding.model.BleSession;
import fangzuzu.com.ding.model.Key;
import fangzuzu.com.ding.ui.activity.ElectKeyManagerActivity;
import fangzuzu.com.ding.ui.activity.FingerPrintManageActivity;
import fangzuzu.com.ding.ui.activity.PasswordManagementActivity;
import fangzuzu.com.ding.ui.activity.addICCardActivity;
import fangzuzu.com.ding.ui.activity.keySetActivity;
import fangzuzu.com.ding.ui.activity.openLockRecodeActivity;
import fangzuzu.com.ding.ui.activity.sendKeyActivity;
import fangzuzu.com.ding.ui.activity.sendPassWordActivity;
import fangzuzu.com.ding.utils.ScreenSizeUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import fangzuzu.com.ding.view.RoundProgressBarWidthNumber;

import static fangzuzu.com.ding.MainApplication.mTTLockAPI;

/**
 * Created by yuanling on 2018/5/12.
 */

public class HomeFragment extends BaseFragment implements MainApplication.BleOperListenner ,MainApplication.BleOperNotAroundListenner{
    private static final int REQUEST_PERMISSION_REQ_CODE = 1;

    ProgressDialog progressDialog;
    Toolbar toolbar;
    Button tv_ding;
    public  int REQUEST_ACCESS_COARSE_LOCATION=1;

    MediaPlayer mediaPlayer01;
    TextView tv_lock_name,elect;
     RecyclerView re_auth_list;
    PermissionLockhomeAdapter adapter;    //Type 0 为正常  1为过期
    List authData;
    boolean isKitKat = false;
    LinearLayout ll_home;
    String type="0";
    private Key mKey;
    public static Key curKey;
    private BleSession bleSession;
    RoundProgressBarWidthNumber  mRoundProgressBar;
   // private HorizontalProgressBarWithNumber mProgressBar;
    private static final int MSG_PROGRESS_UPDATE = 0x110;
    private static final int MSG_PROGRESS_STOP = 0x111;
    private static final int MSG_PROGRESS_UI = 0x112;
    private static final int MSG_PROGRESS_UNONCLICK = 0x113; //开锁
    private static final int MSG_PROGRESS_NOTAROUND = 0x114; //设备不在身边
    private  Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case MSG_PROGRESS_UPDATE:
                    int roundProgress = mRoundProgressBar.getProgress();

                mRoundProgressBar.setProgress(++roundProgress);

                    if (roundProgress >= 100) {

                        mRoundProgressBar.setProgress(0);
            }
                    mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 5);
                    break;
                case MSG_PROGRESS_STOP:
                    Log.d("TAG","handler停止");
                    new Handler().postDelayed(new Runnable(){
                        public void run() {
                         flag=false;
                        }
                    }, 5000);


                            mRoundProgressBar.setVisibility(View.GONE);
                            mHandler.removeMessages(MSG_PROGRESS_UPDATE);




                    break;
                case MSG_PROGRESS_UI:
                    Drawable drawable = getActivity().getDrawable(R.mipmap.ding);
                    tv_ding.setBackground(drawable);
                    break;
                case MSG_PROGRESS_UNONCLICK:
                    Log.d("TAG","开锁回调HomeFragmentzoule");
                    Drawable drawable1 = getActivity().getDrawable(R.mipmap.ding_open);
                    tv_ding.setBackground(drawable1);
                    mediaPlayer01 = MediaPlayer.create(getActivity(), R.raw.sound_for_connect);
                    mediaPlayer01.start();
                    mHandler.sendEmptyMessage(MSG_PROGRESS_STOP);

                    //弹出对话框  yyyy年MM月dd日
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
//获取当前时间
                    Date date = new Date(System.currentTimeMillis());

                    View view = View.inflate(getActivity(), R.layout.dialog_normal, null);

                    tv_time= (TextView) view.findViewById(R.id.time_tv);
                    tv_time.setText(simpleDateFormat.format(date));
                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity(),R.style.NoBackGroundDialog);
                    final AlertDialog alertDialog = adb.setView(view).create();
                    alertDialog.setCancelable(false);
                    LinearLayout ll_konw=(LinearLayout) view.findViewById(R.id.ll_open_lock);
                    alertDialog.show();
                    //设置对话框的大小
                    //   view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(getActivity()).getScreenHeight() * 0.23f));
                    Window dialogWindow = alertDialog.getWindow();
                    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                    lp.width = (int) (ScreenSizeUtils.getInstance(getActivity()).getScreenWidth() * 0.80f);
                    lp.height =(int) (ScreenSizeUtils.getInstance(getActivity()).getScreenHeight() * 0.40f);
                    lp.gravity = Gravity.CENTER;
                    dialogWindow.setAttributes(lp);
                    ll_konw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    Timer timer1=new Timer();
                    timer1.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            mHandler.sendEmptyMessage(MSG_PROGRESS_UI);
                            alertDialog.dismiss();
                            mediaPlayer01.release();


                        }
                    },5000);

                    break;
                case MSG_PROGRESS_NOTAROUND:
                    flag=false;
                    mRoundProgressBar.setVisibility(View.GONE);
                    mHandler.removeMessages(MSG_PROGRESS_UPDATE);
                    Toast.makeText(MainApplication.getInstence(),"设备不在身边",Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
    private String noKeyPwd;


    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment_layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.mTTLockAPI.requestBleEnable(getActivity());
        //  LogUtil.d("start bluetooth service", DBG);
        MainApplication.mTTLockAPI.startBleService(getActivity());

       // initgetData();

        mKey=new Key();
        //  initThinkTime();
        getIntetntData();
      //  EventBus.getDefault().register(getActivity());
        MainApplication.SetBleOperListenner(this);
        MainApplication.setBleOperNotAroundListenner(this);

        if(Build.VERSION.SDK_INT>=23){
            //判断是否有权限
            if (ContextCompat.checkSelfPermission(MainApplication.getInstence(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COARSE_LOCATION);
//向用户解释，为什么要申请该权限
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(MainApplication.getInstence(),"打开权限才能用哦", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BleOperMessage(BleOperMessage event){
        Error error = event.getError();
        Log.d("TAG","error"+error);


    }







    /**
     * 拿到权限列表
     */
    private void initgetAuthor() {
        //拿到权限   	钥匙用户类型：110301-管理员钥匙，110302-普通用户钥匙。
        authData=new ArrayList();
        if (TTuserType.equals("110301")){
            authData.add("发送钥匙");
            authData.add("发送密码");
            authData.add("钥匙管理");
            authData.add("密码管理");
            authData.add("IC卡");
            authData.add("指纹");
            authData.add("操作记录");
            authData.add("设置");
        }else if (TTuserType.equals("110302")){
            authData.add("操作记录");
            authData.add("设置");
        }


        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainApplication.getInstence(), 4);
        gridLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        re_auth_list.setLayoutManager(gridLayoutManager);
        adapter=new PermissionLockhomeAdapter( authData,MainApplication.getInstence(),type);
        re_auth_list.setAdapter(adapter);
        if (type.equals("0")){
            authClick();
        }







    }

    private void authClick() {
        adapter.setItemClickListener(new PermissionLockhomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id) {

                if (id.equals("发送钥匙")){
                    Intent intent =new Intent(MainApplication.getInstence(), sendKeyActivity.class);
                    intent.putExtra("TTlockAlias",TTlockAlias);
                    intent.putExtra("TTlockId",TTlockId);
                    startActivity(intent);
                }else if (id.equals("发送密码")){
                    Intent intent=new Intent(MainApplication.getInstence(), sendPassWordActivity.class);
                    intent.putExtra("TTlockId",TTlockId);
                    startActivity(intent);
                }else if (id.equals("钥匙管理")){
                    Intent intent=new Intent(MainApplication.getInstence(), ElectKeyManagerActivity.class);
                    intent.putExtra("id",Lockid);
                    startActivity(intent);
                }else if (id.equals("密码管理")){
                    Intent intent=new Intent(MainApplication.getInstence(), PasswordManagementActivity.class);
                    intent.putExtra("id",TTlockId);  //  TTlockMac
                    intent.putExtra("TTlockMac",TTlockMac);
                    startActivity(intent);
                }else if (id.equals("IC卡")){
                    Intent intent=new Intent(MainApplication.getInstence(), addICCardActivity.class);
                    intent.putExtra("TTlockMac",TTlockMac);
                    intent.putExtra("id",TTlockId);  //  TTlockMac
                    startActivity(intent);
                }else if (id.equals("指纹")){
                    Log.d("TAG", "点击权限指纹");
                    Intent intent=new Intent(MainApplication.getInstence(), FingerPrintManageActivity.class);
                    intent.putExtra("id",TTlockId);  //  TTlockMac
                    startActivity(intent);

                }else if (id.equals("操作记录")){
                    Intent intent=new Intent(MainApplication.getInstence(),  openLockRecodeActivity.class);
                    intent.putExtra("Lockid",TTlockId);
                    intent.putExtra("TTlockMac",TTlockMac);
                    startActivity(intent);
                }else if (id.equals("设置")){
                    Intent intent=new Intent(MainApplication.getInstence(),  keySetActivity.class);
                    intent.putExtra("id",TTlockId);  //TTlockAlias  TTuserType
                    intent.putExtra("TTlockAlias",TTlockAlias);
                    intent.putExtra("TTelectricQuantity",TTelectricQuantity);//  TTelectricQuantity
                    intent.putExtra("keyId",keyId);                                                      // noKeyPwd  keyId
                    intent.putExtra("noKeyPwd",noKeyPwd);
                    intent.putExtra("TTuserType",TTuserType);
                    intent.putExtra("lockVersion", lockVersion);
                    intent.putExtra("lockFlagPos", lockFlagPos);
                    intent.putExtra("timezoneRawOffset", timezoneRawOffset);
                    intent.putExtra("aesKeyStr", aesKeyStr);
                    intent.putExtra("lockKey", lockKey);
                    intent.putExtra("adminPwd", adminPwd );
                    startActivity(intent);
                }
            }
        });
    }







    @Override
    protected void initViews() {
        setHasOptionsMenu(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        ll_home=(LinearLayout) root.findViewById(R.id.ll_home);
        toolbar = (Toolbar)root. findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (many.equals("1")){
            ((AppCompatActivity) getActivity()). getSupportActionBar().setHomeButtonEnabled(false); //设置返回键可用
            ((AppCompatActivity) getActivity()). getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }else {
            ((AppCompatActivity) getActivity()). getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
            ((AppCompatActivity) getActivity()). getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        re_auth_list= (RecyclerView) root.findViewById(R.id.re_auth_list);
        re_auth_list.setNestedScrollingEnabled(false);
        initgetAuthor();

        tv_lock_name= (TextView) root.findViewById(R.id.tv_lock_name);
        tv_lock_name.setText(TTlockAlias);
        elect= (TextView) root.findViewById(R.id.elect);
        elect.setText(TTelectricQuantity+"%");
        tv_ding= (Button) root.findViewById(R.id.tv_ding);

        setStatusBar();


    //  mProgressBar = (HorizontalProgressBarWithNumber)root. findViewById(R.id.id_progressbar01);
        mRoundProgressBar = (RoundProgressBarWidthNumber)root. findViewById(R.id.id_progress02);



    }

    /**
     * (permission request)
     * @param permission
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(MainApplication.getInstence(), permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
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
    protected void setStatusBar() {
        if (isKitKat){
            int statusH = screenAdapterUtils.getStatusHeight(MainApplication.getInstence());
            //获取ToolBar的布局属性，设置ToolBar的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)toolbar.getLayoutParams();
            params.height = params.height + statusH;
            toolbar.setLayoutParams(params);
            //设置ToolBar的PaddingTop属性
            toolbar.setPadding(0, statusH, 0, 0);
            getActivity().getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Log.d("TAG","普通");
        }
    }








    TextView tv_time;
    String Lockid;

    byte[] secretKeyBytes;
    String electricity;
    byte[] adminPswBytes;
    byte[]allowbyt;
    String   lockName;
    String uid;
    String  adminPsw1;
    String jihe;
    String StartTime;
    String endTime;
    String updataFlag;
    private void initgetData() {
        Lockid =getActivity(). getIntent().getStringExtra("id");

      adminPsw1= getActivity().getIntent().getStringExtra("adminPsw");
        String   allow1 = getActivity().getIntent().getStringExtra("allow");
        lockName = getActivity().getIntent().getStringExtra("lockName");
        electricity = getActivity().getIntent().getStringExtra("electricity");
        String adminUserId = getActivity().getIntent().getStringExtra("adminUserId");
        SharedUtils.putString("adminUserId",adminUserId);
        jihe = getActivity().getIntent().getStringExtra("jihe");
        StartTime= getActivity().getIntent().getStringExtra("startTime");
        endTime = getActivity().getIntent().getStringExtra("endTime");  //updataFlag
        updataFlag = getActivity().getIntent().getStringExtra("updataFlag");
        uid= SharedUtils.getString("uid");

        Log.d("TAG","传过来的Id"+Lockid);

        Log.d("TAG","  endTime "+ endTime );
        Log.d("TAG"," StartTime"+StartTime);
        MainApplication.getInstence().setAllow(allow1);
        MainApplication.getInstence().setLockid(Lockid);
        MainApplication.getInstence().setLockName(lockName);
        MainApplication.getInstence().setPasword(adminPsw1);
        MainApplication.getInstence().setElect(electricity);
        MainApplication.getInstence().setStartTime(StartTime);
        MainApplication.getInstence().setEndTime(endTime);
        Log.d("TAG","adminPsw1"+adminPsw1);







    }














    public  void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(getActivity(), title, message, true, false);
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
     * 蓝牙开锁
     */
    List<String> bleScan=new ArrayList<>();
 public    boolean flag=false;
    @Override
    protected void initEvents() {

        tv_ding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( mTTLockAPI.isBLEEnabled(MainApplication.getInstence())){

                    if (!flag){
                        flag=true;
                        int tv_width = tv_ding.getMeasuredWidth();
                        int tv_height = tv_ding.getMeasuredHeight();
                        Log.d("TAG","控件的宽度"+tv_width+"控件的高度"+tv_height);
                        LinearInterpolator lir = new LinearInterpolator();
                        ViewGroup.LayoutParams layoutParams = mRoundProgressBar.getLayoutParams();
                        layoutParams.width=tv_width-70;
                        layoutParams.height=tv_height-70;
                        mRoundProgressBar.setLayoutParams(layoutParams);
                        mRoundProgressBar.setVisibility(View.VISIBLE);
                        mRoundProgressBar.setProgress(0);
                        mRoundProgressBar.setInterpolator(lir);
                        mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);
                        bleSession=MainApplication.bleSession;
                        //It need location permission to start bluetooth scan,or it can not scan device
                        if (bleScan.size()>0){
                            //  直接连接蓝牙

                            bleSession.setOperation(Operation.CLICK_UNLOCK);
                            bleSession.setLockmac(TTlockMac);
                            mTTLockAPI.connect(TTlockMac);
                        }else {
                            if(requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                                //开启蓝牙设备
                                //   mTTLockAPI.connect(TTlockMac);
                                bleSession.setOperation(Operation.CLICK_UNLOCK);
                                bleSession.setLockmac(TTlockMac);
                                mTTLockAPI.startBTDeviceScan();
                                bleScan.add("1");
                            }
                        }


                    }


                    }else {
                    //打开蓝牙设备
                    mTTLockAPI.requestBleEnable(getActivity());
                }





             //   flag=true;

            }
        });
    }

    @Override
    protected void initData() {

    }





    /**
     * 解除接收数据
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mediaPlayer01 !=null) {
            mediaPlayer01.release();
        }

        flag=false;
        mRoundProgressBar.setVisibility(View.GONE);
        if (mRoundProgressBar!=null){
            mHandler.sendEmptyMessage(MSG_PROGRESS_STOP);
        }
        Log.d("TAG","界面销毁");
    }

    @Override
    public void onPause() {
        super.onPause();
        flag=false;
        mRoundProgressBar.setVisibility(View.GONE);
        if (mRoundProgressBar!=null){
            mHandler.sendEmptyMessage(MSG_PROGRESS_STOP);
        }
        Log.d("TAG","界面暂停");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity(). finish();


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 得到上个界面传来得数据
     */
    String TTlockMac;
    String TTlockAlias; //名称
    String TTelectricQuantity; //电量
    String TTuserType; //用户类型    钥匙用户类型：110301-管理员钥匙，110302-普通用户钥匙。
    String TTlockId;  //锁id
    String keyId; //钥匙id
    TTlockListBean.KeyListBean.LockVersionBean lockVersion;
    String lockFlagPos;
    String timezoneRawOffset;
    String aesKeyStr;
    String lockKey;
    String adminPwd;
    private String many;  // 1 一条数据  0  多条
    public void getIntetntData() {
       TTlockMac = getActivity().getIntent().getStringExtra("lockMac");
        TTlockAlias = getActivity().getIntent().getStringExtra("lockAlias");
         TTelectricQuantity = getActivity().getIntent().getStringExtra("electricQuantity");
         TTuserType = getActivity().getIntent().getStringExtra("userType");
       TTlockId = getActivity().getIntent().getStringExtra("lockId");
        MainApplication.getInstence().setTTlockId(TTlockId);
        MainApplication.getInstence().setMac(TTlockMac);
        noKeyPwd = getActivity().getIntent().getStringExtra("noKeyPwd"); //管理员密码
      keyId = getActivity().getIntent().getStringExtra("keyId");
       lockVersion = (TTlockListBean.KeyListBean.LockVersionBean) getActivity().getIntent().getSerializableExtra("lockVersion");
       lockFlagPos = getActivity().getIntent().getStringExtra("lockFlagPos");
      timezoneRawOffset = getActivity().getIntent().getStringExtra("timezoneRawOffset");
       aesKeyStr = getActivity().getIntent().getStringExtra("aesKeyStr");
     lockKey = getActivity().getIntent().getStringExtra("lockKey");
      adminPwd = getActivity().getIntent().getStringExtra("adminPwd");
        Log.d("TAG","lockFlagPos"+lockFlagPos);
        many = getActivity().getIntent().getStringExtra("many");
    }

    /**
     * 开锁回调方法
     * @param error
     */
    @Override
    public void bleOper(Error error) {
        Log.d("TAG", "开锁回调HomeFragment" + error.toString());
        if (error.equals(Error.SUCCESS)) {
            mHandler.sendEmptyMessage(MSG_PROGRESS_UNONCLICK);


        }
    }

    /**
     * 设备不在身边
     */
    @Override
    public void bleOpernotAround() {
        mHandler.sendEmptyMessage(MSG_PROGRESS_NOTAROUND);
    }



}
