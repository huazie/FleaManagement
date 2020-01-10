package com.huazie.fleamgmt.module.login.pojo;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p> 跳主登录封装类 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class FleaerLogin implements Serializable {

    private static final long serialVersionUID = 858246799576975751L;

    private String name;        // 用户名（邮箱或手机号）
    private String password;    // 密码

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
