/***
 * 鼠标右键插件
 */
(function($) {
	$.fn.extend({
		// 定义鼠标右键方法，接收一个函数参数
		"rightClick" : function(callback) {
			// 调用这个方法后将禁止系统的右键菜单
			$(document).bind('contextmenu', function(e) {
				return false;
			});
			// 为这个对象绑定鼠标按下事件
			$(this).mousedown(function(e) {
				// 如果按下的是右键，则执行函数
				if (3 == e.which) {
					if(callback){
						callback(e.target.parentElement);
					}
				}
			});
		}
	});

})(jQuery);