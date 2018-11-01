package fangzuzu.com.ding.bean;

/**
 * Created by lingyuan on 2018/6/1.
 */

public class loginBean {


    /**
     * code : 1001
     * msg : null
     * data : {"userDetail":{"id":"487eb35e-cdf4-11e8-a1c4-00163e0858bc","uid":"78774977568","idcard":null,"nickname":null,"idcardName":null,"gender":null,"age":null,"prefession":null,"headImgUrl":null,"partid":null,"username":"18365408376","ttsusername":"xiaoding_18365408376","state":1},"ttsToken":{"access_token":"f12633f81e28f4a2089db0d1ac436b1b","refresh_token":"acd7367d7f95950e6005913d08eafec1","uid":1372108,"openid":1074476510,"scope":"user,key,room","expires_in":6309214}}
     */

    private int code;
    private Object msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * userDetail : {"id":"487eb35e-cdf4-11e8-a1c4-00163e0858bc","uid":"78774977568","idcard":null,"nickname":null,"idcardName":null,"gender":null,"age":null,"prefession":null,"headImgUrl":null,"partid":null,"username":"18365408376","ttsusername":"xiaoding_18365408376","state":1}
         * ttsToken : {"access_token":"f12633f81e28f4a2089db0d1ac436b1b","refresh_token":"acd7367d7f95950e6005913d08eafec1","uid":1372108,"openid":1074476510,"scope":"user,key,room","expires_in":6309214}
         */

        private UserDetailBean userDetail;
        private TtsTokenBean ttsToken;

        public UserDetailBean getUserDetail() {
            return userDetail;
        }

        public void setUserDetail(UserDetailBean userDetail) {
            this.userDetail = userDetail;
        }

        public TtsTokenBean getTtsToken() {
            return ttsToken;
        }

        public void setTtsToken(TtsTokenBean ttsToken) {
            this.ttsToken = ttsToken;
        }

        public static class UserDetailBean {
            /**
             * id : 487eb35e-cdf4-11e8-a1c4-00163e0858bc
             * uid : 78774977568
             * idcard : null
             * nickname : null
             * idcardName : null
             * gender : null
             * age : null
             * prefession : null
             * headImgUrl : null
             * partid : null
             * username : 18365408376
             * ttsusername : xiaoding_18365408376
             * state : 1
             */

            private String id;
            private String uid;
            private Object idcard;
            private Object nickname;
            private Object idcardName;
            private Object gender;
            private Object age;
            private Object prefession;
            private Object headImgUrl;
            private Object partid;
            private String username;
            private String ttsusername;
            private int state;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public Object getIdcard() {
                return idcard;
            }

            public void setIdcard(Object idcard) {
                this.idcard = idcard;
            }

            public Object getNickname() {
                return nickname;
            }

            public void setNickname(Object nickname) {
                this.nickname = nickname;
            }

            public Object getIdcardName() {
                return idcardName;
            }

            public void setIdcardName(Object idcardName) {
                this.idcardName = idcardName;
            }

            public Object getGender() {
                return gender;
            }

            public void setGender(Object gender) {
                this.gender = gender;
            }

            public Object getAge() {
                return age;
            }

            public void setAge(Object age) {
                this.age = age;
            }

            public Object getPrefession() {
                return prefession;
            }

            public void setPrefession(Object prefession) {
                this.prefession = prefession;
            }

            public Object getHeadImgUrl() {
                return headImgUrl;
            }

            public void setHeadImgUrl(Object headImgUrl) {
                this.headImgUrl = headImgUrl;
            }

            public Object getPartid() {
                return partid;
            }

            public void setPartid(Object partid) {
                this.partid = partid;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getTtsusername() {
                return ttsusername;
            }

            public void setTtsusername(String ttsusername) {
                this.ttsusername = ttsusername;
            }

            public int getState() {
                return state;
            }

            public void setState(int state) {
                this.state = state;
            }
        }

        public static class TtsTokenBean {
            /**
             * access_token : f12633f81e28f4a2089db0d1ac436b1b
             * refresh_token : acd7367d7f95950e6005913d08eafec1
             * uid : 1372108
             * openid : 1074476510
             * scope : user,key,room
             * expires_in : 6309214
             */

            private String access_token;
            private String refresh_token;
            private int uid;
            private int openid;
            private String scope;
            private int expires_in;

            public String getAccess_token() {
                return access_token;
            }

            public void setAccess_token(String access_token) {
                this.access_token = access_token;
            }

            public String getRefresh_token() {
                return refresh_token;
            }

            public void setRefresh_token(String refresh_token) {
                this.refresh_token = refresh_token;
            }

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public int getOpenid() {
                return openid;
            }

            public void setOpenid(int openid) {
                this.openid = openid;
            }

            public String getScope() {
                return scope;
            }

            public void setScope(String scope) {
                this.scope = scope;
            }

            public int getExpires_in() {
                return expires_in;
            }

            public void setExpires_in(int expires_in) {
                this.expires_in = expires_in;
            }
        }
    }
}
