package jinxin.out.com.jinxin_employee.JsonModule;

/**
 * Created by Administrator on 2017/8/5.
 */

public class LoginResponseJson {
    public int code;
    public String action;
    public String message;
    public Data data;

    public class Data {
        public String token;
        public EmpDO empDO;
    }

    public class EmpDO {
        public String avatarPath;
        public int id;
        public String jobNumber;
        public String mobile;
        public String name;
        public int sex;
    }
}
