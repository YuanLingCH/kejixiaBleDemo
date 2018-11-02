package fangzuzu.com.ding.net;


import android.util.Log;

import com.ttlock.bl.sdk.util.DigitUtil;

import java.util.HashMap;

import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.constant.Config;
import fangzuzu.com.ding.model.FirmwareInfo;

/**
 * Created by TTLock on 2016/9/8 0008.
 */
public class ResponseService {
    private static final String TAG = "ResponseService";
    private static String actionUrl = "https://api.ttlock.com.cn";
//    private static String actionUrl = "http://120.26.119.23:8085";
    private static String actionUrlV2 = actionUrl + "/v2";
    private static String actionUrlV3 = actionUrl + "/v3";

    /**
     * authorize
     * @param username  user name
     * @param password  password
     * @return
     */
    public static String auth(String username, String password) {
        String url = actionUrl + "/oauth2/token";
        HashMap params = new HashMap();
        params.put("client_id", Config.CLIENT_ID);
        params.put("client_secret", Config.CLIENT_SECRET);
        params.put("grant_type", "password");
        params.put("username", username);
        params.put("password", DigitUtil.getMD5(password));
        params.put("redirect_uri", Config.REDIRECT_URI);
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Call this api after calling SDK method to add a lock. This api will create an admin ekey for current user.
     * After initializing a lock, the admin can send ekey or create passcode for others.
     * @return
     */
    public static String lockInit(String lockData, String lockAlias) {
        String url = "https://api.sciener.cn/v3/lock/ initialize";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockAlias", lockAlias);
        params.put("lockData", lockData);
        params.put("date", String.valueOf(System.currentTimeMillis()));
        Log.d("TAG","上传服务器");
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Call this api to delete common ekeys and admin ekey
     * All ekeys and passcodes will be deleted from server when you delete the admin ekey
     * @param keyId key id
     * @return
     */
    public static String deleteKey(int keyId) {
        String url ="https://api.sciener.cn/v3/key/delete";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("keyId", String.valueOf(keyId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);

    }
    public static String getLockList() {
        String url ="https://api.sciener.cn/v3/key/syncData";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lastUpdateDate", "");

        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }
    /**
     * The admin of the locks can't send ekeys to himself
     * For one lock, any user can only have one ekey.If you send an ekey to someone who already have one, the previous one will be deleted.
     * Note:It will be a permenant ekey when you set the startDate and endDate to 0
     *
     * @return
     * 发送钥匙
     */
    public static String sendEKey(String TTLockId ,String receiverUsername,String startDate,String endDate) {
        String url = "https://api.sciener.cn/v3/key/send";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", TTLockId);
        params.put("receiverUsername", receiverUsername);

        //It will be a permenant ekey when you set the startDate and endDate to 0
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("remoteEnable", "2");  //是否支持远程开锁：1-是、2-否
        params.put("remarks", "");
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * For locks of non-v4 passcode, if you get the prompt of "passcode is used out" or "no passcode data", please reset the passcode.
     * The valid time of the passcode should be defined in HOUR,set the minute and second to 0. If the valid period is longer than one year, the end time should be XX months later than the start time, without any difference in DAY,HOUR.
     * Earlier passcode version，reference：https://open.sciener.cn/doc/api/keyboardPwdType
     * @param lockId
     * @param keyboardPwdVersion
     * @param keyboardPwdType
     * @param startDate
     * @param endDate
     * @return
     * 获取键盘密码
     */
    public static String getKeyboardPwd(int lockId, int keyboardPwdVersion, int keyboardPwdType, long startDate, long endDate) {
        String url = "https://api.sciener.cn/v3/keyboardPwd/get";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("keyboardPwdVersion", String.valueOf(keyboardPwdVersion));
        params.put("keyboardPwdType", String.valueOf(keyboardPwdType));
        params.put("startDate", String.valueOf(startDate));
        params.put("endDate", String.valueOf(endDate));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 获取钥匙列表
     * @param lockId
     * @return
     */
    public static String getKeyListDataFromeService(int lockId) {
        String url = "https://api.sciener.cn/v3/lock/listKey";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("pageNo", "1");
        params.put("pageSize", "100");
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }
    /**
     * 修改钥匙有效期
     * @param keyId
     * @return
     */
    public static String modificationKeyeffectivityTimeData(int keyId, long startDate, long endDate) {
        String url = "https://api.sciener.cn/v3/key/changePeriod";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("keyId", String.valueOf(keyId));
        params.put("startDate", String.valueOf(startDate));
        params.put("endDate", String.valueOf(endDate));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }


    /**
     * Get all created passcodes of a lock
     * @param lockId        Lock ID
     * @param
     * @param
     * @return
     */
    public static String keyboardPwdList(int lockId) {
        String url = "https://api.sciener.cn/v3/lock/listKeyboardPwd";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("pageNo", "1");
        params.put("pageSize", "100");
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Only the locks with V4 passcode can delete a passcode via bluetooth or gateway.
     * @param lockId            lock id
     * @param keyboardPwdId     Passcode ID
     * @param deleteType        Delete type:1-delete with App via Bluetooth;2-delete via WiFi gateway. The default value is 1. If it is 1, you should delete via bluetooth first. If it is 2, you can call this api to delete it directly.
     * @return
     * 删除键盘密码
     */
    public static String deleteKeyboardPwd(int lockId, int keyboardPwdId, int deleteType) {
        String url ="https://api.sciener.cn/v3/keyboardPwd/delete";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("keyboardPwdId", String.valueOf(keyboardPwdId));
        params.put("deleteType", String.valueOf(deleteType));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }
    public static String AddIcCard(int lockId,String cardName ,String cardNumber ,long startDate, long endDate) {
        String url ="https://api.sciener.cn/v3/identityCard/add ";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("cardNumber", String.valueOf(cardNumber));
        params.put("cardName", String.valueOf(cardName));
        params.put("startDate", String.valueOf(startDate));
        params.put("endDate", String.valueOf(endDate));
        params.put("addType", "1");  //蓝牙
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }


    /**
     * 获取IC卡列表
     * @param lockId
     * @return
     */
    public static String getIcCardList(String lockId) {
        String url ="https://api.sciener.cn/v3/identityCard/list";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", lockId);
        params.put("pageNo", "1");
        params.put("pageSize", "100");
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }



    /**
     * 删除IC
     * @param lockId
     * @return
     */
    public static String deleteIcCard(String lockId,String cardId) {
        String url ="https://api.sciener.cn/v3/identityCard/delete";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", lockId);
        params.put("cardId", cardId);
        params.put("deleteType", "1");  //1 蓝牙
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 添加指纹
     * @param lockId
     * @param fingerName
     * @param fingerNumber
     * @param startDate
     * @param endDate
     * @return
     */
    public static String AddFinger(int lockId,String fingerName ,String fingerNumber ,long startDate, long endDate) {
        String url ="https://api.sciener.cn/v3/fingerprint/add";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("fingerprintName", String.valueOf(fingerName));
        params.put("fingerprintNumber", String.valueOf(fingerNumber));
        params.put("startDate", String.valueOf(startDate));
        params.put("endDate", String.valueOf(endDate));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 获取指纹列表
     * @param lockId

     * @return
     */
    public static String getFingerPrintListFromeServices(int lockId) {
        String url ="https://api.sciener.cn/v3/fingerprint/list";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("pageNo", "1");
        params.put("pageSize", "100");
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 删除IC
     * @param lockId
     * @returninger
     */
    public static String deleteFinger(String lockId,String fingerprintId) {
        String url ="https://api.sciener.cn/v3/fingerprint/delete";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", lockId);
        params.put("fingerprintId", fingerprintId);
        params.put("deleteType", "1");  //1 蓝牙
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }


    /**
     * upload the operation log from lock
     * @param lockId
     * @param records
     * @return
     */
    public static String uploadOperateLog(int lockId, String records) {
        String url ="https://api.sciener.cn/v3/lockRecord/upload";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("records", records);
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }
    /**
     * 获取开锁记录从服务器
     * @param lockId
     * @return
     */
    public static String getOperateLogFromeServiec(int lockId) {
        String url ="https://api.sciener.cn/v3/lockRecord/list";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("pageNo", "1");
        params.put("pageSize", "100");
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 修改锁的名称
     */
    public static String modificationLockName(int lockId,String lockName) {
        String url ="https://api.sciener.cn/v3/lock/rename";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("lockAlias", lockName);
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 上传自定义密码
     */
    public static String sendCustomerPaswToService(int lockId,String keyboardPwd,long startDate, long endDate) {
        String url ="https://api.sciener.cn/v3/keyboardPwd/add";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("keyboardPwd", keyboardPwd);
        params.put("keyboardPwdName","自定义");
        params.put("startDate", String.valueOf(startDate));
        params.put("endDate", String.valueOf(endDate));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }
    /**
     * Check to see whether there is any upgrade for a lock, depending on modelNum、hardwareRevision、firmwareRevision、specialValue.
     * If there is no version info on the server, returns 'unknown'.In this case, you need to call SDK method to get aforementioned 4 parameters first, and then call [Upgrade recheck] to check for upgrading.
     * @param lockId    lock id
     * @return
     */
    public static String isNeedUpdate(int lockId) {
        String url = actionUrlV3 + "/lock/upgradeCheck";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * Recheck to see whether there is any upgrade for a lock
     * Warning: The aforementioned 4 parameters will be set into database on server. Please make sure that they are from the SDK method.
     * @param lockId    lock id
     * @return
     */
    public static String isNeedUpdateAgain(int lockId, FirmwareInfo deviceInfo) {
        String url = actionUrlV3 + "/lock/upgradeRecheck";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("specialValue", String.valueOf(deviceInfo.specialValue));

        params.put("modelNum", deviceInfo.modelNum);
        params.put("hardwareRevision", deviceInfo.hardwareRevision);
        params.put("firmwareRevision", deviceInfo.firmwareRevision);
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }
    /**
     * 冻结钥匙 管理员冻结发给普通用户的钥匙。 https://api.sciener.cn/v3/key/freeze
     */

    public static String freezeKey (int keyId) {
        String url ="https://api.sciener.cn/v3/key/freeze";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("keyId", String.valueOf(keyId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 解冻钥匙    管理员解除发给普通用户的钥匙的冻结
     * @param keyId
     * @return
     */
    public static String  keyRelieveFreeze (int keyId) {
        String url ="https://api.sciener.cn/v3/key/unfreeze";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("keyId", String.valueOf(keyId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 授予普通钥匙用户管理锁的权限，如发送钥匙和获取密码等权限。
     * @param keyId
     * @return
     */
    public static String  authorize(int keyId,int lockId) {
        String url ="https://api.sciener.cn/v3/key/authorize";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("keyId", String.valueOf(keyId));
        params.put("lockId", String.valueOf(lockId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }
    /**
     * 解除授予普通钥匙用户的管理锁的权限
     * @param keyId
     * @return
     */
    public static String  RelieveAuthorize(int keyId,int lockId) {
        String url ="https://api.sciener.cn/v3/key/unauthorize";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("keyId", String.valueOf(keyId));
        params.put("lockId", String.valueOf(lockId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    /**
     * 解除授予普通钥匙用户的管理锁的权限
     *
     * @return
     */
    public static String  emptyKey(int lockId) {
        String url ="https://api.sciener.cn/v3/lock/deleteAllKey";
        HashMap params = new HashMap();
        params.put("clientId", Config.CLIENT_ID);
        params.put("accessToken", SharedUtils.getString("access_token"));
        params.put("lockId", String.valueOf(lockId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }
}
