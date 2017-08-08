package jinxin.out.com.jinxinhospital.MessageCenter;

/**
 * Created by Administrator on 2017/8/8.
 */

public class Message {
    public int id;//
    public int type;//推送类型，1：单推，2：分组推送
    public String title;//标题
    public String content;//内容
    public String createTime;//创建时间
    public int consumptionTypeId;//消费类型Id
    public int customerId;//客户Id
    public int empId;//员工Id
    public int status;//状态，1：正常，2：作废
    public String updateTime;//更新时间
}
