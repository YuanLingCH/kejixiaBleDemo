package fangzuzu.com.ding.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import android.view.Display;
import android.view.Gravity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.adapter.KeyManageAdapter;
import fangzuzu.com.ding.bean.keyManagerBean;
import fangzuzu.com.ding.impl.OnMqttListener;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.utils.screenAdapterUtils;


/**
 * Created by lingyuan on 2018/6/20.
 */

public class ElectKeyManagerActivity extends BaseActivity implements OnMqttListener {
    Toolbar toolbar;
    KeyManageAdapter adapter;
    RecyclerView key_manage_lv;
    List data3;

    SwipeRefreshLayout srf;
    boolean isKitKat = false;
    LinearLayout ll_nodata;
    ImageView iv_no_data;
    TextView tv_no_data;
    String uid;
    private static final int MSG_PROGRESS_UI = 0x112;
    private static final int MSG_PROGRESS_UI_DATA = 0x113;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_PROGRESS_UI:
                    mHandler.sendEmptyMessage(MSG_PROGRESS_UI);
                    key_manage_lv.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.VISIBLE);
                    iv_no_data.setImageResource(R.mipmap.no_key);
                    tv_no_data.setText("暂无钥匙");
                    srf.setRefreshing(false);
                    break;
                case MSG_PROGRESS_UI_DATA:
                    adapter=new KeyManageAdapter(data3,ElectKeyManagerActivity.this);
                    key_manage_lv.setAdapter(adapter);

                    adapter.notifyDataSetChanged();

            srf.setRefreshing(false);
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
        setContentView(R.layout.elect_key_manager_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();

        getData();
        initlize();
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
        uid = SharedUtils.getString("uid");
        iv_no_data=(ImageView) findViewById(R.id.iv_no_data);
        tv_no_data=(TextView) findViewById(R.id.tv_no_data);
        ll_nodata=(LinearLayout) findViewById(R.id.ll_nodata);
        srf= (SwipeRefreshLayout) findViewById(R.id.srf_elect);
        key_manage_lv= (RecyclerView) findViewById(R.id.key_manage_lv);
        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
        lin.setOrientation(OrientationHelper.VERTICAL);
        key_manage_lv.setLayoutManager(lin);
        srf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("TAG","刷新走了");
                getData();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.keymanager,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.send_key_manager:
                Intent intent=new Intent(ElectKeyManagerActivity.this,sendKeyActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.empty_key:  //清空钥匙
                View view = getLayoutInflater().inflate(R.layout.custom_diaglog_layut_exit_app, null);
                tv = (TextView) view.findViewById(R.id.tv);
                tv_cancle= (TextView) view.findViewById(R.id.add_cancle);

                tv.setTextSize(14);
                tv.setGravity(Gravity.CENTER);
                tv_submit= (TextView) view.findViewById(R.id.add_submit);
                final AlertDialog dialog = new AlertDialog.Builder(this)
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

                tv.setText("是否清空");
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
                        showProgressDialog("正在连接数据...");
                        emptyKey();
                    }
                });
                break;
        }
        return true;
    }

    /**
     * 清空钥匙
     */

    TextView tv;
    TextView tv_cancle;
    TextView tv_submit;
    private void emptyKey() {


        new AsyncTask<Void, String, String>() {


            @Override
            protected String doInBackground(Void... params) {
                String tTlockId = MainApplication.getInstence().getTTlockId();
                String s = ResponseService.emptyKey(Integer.parseInt(tTlockId));
                Log.d("TAG","清空钥匙"+s);
                return s ;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                cancelProgressDialog();
                try {
                    JSONObject object=new JSONObject(s);
                    String errcode = object.getString("errcode");
                    String description = object.getString("description");
                    if (errcode.equals("0")){
                        show_Toast("清空钥匙成功");
                        getData();
                    }else {
                        show_Toast(description);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }.execute();
    }

    public void getData() {
        data3=new ArrayList();
     /*   Log.d("TAG","刷新走2");
        String uid = SharedUtils.getString("uid");
        Map<String,String>map=new HashMap<>();
        map.put("pageSize","10");
        map.put("currentPage","1");
        map.put("lockId",lockid);
        map.put("userId",uid );
        Log.d("TAG","lockid"+lockid);
        Log.d("TAG","uid"+uid);
        final Gson gson=new Gson();
        String s = gson.toJson(map);
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();
        data3.clear();
        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.keyManager(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                if (!StringUtils.isEmpty(body)){


                keyManagerBean bean = gson.fromJson(body, new TypeToken<keyManagerBean>() {}.getType());
                keyManagerBean.DataBeanX data = bean.getData();
                if (data==null){
                    //加载没有数据界面
                    key_manage_lv.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.VISIBLE);
                    iv_no_data.setImageResource(R.mipmap.no_key);
                    tv_no_data.setText("暂无钥匙");
                }else if (data!=null){
                    key_manage_lv.setVisibility(View.VISIBLE);
                    ll_nodata.setVisibility(View.GONE);
                    List<keyManagerBean.DataBeanX.DataBean> data1 = data.getData();
                    Iterator<keyManagerBean.DataBeanX.DataBean> iterator = data1.iterator();
                    while (iterator.hasNext()){
                        keyManagerBean.DataBeanX.DataBean next = iterator.next();
                        data3.add(next);
                    }

                    Log.d("TAG", body );
                    adapter=new KeyManageAdapter(data3,ElectKeyManagerActivity.this);

    *//*                adapter.setOnItemLongClickListener(new  KeyManageAdapter.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(View view, final int position, final String id , final String userId) {

                            View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                            final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                            TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                            EditText et_yanzhenpasw= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
                            et_yanzhenpasw.setVisibility(View.GONE);

                            TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                            final AlertDialog dialog = new AlertDialog.Builder(ElectKeyManagerActivity.this)
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
                                    delectData(id,position ,userId);

                                }
                            });

                        }
                    });*//*

                    key_manage_lv.setAdapter(adapter);

                    adapter.notifyDataSetChanged();

                }
                    srf.setRefreshing(false);
            }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }

        });*/

        new AsyncTask<Void, String, String>() {
            @Override
            protected void onPostExecute(String msg) {
                super.onPostExecute(msg);

            }

            @Override
            protected String doInBackground(Void... params) {
                String tTlockId = MainApplication.getInstence().getTTlockId();
                int lockId = Integer.parseInt(tTlockId);
                String json = ResponseService.getKeyListDataFromeService(lockId );
                Gson gson=new Gson();
                keyManagerBean bean = gson.fromJson(json, new TypeToken<keyManagerBean>() {}.getType());
                List<keyManagerBean.ListBean> list = bean.getList();
                if (list.size()==0){
                    //加载没有数据界面
                    mHandler.sendEmptyMessage(MSG_PROGRESS_UI);
                }else {

                    data3.clear();
                    Iterator<keyManagerBean.ListBean> iterator = list.iterator();
                    String username1 = SharedUtils.getString("username");
                    while (iterator.hasNext()){
                        keyManagerBean.ListBean next = iterator.next();
                        String senderUsername = next.getSenderUsername();
                        String[] strArraysenderUsername = senderUsername.split("_");
                        if (username1.equals(strArraysenderUsername[1]+"")){ //蓝牙管理员
                            data3.add(next);
                        }

                    }
                    mHandler.sendEmptyMessage(MSG_PROGRESS_UI_DATA);
                }
                if (data3.size()==0){
                    mHandler.sendEmptyMessage(MSG_PROGRESS_UI);
                }
                String msg = "";
                Log.d("TAG", json);
                return msg;
            }
        }.execute();






    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getData();
    }

    @Override
    public void mqttSuccess() {

    }

    @Override
    public void mqttFaild() {

    }
}
