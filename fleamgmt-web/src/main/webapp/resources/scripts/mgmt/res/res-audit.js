/**
 * @Description res-audit.js
 * 
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月17日
 */
define(function(require, exports, module) {

	// 获取物品未审核
	ReqUrlMap.put("findNotAuditRes", "res!findNotAuditRes.flea");

	// 审核物品通过
	ReqUrlMap.put("auditResSuccess", "res!auditResSuccess.flea");

	// 审核物品不通过
	ReqUrlMap.put("auditResFail", "res!auditResFail.flea");

	var res = require('./res');//加载公共模块res.js


	exports.init = function(){

		// 初始化开始日期和结束日期控件
		res.ResModule.initDateWidgets('.start-date', '.end-date');
		// 初始化搜索事件
		ResAuditModule.initSearch();
	}

	var ResAuditModule = {
		/**
		 *  初始化搜索事件
		 */
		initSearch	: function(){
			BindEvent.bindResAuditSearchEvent();
		},
		/**
		 *  查找没有审核的物品
		 */
		findNotAuditRes	: function(pageIndex){

			var startDate = $("#start_date").val();
			var endDate = $("#end_date").val();
			var currentPage = pageIndex + 1;
			var pageNum = 10;

			var cmd = {
				"resSearch.startDate" : startDate,
				"resSearch.endDate"	  : endDate,
				"resSearch.currentPage" : currentPage,
				"resSearch.pageNum"		: pageNum				
			}

			$("#audit_table_tbody").html(Huazie.msg.tbodyProgressText(8, "物品信息正在加载中..."));

			Huazie.ajax.getJson(ReqUrlMap.get("findNotAuditRes"), cmd, function(data, status) {

				var result = data;

				if(status){
					var notAuditResInfo = result.notAuditResInfo;
					//第一页会显示总数目
					if(notAuditResInfo && notAuditResInfo.count && notAuditResInfo.count > 0){
						$("#audit_pagination").pagination(notAuditResInfo.count, {
						    num_edge_entries: 2,
						    num_display_entries: 4,
						    callback: function(index){

								currentPage = index + 1;

								cmd["resSearch.currentPage"] = currentPage;

								$("#audit_table_tbody").html(Huazie.msg.tbodyProgressText(8, "物品信息正在加载中..."));

								Huazie.ajax.getJson(ReqUrlMap.get("findNotAuditRes"), cmd, function(data, status) {

									var result = data;

									if(status){

										window.resPicInfo = {};

										var notAuditResInfo = result.notAuditResInfo;

										Huazie.tpl.loadTemp($("#audit_table_tbody"), "#tpl_table_not_audit_res", notAuditResInfo.data);

										$.each(notAuditResInfo.data, function(index, element){
											
											var resId = element["RES_ID"];// 获取物品编号

											var resPic = element["RES_PIC"];// 获取物品图片信息

											window.resPicInfo[resId] = resPic;

										});

										BindEvent.bindResAuditOperEvent();// 绑定物品审核操作事件

										res.ResModule.initResPicView("a[id^='audit_pic_']", "#audit_res_pic", "#audit_res_description");// 绑定图片查看按钮

									}else{
										Huazie.dialog.tips("warning", result.retMess, 2);
									}

								});
									
								$("#audit_res_pic").html("");// 清空图片区域的显示
								$("#audit_res_description").html("");// 清空物品描述信息
						    },
							items_per_page : pageNum,
							prev_text : "上一页",
							next_text : "下一页"
						});
					}else{
						//应该就是从第一页就没有数据的情况
						if(cmd["resSearch.currentPage"] == 1){
							if(notAuditResInfo && notAuditResInfo.data){
								Huazie.tpl.loadTemp($("#audit_table_tbody"), "#tpl_table_not_audit_res", notAuditResInfo.data);
							}
						}
					}

				}else{
					Huazie.dialog.tips("warning", result.retMess, 2);
				}

			});

		},
		/**
		 * 	审核通过
		 */
		auditResSuccess : function(resId){

			var cmd  = {
				"resAudit.resId" : resId
			}

			Huazie.ajax.postJson(ReqUrlMap.get("auditResSuccess"), cmd, function(data, status) {

				var result = data;

				if(status){

					if(result.retCode == "Y"){
						Huazie.dialog.tips("info", result.retMess, 1);
						// 查找没有审核过的物品
						ResAuditModule.findNotAuditRes(0);
						$("#audit_res_pic").html("");// 清空图片区域的显示
						$("#audit_res_description").html("");// 清空物品描述信息
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
		 * 	绑定查询事件
		 */
		bindResAuditSearchEvent	: function(){
			$("#search").off("click");
			$("#search").on("click", function(){
				// 查找没有审核过的物品
				ResAuditModule.findNotAuditRes(0);
				$("#audit_res_pic").html("");// 清空图片区域的显示
				$("#audit_res_description").html("");// 清空物品描述信息
			});
		},
		/**
		 * 	绑定物品审核操作事件
		 */
		bindResAuditOperEvent	: function(){
			// 审核通过
			$("a[id^='audit_success_']").off("click");
			$("a[id^='audit_success_']").on("click", function(){
				var resId = $(this).find("input[name=RES_ID]").val();

				Huazie.dialog.confirm("question", "亲，您确定通过审核吗？", function(){
					// 开始进行审核通过操作，后台需要记录操作日志
					ResAuditModule.auditResSuccess(resId);
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
				var resId = $(this).find("input[name=RES_ID]").val();
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

		}


	};

});