package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/7/3.
 */

public class keyManagerBean  {


    /**
     * total : 1
     * pages : 1
     * pageNo : 1
     * pageSize : 100
     * list : [{"lockId":1219039,"date":1539852369000,"keyStatus":"110402","endDate":1546272000000,"openid":1700825877,"keyId":2430564,"startDate":1539852300000,"remarks":"","senderUsername":"xiaoding_18365408376","username":"xiaoding_18617145255","keyRight":0}]
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
         * lockId : 1219039
         * date : 1539852369000
         * keyStatus : 110402
         * endDate : 1546272000000
         * openid : 1700825877
         * keyId : 2430564
         * startDate : 1539852300000
         * remarks :
         * senderUsername : xiaoding_18365408376
         * username : xiaoding_18617145255
         * keyRight : 0
         */

        private int lockId;
        private long date;
        private String keyStatus;
        private long endDate;
        private int openid;
        private int keyId;
        private long startDate;
        private String remarks;
        private String senderUsername;
        private String username;
        private int keyRight;

        public int getLockId() {
            return lockId;
        }

        public void setLockId(int lockId) {
            this.lockId = lockId;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
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

        public void setEndDate(long endDate) {
            this.endDate = endDate;
        }

        public int getOpenid() {
            return openid;
        }

        public void setOpenid(int openid) {
            this.openid = openid;
        }

        public int getKeyId() {
            return keyId;
        }

        public void setKeyId(int keyId) {
            this.keyId = keyId;
        }

        public long getStartDate() {
            return startDate;
        }

        public void setStartDate(long startDate) {
            this.startDate = startDate;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getSenderUsername() {
            return senderUsername;
        }

        public void setSenderUsername(String senderUsername) {
            this.senderUsername = senderUsername;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getKeyRight() {
            return keyRight;
        }

        public void setKeyRight(int keyRight) {
            this.keyRight = keyRight;
        }
    }
}
