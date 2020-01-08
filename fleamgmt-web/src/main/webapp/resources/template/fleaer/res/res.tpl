<!-- 图片相关模板-->
<script id="tpl_res_pic" type="text/x-handlebars-template">
	<h4 class="lighter block grey">
		<i class="fa fa-picture-o blue fa-lg"></i>
		物品图片
	</h4>
	<div>
		<ul class="ace-thumbnails">
		{{#common_list this}}
			<a href="{{RES_PIC_PATH}}" title="{{RES_NAME}}" data-rel="colorbox">
				<img alt="150x150" src="{{RES_PIC_PATH}}" style="width:150px;height:150px;"/>
			</a>
		{{/common_list}}
		</ul>
	</div>
</script>

<script id="tpl_res_description" type="text/x-handlebars-template">
	<h4 class="lighter block grey">
		<i class="fa fa-info-circle blue fa-lg"></i>
		物品描述
	</h4>
	<div class="col-xs-12">
		<div class="clearfix">
			<textarea class="col-xs-12 col-sm-12" disabled>{{RES_DESCRIPTION}}</textarea>
		</div>
	</div>
</script>
