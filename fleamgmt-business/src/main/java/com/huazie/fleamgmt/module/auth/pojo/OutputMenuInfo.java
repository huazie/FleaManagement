package com.huazie.fleamgmt.module.auth.pojo;

import com.huazie.frame.common.pojo.OutputCommonData;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

/**
 * <p> 菜单信息 业务出参 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class OutputMenuInfo extends OutputCommonData {

	private static final long serialVersionUID = -8033720125810548693L;

	private List<Map<String,Object>> menuList;	// 所有可访问菜单信息

	public List<Map<String, Object>> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<Map<String, Object>> menuList) {
		this.menuList = menuList;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
