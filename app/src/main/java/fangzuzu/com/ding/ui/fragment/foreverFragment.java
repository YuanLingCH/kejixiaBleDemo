package fangzuzu.com.ding.ui.fragment;


import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.event.passwordMessage;
import fangzuzu.com.ding.net.ResponseService;

/**
 * 永久密码
 * Created by yuanling on 2018/5/15.
 */

public class foreverFragment extends BaseFragment {
    Button but_send_pasw;
    TextView tv_pasw;
    TextView tv;
    public static int flag = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.forever_fragment_layout;
    }

    @Override
    protected void initViews() {
        String tTlockId = MainApplication.getInstence().getTTlockId();
        //接收数据
        final String lockName = MainApplication.getInstence().getLockName();
        final String allow = MainApplication.getInstence().getAllow();
        final String lockid = MainApplication.getInstence().getLockid();
        final String uid = SharedUtils.getString("uid");
        final String  adminUserId = SharedUtils.getString("adminUserId");
        Log.d("TAG","接收数据"+lockName);
        Log.d("TAG","接收数据"+allow);
        Log.d("TAG","接收数据tTlockId"+tTlockId );
        tv= (TextView) root.findViewById(R.id.tv);
        // 密码在24小时内至少使用一次,否则失效
        String str="密码在<font color='#FF0000'>24小时内</font>至少使用一次,否则失效";
        tv.setTextSize(14);
        tv.setText(Html.fromHtml(str));

        but_send_pasw= (Button) root.findViewById(R.id.but_send_pasw);
        but_send_pasw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                new AsyncTask<Void, Integer, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String tTlockId = MainApplication.getInstence().getTTlockId();
                        int lockId = Integer.parseInt(tTlockId);
                        Log.d("TAG","接收数据tTlockId"+tTlockId );
                        long   startDate = new Date().getTime();
                        long  endDate = new Date().getTime();
                        String json = ResponseService.getKeyboardPwd(lockId, 4, 2, startDate, endDate);
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
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");// HH:mm:ss
                        //获取当前时间
                        Date date = new Date(System.currentTimeMillis());
                        String time=simpleDateFormat.format(date);
                        passwordMessage pamsg=new passwordMessage();
                        pamsg.setPassWord(pwd);
                        pamsg.getPassWord();
                       pamsg.setTimeStart(time);
                        pamsg.setTimeEnd(time);
                        pamsg.setType("1");
                        Log.d("TAG", pamsg.getPassWord());
                        EventBus.getDefault().post(pamsg);
                    }
                }.execute();





            }

        });


    }

    @Override
    protected void initEvents() {
        tv_pasw= (TextView) root.findViewById(R.id.tv_pasw);


    }

    @Override
    protected void initData() {

    }
}
