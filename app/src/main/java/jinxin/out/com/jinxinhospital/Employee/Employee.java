package jinxin.out.com.jinxinhospital.Employee;

/**
 * Created by Administrator on 2017/8/5.
 */

public class Employee {

    public int id;//
    public String jobNumber;//员工工号
    public String name;//
    public String password;//密码
    public int sex;//性别，1：男，2：女；默认1
    public String avatarPath;//员工头像
    public String mobile;//联系电话
    public String idCard;//身份证号码
    public String birthday;//生日
    public String summary;
    public String introduction;//简介
    public int status;//账号状态，1：正常，2：停用，默认1
    public int shows;//APP端介绍是否显示，1：显示，2：不显示，默认1
    public int sequence;//APP端介绍排序，越小越排前
    public String createTime;//创建时间
    public String updateTime;//更新时间
}
