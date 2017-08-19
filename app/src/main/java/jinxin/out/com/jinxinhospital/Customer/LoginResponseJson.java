package jinxin.out.com.jinxinhospital.Customer;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/7.
 */

public class LoginResponseJson {
    public int code;
//    public String action;
    public String message;
    public Data data;

    public class Data{
        public String token;
        public Customer customer;
    }
    public class Customer{
        public int id;//
        public String birthday;
        public int consumptionTypeId;//消费类型Id
        public String name;//客户姓名
        public int sex;//性别，1：男，2：女，默认1
        public String mobile;//电话
        public boolean vip;
    }
}
