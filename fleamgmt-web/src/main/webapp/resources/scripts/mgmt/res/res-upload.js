/**
 * @Description res-upload.js
 * 
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月17日
 */
define(function(require, exports, module) {

	// 开始提交物品审核
	ReqUrlMap.put("uploadRes", "res!uploadRes.flea");

	// 判断是否上传过图片，针对当前用户
	ReqUrlMap.put("isUploadResPic", "res!isUploadResPic.flea");

	// 上传文件
	ReqUrlMap.put("uploadResPic", "upload.flea");

	var res = require('./res');//加载公共模块res.js

	exports.init = function(){

		// 加载物品上传向导
		ResUploadModule.loadResUploadWizard();
		
		// 加载物品类型
		res.ResModule.loadResType('#res_type');

	}

	var ResUploadModule = {
		
		/**
		 * 	加载物品上传向导
		 */
		loadResUploadWizard	: function(){

			$('#res_wizard').ace_wizard().on('change' , function(e, info){
				// 点了下一步
				if(info.direction == "next") {
					if(info.step == 1){

						if(!StepFunction.step1()){
							return false;
						}else{
							//这边要加载
							if(!dropzoneExists("#res_dropzone")){
								ResUploadModule.initDropzone();
							}

						}
					}else if(info.step == 2){
						// 要去判断图片有没有上传
						if(!StepFunction.step2()){
							return false;
						}
					}
				}

			}).on('finished', function(e) {
				// 在这边开始保存物品和物品图片关联
				StepFunction.step3();

			}).on('stepclick', function(e){
				//return false;//prevent clicking on steps
				Huazie.log(e);
			});
		},
		/**
		 *	初始化Dropzone
		 */
		initDropzone : function(){
			$("#res_dropzone").dropzone({
		  		url: ReqUrlMap.get("uploadResPic"),
		    	paramName: "file", 	// The name that will be used to transfer the file
		    	maxFilesize: 5, 	// MB
				addRemoveLinks : true,
				autoProcessQueue: false,
				parallelUploads : 10,
				acceptedFiles : ".jpg,.png,.gif,.bmp,.jpeg,.JPG,.PNG,.GIF,.BMP,.JPEG" ,
				dictDefaultMessage :'<span class="bigger-150 bolder"><i class="fa fa-caret-right red"></i> 拖拽文件</span> 上传 \
					<span class="smaller-80 grey">(或者 点击)</span> <br /> \
					<i class="upload-icon fa fa-cloud-upload blue fa-3x"></i>',
				dictResponseError: '上传文件失败',
				//change the previewTemplate to use Bootstrap progress bars
				previewTemplate: "<div class=\"dz-preview dz-file-preview\">\n  <div class=\"dz-details\">\n    <div class=\"dz-filename\"><span data-dz-name></span></div>\n    <div class=\"dz-size\" data-dz-size></div>\n    <img data-dz-thumbnail />\n  </div>\n  <div class=\"progress progress-small progress-striped active\"><div class=\"progress-bar progress-bar-success\" data-dz-uploadprogress></div></div>\n  <div class=\"dz-success-mark\"><span></span></div>\n  <div class=\"dz-error-mark\"><span></span></div>\n  <div class=\"dz-error-message\"><span data-dz-errormessage></span></div>\n</div>",
				init: function() {
			    	var submitButton = document.querySelector("#res_upload");
			        myDropzone = this; // closure
			    	submitButton.addEventListener("click", function() {
			      		myDropzone.processQueue(); // Tell Dropzone to process all queued files.
			    	});
			    	// You might want to show the submit button only when 
			    	// files are dropped here:
			    	this.on("addedfile", function() {
			      		// Show submit button here and/or inform user to click it.
			      		$("#res_upload").removeClass("hidden");
			    	});

			    	this.on("removedfile", function(){
			    		if(myDropzone.files.length == 0){
							$("#res_upload").addClass("hidden");
			    		}
			    	});

			  	}
		  	});

		}

	};

	var StepFunction = {
		/**
		 *	向导1 中点击了下一步
		 */
		step1	: function(){
			// 首先进行校验
			var resName = $("#res_name").val();
			var resType1 = $("#res_type1").val();
			var resPrice = $("#res_price").val();

			if(resName.trim() == ""){
				Huazie.dialog.tips("warning", "亲，商品名称不能为空哦", 1);
				return false;
			}

			if(resType1.trim() == ""){
				Huazie.dialog.tips("warning", "亲，请选择商品类型", 1);
				return false;
			}

			if(resPrice.trim() == ""){
				Huazie.dialog.tips("warning", "亲，商品价格不能为空哦", 1);
				return false;
			}else{
				// 判断是否为正数
				if(!Huazie.validate.test("pNum", resPrice)){
					Huazie.dialog.tips("warning", "亲，商品价格只能为正数哦", 1);
					return false;
				}
			}

			return true;
		},
		/**
		 *	向导2 中点击了下一步
		 */
		step2	: function(){

			var isUploadResPic = false;

			Huazie.ajax.getJsonSync(ReqUrlMap.get("isUploadResPic"), function(data, status){

				var result = data;

				if(status){
					if(result.retCode == "Y"){
						isUploadResPic = true;
					}else{
						Huazie.dialog.tips("warning", result.retMess, 2);
					}

				}else{
					Huazie.dialog.tips("warning", result.retMess, 2);
				}

			});

			return isUploadResPic;

		},
		/**
		 *	向导3 中点击了提交审核
		 */
		step3	: function(){

			var res = Huazie.form.serialize($("#res_add"));

			Huazie.ajax.postJson(ReqUrlMap.get("uploadRes"), res, function(data, status){

				var result = data;

				if(status){
					if(result.retCode == "Y"){
						Huazie.dialog.tips("info", result.retMess, 2);
						//这边开始重置到第一步
						setTimeout(function () {
						    window.location.reload();
						}, 2000);

					}else{
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
		 * 	绑定事件
		 */

	};

	function dropzoneExists(selector) {
        var elements = $(selector).find('.dz-default');
        return elements.length > 0;
    }

});