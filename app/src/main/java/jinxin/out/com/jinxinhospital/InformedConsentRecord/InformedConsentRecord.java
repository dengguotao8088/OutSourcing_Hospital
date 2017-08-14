package jinxin.out.com.jinxinhospital.InformedConsentRecord;

/**
 * Created by admin on 2017/8/14.
 */

public class InformedConsentRecord {
    public  int id;//
    public int customerId;//客户Id
    public int informedConsentTemplateId;//知情同意书Id
    public String informedConsentTemplateName;//知情同意书名称
    public String relationShip;//与客户关系(本人、监护人、委托人)
    public String customerSignaturePath;//客户签名路径
    public String createTime;//创建时间
    public String updateTime;//更新时间
}
