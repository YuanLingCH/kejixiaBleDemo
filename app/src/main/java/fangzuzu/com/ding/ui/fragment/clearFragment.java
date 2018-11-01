package fangzuzu.com.ding.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.event.createtimeMessage;
import fangzuzu.com.ding.event.losetimeMessage;
import fangzuzu.com.ding.event.passwordMessage;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.widget.DatePicierFragment;

/**
 * Created by yuanling on 2018/5/15.
 */

public class clearFragment extends BaseFragment{
    LinearLayout  create_time ,lose_time;
    private TextView currentDate, currentTime;
    TextView electfrg_effect_time,electfrg_lose_time;
    Button create_pasrord;
    TextView   tv_pasw,tv;
    @Override
    protected int getLayoutId() {
        return R.layout.clear_fragment_layout;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

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

        Log.d("TAG","event1"+endtime);
    }

    String createtime;
    //生效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBuscreate(createtimeMessage event){
        createtime = event.getTime().toString().trim();
        currentTime.setText( createtime);
        Log.d("TAG","event2"+currentTime);


    }


    @Override
    protected void initViews() {
        tv= (TextView) root.findViewById(R.id.tv);
        // 密码在24小时内至少使用一次,否则失效   密码在24小时内使用后,删除之前所有密码
        String str="密码在<font color='#FF0000'>24小时内</font>使用后,删除之前所有密码";
        tv.setTextSize(14);
        tv.setText(Html.fromHtml(str));
        tv_pasw= (TextView) root.findViewById(R.id.tv_pasw);
        create_pasrord= (Button) root.findViewById(R.id.create_pasrord);
        currentTime= (TextView) root.findViewById(R.id.electfrg_effect_time);
        currentDate= (TextView) root.findViewById(R.id.electfrg_lose_time);
        create_time= (LinearLayout) root.findViewById(R.id.create_time);
        lose_time=(LinearLayout) root.findViewById(R.id.lose_time);
        DatePicierFragment.initDatePicker(currentDate, currentTime, getContext());
        create_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicierFragment.getCustomDatePicker2().show(currentTime.getText().toString());

                Log.d("TAG","时间开始");
            }
        });

        lose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicierFragment.getCustomDatePicker1().show(currentDate.getText().toString());
                Log.d("TAG","时间结束");

            }
        });




    }
    String s2;
    String s4;
    String s1;
    String s3;
    @Override
    protected void initEvents() {

        //接收数据
        final String lockName = MainApplication.getInstence().getLockName();
        final String allow = MainApplication.getInstence().getAllow();
        final String lockid = MainApplication.getInstence().getLockid();
        final String uid= SharedUtils.getString("uid");
        Log.d("TAG","接收数据"+lockName);
        Log.d("TAG","接收数据"+allow);
        Log.d("TAG","接收数据"+lockid );
        //生成密码
        create_pasrord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String time = currentTime.getText().toString().trim();
                String s = unixTime.dateToStampone(time);
                Log.d("TAG","开始时间戳"+s);

                final long kaishiTime = Long.parseLong(s);
                String substring1 = s.substring(0, s.length() - 3);
                final int startTime = Integer.parseInt(substring1);
                Log.d("TAG","开始时间"+ startTime);

//  currentDate


                String endtime = currentDate.getText().toString().trim();
                String send = unixTime.dateToStampone(endtime);
                Log.d("TAG","结束时间戳"+send);
                final long jieshuTime = Long.parseLong(send );
                String substring1end = send.substring(0, send.length() - 3);
                final int endTime = Integer.parseInt(substring1end);
                Log.d("TAG","结束时间"+ endTime);

                if (StringUtils.isEmpty(currentTime+"")){

                    Toast.makeText(getActivity(),"生效时间不能为空",Toast.LENGTH_LONG).show();
                }else if (StringUtils.isEmpty(endtime)){
                    Toast.makeText(getActivity(),"请选择失效时间",Toast.LENGTH_LONG).show();
                }else if (!StringUtils.isEmpty(currentTime+"")&&!StringUtils.isEmpty(endtime)){
                    if (startTime<endTime&&startTime!=endTime){



                        //  s1 = createtime.replaceAll(" ", "-");
                        s1 = currentTime.getText().toString().trim().replaceAll(" ", "-");

                        Log.d("TAG","s1"+ s1);
                        s2 = s1.replaceAll(":", "-");

                        Log.d("TAG","s2"+ s2);
                        String trim = s2.replaceAll("-", "").trim();
                        Log.d("TAG","s2Time"+ trim );

                        s3 =   endtime.replaceAll(" ", "-");
                        s4 = s3.replaceAll(":", "-");
                        Log.d("TAG","s4"+s4);


                        new AsyncTask<Void, Integer, String>() {
                            @Override
                            protected String doInBackground(Void... params) {
                                String tTlockId = MainApplication.getInstence().getTTlockId();
                                int lockId = Integer.parseInt(tTlockId);
                                Log.d("TAG","接收数据tTlockId"+tTlockId );
                                long   startDate = new Date().getTime();
                                long  endDate = new Date().getTime();
                                String json = ResponseService.getKeyboardPwd(lockId, 4, 4, kaishiTime, jieshuTime);
                                Log.d("TAG","接收json"+json );
                                String pwd = "";
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    if (jsonObject.has("keyboardPwd"))
                                        pwd = jsonObject.getString("keyboardPwd");
                                    else pwd = jsonObject.getString("description");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return pwd;
                            }

                            @Override
                            protected void onPostExecute(String pwd) {
                                super.onPostExecute(pwd);
                                tv_pasw.setText(pwd);
                                passwordMessage pamsg=new passwordMessage();
                                pamsg.setPassWord(pwd);
                                pamsg.getPassWord();
                                pamsg.setTimeStart(s2);
                                pamsg.setTimeEnd(s4);
                                pamsg.setType("5");//删除密码
                                Log.d("TAG", pamsg.getPassWord());
                                EventBus.getDefault().post(pamsg);
                            }
                        }.execute();



                    }else {
                        Toast.makeText(getActivity(),"失效时间必须比当前时间和生效时间晚",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    @Override
    protected void initData() {

    }
}
