package fangzuzu.com.ding.ui.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.util.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.enumtype.Operation;
import fangzuzu.com.ding.event.createtimeMessage;
import fangzuzu.com.ding.event.losetimeMessage;
import fangzuzu.com.ding.event.passwordMessage;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.widget.DatePicierFragment;

import static fangzuzu.com.ding.MainApplication.bleSession;
import static fangzuzu.com.ding.MainApplication.mTTLockAPI;

/**  自定义密码
 * Created by yuanling on 2018/5/15.
 */

public class customFragment extends BaseFragment implements MainApplication.BleOperCustomerPaswkListenner {
    Button but_set_pasw;
   EditText tv_pasw;
    LinearLayout create_time ,lose_time;
    private TextView currentDate, currentTime;


    CheckBox app_input,lock_input;
    ProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.custome_fragment_layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);


            MainApplication.setBleOperCustomerPaswkListenner(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }
    String endtime;

    //失效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(losetimeMessage event){
        endtime = event.getTime();
        currentDate.setText(endtime);
        Log.d("TAG","event"+endtime);
    }

    String createtime;
    //生效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBuscreate(createtimeMessage event){
        createtime = event.getTime();
        currentTime.setText( createtime);
        Log.d("TAG","event"+currentTime);


    }
    String senddata; //获取输入密码
    long kaishi=0;
    long jieshu=0;
    String time;
    String endtime1;
    @Override
    protected void initViews() {
        currentTime= (TextView) root.findViewById(R.id.electfrg_effect_time);
        currentDate= (TextView) root.findViewById(R.id.electfrg_lose_time);
        create_time= (LinearLayout) root.findViewById(R.id.create_time);
        lose_time=(LinearLayout) root.findViewById(R.id.lose_time);
        DatePicierFragment.initDatePicker(currentDate, currentTime, getActivity());
        create_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicierFragment.getCustomDatePicker2().show(currentTime.getText().toString());
            }
        });
        lose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicierFragment.getCustomDatePicker1().show(currentTime.getText().toString());
            }
        });



        tv_pasw= (EditText) root.findViewById(R.id.tv_pasw);
        but_set_pasw= (Button) root.findViewById(R.id.but_set_pasw);
        //输入数字
        but_set_pasw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               time = currentTime.getText().toString().trim();
                String s = unixTime.dateToStampone(time);
                kaishi=Long.parseLong(s);
                Log.d("TAG","开始时间戳"+s);
                String substring1 = s.substring(0, s.length() - 3);
                int startTime = Integer.parseInt(substring1);
                Log.d("TAG","开始时间"+ time);

//  currentDate


                 endtime1 = currentDate.getText().toString().trim();
                String send = unixTime.dateToStampone(endtime);
                 jieshu=Long.parseLong(send);
                Log.d("TAG","结束时间戳"+send);
                String substring1end = send.substring(0, send.length() - 3);
                int endTime = Integer.parseInt(substring1end);
                Log.d("TAG","结束时间"+ endtime);
                if (StringUtils.isEmpty(endtime)) {
                    Toast.makeText(MainApplication.getInstence(), "请输入失效时间", Toast.LENGTH_SHORT).show();
                }else if (startTime<endTime&&startTime!=endTime){


            // 点击生成密码 连接蓝牙
                String loseTime = currentDate.getText().toString().trim();
              /*  if (app_input.isChecked()){*/

                senddata = tv_pasw.getText().toString().trim(); //获取输入密码
                Log.d("TAG","senddata"+senddata);

             if (StringUtils.isEmpty(senddata)){

                    Toast.makeText(MainApplication.getInstence(), "请输入密码", Toast.LENGTH_SHORT).show();
                }
                else if(!StringUtils.isEmpty(senddata)&&!StringUtils.isEmpty(endtime)) {
                 if(senddata.length() < 4 || senddata.length() > 9){

                     Toast.makeText(MainApplication.getInstence(), "请输入4-9位数字", Toast.LENGTH_SHORT).show();

                 }else {
                     if ( mTTLockAPI.isBLEEnabled(MainApplication.getInstence())){
                         showProgressDialog("","正在连接蓝牙...");
                         bleSession.setOperation(Operation.ADD_PASSCODE);
                         String mac = MainApplication.getInstence().getMac();
                         MainApplication.bleSession.setPassword(senddata);
                         bleSession.setLockmac(mac);
                         Log.d("TAG","mac"+mac);
                         mTTLockAPI.startBTDeviceScan();
                         MainApplication.bleSession.setEndDate(jieshu);
                         MainApplication.bleSession.setStartDate(kaishi);

                     }else {
                         //打开蓝牙设备
                         mTTLockAPI.requestBleEnable(getActivity());
                     }
                 }


                }


                }else {
                    Toast.makeText(getActivity(),"失效时间必须比当前时间和生效时间晚",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    protected void initEvents() {


    }


    private void dialog(String text) {
        View viewDialog = getActivity().getLayoutInflater().inflate(R.layout.custom_diaglog_deviceslayut, null);
        final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
        TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(18);
        tv.setGravity(Gravity.CENTER);

        TextView tv_de_me= (TextView)viewDialog.findViewById(R.id.tv_de_me);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(viewDialog)
                .create();
        Window window=dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        WindowManager manager=getActivity().getWindowManager();
        Display defaultDisplay = manager.getDefaultDisplay();
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.width= (int) (defaultDisplay.getWidth()*0.85);
        dialog.getWindow().setAttributes(p);     //设置生效
        tv_de_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void initData() {

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
     * 密码回调
     * @param error
     * @param pasw
     * @param data1
     */
    @Override
    public void bleOperCustomerPasw(Error error, final String pasw, final long data1) {
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
                        String cardId = jsonObject.getString("keyboardPwdId");
                        String msg;
                        if(!StringUtils.isEmpty(cardId)) {
                            msg = "自定义密码成功";
                            Toast.makeText(MainApplication.getInstence(), msg, Toast.LENGTH_SHORT).show();
                            passwordMessage pamsg=new passwordMessage();
                            pamsg.setPassWord(pasw);
                          pamsg.setTimeStart(time);
                           pamsg.setTimeEnd(endtime1);
                            pamsg.setType("2");
                            pamsg.setPaswType("限时");
                            Log.d("TAG", pamsg.getPassWord());
                            EventBus.getDefault().post(pamsg);
                        } else {
                            msg = "delete passcode successed by server";
                          /*  keyboardPwds.remove(position);
                            notifyDataSetChanged();*/



                        }

                        hideProgressDialog();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected String doInBackground(Void... params) {
                    String tTlockId = MainApplication.getInstence().getTTlockId();
                    int id = Integer.parseInt(tTlockId);
                    String json = ResponseService.sendCustomerPaswToService(id,pasw,kaishi,jieshu );  //1  表示通过蓝牙  2 网管
                    return json;
                }
            }.execute();
        }else if (error==Error.LOCK_PASSWORD_EXIST){
            Looper.prepare();
            Toast.makeText(MainApplication.getInstence(), "密码已存在，请换一个", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }

    }
}
