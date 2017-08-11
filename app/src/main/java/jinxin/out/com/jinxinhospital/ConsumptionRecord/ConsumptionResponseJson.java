package jinxin.out.com.jinxinhospital.ConsumptionRecord;

/**
 * Created by admin on 2017/8/11.
 */

public class ConsumptionResponseJson {
    public int code;
    public String action;
    public String message;
    public Data data;

    public class Data {
        public String projectName;
        public String remark;
        public ConsumptionRecord[] datas;
    }
}
