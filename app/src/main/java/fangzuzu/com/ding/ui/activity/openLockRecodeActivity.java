package fangzuzu.com.ding.ui.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.entity.Error;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.adapter.openLockRecodeAdapter;
import fangzuzu.com.ding.bean.openLockRecoderBean;
import fangzuzu.com.ding.enumtype.Operation;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;

import static com.ttlock.bl.sdk.util.GsonUtil.gson;
import static fangzuzu.com.ding.MainApplication.bleSession;
import static fangzuzu.com.ding.MainApplication.mTTLockAPI;

/**
 * 获取开锁记录
 * Created by lingyuan on 2018/6/20.
 */

public class openLockRecodeActivity extends BaseActivity implements MainApplication.BleOperlockRecordListenner {

    Toolbar toolbar;
    openLockRecodeAdapter adapter;

    RecyclerView rc;
    List data3;
    String lockid;

    EditText et_open_lock;
    FrameLayout open_lock_frl;
    TextView open_lock_pasw,open_lock_app,open_lock_finger,open_lock_ic,open_lock_shengfenz,tv_open_lock_cannel;
    LinearLayout open_molder_ll;
    RecyclerView re_reach;
    boolean isKitKat = false;
    LinearLayout ll_nodata,ll_no_noe;
    FrameLayout fr_no_two;
    ImageView iv_no_data;
    TextView tv_no_data;
    String uid;
    private String tTlockMac;



    private static final int MSG_PROGRESS_UI = 0x112;
    private static final int MSG_PROGRESS_UI_DATA = 0x113;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_PROGRESS_UI:
                    mHandler.sendEmptyMessage(MSG_PROGRESS_UI);
                    ll_no_noe.setVisibility(View.GONE);
                    fr_no_two.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.VISIBLE);
                    iv_no_data.setImageResource(R.mipmap.no_open_door);
                    tv_no_data.setText("暂无开锁记录");
                   // re_reach.setRefreshing(false);
                    break;
                case MSG_PROGRESS_UI_DATA:
                    ll_nodata.setVisibility(View.GONE);
                    ll_no_noe.setVisibility(View.VISIBLE);
                    fr_no_two.setVisibility(View.VISIBLE);
                    rc.setVisibility(View.VISIBLE);
                    adapter = new openLockRecodeAdapter(data3, openLockRecodeActivity.this);
                    rc.setAdapter(adapter);
                    re_reach.setAdapter(adapter);
                   // srf.setRefreshing(false);
                    break;
            }
        }
    };





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
        setContentView(R.layout.open_lock_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        //初始化蓝牙
       lockid = getIntent().getStringExtra("Lockid");
        tTlockMac = getIntent().getStringExtra("TTlockMac");
        Log.d("TAG","传过来的id"+lockid);
        uid= SharedUtils.getString("uid");

        initlize();
        initEvent();
        getOpenLockRecoder("");
        initSearch();
        MainApplication.setBleOperlockRecordListenner(this);

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
     * 获取锁的操作录
     */
    private void initEvent( ) {
        if (mTTLockAPI.isBLEEnabled(MainApplication.getInstence())){
            mTTLockAPI.connect( tTlockMac);
            bleSession.setOperation(Operation.GET_OPERATE_LOG);
            bleSession.setLockmac( tTlockMac);

        }else {
            //打开蓝牙设备
            mTTLockAPI.requestBleEnable(openLockRecodeActivity.this);
        }



    }





    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initlize() {
        data3=new ArrayList();
        ll_no_noe=(LinearLayout) findViewById(R.id.ll_no_one);
        fr_no_two=(FrameLayout) findViewById(R.id.fr_on_two);
        ll_nodata=(LinearLayout) findViewById(R.id.ll_nodata);
        tv_no_data=(TextView) findViewById(R.id.tv_no_data);
        iv_no_data=(ImageView) findViewById(R.id.iv_no_data);
        rc= (RecyclerView) findViewById(R.id.rc);
        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
        lin.setOrientation(OrientationHelper.VERTICAL);
        rc.setLayoutManager(lin);

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
     * 获取开锁记录
     */
    public void getOpenLockRecoder(String key) {

        new Thread(){
            @Override
            public void run() {
                super.run();
                String json = ResponseService.getOperateLogFromeServiec(Integer.parseInt(lockid));
                Log.d("TAG","json"+json);
                if (!StringUtils.isEmpty(json)){
                    openLockRecoderBean bean = gson.fromJson(json, new TypeToken<openLockRecoderBean>() {}.getType());
                    List<openLockRecoderBean.ListBean> list = bean.getList();
                    data3.clear();
                    if (list.size()==0){
                        mHandler.sendEmptyMessage(MSG_PROGRESS_UI);
                    }else {
                        Iterator<openLockRecoderBean.ListBean> iterator = list.iterator();
                        while (iterator.hasNext()){
                            openLockRecoderBean.ListBean next = iterator.next();
                            data3.add(next);

                        }
                        mHandler.sendEmptyMessage(MSG_PROGRESS_UI_DATA);
                    }
                }
            }
        }.start();

    }

    /**
     * 点击搜索
     */
    private void initSearch() {
    et_open_lock= (EditText) findViewById(R.id.et_open_lock);
        open_lock_frl= (FrameLayout) findViewById(R.id.open_lock_frl);
        open_lock_pasw= (TextView) findViewById(R.id.open_lock_pasw);
        open_lock_app= (TextView) findViewById(R.id.open_lock_app);
        open_lock_finger= (TextView) findViewById(R.id.open_lock_finger);
        open_lock_ic= (TextView) findViewById(R.id.open_lock_ic);
        open_lock_shengfenz= (TextView) findViewById(R.id.open_lock_shengfenz);
        open_molder_ll= (LinearLayout) findViewById(R.id.open_molder_ll);
        re_reach= (RecyclerView) findViewById(R.id.re_reach);
        tv_open_lock_cannel= (TextView) findViewById(R.id.tv_open_lock_cannel);

        Iterator<openLockRecoderBean.ListBean> iterator = data3.iterator();
        while (iterator.hasNext()){
            openLockRecoderBean.ListBean next = iterator.next();
            int recordType = next.getRecordType();
        }

        et_open_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rc.setVisibility(View.GONE);//隐藏正常的recycleview
                open_molder_ll.setVisibility(View.VISIBLE);
                //显示搜索的布局
                //显示取消按钮
                //  open_lock_frl.setVisibility(View.VISIBLE);
                open_molder_ll.setVisibility(View.VISIBLE);
                re_reach.setVisibility(View.GONE);
                tv_open_lock_cannel.setVisibility(View.VISIBLE);
                // 每个模块的点击事件

                open_lock_pasw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 0
                        open_molder_ll.setVisibility(View.GONE);
                        re_reach.setVisibility(View.VISIBLE);
                        getOpenLockRecoder("0");
                        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
                        lin.setOrientation(OrientationHelper.VERTICAL);
                        re_reach.setLayoutManager(lin);


                    }
                });

                open_lock_app.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  1
                        open_molder_ll.setVisibility(View.GONE);
                        getOpenLockRecoder("1");
                        re_reach.setVisibility(View.VISIBLE);
                        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
                        lin.setOrientation(OrientationHelper.VERTICAL);
                        re_reach.setLayoutManager(lin);


                    }
                });

                open_lock_finger.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 2
                        re_reach.setVisibility(View.VISIBLE);
                        open_molder_ll.setVisibility(View.GONE);
                        getOpenLockRecoder("2");
                        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
                        lin.setOrientation(OrientationHelper.VERTICAL);
                        re_reach.setLayoutManager(lin);

                    }
                });

                open_lock_ic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    // 3
                        open_molder_ll.setVisibility(View.GONE);
                        getOpenLockRecoder("3");
                        re_reach.setVisibility(View.VISIBLE);
                        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
                        lin.setOrientation(OrientationHelper.VERTICAL);
                        re_reach.setLayoutManager(lin);

                    }
                });

                open_lock_shengfenz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    // 4
                        open_molder_ll.setVisibility(View.GONE);
                        getOpenLockRecoder("4");
                        re_reach.setVisibility(View.VISIBLE);
                        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
                        lin.setOrientation(OrientationHelper.VERTICAL);
                        re_reach.setLayoutManager(lin);

                    }
                });
                //显示结果   隐藏模块

            }

        });
        //点击取消
        tv_open_lock_cannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","取消走了");
                //隐藏搜索模块和搜索recycle
                open_molder_ll.setVisibility(View.GONE);
                re_reach.setVisibility(View.GONE);
                //显示正常的recycle
                rc.setVisibility(View.VISIBLE);
                getOpenLockRecoder("");
                LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
                lin.setOrientation(OrientationHelper.VERTICAL);
                rc.setLayoutManager(lin);
                tv_open_lock_cannel.setVisibility(View.GONE);
            }
        });

    }



    /**
     * 开锁记录回调  在上传
     * @param error
     * @param record
     */
    @Override
    public void bleOperlockRecord(final Error error, final String record) {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = null;
                if(error == Error.SUCCESS) {
                    String json = ResponseService.uploadOperateLog(Integer.parseInt(lockid), record);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        int errcode = jsonObject.getInt("errcode");
                        if(errcode == 0) {
                            msg = "上传成功";
                            getOpenLockRecoder("");
                        } else msg = jsonObject.getString("errmsg");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else msg = error.getErrorMsg();
                return msg;
            }
            @Override
            protected void onPostExecute(String msg) {
                super.onPostExecute(msg);

               Toast.makeText(MainApplication.getInstence(), msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}
