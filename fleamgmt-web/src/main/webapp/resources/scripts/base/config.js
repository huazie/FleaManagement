/**
 * @Description config.js
 * 
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月3日
 */

//window.USER = {};//登录的用户
window.currentMenu = {};	// 当前选中的菜单

/**
 * request url Map
 */
var ReqUrlMap = (function(){
	// 请求前缀
	var reqUrlPrefix = "business/";
	// 请求URL
	var reqUrl = {
		"fleaMgmtHome" : reqUrlPrefix + "jump!goToIndex.flea",	//跳蚤管家主页
		"fleaMgmtMenu" : "jump!goToMenu.flea?menuCode="			//指定菜单页
	};
	
	return {
		put : function (key, value){
			reqUrl[key] = reqUrlPrefix + value;
		},
		get : function(key){
			return reqUrl[key];
		},
		getReqUrlPrefix	: function(){
			return reqUrlPrefix;
		}
	}
	
})(jQuery);

/**
 * tpl Map
 */
var TplUrlMap = (function(){
	
	// tpl前缀
	var tplUrlPrefix = ["resources/template/","../resources/template/"];
	
	// tplUrl
	var tplUrl = [{
		"dialog"	: tplUrlPrefix[0] + "dialog/dialog.tpl",	// 通用弹出框tpl模板
		"commonLi"	: tplUrlPrefix[0] + "common/common_li.tpl",	// 通用li模板
		"commonDiv"	: tplUrlPrefix[0] + "common/common_div.tpl",// 通用div模板
		"tree" 		: tplUrlPrefix[0] + "tree/tree.tpl"			// 通用树模板
	},{
		"dialog"	: tplUrlPrefix[1] + "dialog/dialog.tpl", 	// 通用弹出框tpl模板
		"commonLi"	: tplUrlPrefix[1] + "common/common_li.tpl",	// 通用li模板
		"commonDiv"	: tplUrlPrefix[1] + "common/common_div.tpl",// 通用div模板
		"tree" 		: tplUrlPrefix[1] + "tree/tree.tpl"			// 通用树模板
	}];
	
	return {
		put	: function (key, value){
			tplUrl[0][key] = tplUrlPrefix[0] + value;
			tplUrl[1][key] = tplUrlPrefix[1] + value;
		},
		get : function (key){
			var conf = 0;
			if(Huazie.browser.isBusiRequest()){
				conf = 1;
			}
			return tplUrl[conf][key];
		}
	}
	
})(jQuery);

/**
 * image Map
 */
var ImgUrlMap = (function(){
	
	// image 前缀
	var imgUrlPrefix  = ["resources/images/", "../resources/images/"];
	
	// imgUrl
	var imgUrl = [{
		"user"	: imgUrlPrefix[0] + "user/"	// 用户图像路径
	},{
		"user"	: imgUrlPrefix[1] + "user/" // 用户图像路径
	}];
	
	return {
		put	: function (key, value){
			imgUrl[0][key] = imgUrlPrefix[0] + value;
			imgUrl[1][key] = imgUrlPrefix[1] + value;
		},
		get : function (key){
			var conf = 0;
			if(Huazie.browser.isBusiRequest()){
				conf = 1;
			}
			return imgUrl[conf][key];
		}
	}
	
})(jQuery);

/**
 * seajs Map
 */
var SeaJsUrlMap = (function(){
	
	// seajs 前缀
	var seaJsUrlPrefix  = ["./resources/scripts/", "../resources/scripts/"];
	
	// imgUrl
	var seaJsUrl = [{
		"login"	: seaJsUrlPrefix[0] + "mgmt/login",								// 用户登录模块js
		"index" : seaJsUrlPrefix[0] + "mgmt/index",								// 首页模块js
		"console" : seaJsUrlPrefix[0] + "mgmt/console",							// 控制台js
		"menu"	: seaJsUrlPrefix[0] + "common/menu",							// 公共菜单模块js
		"favorite" : seaJsUrlPrefix[0] + "common/favorite",						// 菜单收藏夹js
		"authMenu"	: seaJsUrlPrefix[0] + "auth/function/menu/auth-menu",		// 菜单管理模块js
		"uploadRes"	: seaJsUrlPrefix[0] + "mgmt/res/res-upload",				// 上传物品模块js
		"auditRes"	: seaJsUrlPrefix[0] + "mgmt/res/res-audit",					// 物品审核模块js
		"searchRes"	: seaJsUrlPrefix[0] + "mgmt/res/res-search",				// 物品浏览模块js
		"auditUser"	: seaJsUrlPrefix[0] + "mgmt/user/user-audit",				// 跳主审核模块js
	},{
        "login"	: seaJsUrlPrefix[1] + "mgmt/login",								// 用户登录模块js
        "index" : seaJsUrlPrefix[1] + "mgmt/index",								// 首页模块js
        "console" : seaJsUrlPrefix[1] + "mgmt/console",							// 控制台js
        "menu"	: seaJsUrlPrefix[1] + "common/menu",							// 公共菜单模块js
        "favorite" : seaJsUrlPrefix[1] + "common/favorite",						// 菜单收藏夹js
        "authMenu"	: seaJsUrlPrefix[1] + "auth/function/menu/auth-menu",		// 菜单管理模块js
        "uploadRes"	: seaJsUrlPrefix[1] + "mgmt/res/res-upload",				// 上传物品模块js
        "auditRes"	: seaJsUrlPrefix[1] + "mgmt/res/res-audit",					// 物品审核模块js
        "searchRes"	: seaJsUrlPrefix[1] + "mgmt/res/res-search",				// 物品浏览模块js
        "auditUser"	: seaJsUrlPrefix[1] + "mgmt/user/user-audit",				// 跳主审核模块js
	}];
	
	return {
		put	: function (key, value){
			seaJsUrl[0][key] = seaJsUrlPrefix[0] + value;
			seaJsUrl[1][key] = seaJsUrlPrefix[1] + value;
		},
		get : function (key){
			var conf = 0;
			if(Huazie.browser.isBusiRequest()){
				conf = 1;
			}
			return seaJsUrl[conf][key];
		}
	}
	
})(jQuery);

/**
 * 判断是否包含
 * 
 * @param 子串
 */
String.prototype.contains = function(str){
	return this.indexOf(str) > 0;
}

/**
 * 去掉首尾空格
 */
String.prototype.trim = function () {
	return this.replace(/^\s\s*/, '' ).replace(/\s\s*$/, '' );
}


