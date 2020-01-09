package com.huazie.fleamgmt.constant;

/**
 * <p> 常量定义 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public interface FleaMgmtConstants {

    /**
     * <p> 用户session常量 </p>
     *
     * @since 1.0.0
     */
    interface SessionConstants {
        /**
         * <p> 跳蚤管家系统登录的账户 </p>
         */
        String SESSION_ACCOUNT = "Account";
        /**
         * <p> 跳蚤管家系统登录的账户 </p>
         */
        String SESSION_FLEAER_ACCOUNT = "FleaerAccount";
        /**
         * <p> 用户注册封装类 </p>
         */
        String SESSION_USERREGISTER = "UserRegister";
        /**
         * <p> 用户验证状态封装类 </p>
         */
        String SESSION_USERVALIDATE = "UserValidate";
        /**
         * <p> 系统发送的短信验证码 </p>
         */
        String SESSION_SMS_VALIDATE_CODE = "SmsValidateCode";
        /**
         * <p> 系统发送的短信验证码 </p>
         */
        String SESSION_EMAIL_VALIDATE_CODE = "EMailValidateCode";
        /**
         * <p> 登录时的验证码 </p>
         */
        String SESSION_LOGIN_CODE = "LoginCode";
        /**
         * <p> 用途（记录跳转到信息页面的标志） </p>
         */
        String SESSION_USES = "Uses";
        /**
         * <p> 一次会话中用户上传文件的集合 </p>
         */
        String SESSION_UPLOAD_FILES = "UploadFiles";
    }

    /**
     * <p> 公共跳转信息常量 </p>
     *
     * @since 1.0.0
     */
    interface CommonJumpToInfoConstants {
        /**
         * <p> 注册跳转至公用信息页面 </p>
         */
        int COMMON_INFO_REGISTER_JUMP = 1;
        /**
         * <p> 注册跳转至公用信息页面的返回码 </p>
         */
        String COMMON_CODE_REGISTER_JUMP = "Y_01";
        /**
         * <p> 注册验证跳转至公用信息页面 </p>
         */
        int COMMON_INFO_REGISTER_VALIDATE = 2;
        /**
         * <p> 注册验证跳转至公用信息页面的返回码 </p>
         */
        String COMMON_CODE_REGISTER_VALIDATE = "Y_02";
        /**
         * <p> 忘记密码跳转至公用信息页面 </p>
         */
        int COMMON_INFO_FORGET_PSW = 3;
        /**
         * <p> 忘记密码跳转至公用信息页面的返回码 </p>
         */
        String COMMON_CODE_FORGET_PSW = "Y_03";
        /**
         * <p> 密码找回验证跳转至公用信息页面 </p>
         */
        int COMMON_INFO_FIND_PSW = 4;
        /**
         * <p> 密码找回验证跳转至公用信息页面的返回码 </p>
         */
        String COMMON_CODE_FIND_PSW = "Y_04";
        /**
         * <p> 重置密码跳转至公用信息页面 </p>
         */
        int COMMON_INFO_RESET_PSW_JUMP = 5;
        /**
         * <p> 重置密码跳转至公用信息页面的返回码 </p>
         */
        String COMMON_CODE_RESET_PSW_JUMP = "Y_05";
        /**
         * <p> 重置密码验证跳转至公用信息页面 </p>
         */
        int COMMON_INFO_VALIDATE_RESET_PSW = 6;
        /**
         * <p> 重置密码验证跳转至公用信息页面的返回码 </p>
         */
        String COMMON_CODE_VALIDATE_RESET_PSW = "Y_06";
    }

    /**
     * <p> 返回码常量 </p>
     *
     * @since 1.0.0
     */
    interface ReturnCodeConstants {
        /**
         * <p> 操作正确 </p>
         */
        String RETURN_CODE_Y = "Y";
        /**
         * <p> 操作错误 </p>
         */
        String RETURN_CODE_N = "N";
        /**
         * <p> 其他操作情况 </p>
         */
        String RETURN_CODE_X = "X";
    }

    /**
     * <p> 业务常量 </p>
     *
     * @since 1.0.0
     */
    interface BusinessIdConstants {
        /**
         * <p> 物品审核 </p>
         */
        long BUSINESS_RES_AUDIT = 1000000001L;
        /**
         * <p> 跳主审核 </p>
         */
        long BUSINESS_FLEAER_AUDIT = 1000000002L;
    }

}
