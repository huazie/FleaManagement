/**
 * 物品操作公共模块
 */
define(function(require, exports, module) {

	// 获取物品类别
	ReqUrlMap.put("resCategory", "res!category.flea");

	// 侧边栏快捷菜单tpl加载
	TplUrlMap.put("res", "fleaer/res/res.tpl");

	exports.ResModule = {
		/**
		 *	初始化日期控件
		 */
		initDateWidgets	: function(start, end){
			BindEvent.bindDateWidgetsEvent(start, end);
		},
		/**
		 *	加载物品类型
		 */
		initResPicView	: function(picBtnId, resPicId, resDescId){
			BindEvent.bindResPicViewEvent(picBtnId, resPicId, resDescId);
		},
		/**
		 *	加载物品类型
		 */
		loadResType	: function(obj){

			if(parent.window.categoryList){//将categoryList缓存起来
				// 
			  	$(obj).cxSelect({
			    	selects: ['first', 'second', 'third'],
			    	// required: true,
			    	jsonValue: 'v',
			    	firstValue: 0,
			    	data: parent.window.categoryList
			  	});

			}else{
				Huazie.ajax.getJson(ReqUrlMap.get("resCategory"), function(data, status) {
					var result = data;

					parent.window.categoryList = result.categoryList;

					if(status){
						// 自定义选项
					  	$(obj).cxSelect({
					    	selects: ['first', 'second', 'third'],
					    	// required: true,
					    	jsonValue: 'v',
					    	firstValue: 0,
					    	data: result.categoryList
					  	});
					}else{
						Huazie.dialog.tips("warning", result.retMess, 2);
					}
				});
			}
		}

	}

	var BindEvent = {
		/**
		 * 	绑定日期控件
		 */
		bindDateWidgetsEvent	: function(start, end){
			$(start).datetimepicker({
		        language:  'zh-CN',
		        weekStart: 1,
		        todayBtn:  1,
				autoclose: 1,
				todayHighlight: 1,
				startView: 2,
				forceParse: 0,
				showMeridian: 1,
				endDate : new Date() 
		    }).on('changeDate',function(e){ 
				var startTime = e.date; 
				$(end).datetimepicker('setStartDate',startTime); 
			});

		    $(end).datetimepicker({
		        language:  'zh-CN',
		        weekStart: 1,
		        todayBtn:  1,
				autoclose: 1,
				todayHighlight: 1,
				startView: 2,
				forceParse: 0,
				showMeridian: 1,
				endDate : new Date() 
		    }).on('changeDate',function(e){ 
				var endTime = e.date;
				$(start).datetimepicker('setEndDate',endTime); 
			});
		},
		/**
		 * 	绑定图片查看按钮
		 */
		bindResPicViewEvent	: function(picBtnId, resPicId, resDescId){
			// 图片查看按钮
			$(picBtnId).off("click");
			$(picBtnId).on("click", function(){
				var $thiz = $(this);

				//加载物品相关模板
				Huazie.tpl.loadTpl(TplUrlMap.get("res"), function(){
					var resId = $thiz.find("input[name=RES_ID]").val();
					// 这边加载图片的信息
					Huazie.tpl.loadTemp($(resPicId), "#tpl_res_pic", window.resPicInfo[resId]);

					var resDescription = $thiz.find("input[name=RES_DESCRIPTION]").val();
					var res = {
						"RES_DESCRIPTION" : resDescription
					};
					// 这边加载物品描述的信息
					Huazie.tpl.loadTemp($(resDescId), "#tpl_res_description", res);

					// 绑定图片显示事件
					BindEvent.bindResPicEvent();
				});
			});
			$(picBtnId).tooltip({
				hide: {
					effect: "explode",
					delay: 250
				}
			});

		},
		/**
		 * 绑定图片显示事件
		 */
		bindResPicEvent 	: function(){
			var colorbox_params = {
				reposition:true,
				scalePhotos:true,
				scrolling:false,
				previous:'<i class="fa fa-arrow-left"></i>',
				next:'<i class="fa fa-arrow-right"></i>',
				close:'&times;',
				current:'{current} / {total}',
				maxWidth:'100%',
				maxHeight:'100%',
				onOpen:function(){
					document.body.style.overflow = 'hidden';
				},
				onClosed:function(){
					document.body.style.overflow = 'auto';
				},
				onComplete:function(){
					$.colorbox.resize();
				}
			};

			$('.ace-thumbnails [data-rel="colorbox"]').colorbox(colorbox_params);
			$("#cboxLoadingGraphic").html("<i class='fa fa-spinner blue'></i>");
		}


	}


});