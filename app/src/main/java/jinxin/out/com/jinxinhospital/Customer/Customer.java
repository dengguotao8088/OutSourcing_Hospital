package jinxin.out.com.jinxinhospital.Customer;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/7.
 */

public class Customer implements Serializable {

    public int id;//
    public String name;//客户姓名
    public int sex;//性别，1：男，2：女，默认1
    public String birthday;/* 生日 */
    public String mobile;//电话
    public String address;//住址
    public int allergy;//有无过敏史，1：无，2：有，默认1
    public int disease;//有无疾病史，1：无，2：有，默认1
    public int diagnosticAnalysisId;//诊断分析Id
    public int archivesTypeId;//档案类型
    public int consumptionTypeId;//消费类型Id
    public int archivesStatus;//档案状态，3：待审，1：正常，2：停用
    public String createTime;//创建时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAllergy() {
        return allergy;
    }

    public void setAllergy(int allergy) {
        this.allergy = allergy;
    }

    public int getDisease() {
        return disease;
    }

    public void setDisease(int disease) {
        this.disease = disease;
    }

    public int getDiagnosticAnalysisId() {
        return diagnosticAnalysisId;
    }

    public void setDiagnosticAnalysisId(int diagnosticAnalysisId) {
        this.diagnosticAnalysisId = diagnosticAnalysisId;
    }

    public int getArchivesTypeId() {
        return archivesTypeId;
    }

    public void setArchivesTypeId(int archivesTypeId) {
        this.archivesTypeId = archivesTypeId;
    }

    public int getConsumptionTypeId() {
        return consumptionTypeId;
    }

    public void setConsumptionTypeId(int consumptionTypeId) {
        this.consumptionTypeId = consumptionTypeId;
    }

    public int getArchivesStatus() {
        return archivesStatus;
    }

    public void setArchivesStatus(int archivesStatus) {
        this.archivesStatus = archivesStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
