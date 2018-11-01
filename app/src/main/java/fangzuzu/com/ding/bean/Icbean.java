package fangzuzu.com.ding.bean;

import java.util.List;

/**
 * Created by lingyuan on 2018/7/9.
 */

public class Icbean {

    /**
     * total : 1
     * pages : 1
     * pageNo : 1
     * pageSize : 100
     * list : [{"lockId":1259450,"cardName":"测试","endDate":0,"nickName":"xiaoding_18365408376","cardId":116728,"userId":"xiaoding_18365408376","cardNumber":"3301608137","startDate":0,"createDate":1540204271000,"status":1}]
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
         * cardName : 测试
         * endDate : 0
         * nickName : xiaoding_18365408376
         * cardId : 116728
         * userId : xiaoding_18365408376
         * cardNumber : 3301608137
         * startDate : 0
         * createDate : 1540204271000
         * status : 1
         */

        private int lockId;
        private String cardName;
        private long endDate;
        private String nickName;
        private int cardId;
        private String userId;
        private String cardNumber;
        private long startDate;
        private long createDate;
        private int status;

        public int getLockId() {
            return lockId;
        }

        public void setLockId(int lockId) {
            this.lockId = lockId;
        }

        public String getCardName() {
            return cardName;
        }

        public void setCardName(String cardName) {
            this.cardName = cardName;
        }

        public long getEndDate() {
            return endDate;
        }

        public void setEndDate(int endDate) {
            this.endDate = endDate;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getCardId() {
            return cardId;
        }

        public void setCardId(int cardId) {
            this.cardId = cardId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public long getStartDate() {
            return startDate;
        }

        public void setStartDate(int startDate) {
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
