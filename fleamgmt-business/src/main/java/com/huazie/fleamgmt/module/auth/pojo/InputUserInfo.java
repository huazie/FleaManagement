package com.huazie.fleamgmt.module.auth.pojo;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * <p> 用户信息 业务入参 </p>
 *
 * <p> 同时服务于「用户注册」和「用户变更」两个页面：
 * 注册时 accountCode/accountPwd 必填（框架据此新建 Flea用户 + Flea账户）；
 * 变更时 userId/accountId 必填，用于定位待变更的用户与账户数据。 </p>
 *
 * <p> 生效日期、失效日期以 {@code yyyy-MM-dd} 字符串接收，由业务层统一解析，
 * 避免依赖 Spring MVC 默认的日期绑定格式。 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class InputUserInfo implements Serializable {

    private static final long serialVersionUID = 4620145793841265761L;

    private Long userId; // 用户编号

    private Long accountId; // 账户编号

    private String accountCode; // 账号

    private String accountPwd; // 密码

    private String userName; // 昵称

    private Integer userSex; // 性别（1：男 2：女 3：其他）

    private String userEmail; // 邮箱

    private String userPhone; // 手机

    private String userAddress; // 住址

    private Long groupId; // 用户组编号

    private Integer state; // 状态（0：删除，1：正常，2：禁用，3：待审核）

    private String effectiveDate; // 生效日期（yyyy-MM-dd）

    private String expiryDate; // 失效日期（yyyy-MM-dd）

    private String remarks; // 备注

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountPwd() {
        return accountPwd;
    }

    public void setAccountPwd(String accountPwd) {
        this.accountPwd = accountPwd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserSex() {
        return userSex;
    }

    public void setUserSex(Integer userSex) {
        this.userSex = userSex;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
