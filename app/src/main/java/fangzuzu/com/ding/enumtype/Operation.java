package fangzuzu.com.ding.enumtype;

public enum Operation {
    /**
     * add admin
     */
    ADD_ADMIN,

    /**
     * nulock
     */
    UNLOCK,

    /**
     * reset keyboard passcode
     */
    RESET_KEYBOARD_PASSWORD,

    /**
     * reset ekey
     */
    RESET_EKEY,

    /**
     * set lock time
     */
    SET_LOCK_TIME,
    /**
     * get lock time
     */
    GET_LOCK_TIME,

    /**
     * get operation log
     */
    GET_OPERATE_LOG,

    /**
     * parking lock up
     */
    LOCKCAR_UP,

    /**
     * parking lock down
     */
    LOCKCAR_DOWN,

    /**
     * click to unlock
     */
    CLICK_UNLOCK,

    /**
     * set admin passcode
     */
    SET_ADMIN_KEYBOARD_PASSWORD,

    /**
     * set delete passcode
     */
    SET_DELETE_PASSWORD,

    /**
     * Restore factory settings
     */
    RESET_LOCK,

    /**
     * set lock time
     */
    CHECK_LOCKTIME,

    /**
     * modify key name
     */
    MODIFY_KEYNAME,

    /**
     * delete keyboard passcode
     *
     */
    DELETE_ONE_KEYBOARDPASSWORD,
    /**
     * get lock version information
     */
    GET_LOCK_VERSION_INFO,
    /**
     * add customized passcode
     */
    ADD_PASSCODE,

    /**
     * addIcCard 添加ic卡
     */
    ADD_ICCARD,
    /**|
     * 修改IC卡有效期
     */
    MODIFY_ICPEROID,
    /**
     * 删除ic卡
     */
    DELETE_ICCARD,
    /**
     * 添加指纹
     */
    ADD_FINGER,
    /**
     * 删除指纹
     */
    DELETE_FINGER,
    /**
     * 修改指纹时间
     */
    MODIFY_FINGER_PRINT_PEROID,
}