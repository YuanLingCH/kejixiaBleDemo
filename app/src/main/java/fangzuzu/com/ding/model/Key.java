package fangzuzu.com.ding.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
@Entity
public class Key implements Serializable {

    @Id
    private Long id;

//    /**
//     * access token
//     */
//    private String accessToken;

    /**
     * ekey type:110301-admin ekey, 110302-common user ekey
     */
    private String userType;

    /**
     * key status
     * "110401"	Normal use
     * "110402"	To be received
     * "110405"	Frozen
     * "110408"	deleted
     * "110410"	Reset
     */
    private String keyStatus;

    /**
     * lock id
     */
    private int lockId;

    /**
     * key id
     */
    private int keyId;

//    /**
//     * judge whether it is an administrator
//     */
//    private boolean isAdmin;

    /**
     * lock version information(json format)
     */
    private String lockVersion;

    /**
     * lock name
     */
    private String lockName;

    /**
     * lock alias
     */
    private String lockAlias;

    /**
     * lock mac
     */
    private String lockMac;

    /**
     * battery
     */
    private int electricQuantity;

    /**
     * lock flag position
     */
    private int lockFlagPos;

    /**
     *  admin code, which only belongs to the admin ekey, will be used to verify the admin permission.
     *
     */
    private String adminPwd;

    /**
     * The key data which will be used to unlock
     */
    private String lockKey;

    /**
     * Super passcode, which only belongs to the admin ekey, can be entered on the keypad to unlock
     */
    private String noKeyPwd;

    /**
     * Erasing passcode,which belongs to old locks, has been abandoned. Please don't use it.
     */
    private String deletePwd;

    /**
     * Initial data of passcode, which is generated by SDK, is used to create passcode
     */
    private String pwdInfo;

    /**
     * The timestamp which is generated by SDK during lock initializing time
     */
    private long timestamp;

    /**
     * aesKey
     */
    private String aesKeyStr;

    /**
     * The time when it becomes valid
     */
    private long startDate;

    /**
     * The time when it is expired
     */
    private long endDate;

    /**
     * characteristic value. it is used to indicate what kinds of feature do a lock support.
     */
    private int specialValue;

    /**
     * The offset between your time zone and UTC, in millisecond
     */
    private int timezoneRawOffset;

    /**
     * Is key authorized:0-NO,1-yes
     */
    private int keyRight;

    /**
     * Passcode version: 0、1、2、3、4
     */
    private int keyboardPwdVersion;

    /**
     * is support remote unlock: 1 - yes 、 2 - no
     */
    private int remoteEnable;

    /**
     * Comment
     */
    private String remarks;

    /**
     * Product model
     */
    private String modelNum;

    /**
     * Hardware version
     */
    private String hardwareRevision;
    
    /**
     * Firmware version
     */
    private String firmwareRevision;


    @Generated(hash = 2076226027)
    public Key() {
    }

    @Generated(hash = 1626650047)
    public Key(Long id, String userType, String keyStatus, int lockId, int keyId, String lockVersion,
               String lockName, String lockAlias, String lockMac, int electricQuantity, int lockFlagPos,
               String adminPwd, String lockKey, String noKeyPwd, String deletePwd, String pwdInfo,
               long timestamp, String aesKeyStr, long startDate, long endDate, int specialValue,
               int timezoneRawOffset, int keyRight, int keyboardPwdVersion, int remoteEnable,
               String remarks, String modelNum, String hardwareRevision, String firmwareRevision) {
        this.id = id;
        this.userType = userType;
        this.keyStatus = keyStatus;
        this.lockId = lockId;
        this.keyId = keyId;
        this.lockVersion = lockVersion;
        this.lockName = lockName;
        this.lockAlias = lockAlias;
        this.lockMac = lockMac;
        this.electricQuantity = electricQuantity;
        this.lockFlagPos = lockFlagPos;
        this.adminPwd = adminPwd;
        this.lockKey = lockKey;
        this.noKeyPwd = noKeyPwd;
        this.deletePwd = deletePwd;
        this.pwdInfo = pwdInfo;
        this.timestamp = timestamp;
        this.aesKeyStr = aesKeyStr;
        this.startDate = startDate;
        this.endDate = endDate;
        this.specialValue = specialValue;
        this.timezoneRawOffset = timezoneRawOffset;
        this.keyRight = keyRight;
        this.keyboardPwdVersion = keyboardPwdVersion;
        this.remoteEnable = remoteEnable;
        this.remarks = remarks;
        this.modelNum = modelNum;
        this.hardwareRevision = hardwareRevision;
        this.firmwareRevision = firmwareRevision;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLockId() {
        return this.lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public String getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(String lockVersion) {
        this.lockVersion = lockVersion;
    }

    public String getDeletePwd() {
        return this.deletePwd;
    }

    public void setDeletePwd(String deletePwd) {
        this.deletePwd = deletePwd;
    }

    public String getPwdInfo() {
        return this.pwdInfo;
    }

    public void setPwdInfo(String pwdInfo) {
        this.pwdInfo = pwdInfo;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLockName() {
        return this.lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getLockMac() {
        return this.lockMac;
    }

    public void setLockMac(String lockMac) {
        this.lockMac = lockMac;
    }

    public int getLockFlagPos() {
        return this.lockFlagPos;
    }

    public void setLockFlagPos(int lockFlagPos) {
        this.lockFlagPos = lockFlagPos;
    }

    public int getKeyId() {
        return this.keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public boolean isAdmin() {
        return "110301".equals(userType);
    }

//    public String getAccessToken() {
//        return this.accessToken;
//    }
//
//    public void setAccessToken(String accessToken) {
//        this.accessToken = accessToken;
//    }

    public long getStartDate() {
        return this.startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return this.endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getSpecialValue() {
        return this.specialValue;
    }

    public void setSpecialValue(int specialValue) {
        this.specialValue = specialValue;
    }

    public int getTimezoneRawOffset() {
        return this.timezoneRawOffset;
    }

    public void setTimezoneRawOffset(int timezoneRawOffset) {
        this.timezoneRawOffset = timezoneRawOffset;
    }

    public String getHardwareRevision() {
        return this.hardwareRevision;
    }

    public void setHardwareRevision(String hardwareRevision) {
        this.hardwareRevision = hardwareRevision;
    }

    public String getFirmwareRevision() {
        return this.firmwareRevision;
    }

    public void setFirmwareRevision(String firmwareRevision) {
        this.firmwareRevision = firmwareRevision;
    }

    public String getLockAlias() {
        return this.lockAlias;
    }

    public void setLockAlias(String lockAlias) {
        this.lockAlias = lockAlias;
    }

    public String getKeyStatus() {
        return this.keyStatus;
    }

    public void setKeyStatus(String keyStatus) {
        this.keyStatus = keyStatus;
    }

    public int getElectricQuantity() {
        return electricQuantity;
    }

    public void setElectricQuantity(int electricQuantity) {
        this.electricQuantity = electricQuantity;
    }

    public String getAdminPwd() {
        return adminPwd;
    }

    public void setAdminPwd(String adminPwd) {
        this.adminPwd = adminPwd;
    }

    public String getLockKey() {
        return lockKey;
    }

    public void setLockKey(String lockKey) {
        this.lockKey = lockKey;
    }

    public String getNoKeyPwd() {
        return noKeyPwd;
    }

    public void setNoKeyPwd(String noKeyPwd) {
        this.noKeyPwd = noKeyPwd;
    }

    public String getAesKeyStr() {
        return aesKeyStr;
    }

    public void setAesKeyStr(String aesKeyStr) {
        this.aesKeyStr = aesKeyStr;
    }

    public String getModelNum() {
        return modelNum;
    }

    public void setModelNum(String modelNum) {
        this.modelNum = modelNum;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getKeyRight() {
        return keyRight;
    }

    public void setKeyRight(int keyRight) {
        this.keyRight = keyRight;
    }

    public int getKeyboardPwdVersion() {
        return keyboardPwdVersion;
    }

    public void setKeyboardPwdVersion(int keyboardPwdVersion) {
        this.keyboardPwdVersion = keyboardPwdVersion;
    }

    public int getRemoteEnable() {
        return remoteEnable;
    }

    public void setRemoteEnable(int remoteEnable) {
        this.remoteEnable = remoteEnable;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
