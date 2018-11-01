package fangzuzu.com.ding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.util.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.bean.TTlockListBean;
import fangzuzu.com.ding.bean.UserBean;
import fangzuzu.com.ding.dao.DbService;
import fangzuzu.com.ding.model.Key;
import fangzuzu.com.ding.model.KeyObj;
import fangzuzu.com.ding.net.ResponseService;
import fangzuzu.com.ding.utils.NetWorkTesting;
import fangzuzu.com.ding.utils.StringUtils;


/**
 * Created by lingyuan on 2018/7/12.
 */

public class SplashActivity extends BaseActivity {

    List data3;
    String uid;
    List dataPart=new ArrayList();
    private List<Key> keys;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,

                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.splash_activity_layout);
        keys = new ArrayList<>();
initlize();

    }

    private void initlize() {
        UserBean user = SharedUtils.getUser();
        Handler handler = new Handler();

        if (user!=null){
        getUserLockList();
            String name = user.name;
            if (!StringUtils.isEmpty(name)){
         /*       Log.d("TAG","直接走了啊");
                Intent intent=new Intent(SplashActivity.this, mqttService.class);
                startService(intent);*/


   /*             handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        *//**
                         *要执行的操作
                         *//*
                        Intent intent=new Intent(MainApplication.getInstence(),lockListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1000);//3秒后执行Runnable中的run方法*/
            }
        }else {
       /*     Timer timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent=new Intent(MainApplication.getInstence(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            },1000);*/


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    /**
                     *要执行的操作
                     */
                    Intent intent=new Intent(MainApplication.getInstence(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);//3秒后执行Runnable中的run方法




        }
    }

    String lockMac;
    String lockAlias;
    int electricQuantity;
    String userType;
    int lockId;
    String noKeyPwd;
    int keyId;
    int lockFlagPos;
    int timezoneRawOffset;
    String aesKeyStr;
    String lockKey;
    String adminPwd;
    TTlockListBean.KeyListBean.LockVersionBean lockVersion;

    public void getUserLockList() {

        final NetWorkTesting net=new NetWorkTesting(this);
        if (net.isNetWorkAvailable()){

/*            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String body = response.body();
                    if (StringUtils.isEmpty(body)){
                        Log.d("TAG","网络错误11"+body);
                      finish();

                    }else {
                        Log.d("TAG","测试一test把手锁"+body );
                        Gson gson=new Gson();
                        userLockBean bean = gson.fromJson(body, new TypeToken<userLockBean>() {}.getType());
                        int code = bean.getCode();
                        data3.clear();// 防止加载重复数据
                        if (code==1001){
                            userLockBean.DataBean data = bean.getData();
                            List<?> parentLock = data.getParentLock();
                            Iterator<?> iterator = parentLock.iterator();
                            while (iterator.hasNext()){
                                Object next = iterator.next();
                                dataPart.add(next);
                            }
                            List<userLockBean.DataBean.UserLockBean> userLock = data.getUserLock();
                            Iterator<userLockBean.DataBean.UserLockBean> iterator1 = userLock.iterator();
                            while (iterator1.hasNext()){
                                userLockBean.DataBean.UserLockBean next = iterator1.next();
                               lockName = next.getLockName();
                              secretKey = next.getSecretKey();
                                adminPsw = next.getAdminPsw();
                                 adminUserId = next.getAdminUserId();
                                 electricity = next.getElectricity();
                                allow = next.getAllow();
                                String keyId = next.getKeyId();
                                SharedUtils.putString("keyId",keyId);
                                id1 = next.getId();
                             updataFlag = next.getUpdataFlag()+"";
                                endTime = next.getEndTime();
                           startTime = next.getStartTime();
                                lockNumber = next.getLockNumber();
                                Log.d("TAG","锁命"+lockName);
                                data3.add(next);
                            }
                            data3.addAll(dataPart);
                            Log.d("TAG","集合大小"+data3.size());
                            Log.d("TAG",body);

                            if (data3.size()==1){
                                Timer timer=new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(MainApplication.getInstence(),MainActviity.class);
                                        intent.putExtra("id",id1);
                                        intent.putExtra("secretKey",secretKey);
                                        intent.putExtra("allow",allow);
                                        intent.putExtra("electricity",electricity);
                                        intent.putExtra("lockNumber",lockNumber);
                                        intent.putExtra("adminPsw",adminPsw);
                                        intent.putExtra("lockName",lockName);
                                        intent.putExtra("jihe","1");
                                        intent.putExtra("adminUserId",adminUserId);
                                        intent.putExtra("startTime",startTime);
                                        intent.putExtra("endTime",endTime); //updataFlag
                                        intent.putExtra("updataFlag",updataFlag);
                                        startActivity(intent);
                                        finish();
                                    }
                                },1000);


                            }else {
                                Timer timer=new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(MainApplication.getInstence(),lockListActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                },1000);
                            }


                        }else if(code==1002){
                            Log.d("TAG","网络错误1002");

                        }

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });*/

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    String lockList = ResponseService. getLockList();
                    if (!StringUtils.isEmpty(lockList)){


                        Log.d("TAG","锁列表"+lockList);
                        Gson gson=new Gson();
                        TTlockListBean m = gson.fromJson(lockList, new TypeToken<TTlockListBean>() {}.getType());
                        List<TTlockListBean.KeyListBean> keyList = m.getKeyList();
                        Message message=new Message();
                        Iterator<TTlockListBean.KeyListBean> iterator = keyList.iterator();
                        if (keyList.size()>1 ||keyList.size()==0){
                            Timer timer=new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Intent intent=new Intent(MainApplication.getInstence(),lockListActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            },1000);

                        } else if (keyList.size()==1){
                            while (iterator.hasNext()){
                                TTlockListBean.KeyListBean userLockBean = iterator.next();
                                lockMac = userLockBean.getLockMac();
                                lockAlias = userLockBean.getLockAlias();
                                electricQuantity = userLockBean.getElectricQuantity();
                                userType = userLockBean.getUserType();
                                lockId = userLockBean.getLockId();
                                noKeyPwd = userLockBean.getNoKeyPwd();
                                keyId = userLockBean.getKeyId();
                                lockFlagPos = userLockBean.getLockFlagPos();
                                timezoneRawOffset = userLockBean.getTimezoneRawOffset();
                                aesKeyStr = userLockBean.getAesKeyStr();
                                lockVersion = userLockBean.getLockVersion();
                                lockKey = userLockBean.getLockKey();
                                adminPwd = userLockBean.getAdminPwd();

                            }
                            Intent intent =new Intent(SplashActivity.this,MainActviity.class);
                            intent.putExtra("lockMac",lockMac);
                            intent.putExtra("lockAlias",lockAlias);
                            intent.putExtra("electricQuantity", electricQuantity+"");
                            intent.putExtra("userType", userType);
                            intent.putExtra("lockId", lockId+"");
                            intent.putExtra("keyId", keyId+"");
                            intent.putExtra("noKeyPwd", noKeyPwd);
                            intent.putExtra("lockVersion", lockVersion);
                            intent.putExtra("lockFlagPos", lockFlagPos+"");
                            intent.putExtra("timezoneRawOffset", timezoneRawOffset+"");
                            intent.putExtra("aesKeyStr", aesKeyStr);
                            intent.putExtra("lockKey", lockKey);
                            intent.putExtra("many", "1");  // 1 表示1条数据
                            intent.putExtra("adminPwd", adminPwd );
                            startActivity(intent);

                        }

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(lockList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //use lastUpdateDate you can get the newly added key and data after the time
                        String keyList1 = null;
                        try {
                            keyList1 = jsonObject.getString("keyList");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        keys.clear();
                        ArrayList<KeyObj> list = GsonUtil.toObject(keyList1, new TypeToken<ArrayList<KeyObj>>(){});
                        keys.addAll(convert2DbModel(list));
                        //
                        DbService.deleteAllKey();
                        DbService.saveKeyList(keys);
                    }
                }
            }.start();




        }else {

            Toast.makeText(SplashActivity.this,"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();



        }
    }


    private static ArrayList<Key> convert2DbModel(ArrayList<KeyObj> list){
        ArrayList<Key> keyList = new ArrayList<>();
        if(list != null && list.size() > 0){
            for(KeyObj key : list){
                Key DbKey = new Key();
                DbKey.setUserType(key.userType);
                DbKey.setKeyStatus(key.keyStatus);
                DbKey.setLockId(key.lockId);
                DbKey.setKeyId(key.keyId);
                DbKey.setLockVersion(GsonUtil.toJson(key.lockVersion));
                DbKey.setLockName(key.lockName);
                DbKey.setLockAlias(key.lockAlias);
                DbKey.setLockMac(key.lockMac);
                DbKey.setElectricQuantity(key.electricQuantity);
                DbKey.setLockFlagPos(key.lockFlagPos);
                DbKey.setAdminPwd(key.adminPwd);
                DbKey.setLockKey(key.lockKey);
                DbKey.setNoKeyPwd(key.noKeyPwd);
                DbKey.setDeletePwd(key.deletePwd);
                DbKey.setPwdInfo(key.pwdInfo);
                DbKey.setTimestamp(key.timestamp);
                DbKey.setAesKeyStr(key.aesKeyStr);
                DbKey.setStartDate(key.startDate);
                DbKey.setEndDate(key.endDate);
                DbKey.setSpecialValue(key.specialValue);
                DbKey.setTimezoneRawOffset(key.timezoneRawOffset);
                DbKey.setKeyRight(key.keyRight);
                DbKey.setKeyboardPwdVersion(key.keyboardPwdVersion);
                DbKey.setRemoteEnable(key.remoteEnable);
                DbKey.setRemarks(key.remarks);

                keyList.add(DbKey);
            }
        }
        return keyList;
    }

}
