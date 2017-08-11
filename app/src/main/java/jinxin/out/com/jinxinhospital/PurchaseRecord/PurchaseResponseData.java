package jinxin.out.com.jinxinhospital.PurchaseRecord;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/7.
 */

public class PurchaseResponseData {

    public int id;//
    public int customerId;//客户Id
    public String customerName;//客户姓名
    public int projectId;//项目Id
    public String projectName;//项目名称
    public int number;//购买数量
    public int projectFrequency;//一次包含项目次数
    public int useFrequency;//使用次数
    public Double totalPrice;//总价
    public int status;//购买项目的状态，1：可用，2：完成，3：过期，4：退费，5：作废
    public int empId;//员工Id
    public String remark;//备注
    public String expirationDate;//到期时间
    public String createTime;//创建时间
    public String updateTime;//更新时间
    public String statusName;
}
