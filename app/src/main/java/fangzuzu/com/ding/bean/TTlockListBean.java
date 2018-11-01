package fangzuzu.com.ding.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lingyuan on 2018/10/15.
 */

public class TTlockListBean implements Serializable{


    /**
     * lastUpdateDate : 1539597806437
     * keyList : [{"date":1539590797000,"specialValue":20705,"lockAlias":"M201T_d6d197","keyStatus":"110401","endDate":1544861160,"keyId":2400741,"lockMac":"C9:F0:29:97:D1:D6","timezoneRawOffset":28800000,"lockId":1219039,"electricQuantity":70,"lockFlagPos":0,"keyboardPwdVersion":4,"aesKeyStr":"b9,15,16,c7,33,0a,f1,29,da,7e,82,a8,61,80,46,30","remoteEnable":1,"lockVersion":{"showAdminKbpwdFlag":true,"groupId":1,"protocolVersion":3,"protocolType":5,"orgId":1,"logoUrl":"","scene":2},"userType":"110302","lockKey":"MzAsMjksMzEsMjYsMjQsMjQsMjgsMjQsMjcsMjksODA=","lockName":"M201T_d6d197","startDate":1539590760,"remarks":"","keyRight":0},{"date":1539593870000,"specialValue":21735,"lockAlias":"AC-M6_918e3d","keyStatus":"110401","endDate":0,"noKeyPwd":"2665131","keyId":2402996,"lockMac":"D9:FD:8B:3D:8E:91","deletePwd":"","timezoneRawOffset":28800000,"lockId":1233471,"electricQuantity":100,"adminPwd":"OTksMTAwLDEwMywxMDMsOTcsMTAwLDEwNywxMDYsMTAyLDEwMCw0NQ==","lockFlagPos":0,"keyboardPwdVersion":4,"aesKeyStr":"31,a3,66,3a,2d,66,f1,f5,bd,24,60,a8,19,70,93,2f","remoteEnable":2,"lockVersion":{"showAdminKbpwdFlag":true,"groupId":10,"protocolVersion":3,"protocolType":5,"orgId":9,"logoUrl":"","scene":2},"userType":"110301","lockKey":"MTE1LDExNywxMTMsMTE0LDExMiwxMTgsMTEyLDExOCwxMTYsMTE2LDYx","lockName":"AC-M6_918e3d","startDate":0,"remarks":"","keyRight":0}]
     */

    private long lastUpdateDate;
    private List<KeyListBean> keyList;

    public long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<KeyListBean> getKeyList() {
        return keyList;
    }

    public void setKeyList(List<KeyListBean> keyList) {
        this.keyList = keyList;
    }

    public static class KeyListBean {
        /**
         * date : 1539590797000
         * specialValue : 20705
         * lockAlias : M201T_d6d197
         * keyStatus : 110401
         * endDate : 1544861160
         * keyId : 2400741
         * lockMac : C9:F0:29:97:D1:D6
         * timezoneRawOffset : 28800000
         * lockId : 1219039
         * electricQuantity : 70
         * lockFlagPos : 0
         * keyboardPwdVersion : 4
         * aesKeyStr : b9,15,16,c7,33,0a,f1,29,da,7e,82,a8,61,80,46,30
         * remoteEnable : 1
         * lockVersion : {"showAdminKbpwdFlag":true,"groupId":1,"protocolVersion":3,"protocolType":5,"orgId":1,"logoUrl":"","scene":2}
         * userType : 110302
         * lockKey : MzAsMjksMzEsMjYsMjQsMjQsMjgsMjQsMjcsMjksODA=
         * lockName : M201T_d6d197
         * startDate : 1539590760
         * remarks :
         * keyRight : 0
         * noKeyPwd : 2665131
         * deletePwd :
         * adminPwd : OTksMTAwLDEwMywxMDMsOTcsMTAwLDEwNywxMDYsMTAyLDEwMCw0NQ==
         */

        private long date;
        private int specialValue;
        private String lockAlias;
        private String keyStatus;
        private long endDate;
        private int keyId;
        private String lockMac;
        private int timezoneRawOffset;
        private int lockId;
        private int electricQuantity;
        private int lockFlagPos;
        private int keyboardPwdVersion;
        private String aesKeyStr;
        private int remoteEnable;
        private LockVersionBean lockVersion;
        private String userType;
        private String lockKey;
        private String lockName;
        private long startDate;
        private String remarks;
        private int keyRight;
        private String noKeyPwd;
        private String deletePwd;
        private String adminPwd;

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public int getSpecialValue() {
            return specialValue;
        }

        public void setSpecialValue(int specialValue) {
            this.specialValue = specialValue;
        }

        public String getLockAlias() {
            return lockAlias;
        }

        public void setLockAlias(String lockAlias) {
            this.lockAlias = lockAlias;
        }

        public String getKeyStatus() {
            return keyStatus;
        }

        public void setKeyStatus(String keyStatus) {
            this.keyStatus = keyStatus;
        }

        public long getEndDate() {
            return endDate;
        }

        public void setEndDate(int endDate) {
            this.endDate = endDate;
        }

        public int getKeyId() {
            return keyId;
        }

        public void setKeyId(int keyId) {
            this.keyId = keyId;
        }

        public String getLockMac() {
            return lockMac;
        }

        public void setLockMac(String lockMac) {
            this.lockMac = lockMac;
        }

        public int getTimezoneRawOffset() {
            return timezoneRawOffset;
        }

        public void setTimezoneRawOffset(int timezoneRawOffset) {
            this.timezoneRawOffset = timezoneRawOffset;
        }

        public int getLockId() {
            return lockId;
        }

        public void setLockId(int lockId) {
            this.lockId = lockId;
        }

        public int getElectricQuantity() {
            return electricQuantity;
        }

        public void setElectricQuantity(int electricQuantity) {
            this.electricQuantity = electricQuantity;
        }

        public int getLockFlagPos() {
            return lockFlagPos;
        }

        public void setLockFlagPos(int lockFlagPos) {
            this.lockFlagPos = lockFlagPos;
        }

        public int getKeyboardPwdVersion() {
            return keyboardPwdVersion;
        }

        public void setKeyboardPwdVersion(int keyboardPwdVersion) {
            this.keyboardPwdVersion = keyboardPwdVersion;
        }

        public String getAesKeyStr() {
            return aesKeyStr;
        }

        public void setAesKeyStr(String aesKeyStr) {
            this.aesKeyStr = aesKeyStr;
        }

        public int getRemoteEnable() {
            return remoteEnable;
        }

        public void setRemoteEnable(int remoteEnable) {
            this.remoteEnable = remoteEnable;
        }

        public LockVersionBean getLockVersion() {
            return lockVersion;
        }

        public void setLockVersion(LockVersionBean lockVersion) {
            this.lockVersion = lockVersion;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getLockKey() {
            return lockKey;
        }

        public void setLockKey(String lockKey) {
            this.lockKey = lockKey;
        }

        public String getLockName() {
            return lockName;
        }

        public void setLockName(String lockName) {
            this.lockName = lockName;
        }

        public long getStartDate() {
            return startDate;
        }

        public void setStartDate(int startDate) {
            this.startDate = startDate;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public int getKeyRight() {
            return keyRight;
        }

        public void setKeyRight(int keyRight) {
            this.keyRight = keyRight;
        }

        public String getNoKeyPwd() {
            return noKeyPwd;
        }

        public void setNoKeyPwd(String noKeyPwd) {
            this.noKeyPwd = noKeyPwd;
        }

        public String getDeletePwd() {
            return deletePwd;
        }

        public void setDeletePwd(String deletePwd) {
            this.deletePwd = deletePwd;
        }

        public String getAdminPwd() {
            return adminPwd;
        }

        public void setAdminPwd(String adminPwd) {
            this.adminPwd = adminPwd;
        }

        public static class LockVersionBean implements Serializable {
            /**
             * showAdminKbpwdFlag : true
             * groupId : 1
             * protocolVersion : 3
             * protocolType : 5
             * orgId : 1
             * logoUrl :
             * scene : 2
             */

            private boolean showAdminKbpwdFlag;
            private int groupId;
            private int protocolVersion;
            private int protocolType;
            private int orgId;
            private String logoUrl;
            private int scene;

            public boolean isShowAdminKbpwdFlag() {
                return showAdminKbpwdFlag;
            }

            public void setShowAdminKbpwdFlag(boolean showAdminKbpwdFlag) {
                this.showAdminKbpwdFlag = showAdminKbpwdFlag;
            }

            public int getGroupId() {
                return groupId;
            }

            public void setGroupId(int groupId) {
                this.groupId = groupId;
            }

            public int getProtocolVersion() {
                return protocolVersion;
            }

            public void setProtocolVersion(int protocolVersion) {
                this.protocolVersion = protocolVersion;
            }

            public int getProtocolType() {
                return protocolType;
            }

            public void setProtocolType(int protocolType) {
                this.protocolType = protocolType;
            }

            public int getOrgId() {
                return orgId;
            }

            public void setOrgId(int orgId) {
                this.orgId = orgId;
            }

            public String getLogoUrl() {
                return logoUrl;
            }

            public void setLogoUrl(String logoUrl) {
                this.logoUrl = logoUrl;
            }

            public int getScene() {
                return scene;
            }

            public void setScene(int scene) {
                this.scene = scene;
            }
        }
    }
}
