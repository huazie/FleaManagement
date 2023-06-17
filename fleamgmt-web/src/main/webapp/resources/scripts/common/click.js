/***
 * 鼠标右键插件
 */
(function($) {
	$.fn.extend({
		// 定义鼠标右键方法，接收一个函数参数
		"rightClick" : function(callback) {
			// 调用这个方法后将禁止系统的右键菜单
			$(document).bind('contextmenu', function (e) {
				return false;
			});
			var thiz = this;
			// 绑定鼠标按下事件
			$(thiz).mousedown(function (e) {
				// 如果按下的是右键，则执行函数
				if (3 === e.which) {
					if (callback) {
						callback(e.target.parentElement);
					}
				}
			});

			var timeout;
			// 绑定触摸事件
			$(thiz).on('touchstart', function(e) {
				// 在500毫秒后触发操作
				timeout = setTimeout(function() {
					if (callback) {
						callback(e.target.parentElement);
					}
				}, 500);
			});

			$(thiz).on('touchend', function(e) {
				// 如果在500毫秒内松开手指，则取消操作
				clearTimeout(timeout);
			});

		}
	});

})(jQuery);