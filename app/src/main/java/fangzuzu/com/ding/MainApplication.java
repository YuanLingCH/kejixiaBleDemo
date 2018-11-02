package fangzuzu.com.ding;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.tencent.bugly.crashreport.CrashReport;
import com.ttlock.bl.sdk.api.TTLockAPI;
import com.ttlock.bl.sdk.callback.TTLockCallback;
import com.ttlock.bl.sdk.entity.DeviceInfo;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.entity.LockData;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import org.greenrobot.greendao.database.Database;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.constant.BleConstant;
import fangzuzu.com.ding.dao.DbService;
import fangzuzu.com.ding.dao.gen.DaoMaster;
import fangzuzu.com.ding.dao.gen.DaoSession;
import fangzuzu.com.ding.dao.gen.KeyDao;
import fangzuzu.com.ding.model.BleSession;
import fangzuzu.com.ding.model.Key;
import fangzuzu.com.ding.ui.activity.BaseActivity;
import fangzuzu.com.ding.ui.activity.lockListActivity;
import okhttp3.Cookie;
import okhttp3.OkHttpClient;

import static com.tencent.bugly.crashreport.crash.c.l;
import static fangzuzu.com.ding.enumtype.Operation.UNLOCK;

/**
 * Created by Administrator on 2016/12/1.
 */
public class MainApplication extends Application {

    String uid1;
    String partid;
    String allow;
    String lockid;
    String lockName;
    String pasword;
    String elect;
    String mac;
    String appVersion;
    String startTime;
    String endTime;
    String TTlockId;
String userType;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getTTlockId() {
        return TTlockId;
    }

    public void setTTlockId(String TTlockId) {
        this.TTlockId = TTlockId;
    }

    private static DaoSession daoSession;
    /**
     * 蓝牙开锁
     */
        private static BleOperListenner listenner;

    public interface  BleOperListenner{
        void bleOper(Error error);
    }

    public static void SetBleOperListenner(BleOperListenner blelistenner) {
        listenner = blelistenner;
    }

    /**
     * 删除密码
     */

    private static BleOperDelectPaswListenner DelectPaswlistenner;

    public interface  BleOperDelectPaswListenner{
        void bleOperDelectPasw(Error error);
    }

    public static void SetBleOperDelectPaswListennerr(BleOperDelectPaswListenner Delectlistenner) {
        DelectPaswlistenner = Delectlistenner;
    }


    /**
     * 添加IC卡
     */

    private static BleOperaddIcListenner addIclistenner;

    public interface  BleOperaddIcListenner{
        void bleOperaddIc(Error error,long l);
    }

    public static void SetBleOperaddIcListennerr(BleOperaddIcListenner maddIclistenner) {
        addIclistenner = maddIclistenner;
    }


    /**
     * 修改IC卡时间  也是限时  modificationime
     */

    private static BleOperaddmodificationIcTListenner modificationIcTListenner;

    public interface BleOperaddmodificationIcTListenner{
        void bleOperaddmodificationIcTime(Error error,long l);
    }

    public static void SetBleOperBleOperaddmodificationIcTListenner(BleOperaddmodificationIcTListenner maddIclistenner) {
        modificationIcTListenner = maddIclistenner;
    }

    /**
     * 删除IC卡 也是限时  modificationime
     */

    private static BleOperDeleteIcTListenner deleteIcTListenner;

    public interface BleOperDeleteIcTListenner{
        void bleOperDeleteIc(Error error,long l);
    }

    public static void SetBleOperDeleteIcTListenner(BleOperDeleteIcTListenner  maddIclistenner) {
        deleteIcTListenner = maddIclistenner;
    }


    /**
     * 添加指纹
     */

    private static BleOperAddFingerListenner addFingerListenner;

    public interface  BleOperAddFingerListenner{
        void bleOperAddFinger(Error error,long l);
    }

    public static void SetBleOperAddFingerListenner( BleOperAddFingerListenner  maddIclistenner) {
        addFingerListenner = maddIclistenner;
    }

    /**
     * 添加指纹采集回调
     */

    private static BleOperAddFingerCollectionListenner addFingerCollectionListenner;

    public interface  BleOperAddFingerCollectionListenner{
        void bleOperAddFingerCollection(Error error,int l);
    }

    public static void SetBleOperAddFingerCollectionListenner( BleOperAddFingerCollectionListenner  maddIclistenner) {
        addFingerCollectionListenner = maddIclistenner;
    }


    /**
     * 上传开锁记录
     */

    private static BleOperlockRecordListenner lockRecordListenner;

    public interface  BleOperlockRecordListenner{
        void bleOperlockRecord(Error error,String record);
    }

    public static void setBleOperlockRecordListenner( BleOperlockRecordListenner  maddIclistenner) {
        lockRecordListenner = maddIclistenner;
    }

    /**
     * 锁不在身边
     */

    private static BleOperNotAroundListenner notAroundListenner;

    public interface  BleOperNotAroundListenner{
        void bleOpernotAround();
    }

    public static void setBleOperNotAroundListenner( BleOperNotAroundListenner  maddIclistenner) {
        notAroundListenner = maddIclistenner;
    }


    /**
     * 删除锁
     */

    private static BleOperDeleteLockListenner deleteLockListenner;

    public interface  BleOperDeleteLockListenner{
        void bleOperDeleteLock(Error error);
    }

    public static void setBleOperDeleteLockListenner( BleOperDeleteLockListenner  maddIclistenner) {
        deleteLockListenner = maddIclistenner;
    }



    /**
     * 自定义密码
     */

    private static BleOperCustomerPaswkListenner customerPaswListenner;

    public interface  BleOperCustomerPaswkListenner{
        void bleOperCustomerPasw(Error error,String pasw,long data);
    }

    public static void setBleOperCustomerPaswkListenner( BleOperCustomerPaswkListenner  maddIclistenner) {
        customerPaswListenner = maddIclistenner;
    }

    public static KeyDao keyDao;
    private Activity curActivity;
    /**
     * current used key
     */
    private Key curKey;
    /**
     *  bluetooth operation
     */
    public static BleSession bleSession = BleSession.getInstance(UNLOCK, null);
    /**
     * TTLockAPI
     */
    public static TTLockAPI mTTLockAPI;
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPasword() {
        return pasword;
    }

    public void setPasword(String pasword) {
        this.pasword = pasword;
    }

    public String getElect() {
        return elect;
    }

    public void setElect(String elect) {
        this.elect = elect;
    }

    public String getAllow() {
        return allow;
    }

    public void setAllow(String allow) {
        this.allow = allow;
    }

    public String getLockid() {
        return lockid;
    }

    public void setLockid(String lockid) {
        this.lockid = lockid;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getPartid() {
        return partid;
    }

    public void setPartid(String partid) {
        this.partid = partid;
    }

    private List<Activity> oList;//用于存放所有启动的Activity的集合
    public String getUid() {
        return uid1;
    }

    public void setUid(String uid) {
        this.uid1 = uid;
    }

    static MainApplication app;

    OkHttpClient client;

    //请求的Cookies
    List<Cookie> cookiesStore = new ArrayList<>();






    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        oList = new ArrayList<Activity>();

        CrashReport.initCrashReport(MainApplication.getInstence(), "09743bc044", false);

        initTTLock();


    }

    private void initTTLock() {
        mTTLockAPI = new TTLockAPI(MainApplication.getInstence(), mTTLockCallback);
        registerActivityLifecycleCallbacks(callbacks);
        initGreenDao();
    }


    /**
     * database initial
     */
    private void initGreenDao() {
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "ttlock-db-encrypted.db" : "ttlock-db.db");
//        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "ttlock.db", null);
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        keyDao = daoSession.getKeyDao();
    }



    public static DaoSession getDaoSession() {
        return daoSession;
    }


    OkHttpClient okHttpClient;
    ClearableCookieJar cookieJar;
    public OkHttpClient getClient() {
/*        if (client == null) {
            //保存Cookies
            //添加请求头
            client = new OkHttpClient.Builder()
                   .cookieJar(new CookieJar() {
                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            Log.d("ytmfdw", cookies.toArray().toString());
                            //拿到登录成功后的Cookies，
                            if (cookiesStore.size() == 0) {
                                if (cookies != null) {
                                    cookiesStore.addAll(cookies);
                                }
                            }
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            //返回登录成功后的Cookies
                            return cookiesStore;
                        }
                    }).build();
        }
        return client;*/

        if (cookieJar==null){
            cookieJar =
                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(MainApplication.getInstence()));
        }

        if (okHttpClient==null){
            okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .build();
        }

        return okHttpClient;
    }


    public static MainApplication getInstence() {
        return app;
    }


    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
// 判断当前集合中不存在该Activity
        if (!oList.contains(activity)) {
            oList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
//判断当前集合中存在该Activity
        if (oList.contains(activity)) {
            oList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : oList) {
            activity.finish();
        }
    }
    /**
     * TTLock Callback
     */

    Key localKey;
    int uid;
    private TTLockCallback mTTLockCallback=new TTLockCallback() {
        @Override
        public void onFoundDevice(final ExtendedBluetoothDevice extendedBluetoothDevice) {
           broadcastUpdate(BleConstant.ACTION_BLE_DEVICE, BleConstant.DEVICE, extendedBluetoothDevice);
            String address = extendedBluetoothDevice.getAddress();
            String lockmac = bleSession.getLockmac();
            String TTLockmac = MainApplication.getInstence().getMac();
            Log.d("TAG",address+"发现设备lockmac"+lockmac+"mac"+TTLockmac);
            //此处是搜索到锁列表页面 选择要添加并且是进入可添加状态的锁进行连接
            if (extendedBluetoothDevice.getAddress().equals(TTLockmac)){
                mTTLockAPI.stopBTDeviceScan();
       Key   localKey = DbService.getKeyByLockmac(extendedBluetoothDevice.getAddress());
            if(localKey != null) {
//                operateSuccess = false;
                switch (bleSession.getOperation()) {
//                    case UNLOCK:
//                        if(extendedBluetoothDevice.isTouch())
//                             mTTLockAPI.connect(extendedBluetoothDevice);
//                        break;
                    case SET_ADMIN_KEYBOARD_PASSWORD:
                    case SET_DELETE_PASSWORD:
                    case SET_LOCK_TIME:
                    case RESET_KEYBOARD_PASSWORD:
                    case RESET_EKEY:
                    case RESET_LOCK:
                    case GET_LOCK_TIME:
                    case GET_OPERATE_LOG:
                    case ADD_PASSCODE:
                    case CLICK_UNLOCK:
                    case ADD_ICCARD:
                        if(extendedBluetoothDevice.getAddress().equals(bleSession.getLockmac())){
                            Timer timer=new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    mTTLockAPI.connect(extendedBluetoothDevice);
                                }
                            },100);

                        }

                        break;
                }
           }
            }else {


             if (!mTTLockAPI.isConnected(extendedBluetoothDevice)){


                Timer timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mTTLockAPI.stopBTDeviceScan();
                        if (notAroundListenner!=null){
                            notAroundListenner.bleOpernotAround();
                        }
                        Log.d("TAG","锁不在身边");
                    }
                },10000);

                }
            //    Log.d("TAG","没有扫到设备");
            }


        }

        @Override
        public void onDeviceConnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
            mTTLockAPI.stopBTDeviceScan();
            Log.d("TAG","连接设备");
         //   mTTLockAPI.lockInitialize(extendedBluetoothDevice);
            Log.d("TAG","localKey"+localKey);
            String openid = SharedUtils.getString("openid");
            localKey = DbService.getKeyByLockmac(extendedBluetoothDevice.getAddress());
             uid = Integer.parseInt(openid);
            Log.d("TAG","uid"+uid);
            switch (bleSession.getOperation()) {
                case ADD_ADMIN:

                       mTTLockAPI.lockInitialize(extendedBluetoothDevice);

                       // toast(getString(R.string.words_has_exist_lock));


                    break;
                case UNLOCK:
                case CLICK_UNLOCK:
                    if(localKey != null) {
                        if(localKey.isAdmin()){
                            mTTLockAPI.unlockByAdministrator(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(), System.currentTimeMillis(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
                        }
                        else
                            mTTLockAPI.unlockByUser(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getLockKey(), localKey.getLockFlagPos(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
                    }
//                    mTTLockAPI.unlockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getUnlockKey(), 0, localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    break;
                case SET_ADMIN_KEYBOARD_PASSWORD:
                    mTTLockAPI.setAdminKeyboardPassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPwd(), curKey.getLockKey(), curKey.getLockFlagPos(), curKey.getAesKeyStr(), bleSession.getPassword());
                    break;
                case SET_DELETE_PASSWORD:
                    mTTLockAPI.setDeletePassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPwd(), curKey.getLockKey(), curKey.getLockFlagPos(), curKey.getAesKeyStr(), bleSession.getPassword());
                    break;
                case SET_LOCK_TIME:
                    mTTLockAPI.setLockTime(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), System.currentTimeMillis(), localKey.getLockFlagPos(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
                    break;
                case RESET_KEYBOARD_PASSWORD:
                    mTTLockAPI.resetKeyboardPassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPwd(), curKey.getLockKey(), curKey.getLockFlagPos(), curKey.getAesKeyStr());
                    break;
                case RESET_EKEY://reset ekey, lockFlagPos +1
                    mTTLockAPI.resetEKey(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPwd(), curKey.getLockFlagPos() + 1, curKey.getAesKeyStr());
                    break;
                case RESET_LOCK:
                    mTTLockAPI.resetLock(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(), localKey.getAesKeyStr());
                    break;
                case GET_OPERATE_LOG:  //操作记录
                    mTTLockAPI.getOperateLog(extendedBluetoothDevice, localKey.getLockVersion(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
                    break;
                case GET_LOCK_TIME:
                    mTTLockAPI.getLockTime(extendedBluetoothDevice, curKey.getLockVersion(), curKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
                    break;
                case LOCKCAR_UP:
                    if(localKey.isAdmin())
                        mTTLockAPI.lockByAdministrator(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(), localKey.getAesKeyStr());
                    else
                        mTTLockAPI.lockByUser(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getLockKey(), localKey.getLockFlagPos(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
//                    mTTLockAPI.lockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), 1489990922165l, 1490077322165l, localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    break;
                case LOCKCAR_DOWN:
                    if(localKey.isAdmin())
                        mTTLockAPI.unlockByAdministrator(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(), System.currentTimeMillis(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
                    else
                        mTTLockAPI.unlockByUser(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getLockKey(), localKey.getLockFlagPos(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
//                    mTTLockAPI.unlockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), 1489990922165l, 1490077322165l, localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                    break;
                case DELETE_ONE_KEYBOARDPASSWORD://set the keyboard password type to 0
                    Log.d("TAG","删除密码蓝牙");
                    mTTLockAPI.deleteOneKeyboardPassword(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(), 0, bleSession.getPassword(), localKey.getAesKeyStr());
                    break;
                case GET_LOCK_VERSION_INFO:
                    mTTLockAPI.readDeviceInfo(extendedBluetoothDevice, localKey.getLockVersion(), localKey.getAesKeyStr());
                    break;
                case ADD_PASSCODE:
                    mTTLockAPI.addPeriodKeyboardPassword(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(), bleSession.getPassword(), bleSession.getStartDate(), bleSession.getEndDate(), localKey.getAesKeyStr(), localKey.getTimezoneRawOffset());
                    break;
                case ADD_ICCARD:
                    mTTLockAPI.addICCard(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(),localKey.getAesKeyStr());
                    break;
                case MODIFY_ICPEROID:  //修改IC 卡时间   3301608137

                    mTTLockAPI.modifyICPeriod(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(),0,bleSession.getStartDate(), bleSession.getEndDate(),localKey.getAesKeyStr(),localKey.getTimezoneRawOffset());
                    break;
                case DELETE_ICCARD:  //修改IC 卡时间   3301608137

                    mTTLockAPI.deleteICCard(extendedBluetoothDevice,uid,localKey.getLockVersion(),localKey.getAdminPwd(),localKey.getLockKey(), localKey.getLockFlagPos(),bleSession.getCardNo(),localKey.getAesKeyStr());
                    break;
                case ADD_FINGER:  //添加指纹

                    mTTLockAPI.addFingerPrint(extendedBluetoothDevice,uid,localKey.getLockVersion(),localKey.getAdminPwd(),localKey.getLockKey(), localKey.getLockFlagPos(),localKey.getAesKeyStr());
                    break;
                case DELETE_FINGER:  //添加指纹

                    mTTLockAPI.deleteFingerPrint(extendedBluetoothDevice,uid,localKey.getLockVersion(),localKey.getAdminPwd(),localKey.getLockKey(), localKey.getLockFlagPos(),bleSession.getCardNo(),localKey.getAesKeyStr());
                    break;

            }
        }

        @Override
        public void onDeviceDisconnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
            Log.d("TAG","蓝牙断开连接");

        }

        @Override
        public void onGetLockVersion(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, int i3, int i4, Error error) {

        }

        @Override
        public void onLockInitialize(ExtendedBluetoothDevice extendedBluetoothDevice, final LockData lockData, Error error) {
            Log.d("TAG","锁初始化");
            if(error == Error.SUCCESS) {
                final String lockDataJson = lockData.toJson();
                Log.d("TAG","lockDataJson"+lockDataJson);
             //   toast(getString(R.string.words_lock_add_successed_and_init));

                new AsyncTask<Void, String, Boolean>() {

                    @Override
                    protected Boolean  doInBackground(Void... params) {
                        Boolean flag = false;
                       // String json = ResponseService.lockInit(lockDataJson, lockData.getLockName());
                        String json = fangzuzu.com.ding.net.ResponseService.lockInit(lockDataJson, bleSession.getBleManager());
                        Log.d("TAG","返回json"+json);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            if(jsonObject.has("errcode")) {
                                String errmsg = jsonObject.getString("description");
                              //  toast(errmsg);
                            } else {
                                Integer lockId = (Integer) jsonObject.get("lockId");
                                Integer keyId = (Integer) jsonObject.get("keyId");
                                    SharedUtils.putString("TTlockId",lockId+"");
                                SharedUtils.putString("TTkeyId",keyId+"");
                                Intent intent = new Intent(MainApplication.getInstence(),lockListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                ((BaseActivity)curActivity).cancelProgressDialog();

                                Log.d("TAG","蓝牙管理员成功");

                                flag = true;
                              //  toast(getString(R.string.words_lock_init_successed));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                          //  toast(getString(R.string.words_lock_init_failed) + e.getMessage());
                        }
                        return flag;
                    }

                    @Override
                    protected void onPostExecute(Boolean flag) {

                    }
                }.execute();
            } else {
                //失败
                //toast(error.getErrorMsg());
            }
        }

        @Override
        public void onResetEKey(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onSetLockName(ExtendedBluetoothDevice extendedBluetoothDevice, String s, Error error) {

        }

        @Override
        public void onSetAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String s, Error error) {

        }

        @Override
        public void onSetDeletePassword(ExtendedBluetoothDevice extendedBluetoothDevice, String s, Error error) {

        }

        @Override
        public void onUnlock(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, long l, Error error) {
            Log.d("TAG","开锁成功回调");
            if (listenner!=null){
                listenner.bleOper(error);
                Log.d("TAG","开锁成功回调listenner");
            }




        }

        @Override
        public void onSetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {
            Log.d("TAG","同步时间成功");
        }

        @Override
        public void onGetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, long l, Error error) {

        }

        @Override
        public void onResetKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String s, long l, Error error) {

        }

        @Override
        public void onSetMaxNumberOfKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onResetKeyboardPasswordProgress(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onResetLock(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {
            Log.d("TAG","删除锁回调");
            if (deleteLockListenner!=null){
                deleteLockListenner.bleOperDeleteLock(error);
            }
        }

        @Override
        public void onAddKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, long l, long l1, Error error) {
            Log.d("TAG","添加键盘密码"+"error:"+error+"s:"+s+"l:"+l+"l1:"+l1);
            if (customerPaswListenner!=null){
                customerPaswListenner.bleOperCustomerPasw(error,s,l);
            }
        }

        @Override
        public void onModifyKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, String s1, Error error) {

        }

        @Override
        public void onDeleteOneKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, Error error) {
            Log.d("TAG","散出按键密码"+error);
            if (DelectPaswlistenner!=null){
                DelectPaswlistenner.bleOperDelectPasw(error);
                Log.d("TAG","散出按键密码"+error);
            }
        }

        @Override
        public void onDeleteAllKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {

        }

        @Override
        public void onGetOperateLog(ExtendedBluetoothDevice extendedBluetoothDevice, String s, Error error) {
            Log.d("TAG","操作记录回调"+error+":"+s);
         if (error==Error.SUCCESS){
             Log.d("TAG","操作记录回调有数据"+error+":"+s);
             if (lockRecordListenner!=null){
                 lockRecordListenner.bleOperlockRecord(error,s);
             }

         }

        }

        @Override
        public void onSearchDeviceFeature(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

        }

        @Override
        public void onAddICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1,long IcNumbler, Error error) {
            Log.d("TAG","添加ic卡"+"error"+error+"i:"+i+"i1:"+i1+"l:"+l);


            if (IcNumbler!=0){
                if (bleSession.isFlag()){
                    if (addIclistenner!=null){
                        addIclistenner.bleOperaddIc(error, IcNumbler);
                    }
                }else {
                    if (mTTLockAPI.isConnected(extendedBluetoothDevice)){
                        Log.d("TAG","蓝牙已经连接了 ");

                        Log.d("TAG","修改ic卡"+"uid:"+uid+"LockVersion:"+localKey.getLockVersion()+"AdminPwd:"+localKey.getAdminPwd()+"LockKey:"+localKey.getLockKey()+"LockFlagPos:"+localKey.getLockFlagPos()+"IcNumbler:"+IcNumbler+"StartDate:"+bleSession.getStartDate()+"EndDate:"+bleSession.getEndDate()+"KeyStr:"+localKey.getAesKeyStr()+"TimezoneRawOffset:"+localKey.getTimezoneRawOffset());
                        mTTLockAPI.modifyICPeriod(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(),IcNumbler,bleSession.getStartDate(), bleSession.getEndDate(),localKey.getAesKeyStr(),localKey.getTimezoneRawOffset());

                        // mTTLockAPI.modifyICPeriod(extendedBluetoothDevice,uid,localKey.getLockVersion(),localKey.getAdminPwd(),localKey.getLockKey(),);
                    }
                }

        }

        }

        @Override
        public void onModifyICCardPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int i, long l, long l1, long l2,Error error) {
            Log.d("TAG","修改ic回调成功。。。 "+error);
            if (modificationIcTListenner!=null){
                modificationIcTListenner.bleOperaddmodificationIcTime(error,l);
            }
        }

        @Override
        public void onDeleteICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int i, long l, Error error) {
            Log.d("TAG","删除IC卡成功回调"+l);
            if (deleteIcTListenner!=null){
                deleteIcTListenner.bleOperDeleteIc(error,l);
            }
        }

        @Override
        public void onClearICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onSetWristbandKeyToLock(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onSetWristbandKeyToDev(Error error) {

        }

        @Override
        public void onSetWristbandKeyRssi(Error error) {

        }

        @Override
        public void onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, long fingerNumbler, Error error) {
            Log.d("TAG","添加指纹回调"+error+":"+fingerNumbler);
            if (addFingerListenner!=null){
                addFingerListenner.bleOperAddFinger(error,fingerNumbler);
            }
            if (error==Error.SUCCESS){
                if (fingerNumbler!=0){
                    if (!bleSession.isFlag()){   //限时 flase
                        Log.d("TAG","添加指纹回调限时"+"StartDate"+bleSession.getStartDate()+"endData"+bleSession.getEndDate()+"fingerNumbler"+fingerNumbler);   //修改时

                        if (mTTLockAPI.isConnected(extendedBluetoothDevice)){
                            //蓝牙连接
                            mTTLockAPI.modifyFingerPrintPeriod(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(),fingerNumbler,bleSession.getStartDate(), bleSession.getEndDate(),localKey.getAesKeyStr(),localKey.getTimezoneRawOffset());
                        }

                    }
                }
            }

        }

        @Override
        public void onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, long l, int i2, Error error) {

        }

        @Override
        public void onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, Error error) {
            Log.d("TAG","添加指纹回调采集2"+error+"i2:"+i2+"i:"+i+"i1:"+i1);

                if (addFingerCollectionListenner!=null){
                    addFingerCollectionListenner.bleOperAddFingerCollection(error,i1);
                }


        }

        @Override
        public void onModifyFingerPrintPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int i, long l, long l1, long l2, Error error) {
            Log.d("TAG","修改指纹时间回调"+error);
        }

        @Override
        public void onDeleteFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int i, long l, Error error) {
            Log.d("TAG","删除指纹回调"+error);
            if (deleteIcTListenner!=null){
                deleteIcTListenner.bleOperDeleteIc(error,l);
            }
        }

        @Override
        public void onClearFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onSearchAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, int i3, Error error) {

        }

        @Override
        public void onModifyAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

        }

        @Override
        public void onReadDeviceInfo(ExtendedBluetoothDevice extendedBluetoothDevice, DeviceInfo deviceInfo, Error error) {

        }

        @Override
        public void onEnterDFUMode(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {

        }

        @Override
        public void onGetLockSwitchState(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

        }

        @Override
        public void onLock(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, long l, Error error) {

        }

        @Override
        public void onScreenPasscodeOperate(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

        }

        @Override
        public void onRecoveryData(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onSearchICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, Error error) {

        }

        @Override
        public void onSearchFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, Error error) {

        }

        @Override
        public void onSearchPasscode(ExtendedBluetoothDevice extendedBluetoothDevice, String s, Error error) {

        }

        @Override
        public void onSearchPasscodeParam(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, long l, Error error) {

        }

        @Override
        public void onOperateRemoteUnlockSwitch(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, int i3, Error error) {

        }

        @Override
        public void onGetElectricQuantity(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }

        @Override
        public void onOperateAudioSwitch(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, Error error) {

        }

        @Override
        public void onOperateRemoteControl(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, Error error) {

        }

        @Override
        public void onOperateDoorSensorLocking(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, Error error) {

        }

        @Override
        public void onGetDoorSensorState(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

        }

        @Override
        public void onSetNBServer(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

        }
    };
    //TODO:
    private <K,V extends Parcelable> void broadcastUpdate(String action, K key, V value) {
        final Intent intent = new Intent(action);
        if(key != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable((String) key, value);
            intent.putExtras(bundle);
        }
        sendBroadcast(intent);
    }

    ActivityLifecycleCallbacks callbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            curActivity = activity;
          //  LogUtil.d("activity:" + activity.getClass(), DBG);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            //TODO:
          //  LogUtil.d("activity:" + activity.getClass(), DBG);
//            curActivity = null;
        }
    };
}
