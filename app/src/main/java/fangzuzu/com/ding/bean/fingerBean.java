package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/9/10.
 */

public class fingerBean {


    /**
     * total : 2
     * pages : 1
     * pageNo : 1
     * pageSize : 100
     * list : [{"lockId":1259450,"fingerprintNumber":"38904474238995","fingerprintName":"测试","endDate":1540291380000,"nickName":"xiaoding_18365408376","fingerprintId":70140,"userId":"xiaoding_18365408376","startDate":1540291140000,"createDate":1540291167000,"status":1},{"lockId":1259450,"fingerprintNumber":"38907998044180","fingerprintName":"测试","endDate":1540345020000,"nickName":"xiaoding_18365408376","fingerprintId":70224,"userId":"xiaoding_18365408376","startDate":1540344900000,"createDate":1540344934000,"status":1}]
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
         * fingerprintNumber : 38904474238995
         * fingerprintName : 测试
         * endDate : 1540291380000
         * nickName : xiaoding_18365408376
         * fingerprintId : 70140
         * userId : xiaoding_18365408376
         * startDate : 1540291140000
         * createDate : 1540291167000
         * status : 1
         */

        private int lockId;
        private String fingerprintNumber;
        private String fingerprintName;
        private long endDate;
        private String nickName;
        private int fingerprintId;
        private String userId;
        private long startDate;
        private long createDate;
        private int status;

        public int getLockId() {
            return lockId;
        }

        public void setLockId(int lockId) {
            this.lockId = lockId;
        }

        public String getFingerprintNumber() {
            return fingerprintNumber;
        }

        public void setFingerprintNumber(String fingerprintNumber) {
            this.fingerprintNumber = fingerprintNumber;
        }

        public String getFingerprintName() {
            return fingerprintName;
        }

        public void setFingerprintName(String fingerprintName) {
            this.fingerprintName = fingerprintName;
        }

        public long getEndDate() {
            return endDate;
        }

        public void setEndDate(long endDate) {
            this.endDate = endDate;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getFingerprintId() {
            return fingerprintId;
        }

        public void setFingerprintId(int fingerprintId) {
            this.fingerprintId = fingerprintId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public long getStartDate() {
            return startDate;
        }

        public void setStartDate(long startDate) {
            this.startDate = startDate;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
