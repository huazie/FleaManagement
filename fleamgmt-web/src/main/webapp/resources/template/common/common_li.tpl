<!-- 
	通用li标签
-->
<script id="tpl_common_li_1" type="text/x-handlebars-template">
	{{#if_eq MENU_LEVEL compare=1}}
	<li >
		<i class="fa fa-{{MENU_ICON}} home-icon"></i>
	{{else}}
	<li class="active">
	{{/if_eq}}
		{{#if HAS_SUB_MENU}}
		<a href="javascript:;">{{MENU_NAME}}</a>
		{{else}}
		{{MENU_NAME}}
		{{/if}}
	</li>
</script>

