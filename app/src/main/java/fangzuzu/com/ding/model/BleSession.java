package fangzuzu.com.ding.model;


import fangzuzu.com.ding.enumtype.Operation;

/**
 * Created by Administrator on 2016/7/15 0015.
 */
public class BleSession {

    /**
     * operation
     */
    private Operation operation;

    /**
     * lock mac
     */
    private String lockmac;

    /**
     * passcode
     */
    private String password;

    private long startDate;

    private long endDate;
    private boolean flag;
    private long cardNo;
    private  String bleManager;
    String icNumblder;

    public String getIcNumblder() {
        return icNumblder;
    }

    public void setIcNumblder(String icNumblder) {
        this.icNumblder = icNumblder;
    }

    public String getBleManager() {
        return bleManager;
    }

    public void setBleManager(String bleManager) {
        this.bleManager = bleManager;
    }

    public long getCardNo() {
        return cardNo;
    }

    public void setCardNo(long cardNo) {
        this.cardNo = cardNo;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getLockmac() {
        return lockmac;
    }

    public void setLockmac(String lockmac) {
        this.lockmac = lockmac;
    }

    public String getPassword() {
        return password;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static BleSession getInstance(Operation operation, String lockmac) {
        BleSession bleSession = new BleSession();
        bleSession.setOperation(operation);
        bleSession.setLockmac(lockmac);
        return bleSession;
    }
}
