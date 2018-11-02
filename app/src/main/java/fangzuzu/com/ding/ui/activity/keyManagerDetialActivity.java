package fangzuzu.com.ding.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lingyuan on 2018/9/18.
 */

public class keyManagerDetialActivity extends BaseActivity {
    Toolbar toolbar;
    boolean isKitKat = false;
    RelativeLayout rl_name,rl_time,rl_jilu;
    TextView key_lock,key_time,accept_name,send_name,send_time,key_time_end;
    Button but_key_delect;
    private String lockId;


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
        setContentView(R.layout.key_manager_detail_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        initViews();
        getIntentData();
        initEvent();
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
    private void initViews() {
        rl_name=(RelativeLayout) findViewById(R.id.rl_name);
        rl_time=(RelativeLayout) findViewById(R.id.rl_time);
        rl_jilu=(RelativeLayout) findViewById(R.id.rl_jilu);
        key_lock=(TextView) findViewById(R.id.key_lock);
        key_time=(TextView) findViewById(R.id.key_time);
        accept_name=(TextView) findViewById(R.id.accept_name);
        send_name=(TextView) findViewById(R.id.send_name);
        send_time=(TextView) findViewById(R.id.send_time);
        but_key_delect=(Button) findViewById(R.id.but_key_delect);
        key_time_end=(TextView) findViewById(R.id.key_time_end);

    }

    /**
     * 接收上一个界面传过来的数据
     */
    String startTime;
    String endTime;
    String keyName;
    String childUsername;
    String parentId;
    String userId1;
    String id;
    String TTKeyId;
    String TTsendTime;
    String TTlockId;
    private String keyStatus; //钥匙状态
    String keyRight;
    public void getIntentData() {
      startTime = getIntent().getStringExtra("startTime");
        endTime = getIntent().getStringExtra("endTime");
       keyName = getIntent().getStringExtra("keyName");
        childUsername = getIntent().getStringExtra("username");
        parentId = getIntent().getStringExtra("senderUsername");
      userId1 = getIntent().getStringExtra("userId1");
       id = getIntent().getStringExtra("id");
        TTKeyId = getIntent().getStringExtra("KeyId");
        TTsendTime = getIntent().getStringExtra("TTsendTime");
      TTlockId = getIntent().getStringExtra("lockId");
        keyRight = getIntent().getStringExtra("keyRight");
        keyStatus = getIntent().getStringExtra("keyStatus");
        Log.d("TAG","keyStatus:"+keyStatus+"TTlockId"+TTlockId);

    }
    /**
     * 点击事件
     */
    private void initEvent() {

        accept_name.setText(childUsername);
        send_name.setText(parentId);
        send_time.setText(TTsendTime);
        key_lock.setText(childUsername);
        if (startTime.equals("0")&&endTime.equals("0")){
            //永久
            key_time.setText("永久");
            key_time_end.setVisibility(View.GONE);
        }else {
            String   sendTime = endTime.substring(0, endTime.length() - 3);
            int TTend = Integer.parseInt(sendTime);
            final String TTsendendTime = unixTime.stampToTime(TTend );


            String   sendSTime = startTime.substring(0, startTime.length() - 3);
            int TTSend = Integer.parseInt(sendSTime);
            final String TTendTime = unixTime.stampToTime(TTSend );
            key_time.setText(TTendTime);
            key_time_end.setText(TTsendendTime);
        }

    /*    *//**
         * 修改名称
         *//*
        rl_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(keyManagerDetialActivity.this);
                View view = inflater.inflate(R.layout.updata_lock_name, null);
                TextView add_cancle= (TextView) view.findViewById(R.id.add_cancle);
                // add_submit
                TextView add_submit= (TextView) view.findViewById(R.id.add_submit);


                final EditText editText = (EditText) view.findViewById(R.id.dialog_edit_name);
                final AlertDialog dialog = new AlertDialog.Builder(keyManagerDetialActivity.this)
                        .setView(view)
                        .create();
                dialog.show();
                add_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                add_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String trim = editText.getText().toString().trim();
                        if (!StringUtils.isEmpty(trim)){
                            dialog.dismiss();
                         upDatakeyLockName(trim,startTime,endTime);
                        }else {

                            Toast.makeText(keyManagerDetialActivity.this,"内容不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });*/

        /**
         * 修改时间
         */
        rl_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainApplication.getInstence(),modificationKeyLockMessageActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("startTime",startTime);
                if (startTime.equals("0")&&endTime.equals("0")){  //永久
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                    String now = sdf.format(new Date());
                    intent.putExtra("endTime",now);
                    intent.putExtra("startTime",now);
                }else {
                    String   sendTime = endTime.substring(0, endTime.length() - 3);
                    int TTend = Integer.parseInt(sendTime);
                    final String TTsendendTime = unixTime.stampToTime(TTend );
                    intent.putExtra("endTime",TTsendendTime);
                    String   sendSTime = startTime.substring(0, startTime.length() - 3);
                    int TTSend = Integer.parseInt(sendSTime);
                    final String TTendTime = unixTime.stampToTime(TTSend );
                    intent.putExtra("startTime",TTendTime);
                }
                intent.putExtra("TTKeyId",TTKeyId);
                intent.putExtra("keyName",keyName);
                startActivity(intent);
            }
        });
        /**
         * 删除钥匙
         */
        but_key_delect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Integer, String>() {
                    @Override
                    protected String doInBackground(Void... params) {

                        int TKeyId = Integer.parseInt(TTKeyId);
                        String json = ResponseService.deleteKey(TKeyId);
                        Log.d("TAG","接收json"+json );
                        String msg = "";
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            if(jsonObject.getInt("errcode") == 0) {
                                msg = getString(R.string.words_delete_ekey_successed);
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
                        Toast.makeText(keyManagerDetialActivity.this,msg, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }.execute();
            }
        });
        /**
         * 开锁记录
         */
        rl_jilu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainApplication.getInstence(),childOpenClickRecord.class);
                intent.putExtra("uid",userId1); //child 的
                intent.putExtra("keyName",keyName);
                intent.putExtra("lockId",lockId);
                startActivity(intent);
            }
        });
    }

    /**
     * 上传修改钥匙的名称
     *
     */
    private void upDatakeyLockName(final String lockName, String start, String end) {
        Map <String,String>map=new HashMap<>();
        map.put("id",id);
        map.put("keyName",lockName);
        map.put("startTime",start);
        map.put("endTime",end);
        final Gson gson=new Gson();
        final String s = gson.toJson(map);
        Log.d("TAG","上传数据"+s);
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();

        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.modificationKeyLcokMeaasge(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                if (!StringUtils.isEmpty(body)){


                Log.d("TAG","修改数据"+body);
                msg o = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                int code = o.getCode();
                if (code==1001){
                    key_lock.setText(lockName);
                    Toast.makeText(MainApplication.getInstence(),"修改名称成功",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(MainApplication.getInstence(),"修改名称失败",Toast.LENGTH_LONG).show();
                }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.keydetail,menu);
        if (keyStatus.equals("110401")){// 正常使用
            MenuItem item = menu.findItem(R.id.key_freeze);
            item.setVisible(true);
            MenuItem item1 = menu.findItem(R.id.key_relieve_freeze);
            item1.setVisible(false);
        }else if (keyStatus.equals("110405")){  //冻结
            MenuItem item = menu.findItem(R.id.key_freeze);
            item.setVisible(false);
            MenuItem item1 = menu.findItem(R.id.key_relieve_freeze);
            item1.setVisible(true);
        }
        String userType = MainApplication.getInstence().getUserType();
        Log.d("TAG","userType,,,"+userType);
        if (userType.equals("110301")){
        if (keyRight.equals("0")){  //  0没有授权  1授权
            Log.d("TAG","userType"+userType+"走了110301");
            MenuItem item = menu.findItem(R.id.key_relieve_authorization);
            item.setVisible(false);
            MenuItem item1 = menu.findItem(R.id.key_authorization);
            item1.setVisible(true);
        }else {
            MenuItem item = menu.findItem(R.id.key_relieve_authorization);
            item.setVisible(true);
            MenuItem item1 = menu.findItem(R.id.key_authorization);
            item1.setVisible(false);
        }
        }else if (userType.equals("110302")){
            Log.d("TAG","userType"+userType+"走了110302");
            MenuItem item = menu.findItem(R.id.key_relieve_authorization);
            item.setVisible(false);
            MenuItem item1 = menu.findItem(R.id.key_authorization);
            item1.setVisible(false);
        }
        return  true;
    }
    TextView tv;
    TextView tv_cancle;
    TextView tv_submit;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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


        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;

            case R.id.key_authorization:  //钥匙授权
                tv.setText("授权用户可以发送该锁的电子钥匙和密码");
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
                        keyauthorize();
                    }
                });

                break;
            case R.id.key_freeze:  //冻结
                tv.setText("冻结会在用户APP联网后生效");



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
                        keyFreeze();
                    }
                });


                break;
            case R.id.key_relieve_freeze:  //解除冻结

                tv.setText("解冻会在用户APP联网后生效");
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
                        keyRelieveFreeze();  //解冻钥匙
                    }
                });
                break;

            case R.id.key_relieve_authorization:  //解除授权

                tv.setText("取消授权会在用户APP联网后生效");
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
                        keyRelieveauthorization();  //取消授权
                    }
                });
                break;
        }
        return true;
    }

    /**
     * 钥匙取消授权
     */
    private void keyRelieveauthorization() {
        new AsyncTask<Void, String, String>() {


            @Override
            protected String doInBackground(Void... params) {

                String s = ResponseService. RelieveAuthorize(Integer.parseInt(TTKeyId),Integer.parseInt( TTlockId));
                Log.d("TAG","钥匙取消授权"+s);
                return s ;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                cancelProgressDialog();
                try {
                    JSONObject object=new JSONObject(s);
                    String errcode = object.getString("errcode");
                    if (errcode.equals("0")){
                        show_Toast("钥匙取消成功");
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }.execute();
    }

    /**
     * 钥匙授权
     */
    private void keyauthorize() {
        new AsyncTask<Void, String, String>() {


            @Override
            protected String doInBackground(Void... params) {

                String s = ResponseService. authorize(Integer.parseInt(TTKeyId),Integer.parseInt( TTlockId));
                Log.d("TAG","钥匙授权"+s);
                return s ;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                cancelProgressDialog();
                try {
                    JSONObject object=new JSONObject(s);
                    String errcode = object.getString("errcode");
                    if (errcode.equals("0")){
                        show_Toast("钥匙授权成功");
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }.execute();
    }

    /**
     *  //解冻钥匙
     */
    private void keyRelieveFreeze() {
        new AsyncTask<Void, String, String>() {


            @Override
            protected String doInBackground(Void... params) {

                String s = ResponseService. keyRelieveFreeze(Integer.parseInt(TTKeyId));
                Log.d("TAG","解冻数据"+s);
                return s ;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                cancelProgressDialog();
                try {
                    JSONObject object=new JSONObject(s);
                    String errcode = object.getString("errcode");
                    if (errcode.equals("0")){
                        show_Toast("解冻钥匙成功");
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }.execute();
    }

    private void keyFreeze() {
        new AsyncTask<Void, String, String>() {


            @Override
            protected String doInBackground(Void... params) {

                String s = ResponseService.freezeKey(Integer.parseInt(TTKeyId));
                Log.d("TAG","冻结数据"+s);
                return s ;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                cancelProgressDialog();
                try {
                    JSONObject object=new JSONObject(s);
                    String errcode = object.getString("errcode");
                    if (errcode.equals("0")){
                        show_Toast("冻结钥匙成功");
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }.execute();


    }


}
