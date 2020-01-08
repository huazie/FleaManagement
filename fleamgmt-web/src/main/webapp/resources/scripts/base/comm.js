/**
 * @Description comm.js
 * 
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月10日
 */

$(function(){
	
	//判断是否该菜单已经被收藏了
	Comm.isFavorite();

});

Comm = {
	/**
	 * 添加收藏
	 */
	addMenuFav : function(){
		
		var $afav = $(".page-header-fav .page-header-fav-a");
		
		var menuCode = Huazie.browser.getFrameParamter("menuCode");
		
		//Huazie.log(menuCode);
		
		//###start load favorite.js
		seajs.use(SeaJsUrlMap.get("favorite"), function(menuFavorite){
			
			//如果还没有收藏
			if($afav.find("span").html() == "收藏"){
				
				menuFavorite.collectMenu(menuCode, function(data){
					if(data.retCode == "Y"){
						$afav.find("i").removeClass("fa-star-o").addClass("fa-star");
						$afav.find("span").html("已收藏");
					}else{
						// 收藏失败
						
					}
				});
				
			}else{//已经收藏
				menuFavorite.cancelMenuFavorite(menuCode, function(data){
					if(data.retCode == "Y"){
						$afav.find("i").removeClass("fa-star").addClass("fa-star-o");
						$afav.find("span").html("收藏");
					}else{
						// 取消收藏失败
					}
				});

			}
		});//###end load favorite.js
		
	},
	isHome	: function(menuCode){
		if(menuCode == "index"){
			return true;
		}else{
			return false;
		}
	},
	/**
	 * 获取用户某菜单收藏信息，如果已经收藏过了，界面显示已经收藏
	 */
	isFavorite : function(){
		
		var $afav = $(".page-header-fav .page-header-fav-a");
		
		var menuCode = Huazie.browser.getFrameParamter("menuCode");

		if(Comm.isHome(menuCode)){
			return;
		}
		
		//###start load favorite.js
		seajs.use(SeaJsUrlMap.get("favorite"), function(menuFavorite){
			
			menuFavorite.isFavorite(menuCode, function(data){
				if(data.retCode == "Y"){
					$afav.find("i").removeClass("fa-star-o").addClass("fa-star");
					$afav.find("span").html("已收藏");
				}else if(data.retCode == "N"){
					$afav.find("i").removeClass("fa-star").addClass("fa-star-o");
					$afav.find("span").html("收藏");
				}else{
					Huazie.dialog.tips("warning", data.retMess, 2);
				}
			});
			
		});//###end load favorite.js
		
	},
	/**
	 * 打开菜单
	 */
	openMenu : function(menuCode){

		//###start load auth-menu.js
		seajs.use(SeaJsUrlMap.get("menu"), function(fleaerMenu) {
			fleaerMenu.open(menuCode);
		});//###end load auth-menu.js

	}


	
}
