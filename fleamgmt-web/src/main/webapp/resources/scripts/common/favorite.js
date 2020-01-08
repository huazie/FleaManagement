/**
 * @Description 菜单收藏夹
 * 
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月10日
 */
define(function(require, exports, module) {
	
	//收藏菜单URL
	ReqUrlMap.put("collectMenu", "menuFavorite!collect.flea");
	//取消收藏菜单URL
	ReqUrlMap.put("cancelMenuFavorite", "menuFavorite!cancel.flea");
	//获取用户收藏菜单
	ReqUrlMap.put("findMenuFavorite", "menuFavorite!find.flea");
	//用户是否已经收藏某菜单
	ReqUrlMap.put("isFavorite", "menuFavorite!isFavorite.flea");
	
	/**
	 * 收藏菜单
	 */
	exports.collectMenu = function(menuCode, callback){
		MenuFavorite.collect(menuCode, callback);
	}
	
	/**
	 * 取消菜单收藏
	 */
	exports.cancelMenuFavorite = function(menuCode, callback){
		MenuFavorite.cancel(menuCode, callback);
	}
	
	/**
	 * 获取用户收藏菜单
	 */
	exports.findMenuFavorite = function(callback){
		MenuFavorite.find(callback);
	}
	
	exports.isFavorite = function(menuCode, callback){
		MenuFavorite.isFavorite(menuCode, callback);
	}
	
	/**
	 * 菜单收藏夹
	 */
	var MenuFavorite = {
		/**
		 * 收藏菜单
		 */
		collect	: function(menuCode, callback){
			// 收藏菜单
			Huazie.ajax.postJson(ReqUrlMap.get("collectMenu"), {"menuCode" : menuCode}, function(data, status) {
				
				if(status){
					if(callback){
						callback(data);
					}
				}else{
					Huazie.dialog.tips("warning", data.retMess, 2);
				}

			});
		},
		/**
		 * 取消菜单
		 */
		cancel : function(menuCode, callback){
			// 取消收藏菜单
			Huazie.ajax.postJson(ReqUrlMap.get("cancelMenuFavorite"), {"menuCode" : menuCode}, function(data, status) {
				
				if(status){
					if(callback){
						callback(data);
					}
				}else{
					Huazie.dialog.tips("warning", data.retMess, 2);
				}

			});
		},
		/**
		 * 获取用户收藏菜单
		 */
		find : function(callback){
			// 获取用户收藏菜单
			Huazie.ajax.getJson(ReqUrlMap.get("findMenuFavorite"), function(data, status) {
				
				if(status){
					if(callback){
						callback(data);
					}
				}else{
					Huazie.dialog.tips("warning", data.retMess, 2);
				}

			});
		},
		/**
		 * 查看该菜单是否已经收藏了
		 */
		isFavorite : function(menuCode, callback){
			// 获取用户收藏菜单
			Huazie.ajax.getJson(ReqUrlMap.get("isFavorite"), {"menuCode" : menuCode}, function(data, status) {
				
				if(status){
					if(callback){
						callback(data);
					}
				}else{
					Huazie.dialog.tips("warning", data.retMess, 2);
				}

			});
		}
	}
	
});