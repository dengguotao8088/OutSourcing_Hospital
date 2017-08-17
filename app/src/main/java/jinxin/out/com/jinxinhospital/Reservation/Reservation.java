package jinxin.out.com.jinxinhospital.Reservation;

/**
 * Created by Administrator on 2017/8/17.
 */

public class Reservation {
    public  int id;//
    public  int customerId;//客户Id
    public  int departmentId;//部门Id
    public  String statusName;//预约时间
    public  String departmentName;
    public  String reservationTime;//预约时间
    public  int status;//预约状态，1：申请，2：婉拒，3：接受，4：完成
    public  String createTime;//创建时间
    public  String updateTime;//更新时间
}
