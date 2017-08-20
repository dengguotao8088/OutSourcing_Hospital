package jinxin.out.com.jinxinhospital.JsonModule;

import java.net.URL;

import retrofit.http.PUT;

/**
 * Created by Administrator on 2017/8/5.
 */

public class Constants {

    public static final String URL_PREFIX = "http://client.mind-node.com/client/";

    public static final String GET_BANNERS_PATH = URL_PREFIX + "api/banner/list";

    //根据客户Id获取本次检查(当日消费)
    public static final String GET_CURRENT_PAGE = URL_PREFIX + "api/consumption_record/real_list?";

    //根据客户Id获取现场排号情况
    public static final String GET_FEILD_QUEUE_LIST = URL_PREFIX + "api/customer/field_queue/list?";

    //获取理疗类型列表
    public static final String GET_PHYSIOTHERPY_TYPE_LISY = URL_PREFIX + "api/physiotherapy_type/list?";

    //获取理疗内容
    public static final String GET_PHYSIOTHERPY_CONTENT = URL_PREFIX + "api/physiotherapy/get?";

    //获取理疗列表
    public static final String GET_PHYSIOTHERPY_LIST = URL_PREFIX + "api/physiotherapy/list?";

    //获取部门列表
    public static final String GET_DEPARTMENT_LIST = URL_PREFIX + "api/department/list?";

    //根据客户Id获取本次检查(当日消费)
    public static final String GET_CONSUMPTION_REAL_LIST = URL_PREFIX + "api/consumption_record/real_list?";

    //'VIP特权
    public static final String GET_VIP_PRIVILEGE = URL_PREFIX + "api/vip/get_privilege?";

    //获取VIP消息
    public static final String GET_VIP_MESSAGE_LIST = URL_PREFIX + "api/vip/list_message?";

    //根据Id获取详情
    public static final String GET_VIP_MESSAGE_CONTENT = URL_PREFIX + "api/vip/get?";

    //获取喇叭通知
    public static final String GET_NOTICE = URL_PREFIX + "api/notice/list";

    public static final String REQUEST_IMPEMENTATIONVOID = URL_PREFIX + "api/customer/implementationVoid/update?";

    //客户登录
    public static final String LOGIN_URL = "http://client.mind-node.com/client/api/customer/login?";
    //退出登录
    public static final String LOGIN_OUT_URL = "http://client.mind-node.com/client/api/customer/logout?";

    //根据Id获取客户
    public static final String GET_CUSTOMER_WITH_ID = "http://client.mind-node.com/client/api/customer/get?";
    //根据Id获取员工详情
    public static final String GET_EMPLOYEE_WITH_ID = "http://client.mind-node.com/client/api/emp/get?";
    //获取员工列表
    public static final String GET_EMPLOYEE_LIST = "http://client.mind-node.com/client/api/emp/list?";

    //获取新闻列表
    public static final String GET_NEWS_LIST = "http://client.mind-node.com/client/api/news/list?";
    //根据Id获取新闻详情
    public static final String GET_NEWS_CONTENT_WITH_ID = "http://client.mind-node.com/client/api/news/get?";

    //客户注册
    public static final String CUSTOMER_REGIST = "http://client.mind-node.com/client/api/customer/register?";

    //根据Id获取消费记录
    public static final String GET_CONSUMPTIONRECORD_WITH_ID = "http://client.mind-node.com/staff/api/consumption_record/get?";
    //修改消费记录
    public static final String UPDATE_CONSUMPTIONRECORD = "http://client.mind-node.com/client/api/consumption_record/update?";
    //根据购买记录Id获取消费记录列表
    public static final String GET_CONSUMPTIONRECORD_LIST_WITH_ID = "http://client.mind-node.com/client/api/consumption_record/list?";

    //根据Id获取客户知情同意书
    public static final String GET_CONSENT_WITH_ID = "http://client.mind-node.com/client/api/customer_informed_consent_record/get?";
    //根据客户Id获取客户知情同意书列表
    public static final String GET_CONSENT_LIST_WITH_ID = "http://client.mind-node.com/client/api/customer_informed_consent_record/list?";

    //根据客户Id获取购买记录
    public static final String GET_PURCHASE_WITH_ID = "http://client.mind-node.com/client/api/purchase_record/list?";

    //获取部门列表
    public static final String GET_DERP_LIST = "http://client.mind-node.com/client/api/department/list?";

    //根据客户Id获取推送记录
    public static final String GET_PUSH_LIST_WITH_ID = "http://client.mind-node.com/client/api/push_message_record/list?";
    //根据Id获取推送记录详情
    public static final String GET_PUSH_CONTENT_WITH_ID = "http://client.mind-node.com/client/api/push_message_record/get?";

    //根据客户Id获取预约历史
    public static final String GET_RESER_WITH_ID = "http://client.mind-node.com/client/api/reservation/list?";
    //添加预约申请
    public static final String ADD_RESER = "http://client.mind-node.com/client/api/reservation/save?";
}
