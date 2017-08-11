package jinxin.out.com.jinxinhospital.News;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/5.
 */

public class News {
    public int id;//
    public String title;//标题
    public String titleColor;//标题颜色（存颜色代码）
    public String coverPath;//新闻封面图地址
    public String content;//内容
    public String summary;
    public int newsTypeId;//
    public String keyWord;//关键字
    public int publisherId;//发布人Id
    public int status;//新闻状态，1：正常，2：停用
    public int sequence;//排序，越小越前
    public String createTime;//创建时间
    public String updateTime;//更新时间
}
