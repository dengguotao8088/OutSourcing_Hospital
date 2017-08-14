package jinxin.out.com.jinxinhospital.ConsumptionRecord;

import java.util.Date;

/**
 * Created by admin on 2017/8/11.
 */

public class ConsumptionRecord {
    public int id;//
    public int purchaseRecordId;//购买记录Id
    public int empId;//员工Id
    public String createTime;//创建时间
    public String empName;
    public String statusName;
    public String daySymptom;//当日症状
    public int status;//当前消费记录状态
    public String commentLevel;//评论等级
    public String commentContent;//评论内容
    public String remarks;//备注
}
