/**
 * @Description console.js
 * 
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月10日
 */
define(function(require, exports, module) {
	
	exports.init = function(){
		
		ConsoleModule.loadMenuFavorite();
		
	};
	/**
	 * 控制台模块
	 */
	var ConsoleModule = {
		/**
		 * 加载收藏夹
		 */
		loadMenuFavorite : function(){
			require.async("../common/favorite", function(menuFavorite){
				menuFavorite.findMenuFavorite(function(data){
					Huazie.tpl.loadTpl(TplUrlMap.get("commonDiv"),function(){
						Huazie.tpl.loadTemp($("#menufavorite"), "#tpl_common_div_a", data);
						BindEvent.bindMenuFavoriteEvent();
					});
				});
			});
		}
	}
	
	var BindEvent = {
		/**
		 * 绑定收藏夹点击事件
		 */
		bindMenuFavoriteEvent : function(){
			$("a[id^='favorite_']").on("click", function(){
				var menuCode = $(this).attr("name");
				
				require.async("../common/menu", function(fleaerMenu){
					parent.window.currentMenu = {};
					fleaerMenu.open1(menuCode);//打开初始化选中的菜单
				});
				
			});
		}
	}
	
});