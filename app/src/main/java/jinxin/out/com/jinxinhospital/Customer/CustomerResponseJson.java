package jinxin.out.com.jinxinhospital.Customer;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/7.
 */

public class CustomerResponseJson {
    public int code;
    public String action;
    public String message;
    public Customer data;

    public class Data {
        public int id;//
        public String name;//客户姓名
        public int sex;//性别，1：男，2：女，默认1
        public String birthday;//生日
        public String mobile;//电话
        public String address;//住址
        public int allergy;//有无过敏史，1：无，2：有，默认1
        public int disease;//有无疾病史，1：无，2：有，默认1
        public int diagnosticAnalysisId;//诊断分析Id
        public int archivesTypeId;//档案类型
        public int consumptionTypeId;//消费类型Id
        public int archivesStatus;//档案状态，3：待审，1：正常，2：停用
        public String createTime;//创建时间
    }
}
