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

import fangzuzu.com.ding.R;
import fangzuzu.com.ding.adapter.IcListAdapter;
import fangzuzu.com.ding.bean.Icbean;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;

import static fangzuzu.com.ding.R.id.ll_nodata;

/**
 * Created by lingyuan on 2018/6/5.
 */

public class addICCardActivity extends BaseActivity{
    Toolbar toolbar;
    ImageView iv_no_data;
    TextView tv_no_data;
    RecyclerView rc;
    List data3;
    IcListAdapter adapter;
    boolean isKitKat = false;
LinearLayout ll_no_data;
    SwipeRefreshLayout srf;
    String tTlockMac;
    private String TTlockID;

    private static final int MSG_PROGRESS_UI = 0x112;
    private static final int MSG_PROGRESS_UI_DATA = 0x113;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_PROGRESS_UI:
                  //  mHandler.sendEmptyMessage(MSG_PROGRESS_UI);
                    rc.setVisibility(View.GONE);
                    ll_no_data.setVisibility(View.VISIBLE);
                    iv_no_data.setImageResource(R.mipmap.no_card);
                    tv_no_data.setText("暂无卡片");
               srf.setRefreshing(false);
                    break;
                case MSG_PROGRESS_UI_DATA:
                    rc.setVisibility(View.VISIBLE);
                    ll_no_data.setVisibility(View.GONE);
                    adapter=new  IcListAdapter(data3,addICCardActivity.this);
                    rc.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                    srf.setRefreshing(false);
                    Log.d("TAG","刷新走了。。。");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        setContentView(R.layout.add_ic_card_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();

        initViews();
        initlize();

      getData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG","onRestart");
        getData();
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
         tTlockMac = getIntent().getStringExtra("TTlockMac");
        TTlockID = getIntent().getStringExtra("id");
        ll_no_data=(LinearLayout) findViewById(ll_nodata);
        iv_no_data=(ImageView) findViewById(R.id.iv_no_data);
        tv_no_data=(TextView) findViewById(R.id.tv_no_data);
        rc= (RecyclerView) findViewById(R.id.lv);
        srf=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rc.setLayoutManager(layoutManager);
        srf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //  adapter.resetDatas();
                Log.d("TAG","刷新走了");
                getData();
            }
        });
    }

    private void initViews() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return  true;
      //  return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=new Intent(addICCardActivity.this,addICCardOneStepActivity.class);

        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.seting_ic:
           //添加ic卡
                intent.putExtra("addType","0");
                intent.putExtra("toolbar","添加卡片");
                intent.putExtra("byte","3");
                intent.putExtra("tTlockMac",tTlockMac);
                startActivity(intent);
                break;
  /*          case R.id.seting_identity_card:
                //添加身份证

                intent.putExtra("addType","1");
                intent.putExtra("toolbar","添加身份证");
                intent.putExtra("byte","4");
                startActivity(intent);
                break;*/
        }
        return true;
      //  return super.onOptionsItemSelected(item);
    }

    public void getData() {

        data3=new ArrayList();
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    String json = ResponseService.getIcCardList(TTlockID);
                    Log.d("TAG",json);
                    if (!StringUtils.isEmpty(json)){
                        Gson gson=new Gson();
                        Icbean bean= gson.fromJson(json, new TypeToken<Icbean>() {}.getType());
                        List<Icbean.ListBean> list = bean.getList();
                        if (list.size()==0){

                            mHandler.sendEmptyMessage(MSG_PROGRESS_UI);
                        }else {
                            data3.clear();
                            Iterator<Icbean.ListBean> iterator = list.iterator();
                            while (iterator.hasNext()){
                                Icbean.ListBean next = iterator.next();
                                data3.add(next);
                            }
                            mHandler.sendEmptyMessage(MSG_PROGRESS_UI_DATA);
                        }


                    }

                }
            }.start();
    }


}
