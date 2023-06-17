package com.huazie.fleamgmt.springmvc.signup.web;

import com.huazie.fleaframework.auth.common.pojo.user.register.FleaUserRegisterPOJO;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaUserModuleSV;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.springmvc.base.web.BusinessController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * <p> 用户注册 Controller </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class FleamgmtSignUpController extends BusinessController {

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(FleamgmtSignUpController.class);

    private IFleaUserModuleSV fleaUserModuleSV;

    @Resource(name = "fleaUserModuleSV")
    public void setFleaUserModuleSV(IFleaUserModuleSV fleaUserModuleSV) {
        this.fleaUserModuleSV = fleaUserModuleSV;
    }

    @PostMapping("fleamgmtSignUp!signup.flea")
    @ResponseBody
    public OutputCommonData signup(FleaUserRegisterPOJO fleaUserRegisterPOJO) throws CommonException {

        OutputCommonData result = new OutputCommonData();

        fleaUserModuleSV.register(fleaUserRegisterPOJO); // 默认注册用户状态为 3:待审核

        result.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        result.setRetMess("亲，恭喜您注册成功呦");

        return result;
    }

}
