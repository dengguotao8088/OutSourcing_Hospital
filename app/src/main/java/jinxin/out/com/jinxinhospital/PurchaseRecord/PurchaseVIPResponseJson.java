package jinxin.out.com.jinxinhospital.PurchaseRecord;

/**
 * Created by Administrator on 2017/8/19.
 */

public class PurchaseVIPResponseJson {
    public int code;
    public String action;
    public String message;
    public  purchaseRecordList data;

    public class purchaseRecordList{
        public double balance;
        public PurchaseResponseData[] purchaseRecordList;
    }
}
