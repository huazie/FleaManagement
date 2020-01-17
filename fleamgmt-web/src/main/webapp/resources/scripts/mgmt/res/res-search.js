/**
 * @Description res-upload.js
 * 
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月17日
 */
define(function(require, exports, module) {

	// 获取跳主上传物品
	ReqUrlMap.put("findAllRes", "res!findAllRes.flea");

	var res = require('./res');//加载公共模块res.js

	exports.init = function(){

		// 初始化开始日期和结束日期控件
		res.ResModule.initDateWidgets('.start-date', '.end-date');

		// 加载物品类型
		res.ResModule.loadResType('#res_type');

		// 初始化搜索事件
		ResSearchModule.initSearch();

	}

	var ResSearchModule = {
		/**
		 *  初始化搜索事件
		 */
		initSearch				: function(){
			BindEvent.bindResUploadSearchEvent();
		},
		/**
		 *  查找上传的物品
		 */
		findUploadRes	: function(pageIndex){

			var resName = $("#res_name").val();
			var resCategory1 =  Huazie.data.convertToInt($("#res_type1").val(), 0);
			var resCategory2 =  Huazie.data.convertToInt($("#res_type2").val(), 0);
			var resCategory3 =  Huazie.data.convertToInt($("#res_type3").val(), 0);
			var minPrice = $("#min_price").val();
			var maxPrice = $("#max_price").val();
			var isSupportHistory = $("#is_support_history").is(":checked")
			var startDate = $("#start_date").val();
			var endDate = $("#end_date").val();
			var currentPage = pageIndex + 1;
			var pageNum = 10;

			var cmd = {
				"resSearch.resName" 	: resName,
				"resSearch.resCategory1": resCategory1,
				"resSearch.resCategory2": resCategory2,
				"resSearch.resCategory3": resCategory3,
				"resSearch.minResPrice" : minPrice,
				"resSearch.maxResPrice" : maxPrice,
				"resSearch.startDate" 	: startDate,
				"resSearch.endDate"	  	: endDate,
				"resSearch.currentPage" : currentPage,
				"resSearch.pageNum"		: pageNum,
				"resSearch.isSupportHistorySearch": isSupportHistory,
			}

			$("#upload_table_tbody").html(Huazie.msg.tbodyProgressText(8, "物品信息正在加载中..."));

			Huazie.ajax.getJson(ReqUrlMap.get("findAllRes"), cmd, function(data, status) {

				var result = data;

				if(status){
					var uploadResInfo = result.uploadResInfo;
					//第一页会显示总数目
					if(uploadResInfo && uploadResInfo.count && uploadResInfo.count > 0){
						$("#upload_pagination").pagination(uploadResInfo.count, {
						    num_edge_entries: 2,
						    num_display_entries: 4,
						    callback: function(index){

								currentPage = index + 1;

								cmd["resSearch.currentPage"] = currentPage;

								$("#upload_table_tbody").html(Huazie.msg.tbodyProgressText(8, "物品信息正在加载中..."));

								Huazie.ajax.getJson(ReqUrlMap.get("findAllRes"), cmd, function(data, status) {

									var result = data;

									if(status){

										window.resPicInfo = {};

										var uploadResInfo = result.uploadResInfo;

										Huazie.tpl.loadTemp($("#upload_table_tbody"), "#tpl_table_upload_res", uploadResInfo.data);

										$.each(uploadResInfo.data, function(index, element){
											
											var resId = element["RES_ID"];// 获取物品编号

											var resPic = element["RES_PIC"];// 获取物品图片信息

											window.resPicInfo[resId] = resPic;

										});

										res.ResModule.initResPicView("a[id^='upload_pic_']", "#upload_res_pic", "#upload_res_description");// 绑定图片查看按钮
									}else{
										Huazie.dialog.tips("warning", result.retMess, 2);
									}

								});

								$("#upload_res_pic").html("");// 清空图片区域的显示
								$("#upload_res_description").html("");// 清空物品描述信息
						    },
							items_per_page : pageNum,
							prev_text : "上一页",
							next_text : "下一页"
						});
					}else{
						//应该就是从第一页就没有数据的情况
						if(cmd["resSearch.currentPage"] == 1){
							if(uploadResInfo && uploadResInfo.data){
								Huazie.tpl.loadTemp($("#upload_table_tbody"), "#tpl_table_upload_res", uploadResInfo.data);
							}
						}
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
		bindResUploadSearchEvent : function(){
			$("#search").off("click");
			$("#search").on("click", function(){
				// 查找跳主已上传的所有状态的物品
				ResSearchModule.findUploadRes(0);
				$("#upload_res_pic").html("");// 清空图片区域的显示
				$("#upload_res_description").html("");// 清空物品描述信息
			});
		}

	}

});