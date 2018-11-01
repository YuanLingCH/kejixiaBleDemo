package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/7/5.
 */

public class passwordManagerBean {


    /**
     * list : [{"lockId":1219039,"keyboardPwdVersion":4,"endDate":1539928800000,"sendDate":1539930070000,"keyboardPwdId":5976077,"keyboardPwd":"98834021","keyboardPwdType":2,"startDate":1539928800000,"receiverUsername":""}]
     * pageNo : 1
     * pageSize : 100
     * pages : 1
     * total : 1
     */

    private int pageNo;
    private int pageSize;
    private int pages;
    private int total;
    private List<ListBean> list;

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

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * lockId : 1219039
         * keyboardPwdVersion : 4
         * endDate : 1539928800000
         * sendDate : 1539930070000
         * keyboardPwdId : 5976077
         * keyboardPwd : 98834021
         * keyboardPwdType : 2
         * startDate : 1539928800000
         * receiverUsername :
         */

        private int lockId;
        private int keyboardPwdVersion;
        private long endDate;
        private long sendDate;
        private int keyboardPwdId;
        private String keyboardPwd;
        private int keyboardPwdType;
        private long startDate;
        private String receiverUsername;

        public int getLockId() {
            return lockId;
        }

        public void setLockId(int lockId) {
            this.lockId = lockId;
        }

        public int getKeyboardPwdVersion() {
            return keyboardPwdVersion;
        }

        public void setKeyboardPwdVersion(int keyboardPwdVersion) {
            this.keyboardPwdVersion = keyboardPwdVersion;
        }

        public long getEndDate() {
            return endDate;
        }

        public void setEndDate(long endDate) {
            this.endDate = endDate;
        }

        public long getSendDate() {
            return sendDate;
        }

        public void setSendDate(long sendDate) {
            this.sendDate = sendDate;
        }

        public int getKeyboardPwdId() {
            return keyboardPwdId;
        }

        public void setKeyboardPwdId(int keyboardPwdId) {
            this.keyboardPwdId = keyboardPwdId;
        }

        public String getKeyboardPwd() {
            return keyboardPwd;
        }

        public void setKeyboardPwd(String keyboardPwd) {
            this.keyboardPwd = keyboardPwd;
        }

        public int getKeyboardPwdType() {
            return keyboardPwdType;
        }

        public void setKeyboardPwdType(int keyboardPwdType) {
            this.keyboardPwdType = keyboardPwdType;
        }

        public long getStartDate() {
            return startDate;
        }

        public void setStartDate(long startDate) {
            this.startDate = startDate;
        }

        public String getReceiverUsername() {
            return receiverUsername;
        }

        public void setReceiverUsername(String receiverUsername) {
            this.receiverUsername = receiverUsername;
        }
    }
}
