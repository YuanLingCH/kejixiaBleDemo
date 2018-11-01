package fangzuzu.com.ding.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;

import static fangzuzu.com.ding.MainApplication.bleSession;

/**
 * Created by lingyuan on 2018/6/4.
 */

public class addfingerThreeStepActivity extends BaseActivity  implements MainApplication.BleOperAddFingerCollectionListenner,MainApplication.BleOperAddFingerListenner{

    Toolbar toolbar;
TextView tv_finger;
    ImageView iv_finger;
    Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case 1:
                    iv_finger.setImageResource(R.mipmap.finger_2);
                    tv_finger.setText("("+1+"/4)");
                    break;
                case 2:
                    tv_finger.setText("("+2+"/4)");
                    iv_finger.setImageResource(R.mipmap.finger_3);
                    break;
                case 3:
                    tv_finger.setText("("+3+"/4)");
                    iv_finger.setImageResource(R.mipmap.finger_4);
                    break;
                case 0:   //录入指纹成功
                    tv_finger.setText("("+4+"/4)");
                    iv_finger.setImageResource(R.mipmap.finger_5);

                    Toast.makeText(MainApplication.getInstence(),"指纹录入成功",Toast.LENGTH_SHORT).show();

                    break;
                case 6:   //录入指纹成功


                    Toast.makeText(MainApplication.getInstence(),"指纹录入失败",Toast.LENGTH_SHORT).show();
                    finish();

                    break;
            }
        }
    };
    private String statTime;
    private String endTime;
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
        setContentView(R.layout.add_finger_three_activity);

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        MainApplication.SetBleOperAddFingerCollectionListenner(this);
        MainApplication.SetBleOperAddFingerListenner(this);
        iv_finger=(ImageView) findViewById(R.id.iv_finger);
        tv_finger=(TextView) findViewById(R.id.tv_finger);
        getIntentData();
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
     * 指纹采集回调
     * @param error
     * @param l
     */
    @Override
    public void bleOperAddFingerCollection(Error error, int l) {
        Log.d("TAG","指纹回调"+l);
        mhandler.sendEmptyMessage(l);
    }

    @Override
    public void bleOperAddFinger(Error error, long l) {
        if (error==Error.SUCCESS){


        if (l!=0){   // 添加完成时的回调
            mhandler.sendEmptyMessage(0);
            if (!bleSession.isFlag()){  //限时


                sendDataToService(error,l,Long.parseLong(statTime),Long.parseLong(endTime));
            }else { //永久
                sendDataToService(error,l,0,0);
            }
        }

        }else if (error==Error.LOCK_OPERATE_FAILED){
            mhandler.sendEmptyMessage(6); //操作失败
        }
    }


    /**
     * 上传到服务器
     * @param error
     * @param fingerNumbler
     */
    public void sendDataToService(Error error, final long fingerNumbler, final long kaishi1, final long jieshu1){
        if (error==Error.SUCCESS){
            //三处服务器上的数据
            new AsyncTask<Void, Void, String>() {

                @Override
                protected void onPostExecute(String json) {
                    super.onPostExecute(json);
                    Log.d("TAG","json"+json);
                    LogUtil.d("json:" + json, true);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String cardId = jsonObject.getString("fingerprintId");
                        String msg;
                        if(!StringUtils.isEmpty(cardId)) {
                            msg = "上传指纹成功";
                            Toast.makeText(MainApplication.getInstence(), msg, Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(addfingerThreeStepActivity.this,FingerPrintManageActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            msg = "delete passcode successed by server";
                          /*  keyboardPwds.remove(position);
                            notifyDataSetChanged();*/



                        }
                        cancelProgressDialog();
                       // hideProgressDialog();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected String doInBackground(Void... params) {
                    String tTlockId = MainApplication.getInstence().getTTlockId();
                    int id = Integer.parseInt(tTlockId);
                    Log.d("TAG","json"+id+"fingerName:"+fingerName+"fingerNumbler:"+fingerNumbler+"kaishi1:"+kaishi1+"jieshu1"+jieshu1);
                    String json = ResponseService.AddFinger(id, fingerName, fingerNumbler+"",kaishi1,jieshu1);  //1  表示通过蓝牙  2 网管
                    return json;
                }
            }.execute();
        }else {
            cancelProgressDialog();
        }
    }

    public void getIntentData() {
        statTime = getIntent().getStringExtra("statTime");
        endTime = getIntent().getStringExtra("endTime");
        fingerName = getIntent().getStringExtra("fingerName");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
