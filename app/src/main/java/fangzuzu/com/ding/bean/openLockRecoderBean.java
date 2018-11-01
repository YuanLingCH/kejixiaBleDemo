package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/6/29.
 */

public class openLockRecoderBean {


    /**
     * total : 1
     * pages : 1
     * pageNo : 1
     * pageSize : 100
     * list : [{"lockId":1259450,"serverDate":1540361767000,"recordType":1,"success":1,"keyboardPwd":"","username":"xiaoding_18365408376","lockDate":1540361723000}]
     */

    private int total;
    private int pages;
    private int pageNo;
    private int pageSize;
    private List<ListBean> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * lockId : 1259450
         * serverDate : 1540361767000
         * recordType : 1
         * success : 1
         * keyboardPwd :
         * username : xiaoding_18365408376
         * lockDate : 1540361723000
         */

        private int lockId;
        private long serverDate;
        private int recordType;
        private int success;
        private String keyboardPwd;
        private String username;
        private long lockDate;

        public int getLockId() {
            return lockId;
        }

        public void setLockId(int lockId) {
            this.lockId = lockId;
        }

        public long getServerDate() {
            return serverDate;
        }

        public void setServerDate(long serverDate) {
            this.serverDate = serverDate;
        }

        public int getRecordType() {
            return recordType;
        }

        public void setRecordType(int recordType) {
            this.recordType = recordType;
        }

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        public String getKeyboardPwd() {
            return keyboardPwd;
        }

        public void setKeyboardPwd(String keyboardPwd) {
            this.keyboardPwd = keyboardPwd;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public long getLockDate() {
            return lockDate;
        }

        public void setLockDate(long lockDate) {
            this.lockDate = lockDate;
        }
    }
}
