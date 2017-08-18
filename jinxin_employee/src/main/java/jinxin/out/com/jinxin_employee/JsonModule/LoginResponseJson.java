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
        public Employee empDO;
    }

}
