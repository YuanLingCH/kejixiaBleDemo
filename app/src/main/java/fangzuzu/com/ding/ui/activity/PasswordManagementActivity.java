package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.adapter.passwordManagerListAdapter;
import fangzuzu.com.ding.bean.passwordManagerBean;
import fangzuzu.com.ding.enumtype.Operation;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.utils.byteCunchu;
import fangzuzu.com.ding.utils.screenAdapterUtils;

import static fangzuzu.com.ding.MainApplication.mTTLockAPI;
import static fangzuzu.com.ding.R.id.tv_no_data;

/**
 * Created by lingyuan on 2018/7/3.
 */

public class PasswordManagementActivity extends BaseActivity implements MainApplication.BleOperDelectPaswListenner {

    Toolbar toolbar;
  RecyclerView pasw_rc;
    String lockid;
    List <passwordManagerBean.ListBean>data3;
    passwordManagerListAdapter adapter;
    SwipeRefreshLayout srf;
     int page=1;

    String lockType;
    String lockFlag;
    private final int PAGE_COUNT = 100;
    String mac; //蓝牙地址
    byte[]token2=new byte[4];
    ProgressDialog progressDialog;
    byte[] allowbyt;
    TextView tv_delet_quanbuPaws;//清空密码

    boolean isKitKat = false;
 LinearLayout   ll_nodata;
    ImageView iv_no_data;
    TextView tv_no_datae;
    String tTlockMac;
    int keyPawType;
    int keyPawId;
    int TTpostion;
    String ttOperType=null;
    private static final int MSG_PROGRESS_UI = 0x112;
    private static final int MSG_PROGRESS_UI_DATA = 0x113;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_PROGRESS_UI:
                    mHandler.sendEmptyMessage(MSG_PROGRESS_UI);
                    ll_nodata.setVisibility(View.VISIBLE);
                    pasw_rc.setVisibility(View.GONE);
                    tv_no_datae.setText("暂无密码");
                    iv_no_data.setImageResource(R.mipmap.no_miam);
                    srf.setRefreshing(false);
                    break;
                case MSG_PROGRESS_UI_DATA:
                    adapter=new passwordManagerListAdapter(data3, PasswordManagementActivity.this);
                    pasw_rc.setAdapter(adapter);
                    ll_nodata.setVisibility(View.GONE);
                    pasw_rc.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    srf.setRefreshing(false);
                    // 删除密码
                    adapter.setOnItemLongClickListener(new passwordManagerListAdapter.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(View view, final int position, final String keyboardPwd1, final int keyboardPwdId, final int keyboardPwdType) {




                            View view1 = getLayoutInflater().inflate(R.layout.custom_diaglog_layut_exit_app, null);
                            final TextView tv = (TextView) view1.findViewById(R.id.tv);
                            TextView tv_cancle= (TextView) view1.findViewById(R.id.add_cancle);
                            tv.setText("确定退出登录");
                            tv.setTextSize(14);
                            tv.setGravity(Gravity.CENTER);
                            TextView tv_submit= (TextView) view1.findViewById(R.id.add_submit);
                            final AlertDialog dialog = new AlertDialog.Builder(PasswordManagementActivity.this)
                                    .setView(view1)
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

                                    //检查蓝牙
                                    TTpostion=position;
                                    keyPawId=keyboardPwdId;
                                    keyPawType=keyboardPwdType;
                                    if (mTTLockAPI.isBLEEnabled(MainApplication.getInstence())){
                                        showProgressDialog("连接蓝牙...");
                                        mTTLockAPI.connect(tTlockMac);
                                        MainApplication.bleSession.setPassword(keyboardPwd1);
                                        MainApplication.bleSession.setOperation(Operation.DELETE_ONE_KEYBOARDPASSWORD);
                                        MainApplication.bleSession.setLockmac(tTlockMac);

                                    }else {
                                        //打开蓝牙设备
                                        mTTLockAPI.requestBleEnable(PasswordManagementActivity.this);
                                    }

                                }
                            });



                        }
                    });
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
        setContentView(R.layout.password_management_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        lockid = getIntent().getStringExtra("id");
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();


        mac = MainApplication.getInstence().getMac();
        allowbyt = byteCunchu.getbyte("allowbyt"); //得到锁标识符


        getdata();  //默认加载第一页
        initlize();

         tTlockMac = getIntent().getStringExtra("TTlockMac");
        MainApplication.SetBleOperDelectPaswListennerr(this);
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
        ll_nodata=(LinearLayout) findViewById(R.id.ll_nodata);
         iv_no_data=(ImageView) findViewById(R.id.iv_no_data);
        tv_no_datae=(TextView) findViewById(tv_no_data);
        tv_delet_quanbuPaws= (TextView) findViewById(R.id.tv_delet_quanbuPaws);
        srf= (SwipeRefreshLayout) findViewById(R.id.srf);
        pasw_rc= ( RecyclerView) findViewById(R.id.pasw_rc);
        final LinearLayoutManager lin=new LinearLayoutManager(PasswordManagementActivity.this);
        lin.setOrientation(OrientationHelper.VERTICAL);
        pasw_rc.setLayoutManager(lin);

        srf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              //  adapter.resetDatas();
                getdata();
            }
        });
 /*       //滑动监听
        pasw_rc.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                   if ((adapter.isFadeTips() == false && lastVisibleItem + 1 == adapter.getItemCount())){


                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("TAG", "滑动1");

                                    if (totalPage >= page) {
                                        getdata(++page);
                                    }
                                    page++;


                                    updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                                }
                            }, 500);




                   }
                }
                if (adapter.isFadeTips() == true && lastVisibleItem + 2 == adapter.getItemCount()) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("TAG","滑动2");

                            if (totalPage >= page) {
                                getdata(++page);
                            }
                            page++;
                          //  updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                        }
                    }, 500);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = lin.findLastVisibleItemPosition();
            }
        });*/
        //清空密码
        tv_delet_quanbuPaws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //连接蓝牙  删除服务器数据

                View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                EditText et_yanzhenpasw= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
                et_yanzhenpasw.setVisibility(View.GONE);
               // tv.setText("谨慎操作，导致数据丢失...");
              //  tv.setTextColor(Color.RED);
              //  tv.setGravity(Gravity.CENTER);
                TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                final AlertDialog dialog = new AlertDialog.Builder(PasswordManagementActivity.this)
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
                        //1 根据当前uid 来判断  和锁里面的uid 是不是蓝牙管理员和普通用户
                        //连接蓝牙 和删除服务器数据

                        //连接蓝牙




                    }
                });

            }
        });

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

    public void getdata() {
        data3=new ArrayList();
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    String tTlockId = MainApplication.getInstence().getTTlockId();
                    int lockId = Integer.parseInt(tTlockId);
                    String json = ResponseService.keyboardPwdList(lockId );
                    Log.d("TAG", "密码数据"+json);
                    Log.d("TAG", "lockId "+lockId );
                    Gson gson=new Gson();
                    passwordManagerBean bean = gson.fromJson(json, new TypeToken<passwordManagerBean>() {}.getType());
                    List<passwordManagerBean.ListBean> list = bean.getList();
                    if (list.size()==0){
                        //加载没有数据界面
                        mHandler.sendEmptyMessage(MSG_PROGRESS_UI);
                    }else {
                        data3.clear();
                        Iterator<passwordManagerBean.ListBean> iterator = list.iterator();
                        while (iterator.hasNext()){
                            passwordManagerBean.ListBean next = iterator.next();
                            data3.add(next);
                        }
                        mHandler.sendEmptyMessage(MSG_PROGRESS_UI_DATA);
                    }

                }
            }.start();





/*
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.paswManager(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                if (!StringUtils.isEmpty(body)){
                Log.d("TAG",body);
                passwordManagerBean bean = gson.fromJson(body, new TypeToken<passwordManagerBean>() {}.getType());
                DataBeanX data = bean.getData();
                    List<passwordManagerBean.DataBeanX.DataBean> data1 = data.getData();
                    if (data1.size()==0){
                        ll_nodata.setVisibility(View.VISIBLE);
                        pasw_rc.setVisibility(View.GONE);
                        tv_no_datae.setText("暂无密码");
                        Log.d("TAG","没有数据哦 ");
                        iv_no_data.setImageResource(R.mipmap.no_miam);

                    }else if (data1.size()>0){
                        ll_nodata.setVisibility(View.GONE);
                        pasw_rc.setVisibility(View.VISIBLE);


                Iterator<passwordManagerBean.DataBeanX.DataBean> iterator = data1.iterator();
                while (iterator.hasNext()){
                    DataBeanX.DataBean next = iterator.next();
                    data3.add(next);
                }

                adapter=new passwordManagerListAdapter(data3, PasswordManagementActivity.this);
                adapter.setOnItemLongClickListener(new passwordManagerListAdapter.OnItemLongClickListener() {
           @Override
           public void onItemLongClick(View view, final int position, final String id,String unlcokflag,String unlockType) {
               lockType=unlockType;
               lockFlag=unlcokflag;
               Log.d("TAG","点击我了"+position+"id:"+id+unlcokflag);
               View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
               final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
               // et_yanzhenpasw
               EditText et_yanzhenpasw= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
               et_yanzhenpasw.setVisibility(View.GONE);
               TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
              // tv.setText("谨慎操作，导致数据丢失...");
              // tv.setTextColor(Color.RED);
              // tv.setGravity(Gravity.CENTER);
               TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
               final AlertDialog dialog = new AlertDialog.Builder(PasswordManagementActivity.this)
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
                       //1 根据当前uid 来判断  和锁里面的uid 是不是蓝牙管理员和普通用户
                       //连接蓝牙 和删除服务器数据
                       initReceiveData();
                       initConnectBle(position,id);


                   }
               });


           }
       });
                pasw_rc.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }

                srf.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });*/
    }

















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

    @Override
    protected void onDestroy() {
        super.onDestroy();

finish();
    }



    @Override
    public void bleOperDelectPasw(Error error) {
        if (error==Error.SUCCESS){
            //三处服务器上的数据
            new AsyncTask<Void, Void, String>() {

                @Override
                protected void onPostExecute(String json) {
                    super.onPostExecute(json);
                    Log.d("TAG","删除json"+json);
                    LogUtil.d("json:" + json, true);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        int errcode = jsonObject.getInt("errcode");
                        String msg;
                        if(errcode != 0) {
                            msg = jsonObject.getString("errmsg");
                        } else {
                            msg = "删除密码成功";
                          /*  keyboardPwds.remove(position);
                            notifyDataSetChanged();*/

                            Toast.makeText(MainApplication.getInstence(), msg, Toast.LENGTH_SHORT).show();
                            data3.remove(TTpostion);
                            adapter.notifyDataSetChanged();
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
                    String json = ResponseService.deleteKeyboardPwd(id, keyPawId, 1);  //1  表示通过蓝牙  2 网管
                    return json;
                }
            }.execute();
        }else {
            cancelProgressDialog();
        }
    }
}

