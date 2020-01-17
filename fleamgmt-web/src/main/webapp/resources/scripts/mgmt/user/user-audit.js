/**
 * @Description res-audit.js
 * 
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月17日
 */
define(function(require, exports, module) {

	// 获取未审核的跳主信息
	ReqUrlMap.put("findNotAuditUserRealName", "user!findNotAuditUserRealName.flea");

	// 审核跳主通过
	ReqUrlMap.put("auditUserRealNameSuccess", "user!auditUserRealNameSuccess.flea");

	// 审核跳主不通过
	ReqUrlMap.put("auditUserRealNameFail", "user!auditUserRealNameFail.flea");


	exports.init = function(){

		// 初始化开始日期和结束日期控件
		UserAuditModule.initDateWidgets();
		// 初始化搜索事件
		UserAuditModule.initSearch();
	}

	var UserAuditModule = {
		/**
		 *	初始化日期控件
		 */
		initDateWidgets	: function(){
			BindEvent.bindDateWidgetsEvent();
		},
		/**
		 *  初始化搜索事件
		 */
		initSearch	: function(){
			BindEvent.bindUserAuditSearchEvent();
		},
		/**
		 *  查找没有审核的实名用户信息
		 */
		findNotAuditUserRealName	: function(pageIndex){

			var startDate = $("#start_date").val();
			var endDate = $("#end_date").val();
			var currentPage = pageIndex + 1;
			var pageNum = 10;

			var cmd = {
				"userSearch.startDate" 		: startDate,
				"userSearch.endDate"	  	: endDate,
				"userSearch.currentPage" 	: currentPage,
				"userSearch.pageNum"		: pageNum			
			}

			$("#audit_table_tbody").html(Huazie.msg.tbodyProgressText(8, "物品信息正在加载中..."));

			Huazie.ajax.getJson(ReqUrlMap.get("findNotAuditUserRealName"), cmd, function(data, status) {

				var result = data;

				if(status){
					var notAuditUserRealNameInfo = result.notAuditUserRealNameInfo;

					//第一页会显示总数目
					if(notAuditUserRealNameInfo && notAuditUserRealNameInfo.count && notAuditUserRealNameInfo.count > 0){
						$("#audit_pagination").pagination(notAuditUserRealNameInfo.count, {
						    num_edge_entries: 2,
						    num_display_entries: 4,
						    callback: function(index){

								currentPage = index + 1;

								cmd["userSearch.currentPage"] = currentPage;

								$("#audit_table_tbody").html(Huazie.msg.tbodyProgressText(8, "跳主实名信息正在加载中..."));

								Huazie.ajax.getJson(ReqUrlMap.get("findNotAuditUserRealName"), cmd, function(data, status) {

									var result = data;

									if(status){

										window.userRealNamePicInfo = {};

										var notAuditUserRealNameInfo = result.notAuditUserRealNameInfo;

										Huazie.tpl.loadTemp($("#audit_table_tbody"), "#tpl_table_not_audit_user", notAuditUserRealNameInfo.data);

										$.each(notAuditUserRealNameInfo.data, function(index, element){
											
											var userId = element["USER_ID"];// 获取用户编号

											var certCardPic = element["CERT_CARD_PIC"];// 获取证件上传照片信息

											window.userRealNamePicInfo[userId] = certCardPic;

										});

										BindEvent.bindUserAuditOperEvent();// 绑定物品审核操作事件
									}else{
										Huazie.dialog.tips("warning", result.retMess, 2);
									}

								});
						    },
							items_per_page : pageNum,
							prev_text : "上一页",
							next_text : "下一页"
						});
					}else{
						//应该就是从第一页就没有数据的情况
						if(cmd["userSearch.currentPage"] == 1){
							if(notAuditUserRealNameInfo && notAuditUserRealNameInfo.data){
								Huazie.tpl.loadTemp($("#audit_table_tbody"), "#tpl_table_not_audit_user", notAuditUserRealNameInfo.data);
							}
						}
					}

				}else{
					Huazie.dialog.tips("warning", result.retMess, 2);
				}

			});

		},
		/**
		 * 	
		 */
		auditUserRealNameSuccess : function(userId){

			var cmd  = {
				"userAudit.userId" : userId
			}

			Huazie.ajax.postJson(ReqUrlMap.get("auditUserRealNameSuccess"), cmd, function(data, status) {

				var result = data;

				if(status){

					if(result.retCode == "Y"){
						Huazie.dialog.tips("info", result.retMess, 1);
						// 查找没有审核过的跳主信息
						UserAuditModule.findNotAuditUserRealName(0);
						$("#audit_cert_card_pic").html("");// 清空图片区域的显示
					}else if(result.retCode == "N"){
						Huazie.dialog.tips("warning", result.retMess, 2);
					}

				}else{
					Huazie.dialog.tips("warning", result.retMess, 2);
				}

			});

		}

	};

	var BindEvent = {
		/**
		 * 	绑定日期控件
		 */
		bindDateWidgetsEvent	: function(){
			$('.start-date').datetimepicker({
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
				$('.end-date').datetimepicker('setStartDate',startTime); 
			});

		    $('.end-date').datetimepicker({
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
				$('.start-date').datetimepicker('setEndDate',endTime); 
			});
		},
		/**
		 * 	绑定查询事件
		 */
		bindUserAuditSearchEvent	: function(){
			$("#search").off("click");
			$("#search").on("click", function(){
				// 查找没有审核过的物品
				UserAuditModule.findNotAuditUserRealName(0);
				$("#audit_cert_card_pic").html("");// 清空图片区域的显示
			});
		},
		/**
		 * 	绑定物品审核操作事件
		 */
		bindUserAuditOperEvent	: function(){
			// 审核通过
			$("a[id^='audit_success_']").off("click");
			$("a[id^='audit_success_']").on("click", function(){
				var userId = $(this).find("input[name=USER_ID]").val();

				Huazie.dialog.confirm("question", "亲，您确定通过审核吗？", function(){
					// 开始进行审核通过操作，后台需要记录操作日志
					UserAuditModule.auditUserRealNameSuccess(userId);
				});

			});
			$("a[id^='audit_success_']").tooltip({
				show: {
					effect: "slideDown",
					delay: 250
				},
				hide: {
					effect: "slideUp",
					delay: 250
				}
			});

			//审核未通过
			$("a[id^='audit_fail_']").off("click");
			$("a[id^='audit_fail_']").on("click", function(){
				var userId = $(this).find("input[name=USER_ID]").val();
				// 先弹出审核未通过的记录表单（未通过原因等）## 这个先过，可能后台需要新增一个审核未通过表，记录未通过的记录表单，并记下操作日志
				Huazie.dialog.confirm("question", "亲，您确定审核未通过吗？", function(){

				});
			});
			$("a[id^='audit_fail_']").tooltip({
				show: {
					effect: "slideDown",
					delay: 250
				},
				hide: {
					effect: "explode",
					delay: 250
				}
			});

			// 图片查看按钮
			$("a[id^='audit_pic_']").off("click");
			$("a[id^='audit_pic_']").on("click", function(){
				var userId = $(this).find("input[name=USER_ID]").val();
				// 这边加载图片的信息
				Huazie.tpl.loadTemp($("#audit_cert_card_pic"), "#tpl_cert_card_pic", window.userRealNamePicInfo[userId]);

				// 绑定图片显示事件
				BindEvent.bindUserRealNamePicEvent();
			});
			$("a[id^='audit_pic_']").tooltip({
				hide: {
					effect: "explode",
					delay: 250
				}
			});

		},
		/**
		 * 绑定图片显示事件
		 */
		bindUserRealNamePicEvent 	: function(){
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


	};

});