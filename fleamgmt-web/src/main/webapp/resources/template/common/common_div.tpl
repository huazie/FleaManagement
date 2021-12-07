<!-- 
	通用a标签
-->
<script id="tpl_common_div_a" type="text/x-handlebars-template">
	{{#common_list menuFavoritesList}}
	<div class="itemdiv memberdiv">
		<a id="favorite_{{MENU_CODE}}" class="{{COLOR}}" name="{{MENU_CODE}}">
			<i class="fa fa-{{MENU_ICON}} fa-lg"></i>
			{{MENU_NAME}}
		</a>
	</div>
	{{/common_list}}
</script>