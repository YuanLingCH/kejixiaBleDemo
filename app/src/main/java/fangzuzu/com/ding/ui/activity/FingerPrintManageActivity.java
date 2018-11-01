package fangzuzu.com.ding.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.adapter.fingerListAdapter;
import fangzuzu.com.ding.bean.fingerBean;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;

/**
 * 添加指纹
 * Created by lingyuan on 2018/6/4.
 */

public class FingerPrintManageActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
    Toolbar toolbar;
    List fingerData;
    RecyclerView Lv;
    ImageView iv_no_data;
    TextView tv_no_data;
    fingerListAdapter adapter;
    LinearLayout ll_no_data;
    boolean isKitKat = false;
    SwipeRefreshLayout srf_layout;
    String TTLockId;
    private static final int MSG_PROGRESS_UI = 0x112;
    private static final int MSG_PROGRESS_UI_DATA = 0x113;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_PROGRESS_UI:
                    mHandler.sendEmptyMessage(MSG_PROGRESS_UI);
                    Lv.setVisibility(View.GONE);
                    ll_no_data.setVisibility(View.VISIBLE);
                    iv_no_data.setImageResource(R.mipmap.finger);
                    tv_no_data.setText("暂无指纹");
                    srf_layout.setRefreshing(false);
                    break;
                case MSG_PROGRESS_UI_DATA:
                    Log.d("TAG","刷新数据"+ fingerData.size());

                    Lv.setVisibility(View.VISIBLE);
                    ll_no_data.setVisibility(View.GONE);
                   ;
                    adapter=new fingerListAdapter(fingerData,FingerPrintManageActivity.this);
                    Lv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    srf_layout.setRefreshing(false);
                    break;
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_finger_printer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();

        initView();



        srf_layout.setOnRefreshListener(this);
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

    private void initView() {
        ll_no_data=(LinearLayout) findViewById(R.id.ll_nodata);
        iv_no_data=(ImageView) findViewById(R.id.iv_no_data);
        tv_no_data=(TextView) findViewById(R.id.tv_no_data);
        Lv= (RecyclerView) findViewById(R.id.finger_lv);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        Lv.setLayoutManager(layoutManager);
        //添加指纹
        srf_layout=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh);


        srf_layout.post(new Runnable() {
            @Override
            public void run() {
                srf_layout.setRefreshing(true);
                onRefresh();//是否触发onRefresh中的方法
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.finger,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.seting_finger:
                Intent intent=new Intent(MainApplication.getInstence(),addfingerOneStepActivity.class);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 查询指纹
     */
    public void getData() {
        fingerData=new ArrayList();
       new Thread(){
           @Override
           public void run() {
               super.run();
               TTLockId = MainApplication.getInstence().getTTlockId();
               String json = ResponseService.getFingerPrintListFromeServices( Integer.parseInt(TTLockId));
               Log.d("TAG",json);
               if (!StringUtils.isEmpty(json)){
                   Gson gson=new Gson();
                   fingerBean bean= gson.fromJson(json, new TypeToken<fingerBean>() {}.getType());
                   List<fingerBean.ListBean> list = bean.getList();
                   if (list.size()==0){

                       mHandler.sendEmptyMessage(MSG_PROGRESS_UI);
                   }else {
                       fingerData.clear();
                       Iterator<fingerBean.ListBean> iterator = list.iterator();
                       while (iterator.hasNext()){
                           fingerBean.ListBean next = iterator.next();
                           fingerData.add(next);
                       }
                       mHandler.sendEmptyMessage(MSG_PROGRESS_UI_DATA);
                   }


               }
           }
       }.start();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG","界面 onRestart");
        getData();
    }

    @Override
    public void onRefresh() {
        Log.d("TAG","刷新指纹");
        getData();
    }
}
