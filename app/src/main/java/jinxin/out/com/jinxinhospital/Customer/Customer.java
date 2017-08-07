package jinxin.out.com.jinxinhospital.Customer;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/7.
 */

public class Customer {
    public int id;//
    public String name;//客户姓名
    public String password;//客户密码
    public String guardianName;//监护人姓名
    public double balance;//余额
    public int sex;//性别，1：男，2：女，默认1
    public Date birthday;//生日
    public String mobile;//电话
    public String idCard;//身份证号码
    public String address;//住址
    public int allergy;//有无过敏史，1：无，2：有，默认1
    public int disease;//有无疾病史，1：无，2：有，默认1
    public Date childbirthTime;//分娩日期
    public String childbirthSituation;//分娩情况
    public int childbirthSex;//分娩性别，1：男，2：女
    public int diagnosticAnalysisId;//诊断分析Id
    public int archivesTypeId;//档案类型
    public int consumptionTypeId;//消费类型Id
    public int departmentId;//所属部门
    public int empId;//建档人Id(员工Id)
    public String customerSource;//客户来源
    public int archivesStatus;//档案状态，3：待审，1：正常，2：停用
    public int oldArchivesId;//旧档案号(客户资料Id)
    public Date createTime;//创建时间
    public Date updateTime;//更新时间
}
